package com.elcollecte.formulaire.dto;

import com.elcollecte.formulaire.entity.Section;

import java.time.LocalDateTime;

public record SectionDto(
    Long id,
    Long versionId,
    String titre,
    String description,
    Integer ordre,
    boolean repeatable,
    LocalDateTime createdAt
) {
    public static SectionDto from(Section s) {
        return new SectionDto(
            s.getId(),
            s.getVersion() != null ? s.getVersion().getId() : null,
            s.getTitre(),
            s.getDescription(),
            s.getOrdre(),
            s.isRepeatable(),
            s.getCreatedAt()
        );
    }
}
