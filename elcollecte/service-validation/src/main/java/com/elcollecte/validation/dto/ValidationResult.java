package com.elcollecte.validation.dto;

import java.util.List;
import java.util.Map;

/**
 * Résultat d'une validation — champ unique ou formulaire complet.
 */
public record ValidationResult(
        boolean              valide,
        Map<String, List<String>> erreurs,   // champNom → liste d'erreurs
        List<String>         avertissements,
        int                  scoreCompletude // 0-100
) {
    /** Résultat vide sans erreur */
    public static ValidationResult ok() {
        return new ValidationResult(true, Map.of(), List.of(), 100);
    }

    /** Résultat avec erreurs */
    public static ValidationResult withErrors(Map<String, List<String>> erreurs) {
        int completude = erreurs.isEmpty() ? 100 : Math.max(0, 100 - erreurs.size() * 10);
        return new ValidationResult(false, erreurs, List.of(), completude);
    }
}