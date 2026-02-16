package com.elcollecte.collecte.repository;

import com.elcollecte.collecte.entity.CollecteData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CollecteRepository extends JpaRepository<CollecteData, Long> {

    Page<CollecteData> findByProjetId(Long projetId, Pageable pageable);

    Page<CollecteData> findByEnqueteurId(Long enqueteurId, Pageable pageable);

    Page<CollecteData> findByProjetIdAndEnqueteurId(Long projetId, Long enqueteurId, Pageable pageable);

    Page<CollecteData> findByProjetIdAndStatut(Long projetId, CollecteData.Statut statut, Pageable pageable);

    long countByProjetId(Long projetId);

    long countByProjetIdAndStatut(Long projetId, CollecteData.Statut statut);

    @Query("""
        SELECT c FROM CollecteData c
        WHERE c.projetId = :projetId
          AND c.collectedAt BETWEEN :from AND :to
        ORDER BY c.collectedAt DESC
        """)
    List<CollecteData> findByProjetIdAndPeriod(@Param("projetId") Long projetId,
                                               @Param("from")     LocalDateTime from,
                                               @Param("to")       LocalDateTime to);

    @Query("""
        SELECT COUNT(c) FROM CollecteData c
        WHERE c.projetId = :projetId
          AND c.statut = 'SOUMIS'
        """)
    long countEnAttente(@Param("projetId") Long projetId);
}
