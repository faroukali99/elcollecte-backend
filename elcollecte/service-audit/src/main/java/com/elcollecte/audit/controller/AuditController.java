package com.elcollecte.audit.controller;

import com.elcollecte.audit.entity.AuditLog;
import com.elcollecte.audit.repository.AuditLogRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit")
@Tag(name = "Audit", description = "Journal immuable des actions utilisateurs")
public class AuditController {

    private final AuditLogRepository auditRepo;

    public AuditController(AuditLogRepository auditRepo) {
        this.auditRepo = auditRepo;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lister tous les logs (ADMIN uniquement)")
    public ResponseEntity<Page<AuditLog>> list(
            @RequestParam(required = false) Long   userId,
            @RequestParam(required = false) String action,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "50") int size) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());

        Page<AuditLog> result;
        if (userId != null) {
            result = auditRepo.findByUserId(userId, pageable);
        } else if (action != null) {
            result = auditRepo.findByAction(action, pageable);
        } else {
            result = auditRepo.findAll(pageable);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/ressources/{ressource}/{ressourceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF_PROJET')")
    @Operation(summary = "Historique d'une ressource spécifique")
    public ResponseEntity<Page<AuditLog>> byRessource(
            @PathVariable String ressource,
            @PathVariable Long   ressourceId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());
        return ResponseEntity.ok(
                auditRepo.findByRessourceAndRessourceId(ressource, ressourceId, pageable));
    }
}