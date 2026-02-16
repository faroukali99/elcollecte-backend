package com.elcollecte.utilisateur.repository;

import com.elcollecte.utilisateur.entity.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganisationRepository extends JpaRepository<Organisation, Long> {
    Optional<Organisation> findByEmailContact(String emailContact);
    boolean existsByEmailContact(String emailContact);
}
