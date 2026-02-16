package com.elcollecte.collecte.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "collectes")
public class CollecteData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @UuidGenerator
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @Column(name = "formulaire_id", nullable = false)
    private Long formulaireId;

    @Column(name = "enqueteur_id", nullable = false)
    private Long enqueteurId;

    @Column(name = "projet_id", nullable = false)
    private Long projetId;

    @Column(name = "validateur_id")
    private Long validateurId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> donnees;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Object medias;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Statut statut = Statut.SOUMIS;

    @Column(name = "is_offline")
    private boolean offline = false;

    @Column(name = "motif_rejet")
    private String motifRejet;

    @Column(name = "collected_at")
    private LocalDateTime collectedAt;

    @Column(name = "synced_at")
    private LocalDateTime syncedAt;

    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    public enum Statut { BROUILLON, SOUMIS, VALIDE, REJETE }

    @PrePersist
    protected void onCreate() {
        if (this.collectedAt == null) this.collectedAt = LocalDateTime.now();
    }

    public CollecteData() {}

    // ── Getters ───────────────────────────────────────────────────────────────
    public Long                  getId()            { return id; }
    public UUID                  getUuid()          { return uuid; }
    public Long                  getFormulaireId()  { return formulaireId; }
    public Long                  getEnqueteurId()   { return enqueteurId; }
    public Long                  getProjetId()      { return projetId; }
    public Long                  getValidateurId()  { return validateurId; }
    public Map<String, Object>   getDonnees()       { return donnees; }
    public BigDecimal            getLatitude()      { return latitude; }
    public BigDecimal            getLongitude()     { return longitude; }
    public Object                getMedias()        { return medias; }
    public Statut                getStatut()        { return statut; }
    public boolean               isOffline()        { return offline; }
    public String                getMotifRejet()    { return motifRejet; }
    public LocalDateTime         getCollectedAt()   { return collectedAt; }
    public LocalDateTime         getSyncedAt()      { return syncedAt; }
    public LocalDateTime         getValidatedAt()   { return validatedAt; }

    // ── Setters ───────────────────────────────────────────────────────────────
    public void setFormulaireId(Long v)             { this.formulaireId = v; }
    public void setEnqueteurId(Long v)              { this.enqueteurId = v; }
    public void setProjetId(Long v)                 { this.projetId = v; }
    public void setValidateurId(Long v)             { this.validateurId = v; }
    public void setDonnees(Map<String, Object> v)   { this.donnees = v; }
    public void setLatitude(BigDecimal v)           { this.latitude = v; }
    public void setLongitude(BigDecimal v)          { this.longitude = v; }
    public void setMedias(Object v)                 { this.medias = v; }
    public void setStatut(Statut v)                 { this.statut = v; }
    public void setOffline(boolean v)               { this.offline = v; }
    public void setMotifRejet(String v)             { this.motifRejet = v; }
    public void setCollectedAt(LocalDateTime v)     { this.collectedAt = v; }
    public void setSyncedAt(LocalDateTime v)        { this.syncedAt = v; }
    public void setValidatedAt(LocalDateTime v)     { this.validatedAt = v; }
}
