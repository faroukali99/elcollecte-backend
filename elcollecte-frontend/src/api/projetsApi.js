import client from './client';

/**
 * API Projets — service-projet (port 8082)
 * Routé via API Gateway : /api/projets/**
 */
const projetsApi = {
  /**
   * Lister les projets (paginé)
   * @param {number} page  - Numéro de page (0-based)
   * @param {number} size  - Taille de page
   * @returns {Promise<{content: Array, totalPages: number, totalElements: number}>}
   */
  list: async (page = 0, size = 20) => {
    const { data } = await client.get('/projets', { params: { page, size } });
    return data; // Page<ProjetDto>
  },

  /**
   * Détails d'un projet
   */
  getById: async (id) => {
    const { data } = await client.get(`/projets/${id}`);
    return data; // ProjetDto
  },

  /**
   * Créer un projet
   * @param {{ titre, description, dateDebut, dateFin, zoneGeo }} payload
   */
  create: async (payload) => {
    const { data } = await client.post('/projets', payload);
    return data;
  },

  /**
   * Mettre à jour un projet
   * @param {number} id
   * @param {{ titre?, description?, statut?, dateFin?, zoneGeo? }} payload
   */
  update: async (id, payload) => {
    const { data } = await client.put(`/projets/${id}`, payload);
    return data;
  },

  /**
   * Attribuer un enquêteur à un projet
   */
  addEnqueteur: async (projetId, enqueteurId) => {
    const { data } = await client.post(
      `/projets/${projetId}/enqueteurs`,
      null,
      { params: { enqueteurId } }
    );
    return data;
  },

  /**
   * Retirer un enquêteur d'un projet
   */
  removeEnqueteur: async (projetId, enqueteurId) => {
    await client.delete(`/projets/${projetId}/enqueteurs/${enqueteurId}`);
  },
};

export default projetsApi;
