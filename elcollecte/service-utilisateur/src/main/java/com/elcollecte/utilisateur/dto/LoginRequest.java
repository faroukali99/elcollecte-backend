package com.elcollecte.utilisateur.dto;

import jakarta.validation.constraints.*;

// ── Requête de connexion ──────────────────────────────────────────────────────
public record LoginRequest(
    @NotBlank(message = "Email obligatoire")
    @Email(message = "Format email invalide")
    String email,

    @NotBlank(message = "Mot de passe obligatoire")
    @Size(min = 8, message = "Mot de passe: minimum 8 caractères")
    String password
) {}
