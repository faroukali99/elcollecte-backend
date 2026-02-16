package com.elcollecte.utilisateur.dto;

import jakarta.validation.constraints.*;

public record RegisterRequest(
    @NotBlank @Size(max = 100) String nom,
    @NotBlank @Size(max = 100) String prenom,
    @NotBlank @Email           String email,
    @NotBlank @Size(min = 8, max = 100)
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9]).*$",
             message = "Le mot de passe doit contenir au moins une majuscule et un chiffre")
    String password,
    @NotNull Long organisationId,
    String role
) {
    public RegisterRequest {
        if (role == null || role.isBlank()) role = "ENQUETEUR";
    }
}
