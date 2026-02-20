package com.elcollecte.analytique.controller;

import com.elcollecte.analytique.entity.StatsProjet;
import com.elcollecte.analytique.service.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalytiqueController {

    private final StatsService statsService;

    public AnalytiqueController(StatsService statsService) {
        this.statsService = statsService;
    }

    /**
     * Tableau de bord global (admin / coordinateur)
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATEUR')")
    public ResponseEntity<Map<String, Object>> getDashboard(
            @RequestHeader(value = "X-Org-Id",    required = false) Long orgId,
            @RequestHeader(value = "X-User-Id",   required = false) Long userId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        return ResponseEntity.ok(statsService.getDashboard(orgId, userId, role));
    }

    /**
     * Stats d'un projet spécifique
     */
    @GetMapping("/projets/{projetId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATEUR', 'SUPERVISEUR')")
    public ResponseEntity<StatsProjet> getStatsByProjet(@PathVariable Long projetId) {
        return ResponseEntity.ok(statsService.getByProjet(projetId));
    }

    /**
     * Timeline (courbe journalière) d'un projet
     * @param days  nombre de jours en arrière (défaut : 30)
     */
    @GetMapping("/projets/{projetId}/timeline")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATEUR', 'SUPERVISEUR')")
    public ResponseEntity<List<Map<String, Object>>> getTimeline(
            @PathVariable Long projetId,
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(statsService.getTimeline(projetId, days));
    }

    /**
     * Timeline globale toutes projets confondus
     */
    @GetMapping("/timeline")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATEUR')")
    public ResponseEntity<List<Map<String, Object>>> getGlobalTimeline(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(statsService.getTimeline(null, days));
    }
}
