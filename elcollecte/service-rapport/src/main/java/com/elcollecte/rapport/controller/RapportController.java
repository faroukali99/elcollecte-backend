package com.elcollecte.rapport.controller;

import com.elcollecte.rapport.dto.CreateRapportRequest;
import com.elcollecte.rapport.dto.RapportDto;
import com.elcollecte.rapport.service.RapportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rapports")
@Tag(name = "Rapports", description = "Génération et gestion des rapports")
public class RapportController {

    private final RapportService rapportService;

    public RapportController(RapportService rapportService) {
        this.rapportService = rapportService;
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau rapport")
    public ResponseEntity<RapportDto> create(
            @Valid @RequestBody CreateRapportRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(rapportService.create(request, userId));
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Récupérer un rapport par UUID")
    public ResponseEntity<RapportDto> findByUuid(@PathVariable UUID uuid) {
        return ResponseEntity.ok(rapportService.findByUuid(uuid));
    }

    @GetMapping
    @Operation(summary = "Lister tous les rapports")
    public ResponseEntity<List<RapportDto>> findAll() {
        return ResponseEntity.ok(rapportService.findAll());
    }

    @GetMapping("/projet/{projetId}")
    @Operation(summary = "Lister les rapports d'un projet")
    public ResponseEntity<List<RapportDto>> findByProjetId(@PathVariable Long projetId) {
        return ResponseEntity.ok(rapportService.findByProjetId(projetId));
    }

    @GetMapping("/mes-rapports")
    @Operation(summary = "Lister mes rapports")
    public ResponseEntity<List<RapportDto>> findMyRapports(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(rapportService.findByCreePar(userId));
    }
}
