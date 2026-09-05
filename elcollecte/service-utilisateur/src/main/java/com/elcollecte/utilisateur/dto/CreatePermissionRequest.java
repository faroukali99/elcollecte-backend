package com.elcollecte.utilisateur.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePermissionRequest(
    @NotBlank @Size(max = 80)  String code,
    @NotBlank @Size(max = 150) String libelle,
    @NotBlank @Size(max = 50)  String categorie,
    String description
) {}
