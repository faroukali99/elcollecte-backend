package com.elcollecte.projet.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "projets")
public class Projet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organisation_id", nullable = false)
    private Long organisationId;

    @Column(name = "responsable_id", nullable = false)
    private Long responsableId;

    @Column(nullable = false, length = 200)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "projet_statut")
    private Statut statut = Statut.BROUILLON;

    @Column(columnDefinition = "TEXT")
    private String motifRejet;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "zone_geo", columnDefinition = "jsonb")
    private Map<String, Object> zoneGeo;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProjetEnqueteur> enqueteurs = new HashSet<>();

    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProjetMembre> membres = new HashSet<>();

    public enum Statut {
        BROUILLON, PLANIFIE, EN_COURS, SUSPENDU, TERMINE, ARCHIVE
    }

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    public Projet() {}

    // ── Getters / Setters ────────────────────────────────────────────────────
    public Long                    getId()             { return id; }
    public Long                    getOrganisationId() { return organisationId; }
    public Long                    getResponsableId()  { return responsableId; }
    public String                  getTitre()          { return titre; }
    public String                  getDescription()    { return description; }
    public Statut                  getStatut()         { return statut; }
    public String                  getMotifRejet()     { return motifRejet; }
    public Map<String, Object>     getZoneGeo()        { return zoneGeo; }
    public LocalDate               getDateDebut()      { return dateDebut; }
    public LocalDate               getDateFin()        { return dateFin; }
    public LocalDateTime           getCreatedAt()      { return createdAt; }
    public LocalDateTime           getUpdatedAt()      { return updatedAt; }
    public Set<ProjetEnqueteur>    getEnqueteurs()     { return enqueteurs; }
    public Set<ProjetMembre>       getMembres()        { return membres; }

    public void setId(Long id)                           { this.id = id; }
    public void setOrganisationId(Long organisationId)   { this.organisationId = organisationId; }
    public void setResponsableId(Long responsableId)     { this.responsableId = responsableId; }
    public void setTitre(String titre)                   { this.titre = titre; }
    public void setDescription(String description)       { this.description = description; }
    public void setStatut(Statut statut)                 { this.statut = statut; }
    public void setMotifRejet(String motifRejet)         { this.motifRejet = motifRejet; }
    public void setZoneGeo(Map<String, Object> zoneGeo)  { this.zoneGeo = zoneGeo; }
    public void setDateDebut(LocalDate dateDebut)        { this.dateDebut = dateDebut; }
    public void setDateFin(LocalDate dateFin)            { this.dateFin = dateFin; }
    public void setEnqueteurs(Set<ProjetEnqueteur> e)    { this.enqueteurs = e; }
    public void setMembres(Set<ProjetMembre> m)          { this.membres = m; }
}
