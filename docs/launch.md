# Инструкция запуска

## Требования

- **JDK 21** (backend)
- **Maven 3.9+** (сборка JAR)
- **Docker** (PostgreSQL 16 + Redis 7)
- **Node.js 20+** (frontend, опционально)
- Ключ **GigaChat** в `backend/.env`

## 1. Инфраструктура

```bash
docker compose up -d
```

Проверка: PostgreSQL `:5432`, Redis `:6379`.

## 2. Backend

### Сборка JAR

```bash
cd backend
mvn package -DskipTests
```

Результат: `backend/target/dd-person-backend.jar`

### Запуск из JAR

```bash
cd backend
cp .env.example .env   # заполните GIGACHAT_AUTH_KEY и JWT_SECRET
java -jar target/dd-person-backend.jar
```

### Windows: `run.bat`

В корне репозитория — `run.bat`:

1. Укажите путь к JDK 21 в переменной `JAVA_HOME`
2. При отсутствии JAR выполнит `mvn package`
3. Запустит `java -jar backend\target\dd-person-backend.jar`

Перед запуском: `docker compose up -d`.

### Разработка (без JAR)

```bash
cd backend && mvn spring-boot:run
```

- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html

## 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

- UI: http://localhost:5173 (прокси `/api` → `:8080`)

## 4. Тесты

```bash
cd backend
mvn test
```

Интеграционные тесты используют **Testcontainers** (нужен запущенный Docker).

## 5. Типичные проблемы

| Проблема | Решение |
|----------|---------|
| Backend не стартует | Проверьте `docker compose ps`, `.env` |
| 401 на API | Войдите через UI или Swagger (cookies) |
| GigaChat SSL | `app.gigachat.insecure-ssl: true` для localhost |
| Тесты падают | Запустите Docker Desktop |
