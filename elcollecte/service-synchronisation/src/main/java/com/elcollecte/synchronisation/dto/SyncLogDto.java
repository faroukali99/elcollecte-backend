package com.elcollecte.synchronisation.dto;

import com.elcollecte.synchronisation.entity.SyncLog;

import java.time.LocalDateTime;
import java.util.Map;

public record SyncLogDto(
    Long id,
    Long userId,
    String deviceId,
    String direction,
    String statut,
    Map<String, Object> details,
    Integer itemsCount,
    Integer errorsCount,
    LocalDateTime startedAt,
    LocalDateTime completedAt
) {
    public static SyncLogDto from(SyncLog log) {
        return new SyncLogDto(
            log.getId(),
            log.getUserId(),
            log.getDeviceId(),
            log.getDirection().name(),
            log.getStatut().name(),
            log.getDetails(),
            log.getItemsCount(),
            log.getErrorsCount(),
            log.getStartedAt(),
            log.getCompletedAt()
        );
    }
}
