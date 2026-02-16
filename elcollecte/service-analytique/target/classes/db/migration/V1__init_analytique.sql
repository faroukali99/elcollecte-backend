CREATE TABLE stats_projets (
    id                   BIGSERIAL  PRIMARY KEY,
    projet_id            BIGINT     NOT NULL UNIQUE,
    total_collectes      INT        NOT NULL DEFAULT 0,
    collectes_validees   INT        NOT NULL DEFAULT 0,
    collectes_rejetees   INT        NOT NULL DEFAULT 0,
    collectes_en_attente INT        NOT NULL DEFAULT 0,
    nb_enqueteurs_actifs INT        NOT NULL DEFAULT 0,
    taux_validation      NUMERIC(5,2) DEFAULT 0,
    updated_at           TIMESTAMP  NOT NULL DEFAULT NOW()
);

CREATE TABLE stats_daily (
    id          BIGSERIAL  PRIMARY KEY,
    projet_id   BIGINT     NOT NULL,
    date_jour   DATE       NOT NULL,
    nb_collectes INT       NOT NULL DEFAULT 0,
    nb_validees  INT       NOT NULL DEFAULT 0,
    UNIQUE(projet_id, date_jour)
);

CREATE INDEX idx_stats_daily_projet ON stats_daily(projet_id);
CREATE INDEX idx_stats_daily_date   ON stats_daily(date_jour);
