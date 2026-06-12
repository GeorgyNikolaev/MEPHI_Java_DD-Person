package ddperson.gigachat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GigaChatOAuthResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("expires_at") long expiresAt
) {
}
