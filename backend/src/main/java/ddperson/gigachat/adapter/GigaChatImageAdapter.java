package ddperson.gigachat.adapter;

import ddperson.gigachat.client.GigaChatApiClient;
import ddperson.gigachat.dto.GigaChatChatResult;
import ddperson.gigachat.parser.GigaChatImageParser;
import ddperson.service.port.ImageGenerationPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GigaChatImageAdapter implements ImageGenerationPort {

    private final GigaChatApiClient apiClient;
    private final GigaChatImageParser imageParser;

    public GigaChatImageAdapter(GigaChatApiClient apiClient, GigaChatImageParser imageParser) {
        this.apiClient = apiClient;
        this.imageParser = imageParser;
    }

    @Override
    public GeneratedImage generate(UUID userId, UUID requestId, String systemPrompt, String userPrompt) {
        GigaChatChatResult chatResult = apiClient.generateImage(systemPrompt, userPrompt);
        String fileId = imageParser.extractFileId(chatResult.content());
        byte[] bytes = apiClient.downloadFile(fileId);
        return new GeneratedImage(fileId, bytes, chatResult.model(), chatResult.usage());
    }
}
