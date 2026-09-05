-- PHASE 6: Collecte V2 - Évolution service-collecte
-- Ajout des champs mission_id et formulaire_version_id
-- Mise à jour de l'enum statut pour inclure EN_VALIDATION

ALTER TABLE collectes ADD COLUMN IF NOT EXISTS formulaire_version_id BIGINT;
ALTER TABLE collectes ADD COLUMN IF NOT EXISTS mission_id BIGINT;

-- Index pour les nouveaux champs
CREATE INDEX IF NOT EXISTS idx_collectes_mission ON collectes(mission_id);
CREATE INDEX IF NOT EXISTS idx_collectes_formulaire_version ON collectes(formulaire_version_id);

-- Mise à jour de l'enum statut
CREATE TYPE collecte_statut_v2 AS ENUM ('BROUILLON', 'SOUMIS', 'EN_VALIDATION', 'VALIDE', 'REJETE');

ALTER TABLE collectes ALTER COLUMN statut TYPE collecte_statut_v2 
    USING CASE statut 
        WHEN 'BROUILLON' THEN 'BROUILLON'::collecte_statut_v2
        WHEN 'SOUMIS' THEN 'SOUMIS'::collecte_statut_v2
        WHEN 'VALIDE' THEN 'VALIDE'::collecte_statut_v2
        WHEN 'REJETE' THEN 'REJETE'::collecte_statut_v2
        ELSE 'BROUILLON'::collecte_statut_v2
    END;

DROP TYPE collecte_statut;

ALTER TYPE collecte_statut_v2 RENAME TO collecte_statut;
