# DD Person

Генерация портретов персонажей D&D через GigaChat.

## Стек

- **Backend:** Java 21, Spring Boot 3 — папка `backend/`
- **Frontend:** React, TypeScript, Vite — папка `frontend/`
- **Инфраструктура:** PostgreSQL + Redis (`docker compose up -d`)

## Запуск (после реализации кода)

```bash
docker compose up -d          # БД и Redis
cd backend && mvn spring-boot:run
cd frontend && npm run dev
```

Документация: [ARCHITECTURE.md](ARCHITECTURE.md)
