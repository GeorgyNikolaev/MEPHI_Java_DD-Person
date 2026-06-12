# DD Person

Генерация портретов персонажей D&D через GigaChat.

## Стек

- **Backend:** Java 21, Spring Boot 3 — папка `backend/`
- **Frontend:** React, TypeScript, Vite — папка `frontend/`
- **Инфраструктура:** PostgreSQL + Redis (`docker compose up -d`)

## Запуск

```bash
docker compose up -d          # БД и Redis
cd backend && mvn spring-boot:run
cd frontend && npm install && npm run dev
```

- Backend: http://localhost:8080
- Frontend: http://localhost:5173
- Swagger: http://localhost:8080/swagger-ui.html

Документация: [ARCHITECTURE.md](ARCHITECTURE.md)
