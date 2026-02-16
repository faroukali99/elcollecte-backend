package com.elcollecte.formulaire.controller;

import com.elcollecte.formulaire.dto.*;
import com.elcollecte.formulaire.service.FormulaireService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/formulaires")
@Tag(name = "Formulaires", description = "Builder de formulaires JSONB")
public class FormulaireController {

    private final FormulaireService formulaireService;

    public FormulaireController(FormulaireService formulaireService) {
        this.formulaireService = formulaireService;
    }

    @GetMapping
    @Operation(summary = "Lister les formulaires d'un projet")
    public ResponseEntity<Page<FormulaireDto>> list(
            @RequestParam           Long    projetId,
            @RequestParam(defaultValue = "true") boolean activeOnly,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(formulaireService.findByProjet(projetId, activeOnly, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détails d'un formulaire")
    public ResponseEntity<FormulaireDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(formulaireService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Créer un formulaire")
    public ResponseEntity<FormulaireDto> create(
            @Valid @RequestBody          CreateFormulaireRequest request,
            @RequestHeader("X-User-Id") Long createurId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(formulaireService.create(request, createurId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier le schéma d'un formulaire (crée une nouvelle version)")
    public ResponseEntity<FormulaireDto> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateFormulaireRequest request) {
        return ResponseEntity.ok(formulaireService.update(id, request));
    }

    @PostMapping("/{id}/publier")
    @Operation(summary = "Publier un formulaire (rendre actif + événement Kafka)")
    public ResponseEntity<Map<String, String>> publier(
            @PathVariable              Long id,
            @RequestHeader("X-User-Id") Long userId) {
        formulaireService.publier(id, userId);
        return ResponseEntity.ok(Map.of("message", "Formulaire publié"));
    }

    @PostMapping("/{id}/archiver")
    @Operation(summary = "Archiver un formulaire (désactiver)")
    public ResponseEntity<Map<String, String>> archiver(@PathVariable Long id) {
        formulaireService.archiver(id);
        return ResponseEntity.ok(Map.of("message", "Formulaire archivé"));
    }
}