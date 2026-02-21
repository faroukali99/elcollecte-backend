/**
 * api/services.js
 * Centralized service layer — one function per backend operation.
 * All routes go through the API Gateway at /api/...
 */

import client from './client';

// ─────────────────────────────────────────────────────────────────────────────
// AUTH  (service-utilisateur  →  /api/auth/*)
// ─────────────────────────────────────────────────────────────────────────────
export const authApi = {
    login:   (email, password) =>
        client.post('/auth/login', { email, password }),

    register: (payload) =>
        client.post('/auth/register', payload),

    refresh: (refreshToken) =>
        client.post('/auth/refresh', null, {
            headers: { 'X-Refresh-Token': refreshToken },
        }),

    logout: () =>
        client.post('/auth/logout'),
};

// ─────────────────────────────────────────────────────────────────────────────
// PROJETS  (service-projet  →  /api/projets/*)
// ─────────────────────────────────────────────────────────────────────────────
export const projetsApi = {
    list: (page = 0, size = 20) =>
        client.get(`/projets?page=${page}&size=${size}`),

    getById: (id) =>
        client.get(`/projets/${id}`),

    create: (payload) =>
        client.post('/projets', payload),

    update: (id, payload) =>
        client.put(`/projets/${id}`, payload),

    addEnqueteur: (projetId, enqueteurId) =>
        client.post(`/projets/${projetId}/enqueteurs?enqueteurId=${enqueteurId}`),

    removeEnqueteur: (projetId, enqueteurId) =>
        client.delete(`/projets/${projetId}/enqueteurs/${enqueteurId}`),
};

// ─────────────────────────────────────────────────────────────────────────────
// COLLECTES  (service-collecte  →  /api/collectes/*)
// ─────────────────────────────────────────────────────────────────────────────
export const collectesApi = {
    list: ({ projetId, statut, page = 0, size = 20 } = {}) => {
        const params = new URLSearchParams({ page, size });
        if (projetId) params.set('projetId', projetId);
        if (statut)   params.set('statut',   statut);
        return client.get(`/collectes?${params}`);
    },

    submit: (payload) =>
        client.post('/collectes', payload),

    syncBatch: (items) =>
        client.post('/collectes/sync', items),

    valider: (id) =>
        client.put(`/collectes/${id}/valider`),

    rejeter: (id, motif) =>
        client.put(`/collectes/${id}/rejeter?motif=${encodeURIComponent(motif)}`),
};

// ─────────────────────────────────────────────────────────────────────────────
// FORMULAIRES EIES  (service-formulaire  →  /api/formulaires/eies/*)
// ─────────────────────────────────────────────────────────────────────────────
export const formulairesApi = {
    list: (page = 0, size = 20) =>
        client.get(`/formulaires/eies?page=${page}&size=${size}`),

    getById: (id) =>
        client.get(`/formulaires/eies/${id}`),

    create: () =>
        client.post('/formulaires/eies'),

    save: (id, payload) =>
        client.put(`/formulaires/eies/${id}`, payload),

    submit: (id) =>
        client.post(`/formulaires/eies/${id}/soumettre`),
};

// ─────────────────────────────────────────────────────────────────────────────
// VALIDATION  (service-validation  →  /api/validations/*)
// ─────────────────────────────────────────────────────────────────────────────
export const validationApi = {
    validerChamp: (champNom, valeur) =>
        client.post('/validations/champ', { type: 'CHAMP', champNom, valeur, formulaire: null }),

    validerFormulaire: (formulaire) =>
        client.post('/validations/complet', { type: 'EIES_COMPLET', formulaire }),

    health: () =>
        client.get('/validations/health'),
};

// ─────────────────────────────────────────────────────────────────────────────
// ANALYTIQUE  (service-analytique  →  /api/analytics/*)
// ─────────────────────────────────────────────────────────────────────────────
export const analytiqueApi = {
    dashboard: () =>
        client.get('/analytics/dashboard'),

    timelineGlobal: (days = 30) =>
        client.get(`/analytics/timeline?days=${days}`),

    timelineProjet: (projetId, days = 30) =>
        client.get(`/analytics/projets/${projetId}/timeline?days=${days}`),

    statsProjet: (projetId) =>
        client.get(`/analytics/projets/${projetId}`),
};

// ─────────────────────────────────────────────────────────────────────────────
// MÉDIAS  (service-media  →  /api/medias/*)
// ─────────────────────────────────────────────────────────────────────────────
export const mediasApi = {
    upload: (collecteId, file, type = 'PHOTO') => {
        const formData = new FormData();
        formData.append('file', file);
        formData.append('collecteId', collecteId);
        formData.append('typeMedia', type);
        return client.post('/medias/upload', formData, {
            headers: { 'Content-Type': 'multipart/form-data' },
        });
    },

    listByCollecte: (collecteId) =>
        client.get(`/medias?collecteId=${collecteId}`),

    delete: (id) =>
        client.delete(`/medias/${id}`),
};

// ─────────────────────────────────────────────────────────────────────────────
// RAPPORTS  (service-rapport  →  /api/rapports/*)
// ─────────────────────────────────────────────────────────────────────────────
export const rapportsApi = {
    generate: (projetId, type = 'PDF') =>
        client.post('/rapports', { projetId, type }),

    list: (page = 0, size = 10) =>
        client.get(`/rapports?page=${page}&size=${size}`),

    download: (id) =>
        client.get(`/rapports/${id}/download`, { responseType: 'blob' }),
};