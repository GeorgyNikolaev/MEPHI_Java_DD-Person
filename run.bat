@echo off
chcp 65001 >nul
setlocal EnableExtensions

REM ============================================================
REM  DD Person — запуск backend (JAR) на Windows
REM  Перед первым запуском: установите JDK 21 и укажите путь ниже
REM  Инфраструктура: docker compose up -d  (PostgreSQL + Redis)
REM ============================================================

set "JAVA_HOME=C:\Program Files\Java\jdk-21"
set "PATH=%JAVA_HOME%\bin;%PATH%"

cd /d "%~dp0"

where java >nul 2>&1
if errorlevel 1 (
    echo [ОШИБКА] Java не найдена. Проверьте JAVA_HOME: %JAVA_HOME%
    exit /b 1
)

set "JAR=backend\target\dd-person-backend.jar"
if not exist "%JAR%" (
    echo JAR не найден. Сборка Maven...
    pushd backend
    call mvn -q -DskipTests package
    if errorlevel 1 (
        echo [ОШИБКА] Сборка Maven не удалась
        popd
        exit /b 1
    )
    popd
)

if not exist "storage\portraits" mkdir "storage\portraits"
if not exist "backend\.env" (
    echo [ВНИМАНИЕ] Файл backend\.env не найден. Скопируйте backend\.env.example и заполните GIGACHAT_AUTH_KEY.
)

echo Запуск DD Person Backend...
pushd backend
java -jar target\dd-person-backend.jar
set "EXIT_CODE=%ERRORLEVEL%"
popd
exit /b %EXIT_CODE%
