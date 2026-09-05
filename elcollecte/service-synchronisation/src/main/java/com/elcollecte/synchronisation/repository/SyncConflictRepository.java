package com.elcollecte.synchronisation.repository;

import com.elcollecte.synchronisation.entity.SyncConflict;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SyncConflictRepository extends JpaRepository<SyncConflict, Long> {

    List<SyncConflict> findByUserIdAndResolutionOrderByCreatedAtDesc(Long userId, SyncConflict.Resolution resolution);
}
