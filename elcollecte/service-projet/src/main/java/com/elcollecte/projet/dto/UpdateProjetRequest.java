package com.elcollecte.projet.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Map;

// ── Requête de mise à jour ────────────────────────────────────────────────────
public record UpdateProjetRequest(
    @Size(max = 200) String              titre,
    String                               description,
    String                               statut,
    LocalDate                            dateFin,
    Map<String, Object>                  zoneGeo
) {}
