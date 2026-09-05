package com.elcollecte.mission.controller;

import com.elcollecte.mission.dto.CreateMissionRequest;
import com.elcollecte.mission.dto.MissionDto;
import com.elcollecte.mission.dto.UpdateMissionRequest;
import com.elcollecte.mission.service.MissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/missions")
@Tag(name = "Missions", description = "Gestion des missions terrain")
public class MissionController {

    private final MissionService missionService;

    public MissionController(MissionService missionService) {
        this.missionService = missionService;
    }

    @PostMapping
    @Operation(summary = "Créer une mission")
    public ResponseEntity<MissionDto> create(@Valid @RequestBody CreateMissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(missionService.create(request));
    }

    @GetMapping("/projet/{projetId}")
    @Operation(summary = "Lister les missions d'un projet")
    public ResponseEntity<List<MissionDto>> findByProjet(@PathVariable Long projetId) {
        return ResponseEntity.ok(missionService.findByProjet(projetId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détails d'une mission")
    public ResponseEntity<MissionDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(missionService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une mission")
    public ResponseEntity<MissionDto> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMissionRequest request) {
        return ResponseEntity.ok(missionService.update(id, request));
    }

    @PostMapping("/{id}/enqueteurs/{enqueteurId}")
    @Operation(summary = "Affecter un enquêteur à une mission")
    public ResponseEntity<Void> addEnqueteur(
            @PathVariable Long id,
            @PathVariable Long enqueteurId) {
        missionService.addEnqueteur(id, enqueteurId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/enqueteurs/{enqueteurId}")
    @Operation(summary = "Retirer un enquêteur d'une mission")
    public ResponseEntity<Void> removeEnqueteur(
            @PathVariable Long id,
            @PathVariable Long enqueteurId) {
        missionService.removeEnqueteur(id, enqueteurId);
        return ResponseEntity.noContent().build();
    }
}
