package com.elcollecte.cartographie.dto;

import com.elcollecte.cartographie.entity.PointInteret;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public record PointInteretDto(
    Long id,
    Long zoneId,
    String nom,
    String description,
    BigDecimal latitude,
    BigDecimal longitude,
    BigDecimal altitude,
    String type,
    Map<String, Object> properties,
    LocalDateTime createdAt
) {
    public static PointInteretDto from(PointInteret p) {
        return new PointInteretDto(
            p.getId(),
            p.getZoneId(),
            p.getNom(),
            p.getDescription(),
            p.getLatitude(),
            p.getLongitude(),
            p.getAltitude(),
            p.getType().name(),
            p.getProperties(),
            p.getCreatedAt()
        );
    }
}
