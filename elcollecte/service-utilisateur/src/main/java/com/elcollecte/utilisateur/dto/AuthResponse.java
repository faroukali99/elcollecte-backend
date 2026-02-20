package com.elcollecte.utilisateur.dto;

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
        String role,
        Long   organisationId
    ) {}
}
