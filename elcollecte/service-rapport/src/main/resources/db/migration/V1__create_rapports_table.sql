CREATE TYPE rapport_statut AS ENUM ('EN_ATTENTE', 'EN_COURS', 'GENERE', 'ERREUR');

CREATE TABLE rapports (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    projet_id BIGINT NOT NULL,
    formulaire_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    titre VARCHAR(255) NOT NULL,
    format VARCHAR(20) NOT NULL,
    parametres JSONB,
    chemin_fichier TEXT,
    statut rapport_statut NOT NULL DEFAULT 'EN_ATTENTE',
    erreur_message TEXT,
    cree_par BIGINT NOT NULL,
    cree_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    genere_at TIMESTAMP
);

CREATE INDEX idx_rapports_projet ON rapports(projet_id);
CREATE INDEX idx_rapports_formulaire ON rapports(formulaire_id);
CREATE INDEX idx_rapports_statut ON rapports(statut);
CREATE INDEX idx_rapports_cree_par ON rapports(cree_par);
