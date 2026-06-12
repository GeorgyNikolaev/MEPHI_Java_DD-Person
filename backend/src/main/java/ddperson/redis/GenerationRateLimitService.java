package ddperson.redis;

import ddperson.config.AppProperties;
import ddperson.domain.exception.BusinessException;
import ddperson.domain.exception.ErrorCode;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class GenerationRateLimitService {

    private final StringRedisTemplate redisTemplate;
    private final int perDay;
    private final int perHour;

    public GenerationRateLimitService(StringRedisTemplate redisTemplate, AppProperties properties) {
        this.redisTemplate = redisTemplate;
        this.perDay = properties.rateLimit().generationsPerDay();
        this.perHour = properties.rateLimit().generationsPerHour();
    }

    public void checkAndIncrement(UUID userId) {
        String dayKey = "rate:gen:" + userId + ":day:" + LocalDate.now();
        String hourKey = "rate:gen:" + userId + ":hour:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH"));

        long dayCount = increment(dayKey, Duration.ofDays(1));
        long hourCount = increment(hourKey, Duration.ofHours(1));

        if (dayCount > perDay || hourCount > perHour) {
            throw new BusinessException(ErrorCode.RATE_LIMIT_EXCEEDED);
        }
    }

    private long increment(String key, Duration ttl) {
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1L) {
            redisTemplate.expire(key, ttl);
        }
        return count == null ? 0L : count;
    }
}
