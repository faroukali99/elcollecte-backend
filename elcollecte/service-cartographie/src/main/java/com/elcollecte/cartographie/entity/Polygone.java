package com.elcollecte.cartographie.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "polygones")
public class Polygone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "zone_id")
    private Long zoneId;

    @Column(nullable = false, length = 200)
    private String nom;

    @Column(length = 500)
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private java.util.Map<String, Object> coordinates;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypePolygone type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private java.util.Map<String, Object> properties;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    public enum TypePolygone {
        SECTEUR, QUARTIER, ZONE_COLLECTE, ZONE_EXCLUSION, CUSTOM
    }

    public Polygone() {}

    public Long getId() { return id; }
    public Long getZoneId() { return zoneId; }
    public String getNom() { return nom; }
    public String getDescription() { return description; }
    public java.util.Map<String, Object> getCoordinates() { return coordinates; }
    public TypePolygone getType() { return type; }
    public java.util.Map<String, Object> getProperties() { return properties; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setZoneId(Long zoneId) { this.zoneId = zoneId; }
    public void setNom(String nom) { this.nom = nom; }
    public void setDescription(String description) { this.description = description; }
    public void setCoordinates(java.util.Map<String, Object> coordinates) { this.coordinates = coordinates; }
    public void setType(TypePolygone type) { this.type = type; }
    public void setProperties(java.util.Map<String, Object> properties) { this.properties = properties; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
