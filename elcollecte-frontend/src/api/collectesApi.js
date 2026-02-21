import client from './client';

/**
 * API Collectes — service-collecte (port 8084)
 * Routé via API Gateway : /api/collectes/**
 */
const collectesApi = {
  /**
   * Soumettre une collecte (mode online)
   * @param {{ formulaireId, projetId, donnees, latitude?, longitude? }} payload
   */
  submit: async (payload) => {
    const { data } = await client.post('/collectes', payload);
    return data; // CollecteDto
  },

  /**
   * Synchronisation batch des collectes hors-ligne
   * @param {Array<{ localId, formulaireId, projetId, donnees, latitude?, longitude?, collectedAt? }>} requests
   */
  syncBatch: async (requests) => {
    const { data } = await client.post('/collectes/sync', requests);
    return data; // SyncResponse { total, synced, failed, results }
  },

  /**
   * Lister les collectes (filtrées automatiquement par rôle côté backend)
   * @param {{ projetId?, statut?, page?, size? }} params
   */
  list: async ({ projetId, statut, page = 0, size = 50 } = {}) => {
    const { data } = await client.get('/collectes', {
      params: { projetId, statut, page, size },
    });
    return data; // Page<CollecteDto>
  },

  /**
   * Valider une collecte
   */
  valider: async (id) => {
    const { data } = await client.put(`/collectes/${id}/valider`);
    return data;
  },

  /**
   * Rejeter une collecte avec motif
   * @param {number} id
   * @param {string} motif - Obligatoire
   */
  rejeter: async (id, motif) => {
    const { data } = await client.put(`/collectes/${id}/rejeter`, null, {
      params: { motif },
    });
    return data;
  },
};

export default collectesApi;
