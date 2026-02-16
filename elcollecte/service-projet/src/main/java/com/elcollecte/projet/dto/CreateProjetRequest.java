package com.elcollecte.projet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Map;

// ── Requête de création ───────────────────────────────────────────────────────
public record CreateProjetRequest(
    @NotBlank @Size(max = 200) String              titre,
    String                                         description,
    @NotNull  LocalDate                            dateDebut,
    LocalDate                                      dateFin,
    Map<String, Object>                            zoneGeo
) {}
