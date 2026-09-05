package com.elcollecte.mission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateMissionRequest(
    @NotNull Long projetId,
    @NotNull Long formulaireId,
    Long formulaireVersionId,
    Long zoneId,
    @NotBlank String titre,
    String description,
    String objectif,
    @NotNull LocalDate dateDebut,
    LocalDate dateFin
) {}
