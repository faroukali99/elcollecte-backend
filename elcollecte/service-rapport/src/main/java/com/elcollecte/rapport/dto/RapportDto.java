package com.elcollecte.rapport.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RapportDto(
    Long id,
    UUID uuid,
    Long projetId,
    Long formulaireId,
    String type,
    String titre,
    String format,
    String parametres,
    String cheminFichier,
    String statut,
    String erreurMessage,
    Long creePar,
    LocalDateTime creeAt,
    LocalDateTime genereAt
) {
    public static RapportDto from(com.elcollecte.rapport.entity.Rapport r) {
        return new RapportDto(
            r.getId(),
            r.getUuid(),
            r.getProjetId(),
            r.getFormulaireId(),
            r.getType(),
            r.getTitre(),
            r.getFormat(),
            r.getParametres(),
            r.getCheminFichier(),
            r.getStatut(),
            r.getErreurMessage(),
            r.getCreePar(),
            r.getCreeAt(),
            r.getGenereAt()
        );
    }
}
