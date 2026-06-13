# Sequence: регистрация

Создание нового пользователя. Cookies **не выдаются** — после успеха frontend автоматически вызывает [вход](sequence-auth-login.md).

```mermaid
sequenceDiagram
    actor User as Пользователь
    participant UI as React Frontend
    participant API as AuthController
    participant Svc as AuthService
    participant DB as PostgreSQL

    User->>UI: email, пароль, имя → «Зарегистрироваться»
    UI->>API: POST /api/v1/auth/register
    API->>Svc: register(request)

    Svc->>DB: existsByEmail?
    alt Email уже занят
        Svc-->>API: 409 Conflict
        API-->>UI: error.email_taken
        UI-->>User: сообщение об ошибке
    else Email свободен
        Svc->>Svc: BCrypt.encode(password)
        Svc->>DB: INSERT users
        Svc-->>API: UserResponse
        API-->>UI: 201 Created
        UI->>UI: authApi.login (useAuth.register)
        Note over UI: далее → sequence-auth-login.md
    end
```

## Связанные диаграммы

- [sequence-auth-login.md](sequence-auth-login.md) — вход после регистрации
- [sequence-auth.md](sequence-auth.md) — оглавление
