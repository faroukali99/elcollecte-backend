package com.elcollecte.collecte.controller;

import com.elcollecte.collecte.dto.*;
import com.elcollecte.collecte.service.CollecteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/collectes")
@Tag(name = "Collectes", description = "Soumission et gestion des données terrain")
public class CollecteController {

    private final CollecteService collecteService;

    public CollecteController(CollecteService collecteService) {
        this.collecteService = collecteService;
    }

    @PostMapping
    @Operation(summary = "Soumettre une collecte (mode online)")
    public ResponseEntity<CollecteDto> submit(
        @Valid @RequestBody           SubmitCollecteRequest request,
        @RequestHeader("X-User-Id")   Long                 enqueteurId) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(collecteService.submit(request, enqueteurId));
    }

    @PostMapping("/sync")
    @Operation(summary = "Synchronisation batch des collectes hors-ligne")
    public ResponseEntity<SyncResponse> syncBatch(
        @RequestBody              List<@Valid OfflineSyncRequest> requests,
        @RequestHeader("X-User-Id") Long                         enqueteurId) {

        return ResponseEntity.ok(collecteService.syncBatch(requests, enqueteurId));
    }

    @GetMapping
    @Operation(summary = "Lister les collectes (filtrées par rôle)")
    public ResponseEntity<Page<CollecteDto>> list(
        @RequestParam(required = false) Long   projetId,
        @RequestParam(required = false) String statut,
        @RequestHeader("X-User-Id")     Long   userId,
        @RequestHeader("X-User-Role")   String role,
        @RequestParam(defaultValue = "0")  int page,
        @RequestParam(defaultValue = "50") int size) {

        Pageable pageable = PageRequest.of(page, size,
            Sort.by("collectedAt").descending());
        return ResponseEntity.ok(
            collecteService.findAll(projetId, statut, userId, role, pageable));
    }

    @PutMapping("/{id}/valider")
    @Operation(summary = "Valider une soumission")
    public ResponseEntity<Map<String, String>> valider(
        @PathVariable              Long id,
        @RequestHeader("X-User-Id") Long validateurId) {

        collecteService.valider(id, validateurId);
        return ResponseEntity.ok(Map.of("message", "Collecte validée"));
    }

    @PutMapping("/{id}/rejeter")
    @Operation(summary = "Rejeter une soumission avec motif")
    public ResponseEntity<Map<String, String>> rejeter(
        @PathVariable              Long   id,
        @RequestParam              String motif,
        @RequestHeader("X-User-Id") Long  validateurId) {

        collecteService.rejeter(id, motif, validateurId);
        return ResponseEntity.ok(Map.of("message", "Collecte rejetée"));
    }
}
