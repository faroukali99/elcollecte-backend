package com.elcollecte.formulaire.dto;

import com.elcollecte.formulaire.entity.Formulaire;

import java.time.LocalDateTime;

public record FormulaireDto(
    Long id,
    String nom,
    String description,
    String code,
    Long organisationId,
    boolean actif,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    int nbVersions
) {
    public static FormulaireDto from(Formulaire f) {
        return new FormulaireDto(
            f.getId(),
            f.getNom(),
            f.getDescription(),
            f.getCode(),
            f.getOrganisation() != null ? f.getOrganisation().getId() : null,
            f.isActif(),
            f.getCreatedAt(),
            f.getUpdatedAt(),
            f.getVersions().size()
        );
    }
}
