package com.elcollecte.projet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddMembreRequest(
    @NotNull Long    userId,
    @NotBlank String roleMembre
) {}
