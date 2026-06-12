package ddperson.gigachat.client;

import ddperson.config.AppProperties;
import ddperson.gigachat.dto.GigaChatOAuthResponse;
import ddperson.gigachat.exception.GigaChatException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.time.Instant;

@Component
public class GigaChatOAuthClient {

    private final WebClient webClient;
    private final AppProperties properties;

    public GigaChatOAuthClient(WebClient gigaChatWebClient, AppProperties properties) {
        this.webClient = gigaChatWebClient;
        this.properties = properties;
    }

    public OAuthToken fetchToken() {
        String authKey = properties.gigachat().authKey();
        if (authKey == null || authKey.isBlank()) {
            throw new GigaChatException("GIGACHAT_AUTH_KEY не задан", null, "GIGACHAT_NOT_CONFIGURED");
        }

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("scope", properties.gigachat().scope());

        try {
            GigaChatOAuthResponse response = webClient.post()
                    .uri(properties.gigachat().oauthUrl())
                    .header("Authorization", "Basic " + authKey)
                    .header("RqUID", java.util.UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(body))
                    .retrieve()
                    .bodyToMono(GigaChatOAuthResponse.class)
                    .block(Duration.ofMillis(properties.gigachat().connectTimeoutMs()));

            if (response == null || response.accessToken() == null) {
                throw new GigaChatException("Пустой OAuth-ответ GigaChat", null, "OAUTH_EMPTY");
            }
            return new OAuthToken(response.accessToken(), Instant.ofEpochSecond(response.expiresAt()));
        } catch (WebClientResponseException ex) {
            throw new GigaChatException("Ошибка OAuth GigaChat: " + ex.getStatusCode(), ex.getStatusCode().value(), "OAUTH_FAILED");
        } catch (GigaChatException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new GigaChatException("Сбой сети при OAuth GigaChat", ex);
        }
    }

    public record OAuthToken(String accessToken, Instant expiresAt) {
    }
}
