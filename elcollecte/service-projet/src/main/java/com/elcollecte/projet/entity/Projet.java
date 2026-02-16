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

    @Column(name = "chef_projet_id", nullable = false)
    private Long chefProjetId;

    @Column(nullable = false, length = 200)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Statut statut = Statut.BROUILLON;

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

    public enum Statut {
        BROUILLON, ACTIF, SUSPENDU, TERMINE
    }

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    public Projet() {}

    // ── Getters / Setters ────────────────────────────────────────────────────
    public Long                    getId()             { return id; }
    public Long                    getOrganisationId() { return organisationId; }
    public Long                    getChefProjetId()   { return chefProjetId; }
    public String                  getTitre()          { return titre; }
    public String                  getDescription()    { return description; }
    public Statut                  getStatut()         { return statut; }
    public Map<String, Object>     getZoneGeo()        { return zoneGeo; }
    public LocalDate               getDateDebut()      { return dateDebut; }
    public LocalDate               getDateFin()        { return dateFin; }
    public LocalDateTime           getCreatedAt()      { return createdAt; }
    public LocalDateTime           getUpdatedAt()      { return updatedAt; }
    public Set<ProjetEnqueteur>    getEnqueteurs()     { return enqueteurs; }

    public void setId(Long id)                           { this.id = id; }
    public void setOrganisationId(Long organisationId)   { this.organisationId = organisationId; }
    public void setChefProjetId(Long chefProjetId)       { this.chefProjetId = chefProjetId; }
    public void setTitre(String titre)                   { this.titre = titre; }
    public void setDescription(String description)       { this.description = description; }
    public void setStatut(Statut statut)                 { this.statut = statut; }
    public void setZoneGeo(Map<String, Object> zoneGeo)  { this.zoneGeo = zoneGeo; }
    public void setDateDebut(LocalDate dateDebut)        { this.dateDebut = dateDebut; }
    public void setDateFin(LocalDate dateFin)            { this.dateFin = dateFin; }
    public void setEnqueteurs(Set<ProjetEnqueteur> e)    { this.enqueteurs = e; }
}
