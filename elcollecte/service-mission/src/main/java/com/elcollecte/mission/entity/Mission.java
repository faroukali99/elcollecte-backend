package com.elcollecte.mission.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "missions")
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "projet_id", nullable = false)
    private Long projetId;

    @Column(name = "formulaire_id", nullable = false)
    private Long formulaireId;

    @Column(name = "formulaire_version_id")
    private Long formulaireVersionId;

    @Column(name = "zone_id")
    private Long zoneId;

    @Column(nullable = false, length = 200)
    private String titre;

    @Column(length = 500)
    private String description;

    @Column(length = 500)
    private String objectif;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Statut statut = Statut.PLANIFIEE;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MissionEnqueteur> enqueteurs = new HashSet<>();

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    public enum Statut {
        PLANIFIEE, EN_COURS, SUSPENDUE, TERMINEE, ANNULEE
    }

    public Mission() {}

    public Long getId() { return id; }
    public Long getProjetId() { return projetId; }
    public Long getFormulaireId() { return formulaireId; }
    public Long getFormulaireVersionId() { return formulaireVersionId; }
    public Long getZoneId() { return zoneId; }
    public String getTitre() { return titre; }
    public String getDescription() { return description; }
    public String getObjectif() { return objectif; }
    public LocalDate getDateDebut() { return dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public Statut getStatut() { return statut; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Set<MissionEnqueteur> getEnqueteurs() { return enqueteurs; }

    public void setId(Long id) { this.id = id; }
    public void setProjetId(Long projetId) { this.projetId = projetId; }
    public void setFormulaireId(Long formulaireId) { this.formulaireId = formulaireId; }
    public void setFormulaireVersionId(Long formulaireVersionId) { this.formulaireVersionId = formulaireVersionId; }
    public void setZoneId(Long zoneId) { this.zoneId = zoneId; }
    public void setTitre(String titre) { this.titre = titre; }
    public void setDescription(String description) { this.description = description; }
    public void setObjectif(String objectif) { this.objectif = objectif; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public void setStatut(Statut statut) { this.statut = statut; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setEnqueteurs(Set<MissionEnqueteur> enqueteurs) { this.enqueteurs = enqueteurs; }
}
