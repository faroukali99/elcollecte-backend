CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TYPE collecte_statut AS ENUM ('BROUILLON', 'SOUMIS', 'VALIDE', 'REJETE');

CREATE TABLE collectes (
    id               BIGSERIAL       PRIMARY KEY,
    uuid             UUID            NOT NULL DEFAULT uuid_generate_v4() UNIQUE,
    formulaire_id    BIGINT          NOT NULL,
    enqueteur_id     BIGINT          NOT NULL,
    projet_id        BIGINT          NOT NULL,
    validateur_id    BIGINT,
    donnees          JSONB           NOT NULL DEFAULT '{}',
    geolocalisation  GEOMETRY(POINT, 4326),
    medias           JSONB           NOT NULL DEFAULT '[]',
    statut           collecte_statut NOT NULL DEFAULT 'SOUMIS',
    is_offline       BOOLEAN         NOT NULL DEFAULT FALSE,
    motif_rejet      TEXT,
    collected_at     TIMESTAMP       NOT NULL DEFAULT NOW(),
    synced_at        TIMESTAMP,
    validated_at     TIMESTAMP
);

CREATE INDEX idx_collectes_projet      ON collectes(projet_id);
CREATE INDEX idx_collectes_enqueteur   ON collectes(enqueteur_id);
CREATE INDEX idx_collectes_formulaire  ON collectes(formulaire_id);
CREATE INDEX idx_collectes_statut      ON collectes(statut);
CREATE INDEX idx_collectes_geo         ON collectes USING GIST(geolocalisation);
