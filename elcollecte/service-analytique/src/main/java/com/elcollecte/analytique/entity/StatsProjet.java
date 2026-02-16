package com.elcollecte.analytique.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stats_projets")
public class StatsProjet {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "projet_id", nullable = false, unique = true)
    private Long projetId;

    @Column(name = "total_collectes")
    private long totalCollectes = 0;

    @Column(name = "collectes_validees")
    private long collectesValidees = 0;

    @Column(name = "collectes_rejetees")
    private long collectesRejetees = 0;

    @Column(name = "collectes_en_attente")
    private long collectesEnAttente = 0;

    @Column(name = "nb_enqueteurs_actifs")
    private int nbEnqueteursActifs = 0;

    @Column(name = "taux_validation")
    private double tauxValidation = 0.0;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    public StatsProjet() {}

    // Getters
    public Long getId()                   { return id; }
    public Long getProjetId()             { return projetId; }
    public long getTotalCollectes()       { return totalCollectes; }
    public long getCollectesValidees()    { return collectesValidees; }
    public long getCollectesRejetees()    { return collectesRejetees; }
    public long getCollectesEnAttente()   { return collectesEnAttente; }
    public int  getNbEnqueteursActifs()   { return nbEnqueteursActifs; }
    public double getTauxValidation()     { return tauxValidation; }
    public LocalDateTime getUpdatedAt()   { return updatedAt; }

    // Setters
    public void setProjetId(Long v)              { this.projetId = v; }
    public void setTotalCollectes(long v)        { this.totalCollectes = v; }
    public void setCollectesValidees(long v)     { this.collectesValidees = v; }
    public void setCollectesRejetees(long v)     { this.collectesRejetees = v; }
    public void setCollectesEnAttente(long v)    { this.collectesEnAttente = v; }
    public void setNbEnqueteursActifs(int v)     { this.nbEnqueteursActifs = v; }
    public void setTauxValidation(double v)      { this.tauxValidation = v; }
}