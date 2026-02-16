CREATE TYPE projet_statut AS ENUM ('BROUILLON', 'ACTIF', 'SUSPENDU', 'TERMINE');

CREATE TABLE projets
(
    id             BIGSERIAL PRIMARY KEY ,
    organisation_id  BIGINT          NOT NULL,
    chef_projet_id BIGINT        NOT NULL,
    titre          VARCHAR(200)  NOT NULL,
    description    TEXT,
    statut         projet_statut NOT NULL DEFAULT 'BROUILLON',
    zone_geo       JSONB,
    date_debut     DATE          NOT NULL,
    date_fin       DATE,
    created_at     TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP
);

CREATE TABLE projet_enqueteurs (
    projet_id    BIGINT    NOT NULL REFERENCES projets(id) ON DELETE CASCADE,
    user_id      BIGINT    NOT NULL,
    assigned_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    is_active    BOOLEAN   NOT NULL DEFAULT TRUE,
    PRIMARY KEY (projet_id, user_id)
);

CREATE INDEX idx_projets_org        ON projets(organisation_id);
CREATE INDEX idx_projets_chef       ON projets(chef_projet_id);
CREATE INDEX idx_projets_statut     ON projets(statut);
CREATE INDEX idx_pe_user            ON projet_enqueteurs(user_id);
