package com.elcollecte.mission.dto;

import com.elcollecte.mission.entity.Mission;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MissionDto(
    Long id,
    Long projetId,
    Long formulaireId,
    Long formulaireVersionId,
    Long zoneId,
    String titre,
    String description,
    String objectif,
    LocalDate dateDebut,
    LocalDate dateFin,
    String statut,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    int nbEnqueteurs
) {
    public static MissionDto from(Mission m) {
        return new MissionDto(
            m.getId(),
            m.getProjetId(),
            m.getFormulaireId(),
            m.getFormulaireVersionId(),
            m.getZoneId(),
            m.getTitre(),
            m.getDescription(),
            m.getObjectif(),
            m.getDateDebut(),
            m.getDateFin(),
            m.getStatut().name(),
            m.getCreatedAt(),
            m.getUpdatedAt(),
            (int) m.getEnqueteurs().stream().filter(e -> e.isActive()).count()
        );
    }
}
