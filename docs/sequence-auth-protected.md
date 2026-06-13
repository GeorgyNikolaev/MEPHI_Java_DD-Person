# Sequence: защищённый запрос

Как `JwtAuthFilter` проверяет cookie `access_token` перед доступом к `/api/v1/**`.

```mermaid
sequenceDiagram
    actor User as Пользователь
    participant UI as React Frontend
    participant Filter as JwtAuthFilter
    participant JWT as JwtService
    participant DB as PostgreSQL
    participant API as AuthController
    participant Svc as AuthService

    User->>UI: открывает /dashboard
    UI->>API: GET /api/v1/auth/me (Cookie: access_token)
    API->>Filter: doFilterInternal

    Filter->>Filter: readCookie(access_token)
    alt Cookie отсутствует или JWT невалиден
        Filter->>API: chain (аноним)
        API-->>UI: 401 Unauthorized
    else JWT валиден
        Filter->>JWT: parseAccessToken
        JWT-->>Filter: userId, email
        Filter->>DB: findById(userId)
        Filter->>Filter: SecurityContext ← UserPrincipal
        Filter->>API: chain
        API->>Svc: me(principal)
        Svc-->>API: UserResponse
        API-->>UI: 200 OK
        UI-->>User: данные пользователя
    end
```

## Связанные диаграммы

- [sequence-auth-login.md](sequence-auth-login.md) — откуда берётся cookie
- [sequence-auth.md](sequence-auth.md) — оглавление
