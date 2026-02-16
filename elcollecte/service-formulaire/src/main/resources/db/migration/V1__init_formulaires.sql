CREATE TABLE formulaires (
    id                SERIAL        PRIMARY KEY,
    projet_id         BIGINT        NOT NULL,
    createur_id       BIGINT        NOT NULL,
    titre             VARCHAR(200)  NOT NULL,
    description       TEXT,
    schema_questions  JSONB         NOT NULL DEFAULT '[]',
    version           INT           NOT NULL DEFAULT 1,
    is_active         BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at        TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP
);

CREATE TABLE formulaire_versions (
    id              SERIAL    PRIMARY KEY,
    formulaire_id   INT       NOT NULL REFERENCES formulaires(id) ON DELETE CASCADE,
    version         INT       NOT NULL,
    schema_snapshot JSONB     NOT NULL,
    created_by      BIGINT    NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_formulaires_projet   ON formulaires(projet_id);
CREATE INDEX idx_formulaires_active   ON formulaires(is_active);
