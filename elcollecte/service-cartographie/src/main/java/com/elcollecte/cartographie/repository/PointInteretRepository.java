package com.elcollecte.cartographie.repository;

import com.elcollecte.cartographie.entity.PointInteret;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointInteretRepository extends JpaRepository<PointInteret, Long> {

    List<PointInteret> findByZoneId(Long zoneId);
}
