package com.elcollecte.formulaire.repository;

import com.elcollecte.formulaire.entity.Formulaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FormulaireRepository extends JpaRepository<Formulaire, Long> {

    Optional<Formulaire> findByCode(String code);

    @Query("SELECT f FROM Formulaire f WHERE f.organisation.id = :orgId AND f.actif = true")
    java.util.List<Formulaire> findActifsByOrganisation(Long orgId);
}
