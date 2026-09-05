package com.elcollecte.formulaire.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "option_choix")
public class OptionChoix {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false, length = 100)
    private String code;

    @Column(nullable = false, length = 200)
    private String libelle;

    @Column(nullable = false)
    private Integer ordre;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    public OptionChoix() {}

    public Long getId() { return id; }
    public Question getQuestion() { return question; }
    public String getCode() { return code; }
    public String getLibelle() { return libelle; }
    public Integer getOrdre() { return ordre; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setQuestion(Question question) { this.question = question; }
    public void setCode(String code) { this.code = code; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public void setOrdre(Integer ordre) { this.ordre = ordre; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
