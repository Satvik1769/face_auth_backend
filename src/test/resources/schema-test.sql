CREATE TABLE IF NOT EXISTS users (
    id            UUID PRIMARY KEY,
    username      VARCHAR(64) NOT NULL UNIQUE,
    password_hash VARCHAR(256) NOT NULL,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at    TIMESTAMP
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id          UUID PRIMARY KEY,
    user_id     UUID NOT NULL,
    device_id   VARCHAR(256) NOT NULL,
    token_hash  VARCHAR(512) NOT NULL UNIQUE,
    expires_at  TIMESTAMP NOT NULL,
    revoked     BOOLEAN NOT NULL DEFAULT FALSE,
    last_seen   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address  VARCHAR(64),
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS pin_hashes (
    id         UUID PRIMARY KEY,
    user_id    UUID NOT NULL UNIQUE,
    pin_hash   VARCHAR(512) NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS face_enrollments (
    user_id            UUID PRIMARY KEY,
    embedding          VARCHAR(32767) NOT NULL,
    enrollment_version INT NOT NULL DEFAULT 1,
    enrolled_at        TIMESTAMP NOT NULL,
    synced_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS auth_logs (
    log_id         UUID PRIMARY KEY,
    user_id        UUID,
    attempted_at   TIMESTAMP NOT NULL,
    result         VARCHAR(16) NOT NULL,
    match_score    REAL,
    failure_reason VARCHAR(512),
    received_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS wipe_commands (
    id           UUID PRIMARY KEY,
    user_id      UUID NOT NULL UNIQUE,
    issued_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    confirmed    BOOLEAN NOT NULL DEFAULT FALSE,
    confirmed_at TIMESTAMP
);