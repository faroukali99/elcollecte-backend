package com.elcollecte.formulaire.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateFormulaireRequest(
    @NotBlank @Size(max = 200) String nom,
    @Size(max = 500) String description,
    @NotBlank @Size(max = 50) String code,
    Long organisationId
) {}
