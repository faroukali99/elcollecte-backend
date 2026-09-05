package com.elcollecte.utilisateur.dto;

import com.elcollecte.utilisateur.entity.Profil;

import java.util.List;

public record ProfilDto(
    Long id,
    String code,
    String libelle,
    String description,
    Long organisationId,
    boolean systeme,
    boolean active,
    List<PermissionDto> permissions
) {
    public static ProfilDto from(Profil profil) {
        List<PermissionDto> permissions = profil.getProfilPermissions().stream()
            .map(pp -> PermissionDto.from(pp.getPermission()))
            .toList();

        return new ProfilDto(
            profil.getId(),
            profil.getCode(),
            profil.getLibelle(),
            profil.getDescription(),
            profil.getOrganisation() != null ? profil.getOrganisation().getId() : null,
            profil.isSysteme(),
            profil.isActive(),
            permissions
        );
    }
}
