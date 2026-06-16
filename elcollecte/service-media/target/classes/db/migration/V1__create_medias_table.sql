CREATE TABLE medias (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    nom_fichier VARCHAR(255) NOT NULL,
    nom_original VARCHAR(255),
    type_mime VARCHAR(100),
    taille_octets BIGINT,
    chemin_stockage TEXT NOT NULL,
    url_acces TEXT,
    collecte_id BIGINT,
    projet_id BIGINT,
    uploade_par BIGINT NOT NULL,
    uploade_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    metadonnees JSONB
);

CREATE INDEX idx_medias_collecte ON medias(collecte_id);
CREATE INDEX idx_medias_projet ON medias(projet_id);
CREATE INDEX idx_medias_uploade_par ON medias(uploade_par);
CREATE INDEX idx_medias_uuid ON medias(uuid);
