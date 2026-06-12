package ddperson.service.port;

import java.util.UUID;

public interface ImageGenerationPort {

    GeneratedImage generate(UUID userId, UUID requestId, String systemPrompt, String userPrompt);

    record GeneratedImage(String gigachatFileId, byte[] imageBytes) {
    }
}
