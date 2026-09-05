package com.elcollecte.cartographie.controller;

import com.elcollecte.cartographie.dto.*;
import com.elcollecte.cartographie.service.CartographieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cartographie")
@Tag(name = "Cartographie", description = "Gestion des zones géographiques avec PostGIS")
public class CartographieController {

    private final CartographieService cartographieService;

    public CartographieController(CartographieService cartographieService) {
        this.cartographieService = cartographieService;
    }

    @PostMapping("/zones")
    @Operation(summary = "Créer une zone")
    public ResponseEntity<ZoneDto> createZone(@RequestBody CreateZoneRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartographieService.createZone(request));
    }

    @GetMapping("/zones/projet/{projetId}")
    @Operation(summary = "Lister les zones d'un projet")
    public ResponseEntity<List<ZoneDto>> findZonesByProjet(@PathVariable Long projetId) {
        return ResponseEntity.ok(cartographieService.findZonesByProjet(projetId));
    }

    @GetMapping("/zones/{id}")
    @Operation(summary = "Détails d'une zone")
    public ResponseEntity<ZoneDto> findZoneById(@PathVariable Long id) {
        return ResponseEntity.ok(cartographieService.findZoneById(id));
    }

    @PostMapping("/points")
    @Operation(summary = "Créer un point d'intérêt")
    public ResponseEntity<PointInteretDto> createPoint(@RequestBody CreatePointRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartographieService.createPoint(request));
    }

    @GetMapping("/points/zone/{zoneId}")
    @Operation(summary = "Lister les points d'une zone")
    public ResponseEntity<List<PointInteretDto>> findPointsByZone(@PathVariable Long zoneId) {
        return ResponseEntity.ok(cartographieService.findPointsByZone(zoneId));
    }

    @PostMapping("/polygones")
    @Operation(summary = "Créer un polygone")
    public ResponseEntity<PolygoneDto> createPolygone(@RequestBody CreatePolygoneRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartographieService.createPolygone(request));
    }

    @GetMapping("/polygones/zone/{zoneId}")
    @Operation(summary = "Lister les polygones d'une zone")
    public ResponseEntity<List<PolygoneDto>> findPolygonesByZone(@PathVariable Long zoneId) {
        return ResponseEntity.ok(cartographieService.findPolygonesByZone(zoneId));
    }
}
