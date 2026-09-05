-- PHASE 4: service-mission - NOUVEAU
-- Table missions
CREATE TABLE missions (
    id BIGSERIAL PRIMARY KEY,
    projet_id BIGINT NOT NULL,
    formulaire_id BIGINT NOT NULL,
    formulaire_version_id BIGINT,
    zone_id BIGINT,
    titre VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    objectif VARCHAR(500),
    date_debut DATE NOT NULL,
    date_fin DATE,
    statut VARCHAR(50) NOT NULL DEFAULT 'PLANIFIEE',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE TYPE mission_statut AS ENUM ('PLANIFIEE', 'EN_COURS', 'SUSPENDUE', 'TERMINEE', 'ANNULEE');
ALTER TABLE missions ALTER COLUMN statut TYPE mission_statut USING statut::mission_statut;

-- Index pour missions
CREATE INDEX idx_missions_projet ON missions(projet_id);
CREATE INDEX idx_missions_formulaire ON missions(formulaire_id);
CREATE INDEX idx_missions_zone ON missions(zone_id);
CREATE INDEX idx_missions_statut ON missions(statut);
CREATE INDEX idx_missions_dates ON missions(date_debut, date_fin);

-- Table mission_enqueteurs
CREATE TABLE mission_enqueteurs (
    mission_id BIGINT NOT NULL REFERENCES missions(id) ON DELETE CASCADE,
    enqueteur_id BIGINT NOT NULL,
    assigned_at TIMESTAMP NOT NULL DEFAULT NOW(),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (mission_id, enqueteur_id)
);

-- Index pour mission_enqueteurs
CREATE INDEX idx_me_enqueteur ON mission_enqueteurs(enqueteur_id);
CREATE INDEX idx_me_active ON mission_enqueteurs(is_active) WHERE is_active = TRUE;
