package com.elcollecte.formulaire.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "formulaires")
public class Formulaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nom;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, length = 50)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;

    @Column(name = "is_actif")
    private boolean actif = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "formulaire", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FormulaireVersion> versions = new HashSet<>();

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    public Formulaire() {}

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public String getDescription() { return description; }
    public String getCode() { return code; }
    public Organisation getOrganisation() { return organisation; }
    public boolean isActif() { return actif; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Set<FormulaireVersion> getVersions() { return versions; }

    public void setId(Long id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setDescription(String description) { this.description = description; }
    public void setCode(String code) { this.code = code; }
    public void setOrganisation(Organisation organisation) { this.organisation = organisation; }
    public void setActif(boolean actif) { this.actif = actif; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setVersions(Set<FormulaireVersion> versions) { this.versions = versions; }
}
