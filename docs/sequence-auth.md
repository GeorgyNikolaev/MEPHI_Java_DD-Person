# Sequence: аутентификация (оглавление)

JWT в HttpOnly cookies. Токен пользователя **не** передаётся как `Authorization: Bearer` — только cookies.

| Диаграмма | Сценарий |
|-----------|----------|
| [sequence-auth-register.md](sequence-auth-register.md) | Регистрация нового пользователя |
| [sequence-auth-login.md](sequence-auth-login.md) | Вход, выдача access + refresh cookies |
| [sequence-auth-protected.md](sequence-auth-protected.md) | Защищённый запрос (`JwtAuthFilter`, `/auth/me`) |
| [sequence-auth-refresh.md](sequence-auth-refresh.md) | Обновление access token (ротация refresh) |
| [sequence-auth-logout.md](sequence-auth-logout.md) | Выход, отзыв refresh, очистка cookies |

## Участники (общие)

| Компонент | Роль |
|-----------|------|
| `AuthController` | REST: register, login, refresh, logout, /me |
| `AuthService` | BCrypt, JWT, refresh rotation, cookies |
| `CookieService` | HttpOnly cookies, SameSite=Lax |
| `JwtAuthFilter` | Чтение `access_token` из cookie на каждый запрос |
| `TokenHashService` | SHA-256 хэш refresh перед записью в БД |

## Связанные диаграммы

- [sequence-generation.md](sequence-generation.md)
- [er-diagram.md](er-diagram.md)
