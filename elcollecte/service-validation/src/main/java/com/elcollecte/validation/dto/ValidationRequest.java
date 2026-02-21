package com.elcollecte.validation.dto;

import java.util.Map;

/**
 * Requête de validation d'un formulaire EIES ou d'un champ individuel.
 */
public record ValidationRequest(
        String         type,          // "EIES_COMPLET" | "CHAMP"
        String         champNom,      // null si type=EIES_COMPLET
        Object         valeur,        // null si type=EIES_COMPLET
        Map<String, Object> formulaire // null si type=CHAMP
) {}