import client from '../api/client';

/**
 * Service Projets — service-projet (port 8082, via API Gateway)
 * Routes : /api/projets/**
 *
 * ProjetDto backend : { id, organisationId, chefProjetId, titre, description,
 *   statut, zoneGeo, dateDebut, dateFin, createdAt, nbEnqueteurs }
 * Statuts : BROUILLON | ACTIF | SUSPENDU | TERMINE
 */
const projetService = {
  /**
   * Lister les projets (filtrés par rôle côté backend)
   * @param {{ page?: number, size?: number }} params
   */
  list: async (params = {}) => {
    const { page = 0, size = 20 } = params;
    const { data } = await client.get('/projets', { params: { page, size } });
    return data; // Page<ProjetDto> : { content, totalPages, totalElements, ... }
  },

  /** @param {number} id */
  getById: async (id) => {
    const { data } = await client.get(`/projets/${id}`);
    return data;
  },

  /**
   * Créer un projet
   * @param {{ titre, description, dateDebut, dateFin?, zoneGeo? }} payload
   */
  create: async (payload) => {
    const { data } = await client.post('/projets', payload);
    return data;
  },

  /**
   * Modifier un projet
   * @param {number} id
   * @param {{ titre?, description?, statut?, dateFin?, zoneGeo? }} payload
   */
  update: async (id, payload) => {
    const { data } = await client.put(`/projets/${id}`, payload);
    return data;
  },

  /** @param {number} projetId @param {number} enqueteurId */
  addEnqueteur: async (projetId, enqueteurId) => {
    const { data } = await client.post(`/projets/${projetId}/enqueteurs`, null, {
      params: { enqueteurId },
    });
    return data;
  },

  /** @param {number} projetId @param {number} enqueteurId */
  removeEnqueteur: async (projetId, enqueteurId) => {
    await client.delete(`/projets/${projetId}/enqueteurs/${enqueteurId}`);
  },
};

export default projetService;
