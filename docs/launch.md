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

### Windows: `run.bat` / macOS: `run.sh`

В корне репозитория:

| ОС | Скрипт | Запуск |
|----|--------|--------|
| Windows | `run.bat` | двойной клик или `run.bat` в cmd |
| macOS / Linux | `run.sh` | `./run.sh` в терминале |

Скрипт:
1. Проверяет наличие Java (JDK 21)
2. При отсутствии JAR выполняет `mvn package`
3. Запускает `java -jar backend/target/dd-person-backend.jar`

Перед запуском: `docker compose up -d`.

На macOS, если несколько версий Java:
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
./run.sh
```

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
