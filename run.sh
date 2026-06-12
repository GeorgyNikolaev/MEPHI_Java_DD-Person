#!/usr/bin/env bash
set -euo pipefail

# DD Person — запуск backend (JAR) на macOS / Linux
# Перед запуском: docker compose up -d
# При необходимости укажите путь к JDK 21:
#   export JAVA_HOME=$(/usr/libexec/java_home -v 21)

ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

if ! command -v java >/dev/null 2>&1; then
  echo "[ОШИБКА] Java не найдена. Установите JDK 21 и/или задайте JAVA_HOME."
  exit 1
fi

JAVA_VERSION="$(java -version 2>&1 | head -n1)"
echo "Используется: $JAVA_VERSION"

JAR="backend/target/dd-person-backend.jar"
if [[ ! -f "$JAR" ]]; then
  echo "JAR не найден. Сборка Maven..."
  (cd backend && mvn -q -DskipTests package)
fi

mkdir -p storage/portraits

if [[ ! -f backend/.env ]]; then
  echo "[ВНИМАНИЕ] Файл backend/.env не найден. Скопируйте backend/.env.example и заполните GIGACHAT_AUTH_KEY."
fi

echo "Запуск DD Person Backend..."
cd backend
exec java -jar target/dd-person-backend.jar
