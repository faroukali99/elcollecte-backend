package com.elcollecte.formulaire.repository;

import com.elcollecte.formulaire.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findBySectionIdOrderByOrdre(Long sectionId);
}
