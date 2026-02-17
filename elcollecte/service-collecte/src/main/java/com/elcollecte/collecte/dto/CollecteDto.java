package com.elcollecte.collecte.dto;

import com.elcollecte.collecte.entity.CollecteData;
import jakarta.validation.constraints.NotNull;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// ── DTO de lecture ────────────────────────────────────────────────────────────
public record CollecteDto(
    Long                  id,
    UUID                  uuid,
    Long                  formulaireId,
    Long                  enqueteurId,
    Long                  projetId,
    Map<String, Object>   donnees,
    Double                 latitude,
    Double                longitude,
    String                statut,
    boolean               offline,
    LocalDateTime         collectedAt,
    LocalDateTime         syncedAt
) {
    public static CollecteDto from(CollecteData c) {
        return new CollecteDto(
            c.getId(), c.getUuid(),
            c.getFormulaireId(), c.getEnqueteurId(), c.getProjetId(),
            c.getDonnees(),
            c.getLatitude() != null ? c.getLatitude().doubleValue() : null,
            c.getLongitude() != null ? c.getLongitude().doubleValue() : null,
            c.getStatut().name(), c.isOffline(),
            c.getCollectedAt(), c.getSyncedAt()
        );
    }
}

