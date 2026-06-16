package com.elcollecte.rapport.repository;

import com.elcollecte.rapport.entity.Rapport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RapportRepository extends JpaRepository<Rapport, Long> {

    Optional<Rapport> findByUuid(UUID uuid);

    List<Rapport> findByProjetId(Long projetId);

    List<Rapport> findByCreePar(Long creePar);

    @Query("SELECT r FROM Rapport r WHERE r.statut = :statut")
    List<Rapport> findByStatut(String statut);

    @Query("SELECT r FROM Rapport r WHERE r.projetId = :projetId AND r.statut = :statut")
    List<Rapport> findByProjetIdAndStatut(Long projetId, String statut);
}
