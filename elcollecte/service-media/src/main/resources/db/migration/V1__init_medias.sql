CREATE TYPE media_type AS ENUM ('PHOTO', 'SIGNATURE', 'DOCUMENT', 'AUDIO');

CREATE TABLE medias (
    id              BIGSERIAL    PRIMARY KEY,
    collecte_id     BIGINT       NOT NULL,
    uploaded_by     BIGINT       NOT NULL,
    type_media      media_type   NOT NULL,
    filename        VARCHAR(255) NOT NULL,
    original_name   VARCHAR(255),
    url_stockage    TEXT         NOT NULL,
    bucket          VARCHAR(100),
    taille_bytes    BIGINT,
    mime_type       VARCHAR(100),
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_medias_collecte  ON medias(collecte_id);
CREATE INDEX idx_medias_uploader  ON medias(uploaded_by);
