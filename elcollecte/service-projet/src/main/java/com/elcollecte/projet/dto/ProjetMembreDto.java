package com.elcollecte.projet.dto;

import com.elcollecte.projet.entity.ProjetMembre;

import java.time.LocalDateTime;

public record ProjetMembreDto(
    Long                projetId,
    Long                userId,
    String              roleMembre,
    LocalDateTime       assignedAt,
    boolean             active
) {
    public static ProjetMembreDto from(ProjetMembre m) {
        return new ProjetMembreDto(
            m.getId().getProjetId(),
            m.getUserId(),
            m.getRoleMembre(),
            m.getAssignedAt(),
            m.isActive()
        );
    }
}
