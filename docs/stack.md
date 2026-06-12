# Технологический стек

## Backend

| Компонент | Технология |
|-----------|------------|
| Язык | Java 21 |
| Фреймворк | Spring Boot 3.4 |
| Web / REST | Spring Web MVC |
| Безопасность | Spring Security + JWT (HttpOnly cookies) |
| ORM | Spring Data JPA + Hibernate |
| БД | PostgreSQL 16 |
| Кэш / лимиты | Redis 7 |
| HTTP-клиент GigaChat | Spring WebFlux (WebClient) |
| Документация API | springdoc-openapi (Swagger UI) |
| Сборка | Maven → executable JAR |
| Тесты | JUnit 5, Mockito, Testcontainers |

## Frontend

| Компонент | Технология |
|-----------|------------|
| UI | React 19 + TypeScript |
| Сборка | Vite 7 |
| Маршрутизация | React Router 7 |
| HTTP | fetch + `credentials: include` |

## Инфраструктура (localhost)

- `docker-compose.yml` — только PostgreSQL и Redis
- JPG-артефакты — `storage/portraits/`

## Паттерны

- **Strategy** — фрагменты system-промпта (`generation/prompt/strategy`)
- **Builder** — сборка промпта (`PromptBuilder`)
- **Port/Adapter** — GigaChat за `ImageGenerationPort`
- **Repository** — Spring Data JPA
- **DTO + Mapper** — слой `api`
