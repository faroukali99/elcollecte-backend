package com.elcollecte.rapport.dto;

public record CreateRapportRequest(
    Long projetId,
    Long formulaireId,
    String type,
    String titre,
    String format,
    String parametres
) {}
