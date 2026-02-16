package com.elcollecte.validation.repository;

import com.elcollecte.validation.entity.Validation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ValidationRepository extends JpaRepository<Validation, Long> {
    Page<Validation> findByProjetId(Long projetId, Pageable pageable);
    Page<Validation> findByProjetIdAndStatut(Long projetId, Validation.Statut statut, Pageable pageable);
    Page<Validation> findByValidateurId(Long validateurId, Pageable pageable);
    Optional<Validation> findByCollecteId(Long collecteId);
    long countByProjetIdAndStatut(Long projetId, Validation.Statut statut);
}