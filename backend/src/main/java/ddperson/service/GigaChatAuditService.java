package ddperson.service;

import ddperson.domain.enums.GigachatCallType;
import ddperson.persistence.entity.GigachatApiCallEntity;
import ddperson.persistence.repository.GigachatApiCallRepository;
import ddperson.persistence.repository.GenerationRequestRepository;
import ddperson.persistence.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GigaChatAuditService {

    private final GigachatApiCallRepository repository;
    private final UserRepository userRepository;
    private final GenerationRequestRepository requestRepository;

    public GigaChatAuditService(
            GigachatApiCallRepository repository,
            UserRepository userRepository,
            GenerationRequestRepository requestRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(
            UUID userId,
            UUID requestId,
            GigachatCallType callType,
            Integer httpStatus,
            int durationMs,
            String summary,
            String errorCode) {

        GigachatApiCallEntity entity = new GigachatApiCallEntity();
        entity.setUser(userRepository.getReferenceById(userId));
        if (requestId != null) {
            entity.setRequest(requestRepository.getReferenceById(requestId));
        }

        entity.setCallType(callType);
        entity.setHttpStatus(httpStatus);
        entity.setDurationMs(durationMs);
        entity.setResponseSummary(summary);
        entity.setErrorCode(errorCode);
        repository.save(entity);
    }
}
