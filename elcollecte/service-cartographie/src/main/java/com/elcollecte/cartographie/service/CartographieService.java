package com.elcollecte.cartographie.service;

import com.elcollecte.cartographie.dto.*;
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
    public ZoneDto createZone(CreateZoneRequest request) {
        Zone zone = new Zone();
        zone.setNom(request.nom());
        zone.setDescription(request.description());
        zone.setProjetId(request.projetId());
        zone.setType(Zone.TypeZone.valueOf(request.type()));
        zone.setProperties(request.properties());
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
    public PointInteretDto createPoint(CreatePointRequest request) {
        PointInteret point = new PointInteret();
        point.setZoneId(request.zoneId());
        point.setNom(request.nom());
        point.setDescription(request.description());
        point.setLatitude(request.latitude());
        point.setLongitude(request.longitude());
        point.setAltitude(request.altitude());
        point.setType(PointInteret.TypePoint.valueOf(request.type()));
        point.setProperties(request.properties());
        return PointInteretDto.from(pointInteretRepository.save(point));
    }

    @Transactional(readOnly = true)
    public List<PointInteretDto> findPointsByZone(Long zoneId) {
        return pointInteretRepository.findByZoneId(zoneId).stream()
            .map(PointInteretDto::from)
            .toList();
    }

    @Transactional
    public PolygoneDto createPolygone(CreatePolygoneRequest request) {
        Polygone polygone = new Polygone();
        polygone.setZoneId(request.zoneId());
        polygone.setNom(request.nom());
        polygone.setDescription(request.description());
        polygone.setCoordinates(request.coordinates());
        polygone.setType(Polygone.TypePolygone.valueOf(request.type()));
        polygone.setProperties(request.properties());
        return PolygoneDto.from(polygoneRepository.save(polygone));
    }

    @Transactional(readOnly = true)
    public List<PolygoneDto> findPolygonesByZone(Long zoneId) {
        return polygoneRepository.findByZoneId(zoneId).stream()
            .map(PolygoneDto::from)
            .toList();
    }
}
