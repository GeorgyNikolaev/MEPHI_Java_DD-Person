package ddperson.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Управление HttpOnly cookies для JWT (localhost: Secure=false, SameSite=Lax).
 */
@Service
public class CookieService {

    private static final String ACCESS_PATH = "/api";
    private static final String REFRESH_PATH = "/api/v1/auth";

    public void setAccessToken(HttpServletResponse response, String token, Duration maxAge) {
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie(
                CookieNames.ACCESS_TOKEN, token, ACCESS_PATH, maxAge).toString());
    }

    public void setRefreshToken(HttpServletResponse response, String token, Duration maxAge) {
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie(
                CookieNames.REFRESH_TOKEN, token, REFRESH_PATH, maxAge).toString());
    }

    public void clearAuthCookies(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie(
                CookieNames.ACCESS_TOKEN, "", ACCESS_PATH, Duration.ZERO).toString());
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie(
                CookieNames.REFRESH_TOKEN, "", REFRESH_PATH, Duration.ZERO).toString());
    }

    public String readCookie(Cookie[] cookies, String name) {
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private ResponseCookie buildCookie(String name, String value, String path, Duration maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path(path)
                .maxAge(maxAge)
                .build();
    }
}
