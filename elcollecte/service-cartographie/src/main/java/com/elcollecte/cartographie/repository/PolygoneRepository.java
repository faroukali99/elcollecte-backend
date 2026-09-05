package com.elcollecte.cartographie.repository;

import com.elcollecte.cartographie.entity.Polygone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PolygoneRepository extends JpaRepository<Polygone, Long> {

    List<Polygone> findByZoneId(Long zoneId);
}
