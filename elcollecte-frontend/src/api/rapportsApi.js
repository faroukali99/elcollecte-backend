import client from './client';

/**
 * API Rapports — service-rapport (port 8088)
 * Routé via API Gateway : /api/rapports/**
 *
 * La génération de rapports est asynchrone (Kafka + MinIO).
 * Le flux typique :
 *   1. POST /api/rapports → { jobId, statut: 'EN_COURS' }
 *   2. GET  /api/rapports/{jobId} → { statut: 'PRET', downloadUrl }
 *   3. Télécharger via downloadUrl
 */
const rapportsApi = {
  /**
   * Demander la génération d'un rapport
   * @param {{ projetId, type, format, filtres? }} payload
   *   type   : 'COLLECTES' | 'VALIDATION' | 'ANALYTIQUE' | 'EIES'
   *   format : 'PDF' | 'XLSX' | 'DOCX'
   * @returns {Promise<{ jobId: string, statut: string }>}
   */
  generer: async ({ projetId, type = 'COLLECTES', format = 'PDF', filtres = {} }) => {
    const { data } = await client.post('/rapports', {
      projetId, type, format, filtres,
    });
    return data;
  },

  /**
   * Vérifier le statut d'un rapport
   * @param {string} jobId
   * @returns {Promise<{ jobId, statut, downloadUrl?, errorMessage? }>}
   *   statut : 'EN_COURS' | 'PRET' | 'ERREUR'
   */
  getStatut: async (jobId) => {
    const { data } = await client.get(`/rapports/${jobId}`);
    return data;
  },

  /**
   * Lister mes rapports générés
   */
  list: async (page = 0, size = 20) => {
    const { data } = await client.get('/rapports', { params: { page, size } });
    return data;
  },

  /**
   * Attendre qu'un rapport soit prêt (polling toutes les 3 secondes, max 2 minutes)
   * @param {string}   jobId
   * @param {Function} onProgress - Callback appelée à chaque poll
   * @returns {Promise<string>} - downloadUrl
   */
  attendreResultat: async (jobId, onProgress) => {
    const MAX_TENTATIVES = 40;
    const INTERVALLE_MS  = 3000;

    for (let i = 0; i < MAX_TENTATIVES; i++) {
      await new Promise(r => setTimeout(r, INTERVALLE_MS));
      const resultat = await rapportsApi.getStatut(jobId);

      if (onProgress) onProgress(resultat);

      if (resultat.statut === 'PRET') return resultat.downloadUrl;
      if (resultat.statut === 'ERREUR') {
        throw new Error(resultat.errorMessage || 'Erreur lors de la génération du rapport');
      }
    }
    throw new Error('Délai d\'attente dépassé pour la génération du rapport');
  },

  /**
   * Télécharger un rapport directement (si l'URL est une URL relative du backend)
   */
  telecharger: async (downloadUrl) => {
    const response = await client.get(downloadUrl, { responseType: 'blob' });
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `rapport_${Date.now()}.pdf`);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  },
};

export default rapportsApi;
