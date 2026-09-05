package com.elcollecte.formulaire.repository;

import com.elcollecte.formulaire.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {

    List<Section> findByVersionIdOrderByOrdre(Long versionId);
}
