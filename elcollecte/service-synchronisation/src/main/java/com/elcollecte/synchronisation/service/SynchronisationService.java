package com.elcollecte.synchronisation.service;

import com.elcollecte.synchronisation.dto.SyncConflictDto;
import com.elcollecte.synchronisation.dto.SyncLogDto;
import com.elcollecte.synchronisation.entity.SyncConflict;
import com.elcollecte.synchronisation.entity.SyncLog;
import com.elcollecte.synchronisation.repository.SyncConflictRepository;
import com.elcollecte.synchronisation.repository.SyncLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class SynchronisationService {

    private final SyncLogRepository syncLogRepository;
    private final SyncConflictRepository syncConflictRepository;

    public SynchronisationService(SyncLogRepository syncLogRepository,
                                 SyncConflictRepository syncConflictRepository) {
        this.syncLogRepository = syncLogRepository;
        this.syncConflictRepository = syncConflictRepository;
    }

    @Transactional
    public SyncLogDto startSync(Long userId, String deviceId, SyncLog.Direction direction) {
        SyncLog log = new SyncLog();
        log.setUserId(userId);
        log.setDeviceId(deviceId);
        log.setDirection(direction);
        log.setStatut(SyncLog.Statut.EN_COURS);
        return SyncLogDto.from(syncLogRepository.save(log));
    }

    @Transactional
    public SyncLogDto completeSync(Long logId, SyncLog.Statut statut, int itemsCount, int errorsCount, Map<String, Object> details) {
        SyncLog log = syncLogRepository.findById(logId)
            .orElseThrow(() -> new IllegalArgumentException("Sync log introuvable"));
        
        log.setStatut(statut);
        log.setItemsCount(itemsCount);
        log.setErrorsCount(errorsCount);
        log.setDetails(details);
        log.setCompletedAt(LocalDateTime.now());
        
        return SyncLogDto.from(syncLogRepository.save(log));
    }

    @Transactional(readOnly = true)
    public List<SyncLogDto> getUserSyncHistory(Long userId) {
        return syncLogRepository.findByUserIdOrderByStartedAtDesc(userId).stream()
            .map(SyncLogDto::from)
            .toList();
    }

    @Transactional
    public SyncConflictDto createConflict(Long userId, String entityType, Long entityId,
                                          Long localVersion, Long remoteVersion,
                                          Map<String, Object> localData, Map<String, Object> remoteData) {
        SyncConflict conflict = new SyncConflict();
        conflict.setUserId(userId);
        conflict.setEntityType(entityType);
        conflict.setEntityId(entityId);
        conflict.setLocalVersion(localVersion);
        conflict.setRemoteVersion(remoteVersion);
        conflict.setLocalData(localData);
        conflict.setRemoteData(remoteData);
        conflict.setResolution(SyncConflict.Resolution.PENDING);
        
        return SyncConflictDto.from(syncConflictRepository.save(conflict));
    }

    @Transactional(readOnly = true)
    public List<SyncConflictDto> getPendingConflicts(Long userId) {
        return syncConflictRepository.findByUserIdAndResolutionOrderByCreatedAtDesc(userId, SyncConflict.Resolution.PENDING).stream()
            .map(SyncConflictDto::from)
            .toList();
    }

    @Transactional
    public SyncConflictDto resolveConflict(Long conflictId, SyncConflict.Resolution resolution, Long resolvedBy) {
        SyncConflict conflict = syncConflictRepository.findById(conflictId)
            .orElseThrow(() -> new IllegalArgumentException("Conflict introuvable"));
        
        conflict.setResolution(resolution);
        conflict.setResolvedBy(resolvedBy);
        conflict.setResolvedAt(LocalDateTime.now());
        
        return SyncConflictDto.from(syncConflictRepository.save(conflict));
    }
}
