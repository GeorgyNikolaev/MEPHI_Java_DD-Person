package ddperson.service;

import ddperson.gigachat.dto.GigaChatChatResponse;
import ddperson.persistence.entity.GigachatApiCallEntity;
import ddperson.persistence.repository.GigachatApiCallRepository;
import ddperson.persistence.repository.GenerationRequestRepository;
import ddperson.persistence.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    public void log(GigaChatAuditEntry entry) {
        GigachatApiCallEntity entity = new GigachatApiCallEntity();
        entity.setUser(userRepository.getReferenceById(entry.userId()));
        if (entry.requestId() != null) {
            entity.setRequest(requestRepository.getReferenceById(entry.requestId()));
        }

        entity.setCallType(entry.callType());
        entity.setHttpStatus(entry.httpStatus());
        entity.setDurationMs(entry.durationMs());
        entity.setResponseSummary(entry.summary());
        entity.setErrorCode(entry.errorCode());
        entity.setModel(entry.model());

        GigaChatChatResponse.Usage usage = entry.usage();
        if (usage != null) {
            entity.setPromptTokens(usage.promptTokens());
            entity.setCompletionTokens(usage.completionTokens());
            entity.setSystemTokens(usage.systemTokens());
            entity.setTotalTokens(usage.totalTokens());
        }

        repository.save(entity);
    }
}
