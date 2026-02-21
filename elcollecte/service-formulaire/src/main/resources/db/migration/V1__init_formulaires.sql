CREATE TYPE eies_statut AS ENUM ('BROUILLON', 'EN_REVISION', 'SOUMIS', 'VALIDE', 'REJETE');

CREATE TABLE formulaires_eies (
                                  id                          BIGSERIAL     PRIMARY KEY,
                                  projet_id                   BIGINT,
                                  user_id                     BIGINT        NOT NULL,

    -- Section A : Informations générales
                                  nom_projet                  VARCHAR(200),
                                  region                      VARCHAR(100),
                                  departement                 VARCHAR(100),
                                  commune                     VARCHAR(100),
                                  promoteur                   VARCHAR(200),
                                  email_promoteur             VARCHAR(150),
                                  date_debut                  VARCHAR(20),
                                  duree_estimee               VARCHAR(100),

    -- Section B : Description du projet
                                  type_projet                 VARCHAR(100),
                                  superficie                  FLOAT,
                                  description_detaillee       TEXT,
                                  activites_principales       TEXT,
                                  besoins_ressources          TEXT,

    -- Section C : Impacts environnementaux
                                  impact_faune                VARCHAR(10),
                                  impact_flore                VARCHAR(10),
                                  impact_eau                  VARCHAR(10),
                                  impact_air                  VARCHAR(10),
                                  impact_sonore               VARCHAR(10),
                                  gestion_dechets             TEXT,
                                  mesures_attenuation         TEXT,

    -- Section D : Impacts sociaux
                                  population_affectee         INT,
                                  deploiement_populations     VARCHAR(10),
                                  creation_emplois            INT,
                                  impact_economique_local     TEXT,
                                  consultation_communautes    VARCHAR(10),
                                  mesures_compensation_sociale TEXT,

    -- Section E : Documents
                                  documents_annexes           JSONB DEFAULT '[]',

    -- Métadonnées
                                  statut                      eies_statut   NOT NULL DEFAULT 'BROUILLON',
                                  score_completude            INT           NOT NULL DEFAULT 0,
                                  created_at                  TIMESTAMP     NOT NULL DEFAULT NOW(),
                                  updated_at                  TIMESTAMP,
                                  submitted_at                TIMESTAMP
);

CREATE INDEX idx_eies_user   ON formulaires_eies(user_id);
CREATE INDEX idx_eies_projet ON formulaires_eies(projet_id);
CREATE INDEX idx_eies_statut ON formulaires_eies(statut);