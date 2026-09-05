package com.elcollecte.utilisateur.controller;

import com.elcollecte.utilisateur.dto.CreatePermissionRequest;
import com.elcollecte.utilisateur.dto.PermissionDto;
import com.elcollecte.utilisateur.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@Tag(name = "Permissions", description = "Catalogue des permissions disponibles")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PERM_PROFILE_READ') or hasRole('ADMIN')")
    @Operation(summary = "Lister toutes les permissions, éventuellement filtrées par catégorie")
    public ResponseEntity<List<PermissionDto>> list(
            @RequestParam(required = false) String categorie) {

        List<PermissionDto> result = (categorie != null && !categorie.isBlank())
            ? permissionService.findByCategorie(categorie.toUpperCase())
            : permissionService.findAll();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERM_PROFILE_CREATE') or hasRole('ADMIN')")
    @Operation(summary = "Créer une nouvelle permission")
    public ResponseEntity<PermissionDto> create(@Valid @RequestBody CreatePermissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(permissionService.create(request));
    }
}
