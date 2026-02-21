package com.elcollecte.formulaire.dto;

import com.elcollecte.formulaire.entity.FormulaireEies;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO de lecture pour un formulaire EIES.
 */
public record FormulaireEiesDto(
        Long    id,
        Long    projetId,
        Long    userId,
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
        Map<String, Object> documentsAnnexes,
        // Meta
        String  statut,
        Integer scoreCompletude,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime submittedAt
) {
    public static FormulaireEiesDto from(FormulaireEies f) {
        return new FormulaireEiesDto(
                f.getId(), f.getProjetId(), f.getUserId(),
                f.getNomProjet(), f.getRegion(), f.getDepartement(), f.getCommune(),
                f.getPromoteur(), f.getEmailPromoteur(), f.getDateDebut(), f.getDureeEstimee(),
                f.getTypeProjet(), f.getSuperficie(), f.getDescriptionDetaillee(),
                f.getActivitesPrincipales(), f.getBesoinsRessources(),
                f.getImpactFaune(), f.getImpactFlore(), f.getImpactEau(),
                f.getImpactAir(), f.getImpactSonore(), f.getGestionDechets(),
                f.getMesuresAttenuation(),
                f.getPopulationAffectee(), f.getDeploiementPopulations(),
                f.getCreationEmplois(), f.getImpactEconomiqueLocal(),
                f.getConsultationCommunautes(), f.getMesuresCompensationSociale(),
                f.getDocumentsAnnexes(),
                f.getStatut().name(), f.getScoreCompletude(),
                f.getCreatedAt(), f.getUpdatedAt(), f.getSubmittedAt()
        );
    }
}