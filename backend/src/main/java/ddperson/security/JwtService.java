package ddperson.security;

import ddperson.config.AppProperties;
import ddperson.domain.exception.BusinessException;
import ddperson.domain.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long accessTtlMinutes;

    public JwtService(AppProperties appProperties) {
        String secret = appProperties.jwt().secret();
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("app.jwt.secret должен быть не короче 32 символов");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTtlMinutes = appProperties.jwt().accessTokenTtlMinutes();
    }

    public String createAccessToken(UUID userId, String email) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(accessTtlMinutes * 60L);
        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(secretKey)
                .compact();
    }

    public Claims parseAccessToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException ex) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(parseAccessToken(token).getSubject());
    }
}
