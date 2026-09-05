package com.elcollecte.cartographie.repository;

import com.elcollecte.cartographie.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZoneRepository extends JpaRepository<Zone, Long> {

    List<Zone> findByProjetId(Long projetId);
}
