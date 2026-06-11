package ddperson.service;

import ddperson.api.ApiMessages;
import ddperson.api.MessageCode;
import ddperson.api.dto.auth.LoginRequest;
import ddperson.api.dto.auth.RegisterRequest;
import ddperson.api.dto.auth.UserResponse;
import ddperson.config.AppProperties;
import ddperson.domain.exception.BusinessException;
import ddperson.domain.exception.ConflictException;
import ddperson.domain.exception.ErrorCode;
import ddperson.persistence.entity.RefreshTokenEntity;
import ddperson.persistence.entity.UserEntity;
import ddperson.persistence.repository.RefreshTokenRepository;
import ddperson.persistence.repository.UserRepository;
import ddperson.redis.RefreshTokenBlacklistService;
import ddperson.security.CookieService;
import ddperson.security.JwtService;
import ddperson.security.TokenHashService;
import ddperson.security.UserPrincipal;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CookieService cookieService;
    private final TokenHashService tokenHashService;
    private final RefreshTokenBlacklistService blacklistService;
    private final ApiMessages messages;
    private final long refreshTtlDays;
    private final long accessTtlMinutes;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            CookieService cookieService,
            TokenHashService tokenHashService,
            RefreshTokenBlacklistService blacklistService,
            ApiMessages messages,
            AppProperties appProperties) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.cookieService = cookieService;
        this.tokenHashService = tokenHashService;
        this.blacklistService = blacklistService;
        this.messages = messages;
        this.refreshTtlDays = appProperties.jwt().refreshTokenTtlDays();
        this.accessTtlMinutes = appProperties.jwt().accessTokenTtlMinutes();
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException(ErrorCode.EMAIL_TAKEN);
        }

        UserEntity user = new UserEntity();
        user.setEmail(request.email().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setDisplayName(request.displayName());
        user.setEnabled(true);
        userRepository.save(user);

        return toUserResponse(user, MessageCode.SUCCESS_REGISTER);
    }

    @Transactional
    public UserResponse login(LoginRequest request, HttpServletResponse response) {
        UserEntity user = userRepository.findByEmail(request.email().toLowerCase())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
        if (!user.isEnabled()) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        issueTokens(user, response);
        return toUserResponse(user, MessageCode.SUCCESS_LOGIN);
    }

    @Transactional
    public UserResponse refresh(HttpServletRequest request, HttpServletResponse response) {
        String rawRefresh = cookieService.readCookie(request.getCookies(), ddperson.security.CookieNames.REFRESH_TOKEN);
        if (rawRefresh == null || rawRefresh.isBlank()) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        String hash = tokenHashService.hash(rawRefresh);
        if (blacklistService.isBlacklisted(hash)) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        RefreshTokenEntity stored = refreshTokenRepository.findByTokenHashAndRevokedAtIsNull(hash)
                .orElseThrow(() -> new BusinessException(ErrorCode.REFRESH_TOKEN_INVALID));

        if (stored.getExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        stored.setRevokedAt(Instant.now());
        blacklistService.blacklist(hash, stored.getExpiresAt());

        UserEntity user = stored.getUser();
        issueTokens(user, response);
        return toUserResponse(user, MessageCode.SUCCESS_TOKEN_REFRESHED);
    }

    @Transactional
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        String rawRefresh = cookieService.readCookie(request.getCookies(), ddperson.security.CookieNames.REFRESH_TOKEN);
        if (rawRefresh != null && !rawRefresh.isBlank()) {
            String hash = tokenHashService.hash(rawRefresh);
            refreshTokenRepository.findByTokenHashAndRevokedAtIsNull(hash).ifPresent(token -> {
                token.setRevokedAt(Instant.now());
                blacklistService.blacklist(hash, token.getExpiresAt());
            });
        }
        cookieService.clearAuthCookies(response);
        return messages.get(MessageCode.SUCCESS_LOGOUT);
    }

    public UserResponse me(UserPrincipal principal) {
        UserEntity user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        return toUserResponse(user, null);
    }

    private void issueTokens(UserEntity user, HttpServletResponse response) {
        String accessToken = jwtService.createAccessToken(user.getId(), user.getEmail());
        String rawRefresh = UUID.randomUUID().toString();

        RefreshTokenEntity refresh = new RefreshTokenEntity();
        refresh.setUser(user);
        refresh.setTokenHash(tokenHashService.hash(rawRefresh));
        refresh.setExpiresAt(Instant.now().plus(Duration.ofDays(refreshTtlDays)));
        refreshTokenRepository.save(refresh);

        cookieService.setAccessToken(response, accessToken, Duration.ofMinutes(accessTtlMinutes));
        cookieService.setRefreshToken(response, rawRefresh, Duration.ofDays(refreshTtlDays));
    }

    private UserResponse toUserResponse(UserEntity user, String messageKey) {
        String message = messageKey != null ? messages.get(messageKey) : null;
        return new UserResponse(user.getId(), user.getEmail(), user.getDisplayName(), message);
    }
}
