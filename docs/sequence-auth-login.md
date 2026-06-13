# Sequence: вход

Выдача JWT и refresh-токена в HttpOnly cookies.

```mermaid
sequenceDiagram
    actor User as Пользователь
    participant UI as React Frontend
    participant API as AuthController
    participant Svc as AuthService
    participant JWT as JwtService
    participant Hash as TokenHashService
    participant Cookie as CookieService
    participant DB as PostgreSQL

    User->>UI: email, пароль → «Войти»
    UI->>API: POST /api/v1/auth/login (credentials: include)
    API->>Svc: login(request, response)

    Svc->>DB: findByEmail
    alt Неверный email или пароль
        Svc-->>API: 401 Unauthorized
        API-->>UI: error.invalid_credentials
    else Учётная запись найдена
        Svc->>Svc: passwordEncoder.matches
        Svc->>JWT: createAccessToken(userId, email)
        JWT-->>Svc: access JWT
        Svc->>Svc: rawRefresh = UUID.randomUUID()
        Svc->>Hash: hash(rawRefresh)
        Hash-->>Svc: SHA-256 hex
        Svc->>DB: INSERT refresh_tokens
        Svc->>Cookie: setAccessToken + setRefreshToken
        Cookie-->>API: Set-Cookie (HttpOnly, SameSite=Lax)
        Svc-->>API: 200 UserResponse
        API-->>UI: 200 + cookies
        UI->>API: GET /api/v1/auth/me
        Note over UI,API: см. sequence-auth-protected.md
        UI-->>User: переход на /dashboard
    end
```

## Связанные диаграммы

- [sequence-auth-protected.md](sequence-auth-protected.md) — проверка cookie на запросах
- [sequence-auth.md](sequence-auth.md) — оглавление
