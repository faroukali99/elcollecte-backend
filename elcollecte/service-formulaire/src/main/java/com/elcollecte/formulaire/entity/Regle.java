package com.elcollecte.formulaire.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "regles")
public class Regle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "version_id", nullable = false)
    private FormulaireVersion version;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(name = "question_source_id")
    private Long questionSourceId;

    @Column(name = "valeur_attendue")
    private String valeurAttendue;

    @Column(name = "question_cible_id")
    private Long questionCibleId;

    @Column(name = "action", length = 50)
    private String action;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private java.util.Map<String, Object> parametres;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    public Regle() {}

    public Long getId() { return id; }
    public FormulaireVersion getVersion() { return version; }
    public String getType() { return type; }
    public Long getQuestionSourceId() { return questionSourceId; }
    public String getValeurAttendue() { return valeurAttendue; }
    public Long getQuestionCibleId() { return questionCibleId; }
    public String getAction() { return action; }
    public java.util.Map<String, Object> getParametres() { return parametres; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setVersion(FormulaireVersion version) { this.version = version; }
    public void setType(String type) { this.type = type; }
    public void setQuestionSourceId(Long questionSourceId) { this.questionSourceId = questionSourceId; }
    public void setValeurAttendue(String valeurAttendue) { this.valeurAttendue = valeurAttendue; }
    public void setQuestionCibleId(Long questionCibleId) { this.questionCibleId = questionCibleId; }
    public void setAction(String action) { this.action = action; }
    public void setParametres(java.util.Map<String, Object> parametres) { this.parametres = parametres; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
