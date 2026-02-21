import client from './client';

/**
 * API Analytique — service-analytique (port 8087)
 * Routé via API Gateway : /api/analytics/**
 */
const analytiqueApi = {
  /**
   * Tableau de bord global
   * @returns {Promise<{
   *   totalCollectes: number,
   *   totalValidees: number,
   *   totalEnAttente: number,
   *   projetsActifs: number,
   *   tauxValidation: number,
   *   collectesAujourdhui: number
   * }>}
   */
  getDashboard: async () => {
    const { data } = await client.get('/analytics/dashboard');
    return data;
  },

  /**
   * Timeline globale (toutes projets confondus)
   * @param {number} days - Nombre de jours en arrière (7, 30, 365)
   * @returns {Promise<Array<{ date: string, collectes: number, validees: number }>>}
   */
  getGlobalTimeline: async (days = 30) => {
    const { data } = await client.get('/analytics/timeline', { params: { days } });
    // Normalise pour Recharts
    return Array.isArray(data)
      ? data.map(item => ({
          name:     item.date ?? item.dateJour ?? item.name ?? '',
          value:    item.collectes ?? item.nbCollectes ?? item.value ?? 0,
          validees: item.validees  ?? item.nbValidees  ?? 0,
        }))
      : [];
  },

  /**
   * Stats d'un projet spécifique
   * @param {number} projetId
   */
  getStatsByProjet: async (projetId) => {
    const { data } = await client.get(`/analytics/projets/${projetId}`);
    return data; // StatsProjet
  },

  /**
   * Timeline d'un projet
   * @param {number} projetId
   * @param {number} days
   */
  getTimelineByProjet: async (projetId, days = 30) => {
    const { data } = await client.get(`/analytics/projets/${projetId}/timeline`, {
      params: { days },
    });
    return Array.isArray(data)
      ? data.map(item => ({
          name:     item.date ?? item.dateJour ?? '',
          value:    item.collectes ?? item.nbCollectes ?? 0,
          validees: item.validees  ?? item.nbValidees  ?? 0,
        }))
      : [];
  },
};

export default analytiqueApi;
