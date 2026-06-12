package ddperson.service.port;

import ddperson.gigachat.dto.GigaChatChatResponse;

import java.util.UUID;

public interface ImageGenerationPort {

    GeneratedImage generate(UUID userId, UUID requestId, String systemPrompt, String userPrompt);

    record GeneratedImage(
            String gigachatFileId,
            byte[] imageBytes,
            String model,
            GigaChatChatResponse.Usage usage
    ) {
    }
}
