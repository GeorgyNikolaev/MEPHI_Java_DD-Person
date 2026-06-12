# DD Person

Генерация портретов персонажей D&D через GigaChat.

## Стек

| Часть | Технологии |
|-------|------------|
| Backend | Java 21, Spring Boot 3.4, PostgreSQL, Redis |
| Frontend | React 19, TypeScript, Vite 7 |
| Инфраструктура | Docker Compose (PostgreSQL + Redis) |

Подробнее: [docs/stack.md](docs/stack.md)

## Быстрый запуск

```bash
docker compose up -d
cp backend/.env.example backend/.env   # GIGACHAT_AUTH_KEY, JWT_SECRET

# Backend (разработка)
cd backend && mvn spring-boot:run

# Backend (JAR)
cd backend && mvn package -DskipTests && java -jar target/dd-person-backend.jar

# Windows: run.bat в корне (укажите JAVA_HOME → JDK 21)

# macOS / Linux:
./run.sh

# Frontend
cd frontend && npm install && npm run dev
```

- Backend: http://localhost:8080  
- Swagger: http://localhost:8080/swagger-ui.html  
- Frontend: http://localhost:5173  

Полная инструкция: [docs/launch.md](docs/launch.md)

## Документация

- [docs/README.md](docs/README.md) — индекс
- [ARCHITECTURE.md](ARCHITECTURE.md) — архитектура
- [docs/database.md](docs/database.md) — БД и SQL
- [docs/test-cases.md](docs/test-cases.md) — тест-кейсы

## Тесты

```bash
cd backend && mvn test   # unit — всегда; integration — нужен docker compose up -d
```
