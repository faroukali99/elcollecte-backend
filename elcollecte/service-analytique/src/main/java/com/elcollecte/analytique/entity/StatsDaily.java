package com.elcollecte.analytique.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "stats_daily")
public class StatsDaily {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "projet_id", nullable = false)
    private Long projetId;

    @Column(name = "date_jour", nullable = false)
    private LocalDate dateJour;

    @Column(name = "nb_collectes")
    private int nbCollectes = 0;

    @Column(name = "nb_validees")
    private int nbValidees = 0;

    public StatsDaily() {}

    public Long getId()           { return id; }
    public Long getProjetId()     { return projetId; }
    public LocalDate getDateJour(){ return dateJour; }
    public int getNbCollectes()   { return nbCollectes; }
    public int getNbValidees()    { return nbValidees; }

    public void setProjetId(Long v)     { this.projetId = v; }
    public void setDateJour(LocalDate v){ this.dateJour = v; }
    public void setNbCollectes(int v)   { this.nbCollectes = v; }
    public void setNbValidees(int v)    { this.nbValidees = v; }
}