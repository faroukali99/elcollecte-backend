package com.elcollecte.utilisateur.dto;

import com.elcollecte.utilisateur.entity.Permission;

public record PermissionDto(
    Long id,
    String code,
    String libelle,
    String categorie,
    String description
) {
    public static PermissionDto from(Permission p) {
        return new PermissionDto(p.getId(), p.getCode(), p.getLibelle(),
            p.getCategorie(), p.getDescription());
    }
}
