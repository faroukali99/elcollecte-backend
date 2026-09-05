package com.elcollecte.mission.repository;

import com.elcollecte.mission.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MissionRepository extends JpaRepository<Mission, Long> {

    List<Mission> findByProjetId(Long projetId);

    @Query("SELECT m FROM Mission m LEFT JOIN m.enqueteurs me WHERE m.projetId = :projetId AND (me.enqueteurId = :userId OR m.statut = 'PLANIFIEE')")
    List<Mission> findAccessibleByProjetAndUser(Long projetId, Long userId);

    @Query("SELECT m FROM Mission m WHERE m.statut = :statut")
    List<Mission> findByStatut(Mission.Statut statut);
}
