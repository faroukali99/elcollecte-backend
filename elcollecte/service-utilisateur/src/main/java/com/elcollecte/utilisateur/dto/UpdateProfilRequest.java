package com.elcollecte.utilisateur.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfilRequest(
    @NotBlank @Size(max = 150) String libelle,
    String description
) {}
