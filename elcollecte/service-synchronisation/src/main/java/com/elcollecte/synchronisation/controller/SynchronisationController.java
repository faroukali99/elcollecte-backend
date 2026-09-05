package com.elcollecte.synchronisation.controller;

import com.elcollecte.synchronisation.dto.SyncConflictDto;
import com.elcollecte.synchronisation.dto.SyncLogDto;
import com.elcollecte.synchronisation.entity.SyncLog;
import com.elcollecte.synchronisation.entity.SyncConflict;
import com.elcollecte.synchronisation.service.SynchronisationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/synchronisation")
@Tag(name = "Synchronisation", description = "Gestion de la synchronisation offline/online")
public class SynchronisationController {

    private final SynchronisationService synchronisationService;

    public SynchronisationController(SynchronisationService synchronisationService) {
        this.synchronisationService = synchronisationService;
    }

    @PostMapping("/start")
    @Operation(summary = "Démarrer une synchronisation")
    public ResponseEntity<SyncLogDto> startSync(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader(value = "X-Device-Id", required = false) String deviceId,
            @RequestParam SyncLog.Direction direction) {
        return ResponseEntity.ok(synchronisationService.startSync(userId, deviceId, direction));
    }

    @PostMapping("/complete/{logId}")
    @Operation(summary = "Terminer une synchronisation")
    public ResponseEntity<SyncLogDto> completeSync(
            @PathVariable Long logId,
            @RequestParam SyncLog.Statut statut,
            @RequestParam int itemsCount,
            @RequestParam int errorsCount,
            @RequestBody(required = false) Map<String, Object> details) {
        return ResponseEntity.ok(synchronisationService.completeSync(logId, statut, itemsCount, errorsCount, details));
    }

    @GetMapping("/history")
    @Operation(summary = "Historique des synchronisations de l'utilisateur")
    public ResponseEntity<List<SyncLogDto>> getUserSyncHistory(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(synchronisationService.getUserSyncHistory(userId));
    }

    @PostMapping("/conflicts")
    @Operation(summary = "Créer un conflit de synchronisation")
    public ResponseEntity<SyncConflictDto> createConflict(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam String entityType,
            @RequestParam Long entityId,
            @RequestParam Long localVersion,
            @RequestParam Long remoteVersion,
            @RequestBody Map<String, Object> localData,
            @RequestBody Map<String, Object> remoteData) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(synchronisationService.createConflict(userId, entityType, entityId, localVersion, remoteVersion, localData, remoteData));
    }

    @GetMapping("/conflicts/pending")
    @Operation(summary = "Conflits en attente de résolution")
    public ResponseEntity<List<SyncConflictDto>> getPendingConflicts(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(synchronisationService.getPendingConflicts(userId));
    }

    @PostMapping("/conflicts/{conflictId}/resolve")
    @Operation(summary = "Résoudre un conflit")
    public ResponseEntity<SyncConflictDto> resolveConflict(
            @PathVariable Long conflictId,
            @RequestParam SyncConflict.Resolution resolution,
            @RequestHeader("X-User-Id") Long resolvedBy) {
        return ResponseEntity.ok(synchronisationService.resolveConflict(conflictId, resolution, resolvedBy));
    }
}
