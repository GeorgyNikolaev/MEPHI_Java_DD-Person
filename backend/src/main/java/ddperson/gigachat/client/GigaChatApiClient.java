package ddperson.gigachat.client;

import ddperson.config.AppProperties;
import ddperson.gigachat.dto.GigaChatChatRequest;
import ddperson.gigachat.dto.GigaChatChatResponse;
import ddperson.gigachat.exception.GigaChatException;
import ddperson.redis.GigaChatTokenCache;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.List;

@Component
public class GigaChatApiClient {

    private final WebClient webClient;
    private final AppProperties properties;
    private final GigaChatTokenCache tokenCache;

    public GigaChatApiClient(WebClient gigaChatWebClient, AppProperties properties, GigaChatTokenCache tokenCache) {
        this.webClient = gigaChatWebClient;
        this.properties = properties;
        this.tokenCache = tokenCache;
    }

    public String generateImage(String systemPrompt, String userPrompt) {
        return executeWithAuth(token -> {
            GigaChatChatRequest request = new GigaChatChatRequest(
                    properties.gigachat().model(),
                    List.of(
                            new GigaChatChatRequest.GigaChatMessage("system", systemPrompt),
                            new GigaChatChatRequest.GigaChatMessage("user", userPrompt)
                    ),
                    "auto"
            );

            try {
                GigaChatChatResponse response = webClient.post()
                        .uri(properties.gigachat().baseUrl() + "/chat/completions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .bodyValue(request)
                        .retrieve()
                        .bodyToMono(GigaChatChatResponse.class)
                        .block(Duration.ofMillis(properties.gigachat().readTimeoutMs()));

                if (response == null || response.firstContent() == null) {
                    throw new GigaChatException("Пустой ответ chat/completions", null, "EMPTY_COMPLETION");
                }
                return response.firstContent();
            } catch (WebClientResponseException ex) {
                if (ex.getStatusCode().value() == 401) {
                    tokenCache.invalidate();
                }
                throw mapHttpError(ex, "CHAT_COMPLETION_FAILED");
            }
        });
    }

    public byte[] downloadFile(String fileId) {
        return executeWithAuth(token -> {
            try {
                byte[] bytes = webClient.get()
                        .uri(properties.gigachat().baseUrl() + "/files/" + fileId + "/content")
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.parseMediaType("application/jpg"))
                        .retrieve()
                        .bodyToMono(byte[].class)
                        .block(Duration.ofMillis(properties.gigachat().readTimeoutMs()));

                if (bytes == null || bytes.length == 0) {
                    throw new GigaChatException("Пустой файл изображения", null, "EMPTY_FILE");
                }
                return bytes;
            } catch (WebClientResponseException ex) {
                if (ex.getStatusCode().value() == 401) {
                    tokenCache.invalidate();
                }
                throw mapHttpError(ex, "FILE_DOWNLOAD_FAILED");
            }
        });
    }

    private <T> T executeWithAuth(java.util.function.Function<String, T> action) {
        try {
            return action.apply(tokenCache.getAccessToken());
        } catch (GigaChatException ex) {
            if ("OAUTH_FAILED".equals(ex.getErrorCode()) || ex.getHttpStatus() != null && ex.getHttpStatus() == 401) {
                tokenCache.invalidate();
                return action.apply(tokenCache.getAccessToken());
            }
            throw ex;
        } catch (Exception ex) {
            throw new GigaChatException("Сбой сети при обращении к GigaChat", ex);
        }
    }

    private GigaChatException mapHttpError(WebClientResponseException ex, String code) {
        if (ex.getStatusCode().value() == 429) {
            return new GigaChatException("Превышен лимит GigaChat", 429, "GIGACHAT_RATE_LIMIT");
        }
        return new GigaChatException(
                "Ошибка GigaChat HTTP " + ex.getStatusCode().value(),
                ex.getStatusCode().value(),
                code
        );
    }
}
