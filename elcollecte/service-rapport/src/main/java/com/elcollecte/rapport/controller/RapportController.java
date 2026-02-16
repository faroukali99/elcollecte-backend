// RapportController.java
package com.elcollecte.rapport.controller;

import com.elcollecte.rapport.entity.Rapport;
import com.elcollecte.rapport.service.RapportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/rapports")
@Tag(name = "Rapports", description = "Génération asynchrone de rapports PDF/XLSX")
public class RapportController {

    private final RapportService rapportService;

    public RapportController(RapportService rapportService) {
        this.rapportService = rapportService;
    }

    @PostMapping
    @Operation(summary = "Demander la génération d'un rapport (asynchrone)")
    public ResponseEntity<Rapport> demander(
            @RequestParam              Long   projetId,
            @RequestParam              String titre,
            @RequestParam(defaultValue = "PDF") String format,
            @RequestBody(required = false) Map<String, Object> parametres,
            @RequestHeader("X-User-Id") Long demandeurId) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(rapportService.demanderRapport(
                        projetId, demandeurId, titre, format,
                        parametres != null ? parametres : Map.of()));
    }

    @GetMapping
    @Operation(summary = "Lister les rapports d'un projet")
    public ResponseEntity<Page<Rapport>> list(
            @RequestParam              Long projetId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(rapportService.findByProjet(projetId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Statut et détails d'un rapport")
    public ResponseEntity<Rapport> getById(@PathVariable Long id) {
        return ResponseEntity.ok(rapportService.findById(id));
    }
}