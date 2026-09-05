package com.elcollecte.formulaire.dto;

import com.elcollecte.formulaire.entity.Question;

import java.time.LocalDateTime;
import java.util.Map;

public record QuestionDto(
    Long id,
    Long sectionId,
    String libelle,
    String description,
    String type,
    Integer ordre,
    boolean required,
    Map<String, Object> options,
    Map<String, Object> validation,
    LocalDateTime createdAt
) {
    public static QuestionDto from(Question q) {
        return new QuestionDto(
            q.getId(),
            q.getSection() != null ? q.getSection().getId() : null,
            q.getLibelle(),
            q.getDescription(),
            q.getType().name(),
            q.getOrdre(),
            q.isRequired(),
            q.getOptions(),
            q.getValidation(),
            q.getCreatedAt()
        );
    }
}
