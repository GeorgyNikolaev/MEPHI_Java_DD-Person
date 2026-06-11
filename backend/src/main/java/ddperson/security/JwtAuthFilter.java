package ddperson.security;

import ddperson.persistence.entity.UserEntity;
import ddperson.persistence.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CookieService cookieService;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtService jwtService, CookieService cookieService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.cookieService = cookieService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String accessToken = cookieService.readCookie(request.getCookies(), CookieNames.ACCESS_TOKEN);
        if (accessToken != null && !accessToken.isBlank()
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                Claims claims = jwtService.parseAccessToken(accessToken);
                UUID userId = UUID.fromString(claims.getSubject());
                userRepository.findById(userId).ifPresent(user -> setAuthentication(user));
            } catch (RuntimeException ignored) {
                // Невалидный токен — запрос пойдёт как анонимный, Security вернёт 401 при необходимости
            }
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(UserEntity user) {
        if (!user.isEnabled()) {
            return;
        }
        UserPrincipal principal = new UserPrincipal(user.getId(), user.getEmail(), user.getDisplayName());
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
