package com.elcollecte.formulaire.dto;

import com.elcollecte.formulaire.entity.FormulaireVersion;

import java.time.LocalDateTime;

public record FormulaireVersionDto(
    Long id,
    Long formulaireId,
    Integer numeroVersion,
    String description,
    boolean publie,
    boolean active,
    LocalDateTime publishedAt,
    LocalDateTime createdAt
) {
    public static FormulaireVersionDto from(FormulaireVersion v) {
        return new FormulaireVersionDto(
            v.getId(),
            v.getFormulaire() != null ? v.getFormulaire().getId() : null,
            v.getNumeroVersion(),
            v.getDescription(),
            v.isPublie(),
            v.isActive(),
            v.getPublishedAt(),
            v.getCreatedAt()
        );
    }
}
