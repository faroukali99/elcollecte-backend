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
            @RequestHeader("X-User-Id")                            Long   userId,
            @RequestHeader("X-User-Role")                          String role,
            @RequestHeader(value = "X-Org-Id", required = false)   Long   orgId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        Long effectiveOrgId = (orgId != null) ? orgId : 1L;
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(projetService.findAll(userId, role, effectiveOrgId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détails d'un projet")
    public ResponseEntity<ProjetDto> getById(
            @PathVariable                                          Long id,
            @RequestHeader(value = "X-Org-Id", required = false)  Long orgId) {

        Long effectiveOrgId = (orgId != null) ? orgId : 1L;
        return ResponseEntity.ok(projetService.findById(id, effectiveOrgId));
    }

    @PostMapping
    @Operation(summary = "Créer un projet")
    public ResponseEntity<ProjetDto> create(
            @Valid @RequestBody                                    CreateProjetRequest request,
            @RequestHeader("X-User-Id")                           Long               userId,
            @RequestHeader(value = "X-Org-Id", required = false)  Long               orgId) {

        Long effectiveOrgId = (orgId != null) ? orgId : 1L;
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projetService.create(request, userId, effectiveOrgId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un projet")
    public ResponseEntity<ProjetDto> update(
            @PathVariable                                         Long               id,
            @Valid @RequestBody                                   UpdateProjetRequest request,
            @RequestHeader(value = "X-Org-Id", required = false) Long               orgId) {

        Long effectiveOrgId = (orgId != null) ? orgId : 1L;
        return ResponseEntity.ok(projetService.update(id, request, effectiveOrgId));
    }

    @PostMapping("/{id}/enqueteurs")
    @Operation(summary = "Attribuer un enquêteur au projet")
    public ResponseEntity<Map<String, String>> addEnqueteur(
            @PathVariable                                         Long id,
            @RequestParam                                         Long enqueteurId,
            @RequestHeader(value = "X-Org-Id", required = false) Long orgId) {

        Long effectiveOrgId = (orgId != null) ? orgId : 1L;
        projetService.addEnqueteur(id, enqueteurId, effectiveOrgId);
        return ResponseEntity.ok(Map.of("message", "Enquêteur ajouté"));
    }

    @DeleteMapping("/{id}/enqueteurs/{enqueteurId}")
    @Operation(summary = "Retirer un enquêteur du projet")
    public ResponseEntity<Void> removeEnqueteur(
            @PathVariable                                         Long id,
            @PathVariable                                         Long enqueteurId,
            @RequestHeader(value = "X-Org-Id", required = false) Long orgId) {

        Long effectiveOrgId = (orgId != null) ? orgId : 1L;
        projetService.removeEnqueteur(id, enqueteurId, effectiveOrgId);
        return ResponseEntity.noContent().build();
    }
}
