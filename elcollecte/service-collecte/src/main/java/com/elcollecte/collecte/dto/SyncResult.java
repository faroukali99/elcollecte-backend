package com.elcollecte.collecte.dto;

// ── Résultat sync ─────────────────────────────────────────────────────────────
public record SyncResult(
    String  localId,
    Long    serverId,
    boolean success,
    String  errorMessage
) {}
