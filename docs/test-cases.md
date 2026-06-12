# Тест-кейсы

## Автоматические тесты (Maven)

```bash
cd backend && mvn test
```

### Unit-тесты (≥3)

| ID | Класс | Что проверяет |
|----|-------|---------------|
| UT-1 | `PromptBuilderTest` | Параметры только в system, описание в user |
| UT-2 | `TokenHashServiceTest` | Детерминированный SHA-256 хэш токена |
| UT-3 | `JwtServiceTest` | Создание и разбор access JWT |
| UT-4 | `DtoMapperTest` | Маппинг enum и portrait URL в DTO |

### Интеграционные тесты (≥2)

| ID | Класс | Что проверяет |
|----|-------|---------------|
| IT-1 | `AuthIntegrationTest` | Register → Login → /me с cookies |
| IT-2 | `CharacterIntegrationTest` | CRUD API + `SELECT COUNT(*)` в PostgreSQL |
| IT-3 | `DdPersonApplicationTests` | Подъём Spring-контекста с PostgreSQL + Redis |

Интеграционные тесты используют **localhost** PostgreSQL и Redis (`application-test.yml`).

**Требование:** `docker compose up -d` перед `mvn test`.

---

## Ручные тест-кейсы (приёмка)

### TC-01: Health

| Шаг | Действие | Ожидание |
|-----|----------|----------|
| 1 | `GET /api/v1/health` | 200, `status: UP` |

### TC-02: Регистрация и вход

| Шаг | Действие | Ожидание |
|-----|----------|----------|
| 1 | Register в Swagger/UI | 201 |
| 2 | Login | cookies `access_token`, `refresh_token` |
| 3 | GET /auth/me | email пользователя |

### TC-03: Генерация портрета

| Шаг | Действие | Ожидание |
|-----|----------|----------|
| 1 | POST /generations с параметрами | 202, status PENDING |
| 2 | Polling GET /generations/{id} | PROCESSING → COMPLETED |
| 3 | GET /portraits/{id}/image | JPG |

### TC-04: Персонаж

| Шаг | Действие | Ожидание |
|-----|----------|----------|
| 1 | POST /characters | 201 |
| 2 | POST /characters/{id}/generations | 202 |
| 3 | GET /characters/{id} | lastPortrait заполнен после COMPLETED |

### TC-05: Избранное

| Шаг | Действие | Ожидание |
|-----|----------|----------|
| 1 | POST /portraits/{id}/favorite | 201, сообщение i18n |
| 2 | Повторное добавление | 409 |
| 3 | GET /favorites | портрет в списке |
| 4 | DELETE /portraits/{id}/favorite | удалён из списка |

### TC-06: Rate limit

| Шаг | Действие | Ожидание |
|-----|----------|----------|
| 1 | >3 генераций за час (по умолчанию) | 429 |

### TC-07: Запуск JAR (Windows)

| Шаг | Действие | Ожидание |
|-----|----------|----------|
| 1 | `docker compose up -d` | PG + Redis |
| 2 | `run.bat` (JDK 21 в JAVA_HOME) | Backend :8080 |
