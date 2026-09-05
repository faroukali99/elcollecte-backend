-- PHASE 2: Évolution service-projet selon roadmap ElCollecte 2.0
-- 1. Renommage chef_projet_id → responsable_id
-- 2. Mise à jour des statuts (remplacement ACTIF, VALIDE, REJETE par PLANIFIE, EN_COURS, ARCHIVE)
-- 3. Création de la table projet_membres pour l'équipe administrative

-- Renommer la colonne chef_projet_id → responsable_id
ALTER TABLE projets RENAME COLUMN chef_projet_id TO responsable_id;

-- Renommer l'index correspondant
DROP INDEX IF EXISTS idx_projets_chef;
CREATE INDEX idx_projets_responsable ON projets(responsable_id);

-- Créer un nouvel enum avec les statuts de la Phase 2
CREATE TYPE projet_statut_v2 AS ENUM ('BROUILLON', 'PLANIFIE', 'EN_COURS', 'SUSPENDU', 'TERMINE', 'ARCHIVE');

-- Migrer les données vers le nouvel enum
ALTER TABLE projets ALTER COLUMN statut TYPE projet_statut_v2 
    USING CASE statut 
        WHEN 'BROUILLON' THEN 'BROUILLON'::projet_statut_v2
        WHEN 'ACTIF' THEN 'EN_COURS'::projet_statut_v2
        WHEN 'SUSPENDU' THEN 'SUSPENDU'::projet_statut_v2
        WHEN 'TERMINE' THEN 'TERMINE'::projet_statut_v2
        WHEN 'VALIDE' THEN 'PLANIFIE'::projet_statut_v2
        WHEN 'REJETE' THEN 'BROUILLON'::projet_statut_v2
        ELSE 'BROUILLON'::projet_statut_v2
    END;

-- Supprimer l'ancien enum
DROP TYPE projet_statut;

-- Renommer le nouvel enum
ALTER TYPE projet_statut_v2 RENAME TO projet_statut;

-- Créer la table projet_membres pour l'équipe administrative
CREATE TABLE projet_membres (
    projet_id      BIGINT    NOT NULL REFERENCES projets(id) ON DELETE CASCADE,
    user_id        BIGINT    NOT NULL,
    role_membre    VARCHAR(50),
    assigned_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    is_active      BOOLEAN   NOT NULL DEFAULT TRUE,
    PRIMARY KEY (projet_id, user_id)
);

-- Index pour les performances
CREATE INDEX idx_pm_user ON projet_membres(user_id);
CREATE INDEX idx_pm_active ON projet_membres(is_active) WHERE is_active = TRUE;
