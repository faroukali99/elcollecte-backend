package com.elcollecte.utilisateur.dto;

import com.elcollecte.utilisateur.entity.User;

public record UserAdminDto(
    Long id,
    String nom,
    String prenom,
    String email,
    String role,          // legacy — conservé pour compat/affichage
    Long profilId,
    String profilCode,
    String profilLibelle,
    boolean active,
    Long organisationId
) {
    public static UserAdminDto from(User user) {
        return new UserAdminDto(
            user.getId(),
            user.getNom(),
            user.getPrenom(),
            user.getEmail(),
            user.getRole() != null ? user.getRole().name() : null,
            user.getProfil() != null ? user.getProfil().getId() : null,
            user.getProfil() != null ? user.getProfil().getCode() : null,
            user.getProfil() != null ? user.getProfil().getLibelle() : null,
            user.isActive(),
            user.getOrganisation() != null ? user.getOrganisation().getId() : null
        );
    }
}
