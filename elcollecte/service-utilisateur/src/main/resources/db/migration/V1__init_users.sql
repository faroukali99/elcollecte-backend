CREATE TYPE user_role AS ENUM ('ADMIN', 'CHEF_PROJET', 'ENQUETEUR', 'ANALYSTE');

CREATE TABLE organisations (
    id             SERIAL       PRIMARY KEY,
    nom            VARCHAR(200) NOT NULL,
    email_contact  VARCHAR(150) UNIQUE,
    telephone      VARCHAR(20),
    pays           VARCHAR(100),
    logo_url       TEXT,
    is_active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE users (
    id               SERIAL       PRIMARY KEY,
    organisation_id  INT          REFERENCES organisations(id) ON DELETE SET NULL,
    nom              VARCHAR(100) NOT NULL,
    prenom           VARCHAR(100) NOT NULL,
    email            VARCHAR(150) UNIQUE NOT NULL,
    password_hash    VARCHAR(255) NOT NULL,
    role             user_role    NOT NULL DEFAULT 'ENQUETEUR',
    is_active        BOOLEAN      NOT NULL DEFAULT TRUE,
    refresh_token    TEXT,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    last_login       TIMESTAMP
);

CREATE TABLE password_reset_tokens (
    id          SERIAL       PRIMARY KEY,
    user_id     INT          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token       VARCHAR(255) UNIQUE NOT NULL,
    expires_at  TIMESTAMP    NOT NULL,
    used        BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_email        ON users(email);
CREATE INDEX idx_users_org          ON users(organisation_id);
CREATE INDEX idx_users_role         ON users(role);
CREATE INDEX idx_users_active       ON users(is_active);
