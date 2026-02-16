CREATE TYPE rapport_statut AS ENUM ('EN_ATTENTE', 'EN_COURS', 'TERMINE', 'ERREUR');
CREATE TYPE rapport_format AS ENUM ('PDF', 'XLSX', 'CSV');

CREATE TABLE rapports (
                          id              BIGSERIAL       PRIMARY KEY,
                          projet_id       BIGINT          NOT NULL,
                          demandeur_id    BIGINT          NOT NULL,
                          titre           VARCHAR(200)    NOT NULL,
                          format          rapport_format  NOT NULL DEFAULT 'PDF',
                          statut          rapport_statut  NOT NULL DEFAULT 'EN_ATTENTE',
                          url_fichier     TEXT,
                          taille_bytes    BIGINT,
                          message_erreur  TEXT,
                          parametres      JSONB           NOT NULL DEFAULT '{}',
                          created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
                          generated_at    TIMESTAMP
);

CREATE INDEX idx_rapports_projet    ON rapports(projet_id);
CREATE INDEX idx_rapports_demandeur ON rapports(demandeur_id);
CREATE INDEX idx_rapports_statut    ON rapports(statut);