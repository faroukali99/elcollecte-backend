package com.elcollecte.formulaire.repository;

import com.elcollecte.formulaire.entity.Formulaire;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FormulaireRepository extends JpaRepository<Formulaire, Long> {
    Page<Formulaire> findByProjetId(Long projetId, Pageable pageable);
    Page<Formulaire> findByProjetIdAndActiveTrue(Long projetId, Pageable pageable);
    Optional<Formulaire> findByIdAndProjetId(Long id, Long projetId);
    long countByProjetIdAndActiveTrue(Long projetId);
}