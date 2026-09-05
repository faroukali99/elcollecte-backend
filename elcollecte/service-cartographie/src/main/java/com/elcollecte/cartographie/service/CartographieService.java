package com.elcollecte.cartographie.service;

import com.elcollecte.cartographie.dto.PointInteretDto;
import com.elcollecte.cartographie.dto.ZoneDto;
import com.elcollecte.cartographie.entity.PointInteret;
import com.elcollecte.cartographie.entity.Polygone;
import com.elcollecte.cartographie.entity.Zone;
import com.elcollecte.cartographie.repository.PointInteretRepository;
import com.elcollecte.cartographie.repository.PolygoneRepository;
import com.elcollecte.cartographie.repository.ZoneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CartographieService {

    private final ZoneRepository zoneRepository;
    private final PointInteretRepository pointInteretRepository;
    private final PolygoneRepository polygoneRepository;

    public CartographieService(ZoneRepository zoneRepository,
                               PointInteretRepository pointInteretRepository,
                               PolygoneRepository polygoneRepository) {
        this.zoneRepository = zoneRepository;
        this.pointInteretRepository = pointInteretRepository;
        this.polygoneRepository = polygoneRepository;
    }

    @Transactional
    public ZoneDto createZone(Zone zone) {
        return ZoneDto.from(zoneRepository.save(zone));
    }

    @Transactional(readOnly = true)
    public List<ZoneDto> findZonesByProjet(Long projetId) {
        return zoneRepository.findByProjetId(projetId).stream()
            .map(ZoneDto::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public ZoneDto findZoneById(Long id) {
        return zoneRepository.findById(id)
            .map(ZoneDto::from)
            .orElseThrow(() -> new NoSuchElementException("Zone introuvable: " + id));
    }

    @Transactional
    public PointInteretDto createPoint(PointInteret point) {
        return PointInteretDto.from(pointInteretRepository.save(point));
    }

    @Transactional(readOnly = true)
    public List<PointInteretDto> findPointsByZone(Long zoneId) {
        return pointInteretRepository.findByZoneId(zoneId).stream()
            .map(PointInteretDto::from)
            .toList();
    }

    @Transactional
    public Polygone createPolygone(Polygone polygone) {
        return polygoneRepository.save(polygone);
    }

    @Transactional(readOnly = true)
    public List<Polygone> findPolygonesByZone(Long zoneId) {
        return polygoneRepository.findByZoneId(zoneId);
    }
}
