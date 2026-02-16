package com.elcollecte.utilisateur.controller;

import com.elcollecte.utilisateur.entity.User;
import com.elcollecte.utilisateur.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Utilisateurs", description = "Gestion des comptes et profils")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF_PROJET')")
    @Operation(summary = "Lister les utilisateurs de l'organisation")
    public ResponseEntity<Page<UserSummary>> list(
            @RequestHeader("X-Org-Id")     Long   orgId,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("nom").ascending());
        Page<User> users = role != null
                ? userRepository.findAllByOrganisationIdAndRole(
                orgId, User.Role.valueOf(role.toUpperCase()), pageable)
                : userRepository.findAllByOrganisationId(orgId, pageable);

        return ResponseEntity.ok(users.map(UserSummary::from));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Profil d'un utilisateur")
    public ResponseEntity<UserSummary> getById(
            @PathVariable              Long id,
            @RequestHeader("X-Org-Id") Long orgId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Utilisateur introuvable: " + id));
        return ResponseEntity.ok(UserSummary.from(user));
    }

    @GetMapping("/me")
    @Operation(summary = "Profil de l'utilisateur connecté")
    public ResponseEntity<UserSummary> me(@RequestHeader("X-User-Id") Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Utilisateur introuvable"));
        return ResponseEntity.ok(UserSummary.from(user));
    }

    @PutMapping("/{id}/desactiver")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Désactiver un compte (ADMIN)")
    public ResponseEntity<Map<String, String>> desactiver(@PathVariable Long id) {
        userRepository.updateActiveStatus(id, false);
        return ResponseEntity.ok(Map.of("message", "Compte désactivé"));
    }

    @PutMapping("/{id}/activer")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Réactiver un compte (ADMIN)")
    public ResponseEntity<Map<String, String>> activer(@PathVariable Long id) {
        userRepository.updateActiveStatus(id, true);
        return ResponseEntity.ok(Map.of("message", "Compte activé"));
    }

    // ── DTO interne ──────────────────────────────────────────────────────────
    public record UserSummary(
            Long   id, String nom, String prenom, String email,
            String role, Long organisationId, boolean active
    ) {
        public static UserSummary from(User u) {
            return new UserSummary(
                    u.getId(), u.getNom(), u.getPrenom(), u.getEmail(),
                    u.getRole().name(),
                    u.getOrganisation() != null ? u.getOrganisation().getId() : null,
                    u.isActive()
            );
        }
    }
}