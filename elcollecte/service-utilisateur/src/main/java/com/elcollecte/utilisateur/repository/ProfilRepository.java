package com.elcollecte.utilisateur.repository;

import com.elcollecte.utilisateur.entity.Profil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfilRepository extends JpaRepository<Profil, Long> {

    Optional<Profil> findByCode(String code);

    boolean existsByCode(String code);

    Page<Profil> findAllByOrganisationIdOrOrganisationIsNull(Long organisationId, Pageable pageable);

    Page<Profil> findAllByOrganisationIsNull(Pageable pageable);
}
