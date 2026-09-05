-- PHASE 9: Validation/Qualité - Enrichissement service-validation
-- Table regles_qualite pour les règles de validation dynamiques
CREATE TABLE regles_qualite (
    id BIGSERIAL PRIMARY KEY,
    formulaire_version_id BIGINT,
    question_id BIGINT,
    type VARCHAR(50) NOT NULL,
    parametre VARCHAR(100),
    config JSONB,
    is_actif BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TYPE regle_type AS ENUM ('REQUIRED', 'MIN_LENGTH', 'MAX_LENGTH', 'MIN_VALUE', 'MAX_VALUE', 'PATTERN', 'WARNING');
ALTER TABLE regles_qualite ALTER COLUMN type TYPE regle_type USING type::regle_type;

-- Index pour regles_qualite
CREATE INDEX idx_regles_formulaire_version ON regles_qualite(formulaire_version_id);
CREATE INDEX idx_regles_question ON regles_qualite(question_id);
CREATE INDEX idx_regles_actif ON regles_qualite(is_actif) WHERE is_actif = TRUE;
