package ddperson.redis;

import ddperson.gigachat.client.GigaChatOAuthClient;
import ddperson.gigachat.client.GigaChatOAuthClient.OAuthToken;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class GigaChatTokenCache {

    private static final String KEY = "gigachat:oauth:access_token";
    private static final String EXPIRES_KEY = "gigachat:oauth:expires_at";

    private final StringRedisTemplate redisTemplate;
    private final GigaChatOAuthClient oauthClient;

    public GigaChatTokenCache(StringRedisTemplate redisTemplate, GigaChatOAuthClient oauthClient) {
        this.redisTemplate = redisTemplate;
        this.oauthClient = oauthClient;
    }

    public String getAccessToken() {
        String cached = redisTemplate.opsForValue().get(KEY);
        String expiresRaw = redisTemplate.opsForValue().get(EXPIRES_KEY);
        if (cached != null && expiresRaw != null) {
            Instant expiresAt = Instant.ofEpochSecond(Long.parseLong(expiresRaw));
            if (expiresAt.isAfter(Instant.now().plusSeconds(30))) {
                return cached;
            }
        }

        OAuthToken token = oauthClient.fetchToken();
        Duration ttl = Duration.between(Instant.now(), token.expiresAt().minusSeconds(30));
        if (ttl.isNegative() || ttl.isZero()) {
            ttl = Duration.ofMinutes(5);
        }
        redisTemplate.opsForValue().set(KEY, token.accessToken(), ttl);
        redisTemplate.opsForValue().set(EXPIRES_KEY, String.valueOf(token.expiresAt().getEpochSecond()), ttl);
        return token.accessToken();
    }

    public void invalidate() {
        redisTemplate.delete(KEY);
        redisTemplate.delete(EXPIRES_KEY);
    }
}
