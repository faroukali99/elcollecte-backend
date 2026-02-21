-- Journal des validations EIES
CREATE TABLE validations_log (
                                 id              BIGSERIAL     PRIMARY KEY,
                                 formulaire_id   BIGINT,
                                 user_id         BIGINT,
                                 type_validation VARCHAR(50)   NOT NULL,  -- 'CHAMP' | 'COMPLET'
                                 champ_nom       VARCHAR(100),
                                 resultat        BOOLEAN       NOT NULL,
                                 nb_erreurs      INT           NOT NULL DEFAULT 0,
                                 score_completude INT          NOT NULL DEFAULT 0,
                                 created_at      TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_val_log_formulaire ON validations_log(formulaire_id);
CREATE INDEX idx_val_log_user       ON validations_log(user_id);
CREATE INDEX idx_val_log_date       ON validations_log(created_at);