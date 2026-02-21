package com.elcollecte.validation.service;

import com.elcollecte.validation.dto.ValidationResult;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Service de validation métier pour les formulaires EIES.
 * Toutes les règles de validation sont centralisées ici.
 */
@Service
public class EiesValidationService {

    // ── Constantes ──────────────────────────────────────────────────────────
    private static final int MIN_DESCRIPTION_LENGTH = 100;
    private static final int MAX_DESCRIPTION_LENGTH = 5000;
    private static final int MAX_NOM_PROJET_LENGTH  = 200;
    private static final double MAX_SUPERFICIE       = 100_000.0; // ha
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"
    );

    // Champs obligatoires par section
    private static final Map<String, List<String>> CHAMPS_OBLIGATOIRES = Map.of(
            "informations_generales", List.of(
                    "nomProjet", "region", "departement", "commune",
                    "promoteur", "dateDebut"
            ),
            "description_projet", List.of(
                    "typeProjet", "superficie", "descriptionDetaillee", "activitesPrincipales"
            ),
            "impacts_environnementaux", List.of(
                    "impactFaune", "impactFlore", "impactEau", "impactAir",
                    "impactSonore", "gestionDechets", "mesuresAttenuation"
            ),
            "impacts_sociaux", List.of(
                    "populationAffectee", "deploiementPopulations",
                    "creationEmplois", "consultationCommunautes"
            )
    );

    // ── Validation complète du formulaire EIES ───────────────────────────────

    public ValidationResult validerFormulaire(Map<String, Object> formulaire) {
        Map<String, List<String>> erreurs = new LinkedHashMap<>();
        List<String> avertissements       = new ArrayList<>();

        // 1. Champs obligatoires
        validerChampsObligatoires(formulaire, erreurs);

        // 2. Validations métier par champ
        validerNomProjet(formulaire, erreurs);
        validerDateDebut(formulaire, erreurs, avertissements);
        validerSuperficie(formulaire, erreurs, avertissements);
        validerDescriptions(formulaire, erreurs);
        validerPopulation(formulaire, erreurs);
        validerEmail(formulaire, erreurs);

        // 3. Cohérence inter-champs
        validerCoherence(formulaire, erreurs, avertissements);

        int score = calculerScore(formulaire, erreurs);
        boolean valide = erreurs.isEmpty();

        return new ValidationResult(valide, erreurs, avertissements, score);
    }

    // ── Validation d'un champ individuel ────────────────────────────────────

    public ValidationResult validerChamp(String champNom, Object valeur) {
        Map<String, List<String>> erreurs = new LinkedHashMap<>();
        List<String> avertissements       = new ArrayList<>();

        Map<String, Object> ctx = Map.of(champNom, valeur != null ? valeur : "");

        switch (champNom) {
            case "nomProjet"           -> validerNomProjet(ctx, erreurs);
            case "dateDebut"           -> validerDateDebut(ctx, erreurs, avertissements);
            case "superficie"          -> validerSuperficie(ctx, erreurs, avertissements);
            case "descriptionDetaillee" -> validerDescriptions(ctx, erreurs);
            case "populationAffectee"  -> validerPopulation(ctx, erreurs);
            case "emailPromoteur"      -> validerEmail(ctx, erreurs);
            default -> {
                // Validation générique : non vide
                if (isBlank(valeur)) {
                    erreurs.put(champNom, List.of("Ce champ est obligatoire."));
                }
            }
        }

        boolean valide = erreurs.isEmpty();
        return new ValidationResult(valide, erreurs, avertissements, valide ? 100 : 0);
    }

    // ── Règles privées ───────────────────────────────────────────────────────

    private void validerChampsObligatoires(Map<String, Object> f,
                                           Map<String, List<String>> erreurs) {
        CHAMPS_OBLIGATOIRES.values().stream()
                .flatMap(List::stream)
                .forEach(champ -> {
                    if (isBlank(f.get(champ))) {
                        erreurs.computeIfAbsent(champ, k -> new ArrayList<>())
                                .add("Ce champ est obligatoire.");
                    }
                });
    }

    private void validerNomProjet(Map<String, Object> f,
                                  Map<String, List<String>> erreurs) {
        String nom = str(f.get("nomProjet"));
        if (nom == null) return;

        if (nom.length() < 5) {
            erreurs.computeIfAbsent("nomProjet", k -> new ArrayList<>())
                    .add("Le nom du projet doit contenir au moins 5 caractères.");
        }
        if (nom.length() > MAX_NOM_PROJET_LENGTH) {
            erreurs.computeIfAbsent("nomProjet", k -> new ArrayList<>())
                    .add("Le nom ne peut pas dépasser " + MAX_NOM_PROJET_LENGTH + " caractères.");
        }
    }

    private void validerDateDebut(Map<String, Object> f,
                                  Map<String, List<String>> erreurs,
                                  List<String> avertissements) {
        String dateStr = str(f.get("dateDebut"));
        if (dateStr == null || dateStr.isBlank()) return;

        try {
            LocalDate date = LocalDate.parse(dateStr);
            if (date.isBefore(LocalDate.now())) {
                avertissements.add("La date de début est dans le passé. Vérifiez la planification.");
            }
            if (date.isAfter(LocalDate.now().plusYears(10))) {
                erreurs.computeIfAbsent("dateDebut", k -> new ArrayList<>())
                        .add("La date de début semble trop lointaine (> 10 ans).");
            }
        } catch (DateTimeParseException e) {
            erreurs.computeIfAbsent("dateDebut", k -> new ArrayList<>())
                    .add("Format de date invalide. Utilisez le format AAAA-MM-JJ.");
        }
    }

    private void validerSuperficie(Map<String, Object> f,
                                   Map<String, List<String>> erreurs,
                                   List<String> avertissements) {
        Object val = f.get("superficie");
        if (val == null || val.toString().isBlank()) return;

        try {
            double sup = Double.parseDouble(val.toString());
            if (sup <= 0) {
                erreurs.computeIfAbsent("superficie", k -> new ArrayList<>())
                        .add("La superficie doit être un nombre positif.");
            } else if (sup > MAX_SUPERFICIE) {
                erreurs.computeIfAbsent("superficie", k -> new ArrayList<>())
                        .add("La superficie semble excessive (> 100 000 ha). Vérifiez l'unité.");
            } else if (sup > 1000) {
                avertissements.add("Grande superficie détectée (" + sup + " ha). Une étude approfondie est requise.");
            }
        } catch (NumberFormatException e) {
            erreurs.computeIfAbsent("superficie", k -> new ArrayList<>())
                    .add("La superficie doit être un nombre valide.");
        }
    }

    private void validerDescriptions(Map<String, Object> f,
                                     Map<String, List<String>> erreurs) {
        String desc = str(f.get("descriptionDetaillee"));
        if (desc == null) return;

        int len = desc.trim().length();
        if (len > 0 && len < MIN_DESCRIPTION_LENGTH) {
            erreurs.computeIfAbsent("descriptionDetaillee", k -> new ArrayList<>())
                    .add("La description doit contenir au moins " + MIN_DESCRIPTION_LENGTH +
                            " caractères (actuellement : " + len + ").");
        }
        if (len > MAX_DESCRIPTION_LENGTH) {
            erreurs.computeIfAbsent("descriptionDetaillee", k -> new ArrayList<>())
                    .add("La description ne peut pas dépasser " + MAX_DESCRIPTION_LENGTH + " caractères.");
        }
    }

    private void validerPopulation(Map<String, Object> f,
                                   Map<String, List<String>> erreurs) {
        Object val = f.get("populationAffectee");
        if (val == null || val.toString().isBlank()) return;

        try {
            int pop = Integer.parseInt(val.toString());
            if (pop < 0) {
                erreurs.computeIfAbsent("populationAffectee", k -> new ArrayList<>())
                        .add("La population affectée ne peut pas être négative.");
            }
        } catch (NumberFormatException e) {
            erreurs.computeIfAbsent("populationAffectee", k -> new ArrayList<>())
                    .add("La population affectée doit être un nombre entier.");
        }
    }

    private void validerEmail(Map<String, Object> f,
                              Map<String, List<String>> erreurs) {
        String email = str(f.get("emailPromoteur"));
        if (email == null || email.isBlank()) return;

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            erreurs.computeIfAbsent("emailPromoteur", k -> new ArrayList<>())
                    .add("L'adresse email n'est pas valide.");
        }
    }

    private void validerCoherence(Map<String, Object> f,
                                  Map<String, List<String>> erreurs,
                                  List<String> avertissements) {
        // Si déplacement de population déclaré → mesures de compensation requises
        String deploiement = str(f.get("deploiementPopulations"));
        String compensation = str(f.get("mesuresCompensationSociale"));

        if ("OUI".equalsIgnoreCase(deploiement) &&
                (compensation == null || compensation.isBlank())) {
            erreurs.computeIfAbsent("mesuresCompensationSociale", k -> new ArrayList<>())
                    .add("Des déplacements de populations sont prévus : les mesures de compensation sociale sont obligatoires.");
        }

        // Si espèces protégées → mesures d'atténuation requises
        String fauna = str(f.get("impactFaune"));
        String attenuation = str(f.get("mesuresAttenuation"));
        if ("OUI".equalsIgnoreCase(fauna) &&
                (attenuation == null || attenuation.isBlank())) {
            erreurs.computeIfAbsent("mesuresAttenuation", k -> new ArrayList<>())
                    .add("La présence d'espèces protégées impose des mesures d'atténuation détaillées.");
        }

        // Avertissement si aucune consultation communautaire
        String consultation = str(f.get("consultationCommunautes"));
        if ("NON".equalsIgnoreCase(consultation)) {
            avertissements.add("L'absence de consultation des communautés locales peut compromettre l'approbation du projet.");
        }
    }

    private int calculerScore(Map<String, Object> formulaire,
                              Map<String, List<String>> erreurs) {
        int totalChamps = CHAMPS_OBLIGATOIRES.values().stream()
                .mapToInt(List::size).sum();
        long champsRemplis = CHAMPS_OBLIGATOIRES.values().stream()
                .flatMap(List::stream)
                .filter(c -> !isBlank(formulaire.get(c)))
                .count();
        int baseScore = (int) (champsRemplis * 100 / Math.max(1, totalChamps));
        int penalite  = erreurs.size() * 5;
        return Math.max(0, Math.min(100, baseScore - penalite));
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private boolean isBlank(Object val) {
        return val == null || val.toString().isBlank();
    }

    private String str(Object val) {
        return val != null ? val.toString() : null;
    }
}