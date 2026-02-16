package com.elcollecte.projet.repository;

import com.elcollecte.projet.entity.Projet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjetRepository extends JpaRepository<Projet, Long> {

    Page<Projet> findAllByOrganisationId(Long organisationId, Pageable pageable);

    Page<Projet> findAllByChefProjetId(Long chefProjetId, Pageable pageable);

    @Query("""
        SELECT DISTINCT p FROM Projet p
        LEFT JOIN p.enqueteurs pe
        WHERE p.organisationId = :orgId
          AND (p.chefProjetId = :userId OR pe.userId = :userId)
        """)
    Page<Projet> findAccessibleByUser(@Param("orgId")    Long orgId,
                                      @Param("userId")   Long userId,
                                      Pageable pageable);

    Optional<Projet> findByIdAndOrganisationId(Long id, Long organisationId);

    @Query("""
        SELECT COUNT(p) FROM Projet p
        WHERE p.organisationId = :orgId AND p.statut = 'ACTIF'
        """)
    long countActifsByOrganisation(@Param("orgId") Long orgId);
}
