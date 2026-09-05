package com.elcollecte.utilisateur.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "permissions")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 80)
    private String code;

    @Column(nullable = false, length = 150)
    private String libelle;

    @Column(nullable = false, length = 50)
    private String categorie;

    @Column
    private String description;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Permission() {}

    public Permission(String code, String libelle, String categorie, String description) {
        this.code = code;
        this.libelle = libelle;
        this.categorie = categorie;
        this.description = description;
    }

    public Long          getId()          { return id; }
    public String        getCode()        { return code; }
    public String        getLibelle()     { return libelle; }
    public String        getCategorie()   { return categorie; }
    public String        getDescription() { return description; }
    public LocalDateTime getCreatedAt()   { return createdAt; }

    public void setId(Long id)                     { this.id = id; }
    public void setCode(String code)               { this.code = code; }
    public void setLibelle(String libelle)         { this.libelle = libelle; }
    public void setCategorie(String categorie)     { this.categorie = categorie; }
    public void setDescription(String description) { this.description = description; }
    public void setCreatedAt(LocalDateTime t)      { this.createdAt = t; }
}
