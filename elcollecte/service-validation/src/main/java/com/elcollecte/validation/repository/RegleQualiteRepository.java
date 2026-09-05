package com.elcollecte.validation.repository;

import com.elcollecte.validation.entity.RegleQualite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegleQualiteRepository extends JpaRepository<RegleQualite, Long> {

    List<RegleQualite> findByFormulaireVersionIdAndActifTrue(Long formulaireVersionId);

    List<RegleQualite> findByQuestionIdAndActifTrue(Long questionId);
}
