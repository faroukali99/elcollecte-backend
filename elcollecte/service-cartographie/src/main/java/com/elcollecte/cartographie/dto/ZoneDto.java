package com.elcollecte.cartographie.dto;

import com.elcollecte.cartographie.entity.Zone;

import java.time.LocalDateTime;
import java.util.Map;

public record ZoneDto(
    Long id,
    String nom,
    String description,
    Long projetId,
    String type,
    Map<String, Object> properties,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static ZoneDto from(Zone z) {
        return new ZoneDto(
            z.getId(),
            z.getNom(),
            z.getDescription(),
            z.getProjetId(),
            z.getType().name(),
            z.getProperties(),
            z.getCreatedAt(),
            z.getUpdatedAt()
        );
    }
}
