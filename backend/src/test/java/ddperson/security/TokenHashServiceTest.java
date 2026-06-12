package ddperson.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenHashServiceTest {

    private final TokenHashService tokenHashService = new TokenHashService();

    @Test
    void hash_isDeterministicSha256Hex() {
        String first = tokenHashService.hash("refresh-token-value");
        String second = tokenHashService.hash("refresh-token-value");

        assertThat(first).isEqualTo(second);
        assertThat(first).hasSize(64);
        assertThat(first).matches("[0-9a-f]+");
    }

    @Test
    void hash_differsForDifferentTokens() {
        String a = tokenHashService.hash("token-a");
        String b = tokenHashService.hash("token-b");

        assertThat(a).isNotEqualTo(b);
    }
}
