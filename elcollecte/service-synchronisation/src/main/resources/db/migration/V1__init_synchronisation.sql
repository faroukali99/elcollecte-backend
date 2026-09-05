-- PHASE 8: service-synchronisation - NOUVEAU
-- Table sync_logs
CREATE TABLE sync_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    device_id VARCHAR(100),
    direction VARCHAR(50) NOT NULL,
    statut VARCHAR(50) NOT NULL,
    details JSONB,
    items_count INTEGER,
    errors_count INTEGER,
    started_at TIMESTAMP NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMP
);

CREATE TYPE sync_direction AS ENUM ('UPLOAD', 'DOWNLOAD', 'BIDIRECTIONAL');
ALTER TABLE sync_logs ALTER COLUMN direction TYPE sync_direction USING direction::sync_direction;

CREATE TYPE sync_statut AS ENUM ('EN_COURS', 'SUCCES', 'PARTIEL', 'ECHEC');
ALTER TABLE sync_logs ALTER COLUMN statut TYPE sync_statut USING statut::sync_statut;

-- Index pour sync_logs
CREATE INDEX idx_sync_logs_user ON sync_logs(user_id);
CREATE INDEX idx_sync_logs_device ON sync_logs(device_id);
CREATE INDEX idx_sync_logs_started ON sync_logs(started_at DESC);

-- Table sync_conflicts
CREATE TABLE sync_conflicts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    local_version BIGINT,
    remote_version BIGINT,
    local_data JSONB,
    remote_data JSONB,
    resolution VARCHAR(50) NOT NULL,
    resolved_at TIMESTAMP,
    resolved_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TYPE conflict_resolution AS ENUM ('PENDING', 'LOCAL_WINS', 'REMOTE_WINS', 'MANUAL_MERGE');
ALTER TABLE sync_conflicts ALTER COLUMN resolution TYPE conflict_resolution USING resolution::conflict_resolution;

-- Index pour sync_conflicts
CREATE INDEX idx_conflicts_user ON sync_conflicts(user_id);
CREATE INDEX idx_conflicts_entity ON sync_conflicts(entity_type, entity_id);
CREATE INDEX idx_conflicts_resolution ON sync_conflicts(resolution);
CREATE INDEX idx_conflicts_created ON sync_conflicts(created_at DESC);
