package com.elcollecte.formulaire.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "formulaire_versions")
public class FormulaireVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formulaire_id", nullable = false)
    private Formulaire formulaire;

    @Column(nullable = false)
    private Integer numeroVersion;

    @Column(length = 500)
    private String description;

    @Column(name = "is_publie")
    private boolean publie = false;

    @Column(name = "is_active")
    private boolean active = true;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "version", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Section> sections = new HashSet<>();

    @OneToMany(mappedBy = "version", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Regle> regles = new HashSet<>();

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    public FormulaireVersion() {}

    public Long getId() { return id; }
    public Formulaire getFormulaire() { return formulaire; }
    public Integer getNumeroVersion() { return numeroVersion; }
    public String getDescription() { return description; }
    public boolean isPublie() { return publie; }
    public boolean isActive() { return active; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Set<Section> getSections() { return sections; }
    public Set<Regle> getRegles() { return regles; }

    public void setId(Long id) { this.id = id; }
    public void setFormulaire(Formulaire formulaire) { this.formulaire = formulaire; }
    public void setNumeroVersion(Integer numeroVersion) { this.numeroVersion = numeroVersion; }
    public void setDescription(String description) { this.description = description; }
    public void setPublie(boolean publie) { this.publie = publie; }
    public void setActive(boolean active) { this.active = active; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setSections(Set<Section> sections) { this.sections = sections; }
    public void setRegles(Set<Regle> regles) { this.regles = regles; }
}
