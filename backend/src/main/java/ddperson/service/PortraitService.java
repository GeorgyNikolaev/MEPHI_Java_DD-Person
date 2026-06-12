package ddperson.service;

import ddperson.domain.exception.ResourceNotFoundException;
import ddperson.persistence.entity.PortraitEntity;
import ddperson.persistence.repository.PortraitRepository;
import ddperson.security.SecurityUtils;
import ddperson.storage.PortraitStorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.UUID;

@Service
public class PortraitService {

    private final PortraitRepository portraitRepository;
    private final PortraitStorageService storageService;

    public PortraitService(PortraitRepository portraitRepository, PortraitStorageService storageService) {
        this.portraitRepository = portraitRepository;
        this.storageService = storageService;
    }

    @Transactional(readOnly = true)
    public PortraitEntity getOwnedPortrait(UUID portraitId) {
        UUID userId = SecurityUtils.requireCurrentUserId();
        return portraitRepository.findByIdAndUserId(portraitId, userId)
                .orElseThrow(ResourceNotFoundException::new);
    }

    public byte[] readImage(PortraitEntity portrait) {
        try {
            return storageService.read(portrait.getStoragePath());
        } catch (IOException ex) {
            throw new ResourceNotFoundException();
        }
    }
}
