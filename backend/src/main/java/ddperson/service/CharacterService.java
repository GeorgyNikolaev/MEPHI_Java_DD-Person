package ddperson.service;

import ddperson.api.dto.character.CharacterResponse;
import ddperson.api.dto.character.CharacterSummaryResponse;
import ddperson.api.dto.character.CreateCharacterFromGenerationRequest;
import ddperson.api.dto.character.CreateCharacterRequest;
import ddperson.api.dto.character.UpdateCharacterRequest;
import ddperson.api.dto.common.PageResponse;
import ddperson.api.dto.generation.GenerationSummaryResponse;
import ddperson.api.mapper.CharacterMapper;
import ddperson.domain.exception.BusinessException;
import ddperson.domain.exception.ConflictException;
import ddperson.domain.exception.ErrorCode;
import ddperson.domain.exception.ResourceNotFoundException;
import ddperson.persistence.entity.CharacterEntity;
import ddperson.persistence.entity.GenerationParametersEntity;
import ddperson.persistence.entity.GenerationRequestEntity;
import ddperson.persistence.entity.UserEntity;
import ddperson.persistence.repository.CharacterRepository;
import ddperson.persistence.repository.GenerationRequestRepository;
import ddperson.persistence.repository.UserRepository;
import ddperson.security.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CharacterService {

    private final CharacterRepository characterRepository;
    private final UserRepository userRepository;
    private final GenerationRequestRepository generationRequestRepository;
    private final GenerationService generationService;
    private final CharacterMapper mapper;

    public CharacterService(
            CharacterRepository characterRepository,
            UserRepository userRepository,
            GenerationRequestRepository generationRequestRepository,
            GenerationService generationService,
            CharacterMapper mapper) {
        this.characterRepository = characterRepository;
        this.userRepository = userRepository;
        this.generationRequestRepository = generationRequestRepository;
        this.generationService = generationService;
        this.mapper = mapper;
    }

    @Transactional
    public CharacterResponse create(CreateCharacterRequest request) {
        UUID userId = SecurityUtils.requireCurrentUserId();
        UserEntity user = userRepository.getReferenceById(userId);

        CharacterEntity entity = new CharacterEntity();
        entity.setUser(user);
        applyFields(entity, request.name(), request.description(), request.roleArchetype(),
                request.universeStyle(), request.seriousnessLevel(), request.expressivenessLevel(), request.mood());

        return mapper.toDetail(characterRepository.save(entity));
    }

    @Transactional
    public CharacterResponse createFromGeneration(UUID generationId, CreateCharacterFromGenerationRequest request) {
        UUID userId = SecurityUtils.requireCurrentUserId();
        GenerationRequestEntity generation = generationRequestRepository.findByIdAndUserId(generationId, userId)
                .orElseThrow(ResourceNotFoundException::new);

        if (generation.getCharacter() != null) {
            throw new ConflictException();
        }

        GenerationParametersEntity params = generation.getParameters();
        if (params == null) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR);
        }

        UserEntity user = userRepository.getReferenceById(userId);
        CharacterEntity entity = new CharacterEntity();
        entity.setUser(user);
        applyFields(
                entity,
                request.name(),
                params.getCharacterDescription(),
                params.getRoleArchetype(),
                params.getUniverseStyle(),
                params.getSeriousnessLevel(),
                params.getExpressivenessLevel(),
                params.getMood());

        if (generation.getPortrait() != null) {
            entity.setLastPortrait(generation.getPortrait());
        }

        CharacterEntity saved = characterRepository.save(entity);
        generation.setCharacter(saved);
        generationRequestRepository.save(generation);

        return mapper.toDetail(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<CharacterSummaryResponse> list(int page, int size) {
        UUID userId = SecurityUtils.requireCurrentUserId();
        PageRequest pageable = pageable(page, size);

        Page<CharacterEntity> result = characterRepository.findByUserIdOrderByUpdatedAtDesc(userId, pageable);
        return PageResponse.from(result.map(mapper::toSummary));
    }

    @Transactional(readOnly = true)
    public CharacterResponse getById(UUID id) {
        return mapper.toDetail(requireOwned(id));
    }

    @Transactional
    public CharacterResponse update(UUID id, UpdateCharacterRequest request) {
        CharacterEntity entity = requireOwned(id);
        applyFields(entity, request.name(), request.description(), request.roleArchetype(),
                request.universeStyle(), request.seriousnessLevel(), request.expressivenessLevel(), request.mood());
        return mapper.toDetail(characterRepository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        CharacterEntity entity = requireOwned(id);

        for (GenerationRequestEntity generation : generationRequestRepository.findByCharacter_Id(id)) {
            generation.setCharacter(null);
        }

        characterRepository.delete(entity);
    }

    @Transactional
    public GenerationSummaryResponse generatePortrait(UUID id) {
        CharacterEntity character = requireOwned(id);
        return generationService.createFromCharacter(character);
    }

    private CharacterEntity requireOwned(UUID id) {
        UUID userId = SecurityUtils.requireCurrentUserId();
        return characterRepository.findByIdAndUserId(id, userId)
                .orElseThrow(ResourceNotFoundException::new);
    }

    private void applyFields(
            CharacterEntity entity,
            String name,
            String description,
            ddperson.domain.enums.RoleArchetype roleArchetype,
            ddperson.domain.enums.UniverseStyle universeStyle,
            short seriousnessLevel,
            short expressivenessLevel,
            ddperson.domain.enums.Mood mood) {
        entity.setName(name);
        entity.setDescription(description);
        entity.setRoleArchetype(roleArchetype);
        entity.setUniverseStyle(universeStyle);
        entity.setSeriousnessLevel(seriousnessLevel);
        entity.setExpressivenessLevel(expressivenessLevel);
        entity.setMood(mood);
    }

    private PageRequest pageable(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        return PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "updatedAt"));
    }
}
