package com.elcollecte.utilisateur.controller;

import com.elcollecte.utilisateur.dto.AssignPermissionsRequest;
import com.elcollecte.utilisateur.dto.CreateProfilRequest;
import com.elcollecte.utilisateur.dto.ProfilDto;
import com.elcollecte.utilisateur.dto.UpdateProfilRequest;
import com.elcollecte.utilisateur.service.ProfilService;
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

@RestController
@RequestMapping("/api/profils")
@Tag(name = "Profils", description = "Gestion dynamique des profils (remplace progressivement l'enum Role)")
public class ProfilController {

    private final ProfilService profilService;

    public ProfilController(ProfilService profilService) {
        this.profilService = profilService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PERM_PROFILE_READ') or hasRole('ADMIN')")
    @Operation(summary = "Lister les profils (globaux + ceux de l'organisation courante)")
    public ResponseEntity<Page<ProfilDto>> list(
            @RequestHeader(value = "X-Org-Id", required = false) Long orgId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("libelle").ascending());
        return ResponseEntity.ok(profilService.findAll(orgId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_PROFILE_READ') or hasRole('ADMIN')")
    @Operation(summary = "Détails d'un profil, avec ses permissions")
    public ResponseEntity<ProfilDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(profilService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERM_PROFILE_CREATE') or hasRole('ADMIN')")
    @Operation(summary = "Créer un nouveau profil")
    public ResponseEntity<ProfilDto> create(@Valid @RequestBody CreateProfilRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(profilService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_PROFILE_UPDATE') or hasRole('ADMIN')")
    @Operation(summary = "Modifier un profil (libellé, description)")
    public ResponseEntity<ProfilDto> update(@PathVariable Long id,
                                            @Valid @RequestBody UpdateProfilRequest request) {
        return ResponseEntity.ok(profilService.update(id, request));
    }

    @PatchMapping("/{id}/desactiver")
    @PreAuthorize("hasAuthority('PERM_PROFILE_UPDATE') or hasRole('ADMIN')")
    @Operation(summary = "Désactiver un profil (interdit sur un profil système)")
    public ResponseEntity<Map<String, String>> desactiver(@PathVariable Long id) {
        profilService.desactiver(id);
        return ResponseEntity.ok(Map.of("message", "Profil désactivé"));
    }

    @PatchMapping("/{id}/reactiver")
    @PreAuthorize("hasAuthority('PERM_PROFILE_UPDATE') or hasRole('ADMIN')")
    @Operation(summary = "Réactiver un profil")
    public ResponseEntity<Map<String, String>> reactiver(@PathVariable Long id) {
        profilService.reactiver(id);
        return ResponseEntity.ok(Map.of("message", "Profil réactivé"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_PROFILE_DELETE') or hasRole('ADMIN')")
    @Operation(summary = "Supprimer un profil (interdit sur un profil système ou utilisé)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        profilService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('PERM_PROFILE_UPDATE') or hasRole('ADMIN')")
    @Operation(summary = "Associer une ou plusieurs permissions à un profil")
    public ResponseEntity<ProfilDto> assignPermissions(@PathVariable Long id,
                                                       @Valid @RequestBody AssignPermissionsRequest request) {
        return ResponseEntity.ok(profilService.assignPermissions(id, request));
    }

    @DeleteMapping("/{id}/permissions/{permissionId}")
    @PreAuthorize("hasAuthority('PERM_PROFILE_UPDATE') or hasRole('ADMIN')")
    @Operation(summary = "Retirer une permission d'un profil")
    public ResponseEntity<Void> removePermission(@PathVariable Long id,
                                                 @PathVariable Long permissionId) {
        profilService.removePermission(id, permissionId);
        return ResponseEntity.noContent().build();
    }
}
