package com.elcollecte.validation.controller;

import com.elcollecte.validation.entity.Validation;
import com.elcollecte.validation.service.ValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/validations")
@Tag(name = "Validations", description = "Workflow de validation des collectes terrain")
public class ValidationController {

    private final ValidationService validationService;

    public ValidationController(ValidationService validationService) {
        this.validationService = validationService;
    }

    @GetMapping
    @Operation(summary = "Lister les validations d'un projet")
    public ResponseEntity<Page<Validation>> list(
            @RequestParam           Long   projetId,
            @RequestParam(required = false) String statut,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(validationService.findByProjet(projetId, statut, pageable));
    }

    @PutMapping("/{id}/valider")
    @Operation(summary = "Valider une collecte")
    public ResponseEntity<Validation> valider(
            @PathVariable              Long   id,
            @RequestHeader("X-User-Id") Long  validateurId,
            @RequestParam(required = false) String commentaire) {
        return ResponseEntity.ok(validationService.valider(id, validateurId, commentaire));
    }

    @PutMapping("/{id}/rejeter")
    @Operation(summary = "Rejeter une collecte avec motif obligatoire")
    public ResponseEntity<Validation> rejeter(
            @PathVariable              Long   id,
            @RequestHeader("X-User-Id") Long  validateurId,
            @RequestParam              String motif,
            @RequestParam(required = false) String commentaire) {
        return ResponseEntity.ok(validationService.rejeter(id, validateurId, motif, commentaire));
    }

    @PutMapping("/{id}/revision")
    @Operation(summary = "Demander une révision à l'enquêteur")
    public ResponseEntity<Validation> revision(
            @PathVariable              Long   id,
            @RequestHeader("X-User-Id") Long  validateurId,
            @RequestParam              String commentaire) {
        return ResponseEntity.ok(
                validationService.demanderRevision(id, validateurId, commentaire));
    }
}