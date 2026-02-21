package com.elcollecte.formulaire.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Entité persistante pour un formulaire EIES (Étude d'Impact Environnemental et Social).
 */
@Entity
@Table(name = "formulaires_eies")
public class FormulaireEies {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "projet_id")
    private Long projetId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // ── Section A : Informations générales ──────────────────────────────────
    @Column(name = "nom_projet", length = 200)
    private String nomProjet;

    @Column(length = 100)
    private String region;

    @Column(length = 100)
    private String departement;

    @Column(length = 100)
    private String commune;

    @Column(length = 200)
    private String promoteur;

    @Column(name = "email_promoteur", length = 150)
    private String emailPromoteur;

    @Column(name = "date_debut")
    private String dateDebut;

    @Column(name = "duree_estimee")
    private String dureeEstimee;

    // ── Section B : Description du projet ───────────────────────────────────
    @Column(name = "type_projet", length = 100)
    private String typeProjet;

    @Column
    private Double superficie;

    @Column(name = "description_detaillee", columnDefinition = "TEXT")
    private String descriptionDetaillee;

    @Column(name = "activites_principales", columnDefinition = "TEXT")
    private String activitesPrincipales;

    @Column(name = "besoins_ressources", columnDefinition = "TEXT")
    private String besoinsRessources;

    // ── Section C : Impacts environnementaux ────────────────────────────────
    @Column(name = "impact_faune", length = 10)
    private String impactFaune;  // OUI | NON | PARTIEL

    @Column(name = "impact_flore", length = 10)
    private String impactFlore;

    @Column(name = "impact_eau", length = 10)
    private String impactEau;

    @Column(name = "impact_air", length = 10)
    private String impactAir;

    @Column(name = "impact_sonore", length = 10)
    private String impactSonore;

    @Column(name = "gestion_dechets", columnDefinition = "TEXT")
    private String gestionDechets;

    @Column(name = "mesures_attenuation", columnDefinition = "TEXT")
    private String mesuresAttenuation;

    // ── Section D : Impacts sociaux ──────────────────────────────────────────
    @Column(name = "population_affectee")
    private Integer populationAffectee;

    @Column(name = "deploiement_populations", length = 10)
    private String deploiementPopulations;

    @Column(name = "creation_emplois")
    private Integer creationEmplois;

    @Column(name = "impact_economique_local", columnDefinition = "TEXT")
    private String impactEconomiqueLocal;

    @Column(name = "consultation_communautes", length = 10)
    private String consultationCommunautes;

    @Column(name = "mesures_compensation_sociale", columnDefinition = "TEXT")
    private String mesuresCompensationSociale;

    // ── Section E : Documents ───────────────────────────────────────────────
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "documents_annexes", columnDefinition = "jsonb")
    private Map<String, Object> documentsAnnexes;

    // ── Métadonnées ─────────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Statut statut = Statut.BROUILLON;

    @Column(name = "score_completude")
    private Integer scoreCompletude = 0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    public enum Statut {
        BROUILLON, EN_REVISION, SOUMIS, VALIDE, REJETE
    }

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    public FormulaireEies() {}

    // ── Getters ──────────────────────────────────────────────────────────────
    public Long getId()                     { return id; }
    public Long getProjetId()               { return projetId; }
    public Long getUserId()                 { return userId; }
    public String getNomProjet()            { return nomProjet; }
    public String getRegion()               { return region; }
    public String getDepartement()          { return departement; }
    public String getCommune()              { return commune; }
    public String getPromoteur()            { return promoteur; }
    public String getEmailPromoteur()       { return emailPromoteur; }
    public String getDateDebut()            { return dateDebut; }
    public String getDureeEstimee()         { return dureeEstimee; }
    public String getTypeProjet()           { return typeProjet; }
    public Double getSuperficie()           { return superficie; }
    public String getDescriptionDetaillee() { return descriptionDetaillee; }
    public String getActivitesPrincipales() { return activitesPrincipales; }
    public String getBesoinsRessources()    { return besoinsRessources; }
    public String getImpactFaune()          { return impactFaune; }
    public String getImpactFlore()          { return impactFlore; }
    public String getImpactEau()            { return impactEau; }
    public String getImpactAir()            { return impactAir; }
    public String getImpactSonore()         { return impactSonore; }
    public String getGestionDechets()       { return gestionDechets; }
    public String getMesuresAttenuation()   { return mesuresAttenuation; }
    public Integer getPopulationAffectee()  { return populationAffectee; }
    public String getDeploiementPopulations(){ return deploiementPopulations; }
    public Integer getCreationEmplois()     { return creationEmplois; }
    public String getImpactEconomiqueLocal(){ return impactEconomiqueLocal; }
    public String getConsultationCommunautes(){ return consultationCommunautes; }
    public String getMesuresCompensationSociale(){ return mesuresCompensationSociale; }
    public Map<String, Object> getDocumentsAnnexes(){ return documentsAnnexes; }
    public Statut getStatut()               { return statut; }
    public Integer getScoreCompletude()     { return scoreCompletude; }
    public LocalDateTime getCreatedAt()     { return createdAt; }
    public LocalDateTime getUpdatedAt()     { return updatedAt; }
    public LocalDateTime getSubmittedAt()   { return submittedAt; }

    // ── Setters ──────────────────────────────────────────────────────────────
    public void setProjetId(Long v)                  { this.projetId = v; }
    public void setUserId(Long v)                    { this.userId = v; }
    public void setNomProjet(String v)               { this.nomProjet = v; }
    public void setRegion(String v)                  { this.region = v; }
    public void setDepartement(String v)             { this.departement = v; }
    public void setCommune(String v)                 { this.commune = v; }
    public void setPromoteur(String v)               { this.promoteur = v; }
    public void setEmailPromoteur(String v)          { this.emailPromoteur = v; }
    public void setDateDebut(String v)               { this.dateDebut = v; }
    public void setDureeEstimee(String v)            { this.dureeEstimee = v; }
    public void setTypeProjet(String v)              { this.typeProjet = v; }
    public void setSuperficie(Double v)              { this.superficie = v; }
    public void setDescriptionDetaillee(String v)    { this.descriptionDetaillee = v; }
    public void setActivitesPrincipales(String v)    { this.activitesPrincipales = v; }
    public void setBesoinsRessources(String v)       { this.besoinsRessources = v; }
    public void setImpactFaune(String v)             { this.impactFaune = v; }
    public void setImpactFlore(String v)             { this.impactFlore = v; }
    public void setImpactEau(String v)               { this.impactEau = v; }
    public void setImpactAir(String v)               { this.impactAir = v; }
    public void setImpactSonore(String v)            { this.impactSonore = v; }
    public void setGestionDechets(String v)          { this.gestionDechets = v; }
    public void setMesuresAttenuation(String v)      { this.mesuresAttenuation = v; }
    public void setPopulationAffectee(Integer v)     { this.populationAffectee = v; }
    public void setDeploiementPopulations(String v)  { this.deploiementPopulations = v; }
    public void setCreationEmplois(Integer v)        { this.creationEmplois = v; }
    public void setImpactEconomiqueLocal(String v)   { this.impactEconomiqueLocal = v; }
    public void setConsultationCommunautes(String v) { this.consultationCommunautes = v; }
    public void setMesuresCompensationSociale(String v){ this.mesuresCompensationSociale = v; }
    public void setDocumentsAnnexes(Map<String, Object> v){ this.documentsAnnexes = v; }
    public void setStatut(Statut v)                  { this.statut = v; }
    public void setScoreCompletude(Integer v)        { this.scoreCompletude = v; }
    public void setSubmittedAt(LocalDateTime v)      { this.submittedAt = v; }
}
