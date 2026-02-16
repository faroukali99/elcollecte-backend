package com.elcollecte.projet.controller;

import com.elcollecte.projet.dto.CreateProjetRequest;
import com.elcollecte.projet.dto.ProjetDto;
import com.elcollecte.projet.dto.UpdateProjetRequest;
import com.elcollecte.projet.service.ProjetService;
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

import java.util.Map;

@RestController
@RequestMapping("/api/projets")
@Tag(name = "Projets", description = "Gestion des projets de collecte")
public class ProjetController {

    private final ProjetService projetService;

    public ProjetController(ProjetService projetService) {
        this.projetService = projetService;
    }

    @GetMapping
    @Operation(summary = "Lister les projets accessibles")
    public ResponseEntity<Page<ProjetDto>> list(
        @RequestHeader("X-User-Id")   Long   userId,
        @RequestHeader("X-User-Role") String role,
        @RequestHeader("X-Org-Id")    Long   orgId,
        @RequestParam(defaultValue = "0")  int page,
        @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size,
            Sort.by("createdAt").descending());
        return ResponseEntity.ok(projetService.findAll(userId, role, orgId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détails d'un projet")
    public ResponseEntity<ProjetDto> getById(
        @PathVariable           Long id,
        @RequestHeader("X-Org-Id") Long orgId) {

        return ResponseEntity.ok(projetService.findById(id, orgId));
    }

    @PostMapping
    @Operation(summary = "Créer un projet")
    public ResponseEntity<ProjetDto> create(
        @Valid @RequestBody           CreateProjetRequest request,
        @RequestHeader("X-User-Id")   Long               userId,
        @RequestHeader("X-Org-Id")    Long               orgId) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(projetService.create(request, userId, orgId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un projet")
    public ResponseEntity<ProjetDto> update(
        @PathVariable                 Long               id,
        @Valid @RequestBody           UpdateProjetRequest request,
        @RequestHeader("X-Org-Id")    Long               orgId) {

        return ResponseEntity.ok(projetService.update(id, request, orgId));
    }

    @PostMapping("/{id}/enqueteurs")
    @Operation(summary = "Attribuer un enquêteur au projet")
    public ResponseEntity<Map<String, String>> addEnqueteur(
        @PathVariable              Long id,
        @RequestParam              Long enqueteurId,
        @RequestHeader("X-Org-Id") Long orgId) {

        projetService.addEnqueteur(id, enqueteurId, orgId);
        return ResponseEntity.ok(Map.of("message", "Enquêteur ajouté"));
    }

    @DeleteMapping("/{id}/enqueteurs/{enqueteurId}")
    @Operation(summary = "Retirer un enquêteur du projet")
    public ResponseEntity<Void> removeEnqueteur(
        @PathVariable              Long id,
        @PathVariable              Long enqueteurId,
        @RequestHeader("X-Org-Id") Long orgId) {

        projetService.removeEnqueteur(id, enqueteurId, orgId);
        return ResponseEntity.noContent().build();
    }
}
