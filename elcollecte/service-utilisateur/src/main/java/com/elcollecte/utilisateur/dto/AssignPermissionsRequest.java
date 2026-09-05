package com.elcollecte.utilisateur.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AssignPermissionsRequest(
    @NotEmpty List<Long> permissionIds
) {}
