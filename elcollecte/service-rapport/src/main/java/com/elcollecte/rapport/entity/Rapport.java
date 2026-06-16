package com.elcollecte.rapport.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "rapports")
public class Rapport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @Column(name = "projet_id", nullable = false)
    private Long projetId;

    @Column(name = "formulaire_id", nullable = false)
    private Long formulaireId;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "titre", nullable = false)
    private String titre;

    @Column(name = "format", nullable = false)
    private String format;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String parametres;

    @Column(name = "chemin_fichier")
    private String cheminFichier;

    @Column(name = "statut", nullable = false)
    private String statut;

    @Column(name = "erreur_message")
    private String erreurMessage;

    @Column(name = "cree_par", nullable = false)
    private Long creePar;

    @Column(name = "cree_at", nullable = false)
    private LocalDateTime creeAt;

    @Column(name = "genere_at")
    private LocalDateTime genereAt;

    @PrePersist
    protected void onCreate() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
        }
        if (this.creeAt == null) {
            this.creeAt = LocalDateTime.now();
        }
        if (this.statut == null) {
            this.statut = "EN_ATTENTE";
        }
    }

    public Rapport() {}

    public Long getId() { return id; }
    public UUID getUuid() { return uuid; }
    public Long getProjetId() { return projetId; }
    public Long getFormulaireId() { return formulaireId; }
    public String getType() { return type; }
    public String getTitre() { return titre; }
    public String getFormat() { return format; }
    public String getParametres() { return parametres; }
    public String getCheminFichier() { return cheminFichier; }
    public String getStatut() { return statut; }
    public String getErreurMessage() { return erreurMessage; }
    public Long getCreePar() { return creePar; }
    public LocalDateTime getCreeAt() { return creeAt; }
    public LocalDateTime getGenereAt() { return genereAt; }

    public void setProjetId(Long projetId) { this.projetId = projetId; }
    public void setFormulaireId(Long formulaireId) { this.formulaireId = formulaireId; }
    public void setType(String type) { this.type = type; }
    public void setTitre(String titre) { this.titre = titre; }
    public void setFormat(String format) { this.format = format; }
    public void setParametres(String parametres) { this.parametres = parametres; }
    public void setCheminFichier(String cheminFichier) { this.cheminFichier = cheminFichier; }
    public void setStatut(String statut) { this.statut = statut; }
    public void setErreurMessage(String erreurMessage) { this.erreurMessage = erreurMessage; }
    public void setCreePar(Long creePar) { this.creePar = creePar; }
    public void setCreeAt(LocalDateTime creeAt) { this.creeAt = creeAt; }
    public void setGenereAt(LocalDateTime genereAt) { this.genereAt = genereAt; }
}
