package com.elcollecte.analytique.controller;

import com.elcollecte.analytique.entity.StatsProjet;
import com.elcollecte.analytique.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Analytics", description = "KPIs et statistiques des projets")
public class AnalytiqueController {

    private final StatsService statsService;

    public AnalytiqueController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Dashboard global avec KPIs agrégés")
    public ResponseEntity<Map<String, Object>> dashboard(
            @RequestHeader("X-User-Id")   Long   userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Org-Id")    Long   orgId) {
        return ResponseEntity.ok(statsService.getDashboard(orgId, userId, role));
    }

    @GetMapping("/projets/{projetId}")
    @Operation(summary = "Statistiques détaillées d'un projet")
    public ResponseEntity<StatsProjet> getByProjet(@PathVariable Long projetId) {
        return ResponseEntity.ok(statsService.getByProjet(projetId));
    }

    @GetMapping("/projets/{projetId}/timeline")
    @Operation(summary = "Évolution temporelle des collectes (N derniers jours)")
    public ResponseEntity<List<Map<String, Object>>> timeline(
            @PathVariable Long projetId,
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(statsService.getTimeline(projetId, days));
    }
}