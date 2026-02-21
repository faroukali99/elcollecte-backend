package com.elcollecte.formulaire.controller;

import com.elcollecte.formulaire.dto.FormulaireEiesDto;
import com.elcollecte.formulaire.dto.SaveFormulaireRequest;
import com.elcollecte.formulaire.service.FormulaireEiesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * API REST pour les formulaires EIES.
 * Routes :
 *  POST   /api/formulaires/eies          → créer un brouillon
 *  GET    /api/formulaires/eies/{id}     → récupérer
 *  PUT    /api/formulaires/eies/{id}     → sauvegarder (partiel)
 *  POST   /api/formulaires/eies/{id}/soumettre → soumettre
 *  GET    /api/formulaires/eies          → lister les miens
 */
@RestController
@RequestMapping("/api/formulaires/eies")
@Tag(name = "Formulaires EIES", description = "Gestion des formulaires d'Étude d'Impact Environnemental et Social")
public class FormulaireEiesController {

    private final FormulaireEiesService service;

    public FormulaireEiesController(FormulaireEiesService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau formulaire EIES (brouillon)")
    public ResponseEntity<FormulaireEiesDto> creer(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.creerBrouillon(userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un formulaire EIES par ID")
    public ResponseEntity<FormulaireEiesDto> getById(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(service.getById(id, userId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Sauvegarder un formulaire EIES (sauvegarde automatique ou manuelle)")
    public ResponseEntity<FormulaireEiesDto> sauvegarder(
            @PathVariable Long id,
            @RequestBody SaveFormulaireRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(service.sauvegarder(id, userId, request));
    }

    @PostMapping("/{id}/soumettre")
    @Operation(summary = "Soumettre définitivement un formulaire EIES")
    public ResponseEntity<FormulaireEiesDto> soumettre(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(service.soumettre(id, userId));
    }

    @GetMapping
    @Operation(summary = "Lister mes formulaires EIES")
    public ResponseEntity<Page<FormulaireEiesDto>> lister(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(service.listParUser(userId, pageable));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "formulaire-eies"));
    }
}