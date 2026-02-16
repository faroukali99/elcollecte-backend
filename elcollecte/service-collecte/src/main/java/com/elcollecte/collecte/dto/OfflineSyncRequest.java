package com.elcollecte.collecte.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Map;

// ── Soumission offline (batch sync) ──────────────────────────────────────────
public record OfflineSyncRequest(
    String                 localId,
    @NotNull Long          formulaireId,
    @NotNull Long          projetId,
    @NotNull Map<String, Object> donnees,
    Double                 latitude,
    Double                 longitude,
    LocalDateTime          collectedAt
) {}
