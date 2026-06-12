package ddperson.service;

import ddperson.api.dto.common.PageResponse;
import ddperson.api.dto.generation.CreateGenerationRequest;
import ddperson.api.dto.generation.GenerationDetailResponse;
import ddperson.api.dto.generation.GenerationSummaryResponse;
import ddperson.api.mapper.GenerationMapper;
import ddperson.domain.enums.GenerationStatus;
import ddperson.domain.exception.BusinessException;
import ddperson.domain.exception.ErrorCode;
import ddperson.domain.exception.ResourceNotFoundException;
import ddperson.generation.prompt.BuiltPrompt;
import ddperson.generation.prompt.GenerationInput;
import ddperson.generation.prompt.builder.PromptBuilder;
import ddperson.persistence.entity.CharacterEntity;
import ddperson.persistence.entity.GenerationParametersEntity;
import ddperson.persistence.entity.GenerationRequestEntity;
import ddperson.persistence.entity.PortraitEntity;
import ddperson.persistence.entity.UserEntity;
import ddperson.persistence.repository.CharacterRepository;
import ddperson.persistence.repository.FavoritePortraitRepository;
import ddperson.persistence.repository.GenerationRequestRepository;
import ddperson.persistence.repository.GigachatApiCallRepository;
import ddperson.persistence.repository.UserRepository;
import ddperson.redis.GenerationRateLimitService;
import ddperson.security.SecurityUtils;
import ddperson.service.event.GenerationRequestedEvent;
import ddperson.storage.PortraitStorageService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GenerationService {

    private final GenerationRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final CharacterRepository characterRepository;
    private final FavoritePortraitRepository favoritePortraitRepository;
    private final GigachatApiCallRepository gigachatApiCallRepository;
    private final PortraitStorageService portraitStorageService;
    private final PromptBuilder promptBuilder;
    private final GenerationMapper mapper;
    private final GenerationRateLimitService rateLimitService;
    private final ApplicationEventPublisher eventPublisher;

    public GenerationService(
            GenerationRequestRepository requestRepository,
            UserRepository userRepository,
            CharacterRepository characterRepository,
            FavoritePortraitRepository favoritePortraitRepository,
            GigachatApiCallRepository gigachatApiCallRepository,
            PortraitStorageService portraitStorageService,
            PromptBuilder promptBuilder,
            GenerationMapper mapper,
            GenerationRateLimitService rateLimitService,
            ApplicationEventPublisher eventPublisher) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.characterRepository = characterRepository;
        this.favoritePortraitRepository = favoritePortraitRepository;
        this.gigachatApiCallRepository = gigachatApiCallRepository;
        this.portraitStorageService = portraitStorageService;
        this.promptBuilder = promptBuilder;
        this.mapper = mapper;
        this.rateLimitService = rateLimitService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public GenerationSummaryResponse create(CreateGenerationRequest request) {
        return createInternal(toInput(request), null);
    }

    @Transactional
    public GenerationSummaryResponse createFromCharacter(CharacterEntity character) {
        CreateGenerationRequest request = new CreateGenerationRequest(
                character.getDescription(),
                character.getRoleArchetype(),
                character.getUniverseStyle(),
                character.getSeriousnessLevel(),
                character.getExpressivenessLevel(),
                character.getMood()
        );
        return createInternal(toInput(request), character);
    }

    private GenerationSummaryResponse createInternal(GenerationInput input, CharacterEntity character) {
        UUID userId = SecurityUtils.requireCurrentUserId();
        rateLimitService.checkAndIncrement(userId);

        BuiltPrompt builtPrompt = promptBuilder.build(input);

        UserEntity user = userRepository.getReferenceById(userId);
        GenerationRequestEntity entity = new GenerationRequestEntity();
        entity.setUser(user);
        entity.setCharacter(character);
        entity.setStatus(GenerationStatus.PENDING);
        entity.setBuiltSystemPrompt(builtPrompt.systemPrompt());
        entity.setBuiltUserPrompt(builtPrompt.userPrompt());

        GenerationParametersEntity parameters = new GenerationParametersEntity();
        parameters.setCharacterDescription(input.characterDescription());
        parameters.setRoleArchetype(input.roleArchetype());
        parameters.setUniverseStyle(input.universeStyle());
        parameters.setSeriousnessLevel(input.seriousnessLevel());
        parameters.setExpressivenessLevel(input.expressivenessLevel());
        parameters.setMood(input.mood());
        entity.setParameters(parameters);

        GenerationRequestEntity saved = requestRepository.save(entity);
        eventPublisher.publishEvent(new GenerationRequestedEvent(saved.getId()));
        return mapper.toSummary(saved);
    }

    @Transactional(readOnly = true)
    public GenerationDetailResponse getById(UUID id) {
        UUID userId = SecurityUtils.requireCurrentUserId();
        GenerationRequestEntity entity = requestRepository.findByIdAndUserId(id, userId)
                .orElseThrow(ResourceNotFoundException::new);
        return mapper.toDetail(entity);
    }

    @Transactional(readOnly = true)
    public PageResponse<GenerationSummaryResponse> list(GenerationStatus status, int page, int size) {
        UUID userId = SecurityUtils.requireCurrentUserId();
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        PageRequest pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<GenerationRequestEntity> result = status == null
                ? requestRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                : requestRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status, pageable);
        return PageResponse.from(result.map(mapper::toSummary));
    }

    @Transactional
    public GenerationSummaryResponse retry(UUID id) {
        UUID userId = SecurityUtils.requireCurrentUserId();
        GenerationRequestEntity source = requestRepository.findByIdAndUserId(id, userId)
                .orElseThrow(ResourceNotFoundException::new);

        if (source.getParameters() == null) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR);
        }

        CreateGenerationRequest request = new CreateGenerationRequest(
                source.getParameters().getCharacterDescription(),
                source.getParameters().getRoleArchetype(),
                source.getParameters().getUniverseStyle(),
                source.getParameters().getSeriousnessLevel(),
                source.getParameters().getExpressivenessLevel(),
                source.getParameters().getMood()
        );
        return create(request);
    }

    @Transactional
    public void delete(UUID id) {
        UUID userId = SecurityUtils.requireCurrentUserId();
        GenerationRequestEntity entity = requestRepository.findByIdAndUserId(id, userId)
                .orElseThrow(ResourceNotFoundException::new);

        PortraitEntity portrait = entity.getPortrait();
        String storagePath = portrait != null ? portrait.getStoragePath() : null;

        if (portrait != null) {
            favoritePortraitRepository.deleteByPortraitId(portrait.getId());
            for (CharacterEntity character : characterRepository.findByLastPortrait_Id(portrait.getId())) {
                character.setLastPortrait(null);
            }
        }

        gigachatApiCallRepository.deleteByRequestId(id);
        requestRepository.delete(entity);

        if (storagePath != null) {
            portraitStorageService.deleteIfExists(storagePath);
        }
    }

    private GenerationInput toInput(CreateGenerationRequest request) {
        return new GenerationInput(
                request.characterDescription(),
                request.roleArchetype(),
                request.universeStyle(),
                request.seriousnessLevel(),
                request.expressivenessLevel(),
                request.mood()
        );
    }
}
