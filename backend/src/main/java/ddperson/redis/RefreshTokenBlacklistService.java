package ddperson.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class RefreshTokenBlacklistService {

    private static final String KEY_PREFIX = "refresh:blacklist:";

    private final StringRedisTemplate redisTemplate;

    public RefreshTokenBlacklistService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void blacklist(String tokenHash, Instant expiresAt) {
        Duration ttl = Duration.between(Instant.now(), expiresAt);
        if (ttl.isNegative() || ttl.isZero()) {
            return;
        }
        redisTemplate.opsForValue().set(KEY_PREFIX + tokenHash, "1", ttl);
    }

    public boolean isBlacklisted(String tokenHash) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(KEY_PREFIX + tokenHash));
    }
}
