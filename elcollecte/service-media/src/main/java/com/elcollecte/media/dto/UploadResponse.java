package com.elcollecte.media.dto;

import java.util.UUID;

public record UploadResponse(
    UUID uuid,
    String urlAcces,
    String message
) {}
