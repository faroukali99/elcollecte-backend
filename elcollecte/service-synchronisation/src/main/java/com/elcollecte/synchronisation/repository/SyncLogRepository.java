package com.elcollecte.synchronisation.repository;

import com.elcollecte.synchronisation.entity.SyncLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SyncLogRepository extends JpaRepository<SyncLog, Long> {

    List<SyncLog> findByUserIdOrderByStartedAtDesc(Long userId);

    List<SyncLog> findByUserIdAndStartedAtAfter(Long userId, LocalDateTime since);
}
