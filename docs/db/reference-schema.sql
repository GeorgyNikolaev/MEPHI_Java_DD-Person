-- Справочный DDL для отчёта / защиты проекта.
-- В runtime схему создаёт Hibernate (spring.jpa.hibernate.ddl-auto: update).
-- Этот файл НЕ выполняется при старте приложения.

CREATE TABLE users (
    id              UUID PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    display_name    VARCHAR(100) NOT NULL,
    enabled         BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL,
    updated_at      TIMESTAMPTZ NOT NULL
);

CREATE TABLE refresh_tokens (
    id          UUID PRIMARY KEY,
    user_id     UUID NOT NULL REFERENCES users(id),
    token_hash  VARCHAR(64) NOT NULL,
    expires_at  TIMESTAMPTZ NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL
);

CREATE TABLE characters (
    id                   UUID PRIMARY KEY,
    user_id              UUID NOT NULL REFERENCES users(id),
    name                 VARCHAR(150) NOT NULL,
    description          TEXT NOT NULL,
    role_archetype       VARCHAR(50) NOT NULL,
    universe_style       VARCHAR(50) NOT NULL,
    seriousness_level    SMALLINT NOT NULL,
    expressiveness_level SMALLINT NOT NULL,
    mood                 VARCHAR(50),
    last_portrait_id     UUID,
    created_at           TIMESTAMPTZ NOT NULL,
    updated_at           TIMESTAMPTZ NOT NULL
);

CREATE TABLE generation_requests (
    id                 UUID PRIMARY KEY,
    user_id            UUID NOT NULL REFERENCES users(id),
    character_id       UUID REFERENCES characters(id),
    status             VARCHAR(20) NOT NULL,
    error_code         VARCHAR(50),
    error_message      TEXT,
    built_system_prompt TEXT,
    built_user_prompt  TEXT,
    created_at         TIMESTAMPTZ NOT NULL,
    started_at         TIMESTAMPTZ,
    completed_at       TIMESTAMPTZ
);

CREATE TABLE generation_parameters (
    id                    UUID PRIMARY KEY,
    request_id            UUID NOT NULL UNIQUE REFERENCES generation_requests(id),
    character_description TEXT NOT NULL,
    role_archetype        VARCHAR(50) NOT NULL,
    universe_style        VARCHAR(50) NOT NULL,
    seriousness_level     SMALLINT NOT NULL,
    expressiveness_level  SMALLINT NOT NULL,
    mood                  VARCHAR(50)
);

CREATE TABLE portrait_artifacts (
    id              UUID PRIMARY KEY,
    request_id      UUID NOT NULL UNIQUE REFERENCES generation_requests(id),
    gigachat_file_id VARCHAR(100),
    storage_path    VARCHAR(500),
    mime_type       VARCHAR(50),
    file_size_bytes BIGINT,
    width           INTEGER,
    height          INTEGER,
    created_at      TIMESTAMPTZ NOT NULL
);

CREATE TABLE favorite_portraits (
    id          UUID PRIMARY KEY,
    user_id     UUID NOT NULL REFERENCES users(id),
    portrait_id UUID NOT NULL REFERENCES portrait_artifacts(id),
    created_at  TIMESTAMPTZ NOT NULL,
    UNIQUE (user_id, portrait_id)
);

CREATE TABLE gigachat_api_calls (
    id                  UUID PRIMARY KEY,
    user_id             UUID NOT NULL REFERENCES users(id),
    request_id          UUID REFERENCES generation_requests(id),
    call_type           VARCHAR(50) NOT NULL,
    http_status         INTEGER,
    duration_ms         INTEGER,
    response_summary    TEXT,
    error_code          VARCHAR(50),
    prompt_tokens       INTEGER,
    completion_tokens   INTEGER,
    system_tokens       INTEGER,
    total_tokens        INTEGER,
    model               VARCHAR(100),
    created_at          TIMESTAMPTZ NOT NULL
);
