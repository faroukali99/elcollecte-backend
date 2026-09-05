package com.elcollecte.synchronisation.dto;

import com.elcollecte.synchronisation.entity.SyncConflict;

import java.time.LocalDateTime;
import java.util.Map;

public record SyncConflictDto(
    Long id,
    Long userId,
    String entityType,
    Long entityId,
    Long localVersion,
    Long remoteVersion,
    Map<String, Object> localData,
    Map<String, Object> remoteData,
    String resolution,
    LocalDateTime resolvedAt,
    Long resolvedBy,
    LocalDateTime createdAt
) {
    public static SyncConflictDto from(SyncConflict conflict) {
        return new SyncConflictDto(
            conflict.getId(),
            conflict.getUserId(),
            conflict.getEntityType(),
            conflict.getEntityId(),
            conflict.getLocalVersion(),
            conflict.getRemoteVersion(),
            conflict.getLocalData(),
            conflict.getRemoteData(),
            conflict.getResolution().name(),
            conflict.getResolvedAt(),
            conflict.getResolvedBy(),
            conflict.getCreatedAt()
        );
    }
}
