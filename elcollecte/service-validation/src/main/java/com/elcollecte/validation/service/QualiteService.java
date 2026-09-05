package com.elcollecte.validation.service;

import com.elcollecte.validation.dto.ValidationResult;
import com.elcollecte.validation.entity.RegleQualite;
import com.elcollecte.validation.repository.RegleQualiteRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QualiteService {

    private final RegleQualiteRepository regleQualiteRepository;

    public QualiteService(RegleQualiteRepository regleQualiteRepository) {
        this.regleQualiteRepository = regleQualiteRepository;
    }

    public ValidationResult validerFormulaireDynamique(Long formulaireVersionId, Map<String, Object> donnees) {
        Map<String, List<String>> erreurs = new LinkedHashMap<>();
        List<String> avertissements = new ArrayList<>();

        List<RegleQualite> regles = regleQualiteRepository.findByFormulaireVersionIdAndActifTrue(formulaireVersionId);

        for (RegleQualite regle : regles) {
            Object valeur = donnees.get(regle.getParametre());
            appliquerRegle(regle, valeur, erreurs, avertissements);
        }

        int score = calculerScore(donnees, erreurs);
        boolean valide = erreurs.isEmpty();

        return new ValidationResult(valide, erreurs, avertissements, score);
    }

    private void appliquerRegle(RegleQualite regle, Object valeur, 
                                Map<String, List<String>> erreurs, List<String> avertissements) {
        switch (regle.getType()) {
            case "REQUIRED" -> {
                if (valeur == null || valeur.toString().isBlank()) {
                    erreurs.computeIfAbsent(regle.getParametre(), k -> new ArrayList<>())
                        .add("Ce champ est obligatoire.");
                }
            }
            case "MIN_LENGTH" -> {
                int min = (Integer) regle.getConfig().getOrDefault("min", 0);
                if (valeur != null && valeur.toString().length() < min) {
                    erreurs.computeIfAbsent(regle.getParametre(), k -> new ArrayList<>())
                        .add("Ce champ doit contenir au moins " + min + " caractères.");
                }
            }
            case "MAX_LENGTH" -> {
                int max = (Integer) regle.getConfig().getOrDefault("max", 1000);
                if (valeur != null && valeur.toString().length() > max) {
                    erreurs.computeIfAbsent(regle.getParametre(), k -> new ArrayList<>())
                        .add("Ce champ ne peut pas dépasser " + max + " caractères.");
                }
            }
            case "MIN_VALUE" -> {
                double min = ((Number) regle.getConfig().getOrDefault("min", 0)).doubleValue();
                try {
                    double val = Double.parseDouble(valeur.toString());
                    if (val < min) {
                        erreurs.computeIfAbsent(regle.getParametre(), k -> new ArrayList<>())
                            .add("La valeur minimale est " + min + ".");
                    }
                } catch (NumberFormatException e) {
                    erreurs.computeIfAbsent(regle.getParametre(), k -> new ArrayList<>())
                        .add("Valeur numérique invalide.");
                }
            }
            case "MAX_VALUE" -> {
                double max = ((Number) regle.getConfig().getOrDefault("max", 1000000)).doubleValue();
                try {
                    double val = Double.parseDouble(valeur.toString());
                    if (val > max) {
                        erreurs.computeIfAbsent(regle.getParametre(), k -> new ArrayList<>())
                            .add("La valeur maximale est " + max + ".");
                    }
                } catch (NumberFormatException e) {
                    erreurs.computeIfAbsent(regle.getParametre(), k -> new ArrayList<>())
                        .add("Valeur numérique invalide.");
                }
            }
            case "PATTERN" -> {
                String pattern = (String) regle.getConfig().get("pattern");
                if (valeur != null && !valeur.toString().matches(pattern)) {
                    erreurs.computeIfAbsent(regle.getParametre(), k -> new ArrayList<>())
                        .add("Format invalide.");
                }
            }
            case "WARNING" -> {
                String message = (String) regle.getConfig().get("message");
                if (valeur != null && !valeur.toString().isBlank()) {
                    avertissements.add(message != null ? message : "Attention sur ce champ.");
                }
            }
        }
    }

    private int calculerScore(Map<String, Object> donnees, Map<String, List<String>> erreurs) {
        int totalChamps = donnees.size();
        long champsRemplis = donnees.values().stream().filter(v -> v != null && !v.toString().isBlank()).count();
        int baseScore = totalChamps > 0 ? (int) (champsRemplis * 100 / totalChamps) : 100;
        int penalite = erreurs.size() * 5;
        return Math.max(0, Math.min(100, baseScore - penalite));
    }
}
