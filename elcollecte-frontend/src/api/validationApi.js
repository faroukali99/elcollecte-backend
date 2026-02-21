import client from './client';

/**
 * API Validation EIES — service-validation (port 8090)
 * Routé via API Gateway : /api/validations/**
 *
 * NOTE : Ajoutez cette route dans api-gateway/application.yml :
 *   - id: validation-eies-service
 *     uri: lb://service-validation
 *     predicates:
 *       - Path=/api/validations/**
 *     filters:
 *       - AuthFilter
 */
const validationApi = {
  /**
   * Valider un champ individuel en temps réel
   * @param {string} champNom  - Nom du champ (ex: "nomProjet")
   * @param {*}      valeur    - Valeur actuelle du champ
   * @returns {Promise<{ valide: boolean, erreurs: Object, avertissements: string[], scoreCompletude: number }>}
   */
  validerChamp: async (champNom, valeur) => {
    const { data } = await client.post('/validations/champ', {
      type: 'CHAMP',
      champNom,
      valeur,
      formulaire: null,
    });
    return data;
  },

  /**
   * Valider l'ensemble du formulaire EIES avant soumission
   * @param {Object} formulaire - Tous les champs du formulaire
   * @returns {Promise<{ valide: boolean, erreurs: Object, avertissements: string[], scoreCompletude: number }>}
   */
  validerComplet: async (formulaire) => {
    const { data } = await client.post('/validations/complet', {
      type: 'EIES_COMPLET',
      formulaire,
    });
    return data;
  },

  /**
   * Vérifier que le service est disponible
   */
  healthCheck: async () => {
    const { data } = await client.get('/validations/health');
    return data;
  },
};

export default validationApi;
