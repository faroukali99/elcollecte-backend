import client from './client';

/**
 * API Administration — service-utilisateur (via API Gateway)
 * Routes : /api/profils/**, /api/permissions/**, /api/users/**
 */

export const profilsApi = {
  /**
   * Lister les profils (paginé)
   */
  list: async (page = 0, size = 50) => {
    const { data } = await client.get('/profils', { params: { page, size } });
    return data; // Page<ProfilDto>
  },

  getById: async (id) => {
    const { data } = await client.get(`/profils/${id}`);
    return data;
  },

  /**
   * Créer un profil
   * @param {{ code, libelle, description?, organisationId? }} payload
   */
  create: async (payload) => {
    const { data } = await client.post('/profils', payload);
    return data;
  },

  /**
   * Modifier un profil (libellé, description)
   * @param {{ libelle, description? }} payload
   */
  update: async (id, payload) => {
    const { data } = await client.put(`/profils/${id}`, payload);
    return data;
  },

  desactiver: async (id) => {
    await client.patch(`/profils/${id}/desactiver`);
  },

  reactiver: async (id) => {
    await client.patch(`/profils/${id}/reactiver`);
  },

  delete: async (id) => {
    await client.delete(`/profils/${id}`);
  },

  /**
   * Associer une ou plusieurs permissions à un profil
   * @param {number} id
   * @param {number[]} permissionIds
   */
  assignPermissions: async (id, permissionIds) => {
    const { data } = await client.post(`/profils/${id}/permissions`, { permissionIds });
    return data;
  },

  removePermission: async (id, permissionId) => {
    await client.delete(`/profils/${id}/permissions/${permissionId}`);
  },
};

export const permissionsApi = {
  /**
   * Lister toutes les permissions, éventuellement filtrées par catégorie
   */
  list: async (categorie) => {
    const { data } = await client.get('/permissions', { params: categorie ? { categorie } : {} });
    return data; // PermissionDto[]
  },

  /**
   * Créer une nouvelle permission
   * @param {{ code, libelle, categorie, description? }} payload
   */
  create: async (payload) => {
    const { data } = await client.post('/permissions', payload);
    return data;
  },
};

export const usersApi = {
  /**
   * Lister les utilisateurs de l'organisation courante (paginé)
   */
  list: async (page = 0, size = 20) => {
    const { data } = await client.get('/users', { params: { page, size } });
    return data; // Page<UserAdminDto>
  },

  getById: async (id) => {
    const { data } = await client.get(`/users/${id}`);
    return data;
  },

  /**
   * Créer un utilisateur et l'affecter directement à un profil
   * @param {{ nom, prenom, email, password, organisationId, profilId }} payload
   */
  create: async (payload) => {
    const { data } = await client.post('/users', payload);
    return data;
  },

  /**
   * Associer un utilisateur à un profil
   */
  assignProfil: async (id, profilId) => {
    const { data } = await client.patch(`/users/${id}/profil`, { profilId });
    return data;
  },

  desactiver: async (id) => {
    await client.patch(`/users/${id}/desactiver`);
  },

  reactiver: async (id) => {
    await client.patch(`/users/${id}/reactiver`);
  },
};
