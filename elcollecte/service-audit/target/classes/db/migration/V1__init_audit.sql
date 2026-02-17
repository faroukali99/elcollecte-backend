CREATE TABLE audit_logs (
    id            BIGSERIAL    PRIMARY KEY,
    user_id       BIGINT,
    action        VARCHAR(100) NOT NULL,
    ressource     VARCHAR(100),
    ressource_id  BIGINT,
    details       TEXT,
    ip_address    INET,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_user      ON audit_logs(user_id);
CREATE INDEX idx_audit_action    ON audit_logs(action);
CREATE INDEX idx_audit_created   ON audit_logs(created_at);
CREATE INDEX idx_audit_ressource ON audit_logs(ressource, ressource_id);
