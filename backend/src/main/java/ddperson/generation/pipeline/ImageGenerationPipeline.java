package ddperson.generation.pipeline;

import ddperson.domain.enums.GenerationStatus;
import ddperson.domain.enums.GigachatCallType;
import ddperson.gigachat.exception.GigaChatException;
import ddperson.persistence.entity.GenerationRequestEntity;
import ddperson.persistence.entity.PortraitEntity;
import ddperson.persistence.repository.GenerationRequestRepository;
import ddperson.service.GigaChatAuditService;
import ddperson.service.port.ImageGenerationPort;
import ddperson.storage.PortraitStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Component
public class ImageGenerationPipeline {

    private static final Logger log = LoggerFactory.getLogger(ImageGenerationPipeline.class);

    private final GenerationRequestRepository requestRepository;
    private final ImageGenerationPort imageGenerationPort;
    private final PortraitStorageService storageService;
    private final GigaChatAuditService auditService;

    public ImageGenerationPipeline(
            GenerationRequestRepository requestRepository,
            ImageGenerationPort imageGenerationPort,
            PortraitStorageService storageService,
            GigaChatAuditService auditService) {
        this.requestRepository = requestRepository;
        this.imageGenerationPort = imageGenerationPort;
        this.storageService = storageService;
        this.auditService = auditService;
    }

    @Transactional
    public void execute(UUID requestId) {
        GenerationRequestEntity request = requestRepository.findById(requestId)
                .orElse(null);
        if (request == null || request.getStatus() != GenerationStatus.PENDING) {
            return;
        }

        UUID userId = request.getUser().getId();
        request.setStatus(GenerationStatus.PROCESSING);
        request.setStartedAt(Instant.now());
        requestRepository.save(request);

        long started = System.currentTimeMillis();
        try {
            ImageGenerationPort.GeneratedImage image = imageGenerationPort.generate(
                    userId,
                    requestId,
                    request.getBuiltSystemPrompt(),
                    request.getBuiltUserPrompt()
            );

            int duration = (int) (System.currentTimeMillis() - started);
            auditService.log(userId, requestId, GigachatCallType.CHAT_COMPLETION, 200, duration,
                    "imageId=" + image.gigachatFileId(), null);

            String storagePath = storageService.save(userId, requestId, image.imageBytes());

            PortraitEntity portrait = new PortraitEntity();
            portrait.setGigachatFileId(image.gigachatFileId());
            portrait.setStoragePath(storagePath);
            portrait.setMimeType("image/jpeg");
            portrait.setFileSizeBytes((long) image.imageBytes().length);
            request.setPortrait(portrait);

            request.setStatus(GenerationStatus.COMPLETED);
            request.setCompletedAt(Instant.now());
            request.setErrorCode(null);
            request.setErrorMessage(null);
            requestRepository.save(request);

        } catch (GigaChatException ex) {
            fail(request, userId, requestId, ex.getErrorCode(), ex.getMessage(), ex.getHttpStatus(), started);
        } catch (Exception ex) {
            log.error("Ошибка pipeline генерации requestId={}", requestId, ex);
            fail(request, userId, requestId, "PIPELINE_ERROR", ex.getMessage(), null, started);
        }
    }

    private void fail(
            GenerationRequestEntity request,
            UUID userId,
            UUID requestId,
            String code,
            String message,
            Integer httpStatus,
            long started) {
        int duration = (int) (System.currentTimeMillis() - started);
        auditService.log(userId, requestId, GigachatCallType.CHAT_COMPLETION, httpStatus, duration, message, code);

        request.setStatus(GenerationStatus.FAILED);
        request.setErrorCode(code);
        request.setErrorMessage(message);
        request.setCompletedAt(Instant.now());
        requestRepository.save(request);
    }
}
