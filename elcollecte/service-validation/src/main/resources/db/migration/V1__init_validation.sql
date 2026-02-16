CREATE TYPE validation_statut AS ENUM ('EN_ATTENTE', 'VALIDE', 'REJETE', 'REVISION');

CREATE TABLE validations (
    id            BIGSERIAL          PRIMARY KEY,
    collecte_id   BIGINT             NOT NULL UNIQUE,
    projet_id     BIGINT             NOT NULL,
    validateur_id BIGINT,
    statut        validation_statut  NOT NULL DEFAULT 'EN_ATTENTE',
    commentaire   TEXT,
    motif_rejet   TEXT,
    created_at    TIMESTAMP          NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP
);

CREATE INDEX idx_validations_projet    ON validations(projet_id);
CREATE INDEX idx_validations_statut    ON validations(statut);
CREATE INDEX idx_validations_validateur ON validations(validateur_id);
