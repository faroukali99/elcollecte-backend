package com.elcollecte.validation.controller;

import com.elcollecte.validation.dto.ValidationRequest;
import com.elcollecte.validation.dto.ValidationResult;
import com.elcollecte.validation.service.EiesValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * API REST de validation EIES.
 * Deux modes :
 *  - POST /api/validations/champ  → valider un seul champ en temps réel
 *  - POST /api/validations/complet → valider le formulaire entier avant soumission
 */
@RestController
@RequestMapping("/api/validations")
@Tag(name = "Validation EIES", description = "Validation des formulaires d'impact environnemental et social")
public class ValidationController {

    private final EiesValidationService validationService;

    public ValidationController(EiesValidationService validationService) {
        this.validationService = validationService;
    }

    /**
     * Valide un champ individuel (appelé en temps réel depuis le frontend).
     */
    @PostMapping("/champ")
    @Operation(summary = "Valider un champ en temps réel")
    public ResponseEntity<ValidationResult> validerChamp(
            @RequestBody ValidationRequest request) {

        ValidationResult result = validationService.validerChamp(
                request.champNom(), request.valeur()
        );
        return ResponseEntity.ok(result);
    }

    /**
     * Valide l'ensemble du formulaire EIES (avant soumission finale).
     */
    @PostMapping("/complet")
    @Operation(summary = "Valider le formulaire EIES complet")
    public ResponseEntity<ValidationResult> validerComplet(
            @RequestBody ValidationRequest request) {

        ValidationResult result = validationService.validerFormulaire(
                request.formulaire() != null ? request.formulaire() : Map.of()
        );
        return ResponseEntity.ok(result);
    }

    /**
     * Health-check rapide (pas de JWT requis).
     */
    @GetMapping("/health")
    @Operation(summary = "Statut du service de validation")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "validation-eies"));
    }
}