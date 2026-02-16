package com.elcollecte.utilisateur.dto;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    long   expiresIn,
    UserInfo user
) {
    public AuthResponse(String accessToken, String refreshToken, long expiresIn, UserInfo user) {
        this(accessToken, refreshToken, "Bearer", expiresIn, user);
    }

    public record UserInfo(
        Long   id,
        String nom,
        String prenom,
        String email,
        String role,
        Long   organisationId
    ) {}
}
