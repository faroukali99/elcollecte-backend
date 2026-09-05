package com.elcollecte.cartographie.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "points_interet")
public class PointInteret {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "zone_id")
    private Long zoneId;

    @Column(nullable = false, length = 200)
    private String nom;

    @Column(length = 500)
    private String description;

    @Column(precision = 10, scale = 7, nullable = false)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7, nullable = false)
    private BigDecimal longitude;

    @Column(name = "altitude")
    private BigDecimal altitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypePoint type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private java.util.Map<String, Object> properties;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    public enum TypePoint {
        COLLECTE, ENQUETE, OBSERVATION, MARQUEUR, ENTREE, SORTIE, CUSTOM
    }

    public PointInteret() {}

    public Long getId() { return id; }
    public Long getZoneId() { return zoneId; }
    public String getNom() { return nom; }
    public String getDescription() { return description; }
    public BigDecimal getLatitude() { return latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public BigDecimal getAltitude() { return altitude; }
    public TypePoint getType() { return type; }
    public java.util.Map<String, Object> getProperties() { return properties; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setZoneId(Long zoneId) { this.zoneId = zoneId; }
    public void setNom(String nom) { this.nom = nom; }
    public void setDescription(String description) { this.description = description; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public void setAltitude(BigDecimal altitude) { this.altitude = altitude; }
    public void setType(TypePoint type) { this.type = type; }
    public void setProperties(java.util.Map<String, Object> properties) { this.properties = properties; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
