package com.elcollecte.collecte.dto;

import java.util.List;

// ── Réponse sync batch ────────────────────────────────────────────────────────
public record SyncResponse(
    int             total,
    long            synced,
    long            failed,
    List<SyncResult> results
) {}
