package com.elcollecte.utilisateur.dto;

import jakarta.validation.constraints.NotNull;

public record AssignProfilRequest(
    @NotNull Long profilId
) {}
