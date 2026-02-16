package com.elcollecte.collecte.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

// ── Soumission online ─────────────────────────────────────────────────────────
public record SubmitCollecteRequest(
    @NotNull Long                  formulaireId,
    @NotNull Long                  projetId,
    @NotNull Map<String, Object>   donnees,
    Double                         latitude,
    Double                         longitude
) {}
