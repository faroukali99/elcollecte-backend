package com.elcollecte.projet.dto;

import com.elcollecte.projet.entity.Projet;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

// ── DTO de lecture ────────────────────────────────────────────────────────────
public record ProjetDto(
    Long                id,
    Long                organisationId,
    Long                responsableId,
    String              titre,
    String              description,
    String              statut,
    String              motifRejet,
    Map<String, Object> zoneGeo,
    LocalDate           dateDebut,
    LocalDate           dateFin,
    LocalDateTime       createdAt,
    int                 nbEnqueteurs,
    int                 nbMembres
) {
    public static ProjetDto from(Projet p) {
        return new ProjetDto(
            p.getId(),
            p.getOrganisationId(),
            p.getResponsableId(),
            p.getTitre(),
            p.getDescription(),
            p.getStatut().name(),
            p.getMotifRejet(),
            p.getZoneGeo(),
            p.getDateDebut(),
            p.getDateFin(),
            p.getCreatedAt(),
            (int) p.getEnqueteurs().stream().filter(e -> e.isActive()).count(),
            (int) p.getMembres().stream().filter(m -> m.isActive()).count()
        );
    }
}

