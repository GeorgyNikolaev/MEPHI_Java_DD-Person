# ER-диаграмма базы данных

Логическая модель PostgreSQL проекта DD Person. Схему в runtime создаёт Hibernate по JPA-сущностям (`ddl-auto: update`). Справочный DDL: [db/reference-schema.sql](db/reference-schema.sql).

Файлы JPG хранятся на диске (`storage/portraits/`), в БД — только метаданные в `portrait_artifacts`.

```mermaid
erDiagram
    users ||--o{ refresh_tokens : "1:N"
    users ||--o{ characters : "1:N"
    users ||--o{ generation_requests : "1:N"
    users ||--o{ favorite_portraits : "1:N"
    users ||--o{ gigachat_api_calls : "1:N"

    characters ||--o{ generation_requests : "0:N опционально"
    characters }o--o| portrait_artifacts : "last_portrait"

    generation_requests ||--|| generation_parameters : "1:1"
    generation_requests ||--o| portrait_artifacts : "1:0..1"
    generation_requests ||--o{ gigachat_api_calls : "1:N"

    portrait_artifacts ||--o{ favorite_portraits : "1:N"

    users {
        uuid id PK
        varchar email UK "NOT NULL"
        varchar password_hash "NOT NULL"
        varchar display_name
        boolean enabled "NOT NULL"
        timestamptz created_at "NOT NULL"
        timestamptz updated_at "NOT NULL"
    }

    refresh_tokens {
        uuid id PK
        uuid user_id FK "NOT NULL"
        varchar token_hash "NOT NULL"
        timestamptz expires_at "NOT NULL"
        timestamptz revoked_at
        timestamptz created_at "NOT NULL"
    }

    characters {
        uuid id PK
        uuid user_id FK "NOT NULL"
        varchar name "NOT NULL"
        text description "NOT NULL"
        varchar role_archetype "NOT NULL"
        varchar universe_style "NOT NULL"
        smallint seriousness_level "NOT NULL"
        smallint expressiveness_level "NOT NULL"
        varchar mood
        uuid last_portrait_id FK
        timestamptz created_at "NOT NULL"
        timestamptz updated_at "NOT NULL"
    }

    generation_requests {
        uuid id PK
        uuid user_id FK "NOT NULL"
        uuid character_id FK "опционально"
        varchar status "NOT NULL"
        varchar error_code
        text error_message
        text built_system_prompt
        text built_user_prompt
        timestamptz created_at "NOT NULL"
        timestamptz started_at
        timestamptz completed_at
    }

    generation_parameters {
        uuid id PK
        uuid request_id FK "NOT NULL, UNIQUE"
        text character_description "NOT NULL"
        varchar role_archetype "NOT NULL"
        varchar universe_style "NOT NULL"
        smallint seriousness_level "NOT NULL"
        smallint expressiveness_level "NOT NULL"
        varchar mood
    }

    portrait_artifacts {
        uuid id PK
        uuid request_id FK "NOT NULL, UNIQUE"
        varchar gigachat_file_id
        varchar storage_path
        varchar mime_type
        bigint file_size_bytes
        integer width
        integer height
        timestamptz created_at "NOT NULL"
    }

    favorite_portraits {
        uuid id PK
        uuid user_id FK "NOT NULL"
        uuid portrait_id FK "NOT NULL"
        timestamptz created_at "NOT NULL"
    }

    gigachat_api_calls {
        uuid id PK
        uuid user_id FK "NOT NULL"
        uuid request_id FK
        varchar call_type "NOT NULL"
        integer http_status
        integer duration_ms "NOT NULL"
        text response_summary
        varchar error_code
        varchar model
        integer prompt_tokens
        integer completion_tokens
        integer system_tokens
        integer total_tokens
        timestamptz created_at "NOT NULL"
    }
```

## Связи

| Связь | Кардинальность | Назначение |
|-------|----------------|------------|
| `users` → `refresh_tokens` | 1:N | Сессии входа (refresh JWT) |
| `users` → `characters` | 1:N | Шаблоны персонажей пользователя |
| `users` → `generation_requests` | 1:N | История запросов генерации |
| `users` → `favorite_portraits` | 1:N | Избранные портреты |
| `users` → `gigachat_api_calls` | 1:N | Аудит вызовов GigaChat |
| `characters` → `generation_requests` | 0:N | Опционально: генерация по шаблону (обновление `last_portrait`) |
| `characters` → `portrait_artifacts` | 0:1 | Последний портрет шаблона |
| `generation_requests` → `generation_parameters` | 1:1 | Входные параметры запроса |
| `generation_requests` → `portrait_artifacts` | 1:0..1 | Результат (после `COMPLETED`) |
| `generation_requests` → `gigachat_api_calls` | 1:N | Логи вызовов по запросу |
| `portrait_artifacts` → `favorite_portraits` | 1:N | Один портрет у нескольких пользователей невозможен; у пользователя — уникальная пара `(user_id, portrait_id)` |

## Ограничения

- `users.email` — уникальный.
- `generation_parameters.request_id` — уникальный (один набор параметров на запрос).
- `portrait_artifacts.request_id` — уникальный (один портрет на запрос).
- `favorite_portraits (user_id, portrait_id)` — уникальная пара.

## Группы таблиц

| Группа | Таблицы |
|--------|---------|
| Авторизация | `users`, `refresh_tokens` |
| Персонажи | `characters` |
| Генерация | `generation_requests`, `generation_parameters`, `portrait_artifacts` |
| Избранное | `favorite_portraits` |
| Аудит | `gigachat_api_calls` |

## Связанные материалы

- [database.md](database.md) — как создаётся схема и выполняются запросы
- [class-diagram.md](class-diagram.md) — диаграмма классов backend
- [db/example-queries.sql](db/example-queries.sql) — примеры SELECT
