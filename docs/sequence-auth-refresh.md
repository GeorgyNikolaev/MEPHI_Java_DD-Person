# Sequence: обновление access token

Ротация refresh-токена при истечении access JWT.

```mermaid
sequenceDiagram
    participant UI as React Frontend
    participant API as AuthController
    participant Svc as AuthService
    participant Hash as TokenHashService
    participant Redis as Redis
    participant DB as PostgreSQL
    participant Cookie as CookieService

    UI->>API: POST /api/v1/auth/refresh (Cookie: refresh_token)
    API->>Svc: refresh(request, response)

    Svc->>Svc: readCookie(refresh_token)
    alt Cookie отсутствует
        Svc-->>API: 401
    else Cookie есть
        Svc->>Hash: hash(rawRefresh)
        Svc->>Redis: isBlacklisted?
        alt В blacklist или не найден в БД
            Svc-->>API: 401 refresh_token_invalid
        else Refresh валиден
            Svc->>DB: findByTokenHash
            Svc->>DB: UPDATE revoked_at (старый refresh)
            Svc->>Redis: blacklist(hash)
            Svc->>Svc: issueTokens (новая пара)
            Svc->>DB: INSERT refresh_tokens
            Svc->>Cookie: Set-Cookie access + refresh
            Svc-->>API: 200 UserResponse
            API-->>UI: 200 + новые cookies
        end
    end
```

## Связанные диаграммы

- [sequence-auth-logout.md](sequence-auth-logout.md) — выход
- [sequence-auth.md](sequence-auth.md) — оглавление
