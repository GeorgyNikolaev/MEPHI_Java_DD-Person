package ddperson.api.controller;

import ddperson.api.dto.auth.LoginRequest;
import ddperson.api.dto.auth.MessageResponse;
import ddperson.api.dto.auth.RegisterRequest;
import ddperson.api.dto.auth.UserResponse;
import ddperson.security.SecurityUtils;
import ddperson.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Регистрация и аутентификация (JWT в HttpOnly cookies)")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Регистрация нового пользователя")
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    @Operation(summary = "Вход", description = "Устанавливает cookies access_token и refresh_token")
    public UserResponse login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        return authService.login(request, response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Обновление access token", description = "Требует cookie refresh_token")
    public UserResponse refresh(HttpServletRequest request, HttpServletResponse response) {
        return authService.refresh(request, response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Выход", description = "Отзывает refresh token и очищает cookies")
    public MessageResponse logout(HttpServletRequest request, HttpServletResponse response) {
        return new MessageResponse(authService.logout(request, response));
    }

    @GetMapping("/me")
    @Operation(summary = "Текущий пользователь", security = @SecurityRequirement(name = "cookieAuth"))
    public UserResponse me() {
        return authService.me(SecurityUtils.requireCurrentUser());
    }
}
