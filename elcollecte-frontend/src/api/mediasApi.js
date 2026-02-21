import client from './client';

/**
 * API Médias — service-media (port 8085)
 * Routé via API Gateway : /api/medias/**
 *
 * Ce service utilise multipart/form-data pour l'upload de fichiers.
 * Les fichiers sont stockés dans MinIO (S3-compatible).
 */
const mediasApi = {
  /**
   * Uploader un fichier média lié à une collecte
   * @param {number} collecteId
   * @param {File}   file        - Objet File du navigateur
   * @param {string} typeMedia   - 'PHOTO' | 'SIGNATURE' | 'DOCUMENT' | 'AUDIO'
   * @returns {Promise<{ id, filename, urlStockage, mimeType, tailleBytes }>}
   */
  upload: async (collecteId, file, typeMedia = 'PHOTO') => {
    const formData = new FormData();
    formData.append('file',      file);
    formData.append('collecteId', String(collecteId));
    formData.append('typeMedia',  typeMedia);

    const { data } = await client.post('/medias/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
      onUploadProgress: (progressEvent) => {
        // Optionnel : utilisez cette callback pour afficher une barre de progression
        const percent = Math.round(
          (progressEvent.loaded * 100) / (progressEvent.total || 1)
        );
        console.debug(`[MediaUpload] ${file.name}: ${percent}%`);
      },
    });
    return data;
  },

  /**
   * Uploader plusieurs fichiers d'un coup
   * @param {number}   collecteId
   * @param {FileList} files
   * @param {string}   typeMedia
   */
  uploadMultiple: async (collecteId, files, typeMedia = 'PHOTO') => {
    const uploads = Array.from(files).map(file =>
      mediasApi.upload(collecteId, file, typeMedia)
    );
    return Promise.allSettled(uploads);
  },

  /**
   * Lister les médias d'une collecte
   * @param {number} collecteId
   */
  listByCollecte: async (collecteId) => {
    const { data } = await client.get(`/medias/collecte/${collecteId}`);
    return data;
  },

  /**
   * Supprimer un média
   * @param {number} mediaId
   */
  delete: async (mediaId) => {
    await client.delete(`/medias/${mediaId}`);
  },
};

export default mediasApi;
