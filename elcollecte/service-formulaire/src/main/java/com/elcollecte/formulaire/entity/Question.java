package com.elcollecte.formulaire.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @Column(nullable = false, length = 200)
    private String libelle;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private TypeQuestion type;

    @Column(nullable = false)
    private Integer ordre;

    @Column(name = "is_required")
    private boolean required = false;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private java.util.Map<String, Object> options;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private java.util.Map<String, Object> validation;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OptionChoix> choixOptions = new HashSet<>();

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    public enum TypeQuestion {
        TEXT, NUMBER, DECIMAL, DATE, TIME, DATETIME,
        BOOLEAN, SINGLE_CHOICE, MULTIPLE_CHOICE, SELECT,
        PHOTO, VIDEO, AUDIO, GPS, SIGNATURE, FILE
    }

    public Question() {}

    public Long getId() { return id; }
    public Section getSection() { return section; }
    public String getLibelle() { return libelle; }
    public String getDescription() { return description; }
    public TypeQuestion getType() { return type; }
    public Integer getOrdre() { return ordre; }
    public boolean isRequired() { return required; }
    public java.util.Map<String, Object> getOptions() { return options; }
    public java.util.Map<String, Object> getValidation() { return validation; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Set<OptionChoix> getChoixOptions() { return choixOptions; }

    public void setId(Long id) { this.id = id; }
    public void setSection(Section section) { this.section = section; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public void setDescription(String description) { this.description = description; }
    public void setType(TypeQuestion type) { this.type = type; }
    public void setOrdre(Integer ordre) { this.ordre = ordre; }
    public void setRequired(boolean required) { this.required = required; }
    public void setOptions(java.util.Map<String, Object> options) { this.options = options; }
    public void setValidation(java.util.Map<String, Object> validation) { this.validation = validation; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setChoixOptions(Set<OptionChoix> choixOptions) { this.choixOptions = choixOptions; }
}
