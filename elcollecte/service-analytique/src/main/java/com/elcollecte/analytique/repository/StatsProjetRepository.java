package com.elcollecte.analytique.repository;

import com.elcollecte.analytique.entity.StatsProjet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatsProjetRepository extends JpaRepository<StatsProjet, Long> {
    Optional<StatsProjet> findByProjetId(Long projetId);
    boolean existsByProjetId(Long projetId);
}
