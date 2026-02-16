// FormulaireDto.java
package com.elcollecte.formulaire.dto;

import com.elcollecte.formulaire.entity.Formulaire;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record FormulaireDto(
        Long          id,
        Long          projetId,
        Long          createurId,
        String        titre,
        String        description,
        List<Object>  schemaQuestions,
        int           version,
        boolean       active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static FormulaireDto from(Formulaire f) {
        return new FormulaireDto(
                f.getId(), f.getProjetId(), f.getCreateurId(),
                f.getTitre(), f.getDescription(), f.getSchemaQuestions(),
                f.getVersion(), f.isActive(), f.getCreatedAt(), f.getUpdatedAt()
        );
    }
}



