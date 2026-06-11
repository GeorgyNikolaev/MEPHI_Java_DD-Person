package ddperson.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        Cors cors,
        Jwt jwt,
        Gigachat gigachat,
        Storage storage,
        RateLimit rateLimit
) {
    public record Cors(List<String> allowedOrigins) {
    }

    public record Jwt(int accessTokenTtlMinutes, int refreshTokenTtlDays, String secret) {
    }

    public record Gigachat(String authKey, String scope, String model) {
    }

    public record Storage(String portraitsPath) {
    }

    public record RateLimit(int generationsPerDay, int generationsPerHour) {
    }
}
