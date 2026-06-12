package ddperson.service;

import ddperson.gigachat.dto.GigaChatChatResponse;

import java.util.UUID;

public record GigaChatAuditEntry(
        UUID userId,
        UUID requestId,
        ddperson.domain.enums.GigachatCallType callType,
        Integer httpStatus,
        int durationMs,
        String summary,
        String errorCode,
        String model,
        GigaChatChatResponse.Usage usage
) {
}
