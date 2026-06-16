package com.elcollecte.media.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record MediaDto(
    Long id,
    UUID uuid,
    String nomFichier,
    String nomOriginal,
    String typeMime,
    Long tailleOctets,
    String cheminStockage,
    String urlAcces,
    Long collecteId,
    Long projetId,
    Long uploadePar,
    LocalDateTime uploadeAt,
    String metadonnees
) {
    public static MediaDto from(com.elcollecte.media.entity.Media m) {
        return new MediaDto(
            m.getId(),
            m.getUuid(),
            m.getNomFichier(),
            m.getNomOriginal(),
            m.getTypeMime(),
            m.getTailleOctets(),
            m.getCheminStockage(),
            m.getUrlAcces(),
            m.getCollecteId(),
            m.getProjetId(),
            m.getUploadePar(),
            m.getUploadeAt(),
            m.getMetadonnees()
        );
    }
}
