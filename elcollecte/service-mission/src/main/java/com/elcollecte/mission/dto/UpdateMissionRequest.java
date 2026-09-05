package com.elcollecte.mission.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateMissionRequest(
    String titre,
    String description,
    String objectif,
    LocalDate dateDebut,
    LocalDate dateFin,
    String statut
) {}
