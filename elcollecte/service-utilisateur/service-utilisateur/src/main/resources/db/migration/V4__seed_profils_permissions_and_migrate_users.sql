-- ═══════════════════════════════════════════════════════════════════════════
-- V4 : Seed des 4 profils système (miroir exact des anciens rôles), des
-- permissions de base, association profil↔permission, puis rattachement
-- de tous les utilisateurs existants à leur profil correspondant.
--
-- Ces 4 profils sont marqués is_systeme = TRUE : ils ne pourront pas être
-- supprimés par l'ADMIN (règle appliquée côté service Java), afin de ne
-- jamais casser la compatibilité avec l'ancien enum `role`.
-- ═══════════════════════════════════════════════════════════════════════════

-- ── Permissions de base (catalogue initial, extensible par l'ADMIN) ─────────
INSERT INTO permissions (code, libelle, categorie, description) VALUES
    ('USER_READ',        'Consulter les utilisateurs',      'UTILISATEUR', NULL),
    ('USER_CREATE',       'Créer un utilisateur',            'UTILISATEUR', NULL),
    ('USER_UPDATE',       'Modifier un utilisateur',         'UTILISATEUR', NULL),
    ('USER_DELETE',       'Désactiver/supprimer un utilisateur', 'UTILISATEUR', NULL),

    ('PROFILE_READ',      'Consulter les profils',           'PROFIL', NULL),
    ('PROFILE_CREATE',    'Créer un profil',                 'PROFIL', NULL),
    ('PROFILE_UPDATE',    'Modifier un profil / ses permissions', 'PROFIL', NULL),
    ('PROFILE_DELETE',    'Supprimer un profil',             'PROFIL', NULL),

    ('PROJECT_READ',      'Consulter les projets',           'PROJET', NULL),
    ('PROJECT_CREATE',    'Créer un projet',                 'PROJET', NULL),
    ('PROJECT_UPDATE',    'Modifier un projet',              'PROJET', NULL),
    ('PROJECT_DELETE',    'Supprimer un projet',             'PROJET', NULL),

    ('FORM_READ',         'Consulter les formulaires',       'FORMULAIRE', NULL),
    ('FORM_CREATE',       'Créer un formulaire',             'FORMULAIRE', NULL),
    ('FORM_UPDATE',       'Modifier un formulaire',          'FORMULAIRE', NULL),
    ('FORM_DELETE',       'Supprimer un formulaire',         'FORMULAIRE', NULL),

    ('MISSION_READ',      'Consulter les missions',          'MISSION', NULL),
    ('MISSION_CREATE',    'Créer une mission',               'MISSION', NULL),
    ('MISSION_UPDATE',    'Modifier une mission',            'MISSION', NULL),
    ('MISSION_DELETE',    'Supprimer une mission',           'MISSION', NULL),

    ('COLLECTE_READ',     'Consulter les collectes',         'COLLECTE', NULL),
    ('COLLECTE_CREATE',   'Créer une collecte',              'COLLECTE', NULL),
    ('COLLECTE_UPDATE',   'Modifier une collecte',           'COLLECTE', NULL),
    ('COLLECTE_VALIDATE', 'Valider une collecte',            'COLLECTE', NULL),
    ('COLLECTE_DELETE',   'Supprimer une collecte',          'COLLECTE', NULL),

    ('MAP_READ',          'Consulter la cartographie',       'CARTOGRAPHIE', NULL),
    ('MAP_CREATE',        'Créer des éléments cartographiques', 'CARTOGRAPHIE', NULL),
    ('MAP_UPDATE',        'Modifier des éléments cartographiques', 'CARTOGRAPHIE', NULL),

    ('ZONE_READ',         'Consulter les zones',             'ZONE', NULL),
    ('ZONE_CREATE',       'Créer une zone',                  'ZONE', NULL),
    ('ZONE_UPDATE',       'Modifier une zone',               'ZONE', NULL),
    ('ZONE_DELETE',       'Supprimer une zone',              'ZONE', NULL),

    ('STATISTICS_READ',   'Consulter les statistiques',      'ANALYSE', NULL),
    ('REPORT_READ',       'Consulter les rapports',          'RAPPORT', NULL),
    ('REPORT_CREATE',     'Générer un rapport',              'RAPPORT', NULL),

    ('AUDIT_READ',        'Consulter les journaux d''audit',  'AUDIT', NULL),

    ('SYNC_READ',         'Consulter l''état de synchronisation', 'SYNCHRONISATION', NULL),
    ('SYNC_MANAGE',       'Gérer la synchronisation',        'SYNCHRONISATION', NULL);

-- ── Profils système (miroir des 4 rôles historiques) ────────────────────────
INSERT INTO profils (code, libelle, description, organisation_id, is_systeme, is_active) VALUES
    ('ADMIN',       'Administrateur',   'Accès complet à la plateforme (profil historique migré depuis le rôle ADMIN)',       NULL, TRUE, TRUE),
    ('CHEF_PROJET', 'Chef de projet',   'Gestion de projets, formulaires, missions et équipes (profil historique CHEF_PROJET)', NULL, TRUE, TRUE),
    ('ENQUETEUR',   'Enquêteur',        'Collecte terrain (profil historique ENQUETEUR)',                                     NULL, TRUE, TRUE),
    ('ANALYSTE',    'Analyste',         'Consultation des données, statistiques et rapports (profil historique ANALYSTE)',    NULL, TRUE, TRUE);

-- ── ADMIN : toutes les permissions ──────────────────────────────────────────
INSERT INTO profil_permissions (profil_id, permission_id)
SELECT (SELECT id FROM profils WHERE code = 'ADMIN'), p.id FROM permissions p;

-- ── CHEF_PROJET : gestion projet/formulaire/mission + lecture collecte/carte/stats/rapport
INSERT INTO profil_permissions (profil_id, permission_id)
SELECT (SELECT id FROM profils WHERE code = 'CHEF_PROJET'), p.id
FROM permissions p
WHERE p.code IN (
    'PROJECT_READ','PROJECT_CREATE','PROJECT_UPDATE','PROJECT_DELETE',
    'FORM_READ','FORM_CREATE','FORM_UPDATE','FORM_DELETE',
    'MISSION_READ','MISSION_CREATE','MISSION_UPDATE','MISSION_DELETE',
    'COLLECTE_READ','COLLECTE_VALIDATE',
    'MAP_READ','ZONE_READ','ZONE_CREATE','ZONE_UPDATE',
    'STATISTICS_READ','REPORT_READ','REPORT_CREATE',
    'USER_READ'
);

-- ── ENQUETEUR : collecte terrain + carte + sync ─────────────────────────────
INSERT INTO profil_permissions (profil_id, permission_id)
SELECT (SELECT id FROM profils WHERE code = 'ENQUETEUR'), p.id
FROM permissions p
WHERE p.code IN (
    'COLLECTE_READ','COLLECTE_CREATE','COLLECTE_UPDATE',
    'MAP_READ',
    'SYNC_READ','SYNC_MANAGE',
    'MISSION_READ','FORM_READ'
);

-- ── ANALYSTE : lecture données/stats/rapports uniquement ────────────────────
INSERT INTO profil_permissions (profil_id, permission_id)
SELECT (SELECT id FROM profils WHERE code = 'ANALYSTE'), p.id
FROM permissions p
WHERE p.code IN (
    'COLLECTE_READ','STATISTICS_READ','REPORT_READ','MAP_READ','PROJECT_READ'
);

-- ── Rattachement des utilisateurs existants à leur profil miroir ───────────
UPDATE users u
SET profil_id = (SELECT id FROM profils pr WHERE pr.code = u.role::text)
WHERE u.profil_id IS NULL;
