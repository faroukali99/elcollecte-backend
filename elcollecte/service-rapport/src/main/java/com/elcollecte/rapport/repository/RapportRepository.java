// RapportRepository.java
package com.elcollecte.rapport.repository;

import com.elcollecte.rapport.entity.Rapport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RapportRepository extends JpaRepository<Rapport, Long> {
    Page<Rapport> findByProjetId(Long projetId, Pageable pageable);
    Page<Rapport> findByDemandeurId(Long demandeurId, Pageable pageable);
    Page<Rapport> findByStatut(Rapport.Statut statut, Pageable pageable);
}