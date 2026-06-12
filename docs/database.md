# База данных: как устроено в проекте

## Краткий ответ для защиты

**Таблицы создаёт Hibernate автоматически** по JPA-сущностям (`@Entity`).  
**SELECT/INSERT/UPDATE выполняет Spring Data JPA** — вы пишете методы репозитория или используете сервисы, а SQL генерирует фреймворк.

Отдельный `init.sql` или Liquibase **намеренно не используются** — это осознанное решение для localhost-разработки (`ddl-auto: update`).

Для отчёта преподавателю подготовлены **ER-диаграмма**, **справочный DDL** и **примеры SELECT**: [er-diagram.md](er-diagram.md), [db/reference-schema.sql](db/reference-schema.sql), [db/example-queries.sql](db/example-queries.sql).

---

## Создание таблиц

Конфигурация (`backend/src/main/resources/application.yml`):

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
```

При старте приложения Hibernate сравнивает сущности с схемой БД и:

- создаёт отсутствующие таблицы;
- добавляет новые колонки;
- **не удаляет** данные при перезапуске (в отличие от `create-drop`).

Сущности: `backend/src/main/java/ddperson/persistence/entity/`  
Репозитории: `backend/src/main/java/ddperson/persistence/repository/`

### Пример цепочки

```
CharacterEntity (@Table name="characters")
        ↓
Hibernate DDL
        ↓
Таблица characters в PostgreSQL
```

---

## Как выполняются запросы

### 1. Spring Data JPA (основной способ)

```java
Optional<CharacterEntity> findByIdAndUserId(UUID id, UUID userId);
```

Spring генерирует примерно:

```sql
SELECT c.* FROM characters c
WHERE c.id = ? AND c.user_id = ?
```

### 2. JPQL (@Query)

```java
@Query("SELECT p FROM PortraitEntity p WHERE p.id = :id AND p.request.user.id = :userId")
```

### 3. Прямой SQL (только в тестах / отчёте)

В интеграционном тесте `CharacterIntegrationTest` проверяется, что строка реально попала в БД:

```sql
SELECT COUNT(*) FROM characters WHERE name = ?
```

---

## Почему не Liquibase / init.sql?

| Подход | Плюсы | Минусы |
|--------|-------|--------|
| **Hibernate ddl-auto** | Быстрый старт, схема = код | Нет версионирования миграций |
| **Liquibase/Flyway** | Production-ready миграции | Избыточно для учебного localhost |

Для production обычно переходят на Flyway/Liquibase. Для данного проекта достаточно Hibernate + справочный SQL в `docs/db/`.

---

## Таблицы (8 штук)

| Таблица | Назначение |
|---------|------------|
| `users` | Пользователи |
| `refresh_tokens` | Refresh JWT (PostgreSQL) |
| `characters` | Шаблоны персонажей |
| `generation_requests` | Запросы генерации |
| `generation_parameters` | Параметры запроса |
| `portrait_artifacts` | Метаданные JPG |
| `favorite_portraits` | Избранное |
| `gigachat_api_calls` | Аудит вызовов API |

Redis **не хранит** основные данные — только rate limit, blacklist, кэш OAuth.
