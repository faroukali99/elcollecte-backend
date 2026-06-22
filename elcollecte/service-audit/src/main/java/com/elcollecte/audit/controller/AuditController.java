package com.elcollecte.audit.controller;

import com.elcollecte.audit.entity.AuditLog;
import com.elcollecte.audit.repository.AuditLogRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
@Tag(name = "Audit", description = "Journal d'audit des opérations")
public class AuditController {

    private final AuditLogRepository auditLogRepository;

    public AuditController(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping
    @Operation(summary = "Lister tous les logs d'audit")
    public ResponseEntity<Page<AuditLog>> findAll(Pageable pageable) {
        return ResponseEntity.ok(auditLogRepository.findAll(pageable));
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    @Operation(summary = "Logs pour une entité spécifique")
    public ResponseEntity<List<AuditLog>> findByEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        return ResponseEntity.ok(auditLogRepository.findByRessourceAndRessourceId(entityType, entityId));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Logs pour un utilisateur spécifique")
    public ResponseEntity<List<AuditLog>> findByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(auditLogRepository.findByUserId(userId));
    }

    @GetMapping("/since/{date}")
    @Operation(summary = "Logs depuis une date")
    public ResponseEntity<List<AuditLog>> findSince(@PathVariable LocalDateTime date) {
        return ResponseEntity.ok(auditLogRepository.findByCreatedAtAfter(date));
    }
}
