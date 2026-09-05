-- ═══════════════════════════════════════════════════════════════════════════
-- V3 : Introduction du système dynamique Profil / Permission
--
-- Objectif : sortir les rôles du enum figé `user_role` pour aller vers un
-- modèle stocké en base, configurable par l'ADMIN, sans casser l'existant.
-- La colonne `users.role` (enum) est CONSERVÉE pour compatibilité pendant
-- la période de transition (voir V4 pour le rattachement des données).
-- ═══════════════════════════════════════════════════════════════════════════

CREATE TABLE profils (
    id               SERIAL       PRIMARY KEY,
    code             VARCHAR(50)  UNIQUE NOT NULL,
    libelle          VARCHAR(150) NOT NULL,
    description      TEXT,
    organisation_id  INT          REFERENCES organisations(id) ON DELETE CASCADE,
    is_systeme       BOOLEAN      NOT NULL DEFAULT FALSE,
    is_active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP
);

CREATE TABLE permissions (
    id           SERIAL       PRIMARY KEY,
    code         VARCHAR(80)  UNIQUE NOT NULL,
    libelle      VARCHAR(150) NOT NULL,
    categorie    VARCHAR(50)  NOT NULL,
    description  TEXT,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE profil_permissions (
    id             SERIAL    PRIMARY KEY,
    profil_id      INT       NOT NULL REFERENCES profils(id)     ON DELETE CASCADE,
    permission_id  INT       NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    granted_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_profil_permission UNIQUE (profil_id, permission_id)
);

-- Colonne de rattachement sur users. Nullable pendant la transition :
-- un utilisateur peut ne pas encore avoir de profil tant que la migration
-- de données (V4) ne l'a pas rattaché.
ALTER TABLE users ADD COLUMN profil_id INT REFERENCES profils(id) ON DELETE SET NULL;

CREATE INDEX idx_profils_organisation      ON profils(organisation_id);
CREATE INDEX idx_profils_code              ON profils(code);
CREATE INDEX idx_permissions_categorie     ON permissions(categorie);
CREATE INDEX idx_profil_permissions_profil ON profil_permissions(profil_id);
CREATE INDEX idx_users_profil              ON users(profil_id);
