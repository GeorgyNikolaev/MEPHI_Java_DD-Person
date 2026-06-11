# DD Person — архитектура

Проект на **localhost**: backend `:8080`, frontend `:5173`, PostgreSQL + Redis через `docker compose`.

## Структура

```
DD_person/
├── backend/                 ← Spring Boot (pom.xml внутри backend/)
│   ├── pom.xml
│   └── src/main/java/ddperson/...
│   └── src/main/resources/i18n/messages*.properties
├── frontend/                ← React SPA
├── docker-compose.yml       ← PostgreSQL + Redis
├── storage/portraits/       ← JPG-артефакты
└── ARCHITECTURE.md
```

Корневой пакет Java: **`ddperson`** (без `com.mephi.*`).

## Слои backend

```
api → service → domain
         ↓
persistence, gigachat, security, storage, redis, generation
```

## Characters vs Generations

| | **Generation** | **Character** |
|---|----------------|---------------|
| Суть | Одна попытка генерации | Шаблон персонажа |
| Данные | Статус, промпт, портрет/ошибка | Имя, описание, параметры |
| API | `POST /generations` | `POST /characters` |
| Повтор | `POST /generations/{id}/retry` | `POST /characters/{id}/generations` |

## API (кратко)

Префикс: `/api/v1`, JWT в HttpOnly cookies.

- **Auth:** register, login, refresh, logout, me
- **Generations:** create, list, get, retry — без `characterId` в теле
- **Characters:** CRUD + `POST /characters/{id}/generations`
- **Portraits:** get, image, `POST/DELETE /portraits/{id}/favorite`
- **Справочники enum:** отдельных endpoint нет — подписи на русском в enum + frontend constants

## Enum с русскими подписями

```java
RANGER("RANGER", "Следопыт")  // getLabelRu() → "Следопыт"
```

API возвращает `code` + `labelRu` для UI.

## БД

Hibernate `ddl-auto: update`, без Flyway.  
`refresh_tokens` — в PostgreSQL; Redis — blacklist при logout, rate limit, кэш OAuth.

## Асинхронная генерация

1. `POST /generations` → сохранить PENDING → вернуть 202
2. `@Async` listener → pipeline → GigaChat → COMPLETED/FAILED
3. Frontend polling `GET /generations/{id}`

Подробные ответы на вопросы — в README и при обсуждении с командой.
