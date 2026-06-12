package ddperson.service;

import ddperson.api.ApiMessages;
import ddperson.api.MessageCode;
import ddperson.api.dto.auth.MessageResponse;
import ddperson.api.dto.common.PageResponse;
import ddperson.api.dto.favorite.FavoritePortraitResponse;
import ddperson.api.mapper.FavoriteMapper;
import ddperson.domain.exception.ConflictException;
import ddperson.domain.exception.ErrorCode;
import ddperson.domain.exception.ResourceNotFoundException;
import ddperson.persistence.entity.FavoritePortraitEntity;
import ddperson.persistence.entity.PortraitEntity;
import ddperson.persistence.entity.UserEntity;
import ddperson.persistence.repository.FavoritePortraitRepository;
import ddperson.persistence.repository.UserRepository;
import ddperson.security.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class FavoriteService {

    private final FavoritePortraitRepository favoriteRepository;
    private final PortraitService portraitService;
    private final UserRepository userRepository;
    private final FavoriteMapper mapper;
    private final ApiMessages messages;

    public FavoriteService(
            FavoritePortraitRepository favoriteRepository,
            PortraitService portraitService,
            UserRepository userRepository,
            FavoriteMapper mapper,
            ApiMessages messages) {
        this.favoriteRepository = favoriteRepository;
        this.portraitService = portraitService;
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.messages = messages;
    }

    @Transactional
    public MessageResponse add(UUID portraitId) {
        UUID userId = SecurityUtils.requireCurrentUserId();
        PortraitEntity portrait = portraitService.getOwnedPortrait(portraitId);

        if (favoriteRepository.existsByUserIdAndPortraitId(userId, portraitId)) {
            throw new ConflictException(ErrorCode.FAVORITE_ALREADY_EXISTS);
        }

        UserEntity user = userRepository.getReferenceById(userId);
        FavoritePortraitEntity favorite = new FavoritePortraitEntity();
        favorite.setUser(user);
        favorite.setPortrait(portrait);
        favoriteRepository.save(favorite);

        return new MessageResponse(messages.get(MessageCode.SUCCESS_FAVORITE_ADDED));
    }

    @Transactional
    public MessageResponse remove(UUID portraitId) {
        UUID userId = SecurityUtils.requireCurrentUserId();
        portraitService.getOwnedPortrait(portraitId);

        FavoritePortraitEntity favorite = favoriteRepository.findByUserIdAndPortraitId(userId, portraitId)
                .orElseThrow(ResourceNotFoundException::new);
        favoriteRepository.delete(favorite);

        return new MessageResponse(messages.get(MessageCode.SUCCESS_FAVORITE_REMOVED));
    }

    @Transactional(readOnly = true)
    public PageResponse<FavoritePortraitResponse> list(int page, int size) {
        UUID userId = SecurityUtils.requireCurrentUserId();
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        PageRequest pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<FavoritePortraitEntity> result = favoriteRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return PageResponse.from(result.map(mapper::toResponse));
    }
}
