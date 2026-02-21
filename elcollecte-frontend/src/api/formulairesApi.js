import client from './client';

/**
 * API Formulaires EIES — service-formulaire (port 8091)
 * Routé via API Gateway : /api/formulaires/**
 *
 * NOTE : Ajoutez cette route dans api-gateway/application.yml :
 *   - id: formulaires-eies-service
 *     uri: lb://service-formulaire-eies
 *     predicates:
 *       - Path=/api/formulaires/**
 *     filters:
 *       - AuthFilter
 */
const formulairesApi = {
  /**
   * Créer un nouveau brouillon EIES
   * @returns {Promise<FormulaireEiesDto>}
   */
  creerBrouillon: async () => {
    const { data } = await client.post('/formulaires/eies');
    return data;
  },

  /**
   * Récupérer un formulaire par ID
   */
  getById: async (id) => {
    const { data } = await client.get(`/formulaires/eies/${id}`);
    return data;
  },

  /**
   * Sauvegarder un formulaire (autosauvegarde ou manuelle)
   * Tous les champs sont optionnels — envoie uniquement les champs remplis
   * @param {number} id
   * @param {Object} payload - Champs du formulaire EIES
   * @returns {Promise<FormulaireEiesDto>} - Inclut scoreCompletude mis à jour
   */
  sauvegarder: async (id, payload) => {
    const { data } = await client.put(`/formulaires/eies/${id}`, payload);
    return data; // { ..., scoreCompletude: number }
  },

  /**
   * Soumettre définitivement un formulaire EIES
   */
  soumettre: async (id) => {
    const { data } = await client.post(`/formulaires/eies/${id}/soumettre`);
    return data;
  },

  /**
   * Lister mes formulaires EIES (paginé)
   */
  list: async (page = 0, size = 20) => {
    const { data } = await client.get('/formulaires/eies', { params: { page, size } });
    return data;
  },
};

export default formulairesApi;
