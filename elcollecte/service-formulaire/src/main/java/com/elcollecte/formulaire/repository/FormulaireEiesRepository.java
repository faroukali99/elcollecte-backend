package com.elcollecte.formulaire.repository;

import com.elcollecte.formulaire.entity.FormulaireEies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FormulaireEiesRepository extends JpaRepository<FormulaireEies, Long> {

    Optional<FormulaireEies> findByIdAndUserId(Long id, Long userId);

    Page<FormulaireEies> findByUserId(Long userId, Pageable pageable);

    Page<FormulaireEies> findByProjetId(Long projetId, Pageable pageable);

    List<FormulaireEies> findByStatutAndUserId(FormulaireEies.Statut statut, Long userId);

    long countByStatut(FormulaireEies.Statut statut);
}