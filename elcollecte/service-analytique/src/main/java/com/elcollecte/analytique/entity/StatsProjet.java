package com.elcollecte.analytique.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stats_projets")
public class StatsProjet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "projet_id", nullable = false, unique = true)
    private Long projetId;

    @Column(name = "total_collectes", nullable = false)
    private Long totalCollectes = 0L;

    @Column(name = "collectes_validees", nullable = false)
    private Long collectesValidees = 0L;

    @Column(name = "collectes_rejetees", nullable = false)
    private Long collectesRejetees = 0L;

    @Column(name = "collectes_en_attente", nullable = false)
    private Long collectesEnAttente = 0L;

    @Column(name = "nb_enqueteurs_actifs", nullable = false)
    private int nbEnqueteursActifs = 0;

    @Column(name = "taux_validation")
    private double tauxValidation = 0;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    // Getters & Setters
    public Long getId()                   { return id; }
    public Long getProjetId()             { return projetId; }
    public Long  getTotalCollectes()       { return totalCollectes; }
    public Long  getCollectesValidees()    { return collectesValidees; }
    public Long  getCollectesRejetees()    { return collectesRejetees; }
    public Long  getCollectesEnAttente()   { return collectesEnAttente; }
    public int  getNbEnqueteursActifs()   { return nbEnqueteursActifs; }
    public double getTauxValidation()     { return tauxValidation; }
    public LocalDateTime getUpdatedAt()   { return updatedAt; }

    public void setProjetId(Long v)           { this.projetId = v; }
    public void setTotalCollectes(Long v)      { this.totalCollectes = v; }
    public void setCollectesValidees(Long v)   { this.collectesValidees = v; }
    public void setCollectesRejetees(Long v)   { this.collectesRejetees = v; }
    public void setCollectesEnAttente(Long v)  { this.collectesEnAttente = v; }
    public void setNbEnqueteursActifs(int v)  { this.nbEnqueteursActifs = v; }
    public void setTauxValidation(double v)   { this.tauxValidation = v; }
}
