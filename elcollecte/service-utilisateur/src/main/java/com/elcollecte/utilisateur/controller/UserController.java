package com.elcollecte.utilisateur.controller;

import com.elcollecte.utilisateur.dto.AssignProfilRequest;
import com.elcollecte.utilisateur.dto.CreateUserRequest;
import com.elcollecte.utilisateur.dto.UserAdminDto;
import com.elcollecte.utilisateur.service.UserAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Gestion administrative des utilisateurs.
 *
 * Ce contrôleur n'existait pas dans le projet : jusqu'ici, seule
 * l'auto-inscription (AuthController#register) permettait de créer un
 * compte, sans aucun moyen pour un ADMIN de gérer les comptes d'autrui ni
 * de leur affecter un profil.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Utilisateurs", description = "Gestion administrative des utilisateurs et de leurs profils")
public class UserController {

    private final UserAdminService userAdminService;

    public UserController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PERM_USER_READ') or hasRole('ADMIN')")
    @Operation(summary = "Lister les utilisateurs de l'organisation courante")
    public ResponseEntity<Page<UserAdminDto>> list(
            @RequestHeader(value = "X-Org-Id", required = false) Long orgId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        Long effectiveOrgId = (orgId != null) ? orgId : 1L;
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(userAdminService.findAll(effectiveOrgId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_USER_READ') or hasRole('ADMIN')")
    @Operation(summary = "Détails d'un utilisateur")
    public ResponseEntity<UserAdminDto> getById(
            @PathVariable Long id,
            @RequestHeader(value = "X-Org-Id", required = false) Long orgId) {

        Long effectiveOrgId = (orgId != null) ? orgId : 1L;
        return ResponseEntity.ok(userAdminService.findById(id, effectiveOrgId));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERM_USER_CREATE') or hasRole('ADMIN')")
    @Operation(summary = "Créer un utilisateur et l'affecter directement à un profil")
    public ResponseEntity<UserAdminDto> create(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userAdminService.create(request));
    }

    @PatchMapping("/{id}/profil")
    @PreAuthorize("hasAuthority('PERM_USER_UPDATE') or hasRole('ADMIN')")
    @Operation(summary = "Associer un utilisateur à un profil")
    public ResponseEntity<UserAdminDto> assignProfil(
            @PathVariable Long id,
            @RequestHeader(value = "X-Org-Id", required = false) Long orgId,
            @Valid @RequestBody AssignProfilRequest request) {

        Long effectiveOrgId = (orgId != null) ? orgId : 1L;
        return ResponseEntity.ok(userAdminService.assignProfil(id, effectiveOrgId, request.profilId()));
    }

    @PatchMapping("/{id}/desactiver")
    @PreAuthorize("hasAuthority('PERM_USER_DELETE') or hasRole('ADMIN')")
    @Operation(summary = "Désactiver un utilisateur")
    public ResponseEntity<Map<String, String>> desactiver(
            @PathVariable Long id,
            @RequestHeader(value = "X-Org-Id", required = false) Long orgId) {

        Long effectiveOrgId = (orgId != null) ? orgId : 1L;
        userAdminService.desactiver(id, effectiveOrgId);
        return ResponseEntity.ok(Map.of("message", "Utilisateur désactivé"));
    }

    @PatchMapping("/{id}/reactiver")
    @PreAuthorize("hasAuthority('PERM_USER_UPDATE') or hasRole('ADMIN')")
    @Operation(summary = "Réactiver un utilisateur")
    public ResponseEntity<Map<String, String>> reactiver(
            @PathVariable Long id,
            @RequestHeader(value = "X-Org-Id", required = false) Long orgId) {

        Long effectiveOrgId = (orgId != null) ? orgId : 1L;
        userAdminService.reactiver(id, effectiveOrgId);
        return ResponseEntity.ok(Map.of("message", "Utilisateur réactivé"));
    }
}
