package com.elcollecte.validation.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "regles_qualite")
public class RegleQualite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "formulaire_version_id")
    private Long formulaireVersionId;

    @Column(name = "question_id")
    private Long questionId;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(length = 100)
    private String parametre;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> config;

    @Column(name = "is_actif")
    private boolean actif = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    public RegleQualite() {}

    public Long getId() { return id; }
    public Long getFormulaireVersionId() { return formulaireVersionId; }
    public Long getQuestionId() { return questionId; }
    public String getType() { return type; }
    public String getParametre() { return parametre; }
    public Map<String, Object> getConfig() { return config; }
    public boolean isActif() { return actif; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setFormulaireVersionId(Long formulaireVersionId) { this.formulaireVersionId = formulaireVersionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public void setType(String type) { this.type = type; }
    public void setParametre(String parametre) { this.parametre = parametre; }
    public void setConfig(Map<String, Object> config) { this.config = config; }
    public void setActif(boolean actif) { this.actif = actif; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
