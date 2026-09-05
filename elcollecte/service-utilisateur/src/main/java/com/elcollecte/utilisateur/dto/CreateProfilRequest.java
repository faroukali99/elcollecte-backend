package com.elcollecte.utilisateur.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProfilRequest(
    @NotBlank @Size(max = 50)  String code,
    @NotBlank @Size(max = 150) String libelle,
    String description,
    Long organisationId
) {}
