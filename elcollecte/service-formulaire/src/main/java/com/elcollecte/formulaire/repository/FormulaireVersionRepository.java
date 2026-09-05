package com.elcollecte.formulaire.repository;

import com.elcollecte.formulaire.entity.FormulaireVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FormulaireVersionRepository extends JpaRepository<FormulaireVersion, Long> {

    Optional<FormulaireVersion> findByFormulaireIdAndNumeroVersion(Long formulaireId, Integer numeroVersion);

    @Query("SELECT v FROM FormulaireVersion v WHERE v.formulaire.id = :formulaireId AND v.publie = true ORDER BY v.numeroVersion DESC")
    Optional<FormulaireVersion> findLastPublishedVersion(Long formulaireId);

    @Query("SELECT v FROM FormulaireVersion v WHERE v.formulaire.id = :formulaireId AND v.active = true ORDER BY v.numeroVersion DESC")
    Optional<FormulaireVersion> findLastActiveVersion(Long formulaireId);
}
