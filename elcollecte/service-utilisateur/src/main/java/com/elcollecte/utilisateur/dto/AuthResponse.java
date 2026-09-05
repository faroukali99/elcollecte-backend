package com.elcollecte.utilisateur.dto;

import java.util.List;

public record AuthResponse(
    String token,
    String refreshToken,
    String tokenType,
    long   expiresIn,
    UserInfo user
) {
    public AuthResponse(String token, String refreshToken, long expiresIn, UserInfo user) {
        this(token, refreshToken, "Bearer", expiresIn, user);
    }

    public record UserInfo(
        Long   id,
        String nom,
        String prenom,
        String email,
        String role,            // legacy — conservé pour compat frontend existant
        String profil,          // code du profil dynamique (ex: "SUPERVISEUR")
        String profilLibelle,   // libellé lisible du profil (ex: "Superviseur")
        List<String> permissions,
        Long   organisationId
    ) {}
}
