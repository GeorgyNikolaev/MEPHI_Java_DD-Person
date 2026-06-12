package ddperson.security;

import ddperson.config.AppProperties;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService(testProperties());

    @Test
    void createAndParseAccessToken_roundTrip() {
        UUID userId = UUID.randomUUID();
        String email = "hero@example.com";

        String token = jwtService.createAccessToken(userId, email);

        assertThat(jwtService.extractUserId(token)).isEqualTo(userId);
        assertThat(jwtService.parseAccessToken(token).get("email", String.class)).isEqualTo(email);
    }

    private static AppProperties testProperties() {
        return new AppProperties(
                new AppProperties.Cors(java.util.List.of("http://localhost:5173")),
                new AppProperties.Jwt(15, 7, "test-secret-at-least-32-characters-long"),
                new AppProperties.Gigachat("", "GIGACHAT_API_PERS", "GigaChat",
                        "https://gigachat.devices.sberbank.ru/api/v1",
                        "https://ngw.devices.sberbank.ru:9443/api/v2/oauth",
                        10_000, 120_000, true),
                new AppProperties.Storage("../storage/portraits"),
                new AppProperties.RateLimit(10, 3)
        );
    }
}
