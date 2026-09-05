-- PHASE 3: Nouveau moteur de formulaires dynamiques avec versionnement
-- Structure: Formulaire → Version → Section → Question

-- Table organisations (référence pour formulaires)
CREATE TABLE IF NOT EXISTS organisations (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Table formulaires
CREATE TABLE IF NOT EXISTS formulaires (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    code VARCHAR(50) NOT NULL UNIQUE,
    organisation_id BIGINT REFERENCES organisations(id),
    is_actif BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

-- Index pour formulaires
CREATE INDEX idx_formulaires_org ON formulaires(organisation_id);
CREATE INDEX idx_formulaires_code ON formulaires(code);
CREATE INDEX idx_formulaires_actif ON formulaires(is_actif) WHERE is_actif = TRUE;

-- Table formulaire_versions
CREATE TABLE IF NOT EXISTS formulaire_versions (
    id BIGSERIAL PRIMARY KEY,
    formulaire_id BIGINT NOT NULL REFERENCES formulaires(id) ON DELETE CASCADE,
    numero_version INTEGER NOT NULL,
    description VARCHAR(500),
    is_publie BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    published_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(formulaire_id, numero_version)
);

-- Index pour versions
CREATE INDEX idx_versions_formulaire ON formulaire_versions(formulaire_id);
CREATE INDEX idx_versions_publie ON formulaire_versions(is_publie) WHERE is_publie = TRUE;
CREATE INDEX idx_versions_active ON formulaire_versions(is_active) WHERE is_active = TRUE;

-- Table sections
CREATE TABLE IF NOT EXISTS sections (
    id BIGSERIAL PRIMARY KEY,
    version_id BIGINT NOT NULL REFERENCES formulaire_versions(id) ON DELETE CASCADE,
    titre VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    ordre INTEGER NOT NULL,
    is_repeatable BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Index pour sections
CREATE INDEX idx_sections_version ON sections(version_id);
CREATE INDEX idx_sections_ordre ON sections(version_id, ordre);

-- Table questions
CREATE TABLE IF NOT EXISTS questions (
    id BIGSERIAL PRIMARY KEY,
    section_id BIGINT NOT NULL REFERENCES sections(id) ON DELETE CASCADE,
    libelle VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    type VARCHAR(50) NOT NULL,
    ordre INTEGER NOT NULL,
    is_required BOOLEAN NOT NULL DEFAULT FALSE,
    options JSONB,
    validation JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Types de questions valides
CREATE TYPE question_type AS ENUM (
    'TEXT', 'NUMBER', 'DECIMAL', 'DATE', 'TIME', 'DATETIME',
    'BOOLEAN', 'SINGLE_CHOICE', 'MULTIPLE_CHOICE', 'SELECT',
    'PHOTO', 'VIDEO', 'AUDIO', 'GPS', 'SIGNATURE', 'FILE'
);

ALTER TABLE questions ALTER COLUMN type TYPE question_type USING type::question_type;

-- Index pour questions
CREATE INDEX idx_questions_section ON questions(section_id);
CREATE INDEX idx_questions_ordre ON questions(section_id, ordre);

-- Table option_choix (pour SINGLE_CHOICE, MULTIPLE_CHOICE, SELECT)
CREATE TABLE IF NOT EXISTS option_choix (
    id BIGSERIAL PRIMARY KEY,
    question_id BIGINT NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
    code VARCHAR(100) NOT NULL,
    libelle VARCHAR(200) NOT NULL,
    ordre INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Index pour options
CREATE INDEX idx_options_question ON option_choix(question_id);
CREATE INDEX idx_options_ordre ON option_choix(question_id, ordre);

-- Table regles (logique conditionnelle)
CREATE TABLE IF NOT EXISTS regles (
    id BIGSERIAL PRIMARY KEY,
    version_id BIGINT NOT NULL REFERENCES formulaire_versions(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL,
    question_source_id BIGINT,
    valeur_attendue VARCHAR(500),
    question_cible_id BIGINT,
    action VARCHAR(50),
    parametres JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Index pour regles
CREATE INDEX idx_regles_version ON regles(version_id);
CREATE INDEX idx_regles_source ON regles(question_source_id);
CREATE INDEX idx_regles_cible ON regles(question_cible_id);
