package com.elcollecte.validation.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "validations_log")
public class ValidationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "formulaire_id")
    private Long formulaireId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "type_validation", nullable = false)
    private String typeValidation;

    @Column(name = "champ_nom")
    private String champNom;

    @Column(name = "resultat", nullable = false)
    private Boolean resultat;

    @Column(name = "nb_erreurs", nullable = false)
    private Integer nbErreurs = 0;

    @Column(name = "score_completude", nullable = false)
    private Integer scoreCompletude = 0;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public ValidationLog() {}

    public Long getId() { return id; }
    public Long getFormulaireId() { return formulaireId; }
    public Long getUserId() { return userId; }
    public String getTypeValidation() { return typeValidation; }
    public String getChampNom() { return champNom; }
    public Boolean getResultat() { return resultat; }
    public Integer getNbErreurs() { return nbErreurs; }
    public Integer getScoreCompletude() { return scoreCompletude; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setFormulaireId(Long formulaireId) { this.formulaireId = formulaireId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setTypeValidation(String typeValidation) { this.typeValidation = typeValidation; }
    public void setChampNom(String champNom) { this.champNom = champNom; }
    public void setResultat(Boolean resultat) { this.resultat = resultat; }
    public void setNbErreurs(Integer nbErreurs) { this.nbErreurs = nbErreurs; }
    public void setScoreCompletude(Integer scoreCompletude) { this.scoreCompletude = scoreCompletude; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
