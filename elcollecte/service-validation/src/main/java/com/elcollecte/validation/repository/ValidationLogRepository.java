package com.elcollecte.validation.repository;

import com.elcollecte.validation.entity.ValidationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ValidationLogRepository extends JpaRepository<ValidationLog, Long> {

    List<ValidationLog> findByFormulaireId(Long formulaireId);

    List<ValidationLog> findByUserId(Long userId);

    List<ValidationLog> findByCreatedAtAfter(LocalDateTime date);

    List<ValidationLog> findByFormulaireIdAndUserId(Long formulaireId, Long userId);
}
