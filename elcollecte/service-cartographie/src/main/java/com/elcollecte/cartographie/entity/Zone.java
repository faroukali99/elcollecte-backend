package com.elcollecte.cartographie.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "zones")
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nom;

    @Column(length = 500)
    private String description;

    @Column(name = "projet_id")
    private Long projetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeZone type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private java.util.Map<String, Object> properties;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    public enum TypeZone {
        PROJET, SECTEUR, QUARTIER, REGION, PAYS, CUSTOM
    }

    public Zone() {}

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public String getDescription() { return description; }
    public Long getProjetId() { return projetId; }
    public TypeZone getType() { return type; }
    public java.util.Map<String, Object> getProperties() { return properties; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setId(Long id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setDescription(String description) { this.description = description; }
    public void setProjetId(Long projetId) { this.projetId = projetId; }
    public void setType(TypeZone type) { this.type = type; }
    public void setProperties(java.util.Map<String, Object> properties) { this.properties = properties; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
