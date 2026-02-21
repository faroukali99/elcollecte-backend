package com.elcollecte.formulaire.dto;

import java.util.Map;

/**
 * Requête de sauvegarde (brouillon ou soumission).
 * Tous les champs sont optionnels — la sauvegarde automatique envoie
 * uniquement les champs remplis.
 */
public record SaveFormulaireRequest(
        Long    projetId,
        // A
        String  nomProjet,
        String  region,
        String  departement,
        String  commune,
        String  promoteur,
        String  emailPromoteur,
        String  dateDebut,
        String  dureeEstimee,
        // B
        String  typeProjet,
        Double  superficie,
        String  descriptionDetaillee,
        String  activitesPrincipales,
        String  besoinsRessources,
        // C
        String  impactFaune,
        String  impactFlore,
        String  impactEau,
        String  impactAir,
        String  impactSonore,
        String  gestionDechets,
        String  mesuresAttenuation,
        // D
        Integer populationAffectee,
        String  deploiementPopulations,
        Integer creationEmplois,
        String  impactEconomiqueLocal,
        String  consultationCommunautes,
        String  mesuresCompensationSociale,
        // E
        Map<String, Object> documentsAnnexes
) {}