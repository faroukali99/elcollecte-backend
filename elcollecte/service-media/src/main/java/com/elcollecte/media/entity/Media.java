package com.elcollecte.media.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "medias")
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @Column(name = "nom_fichier", nullable = false)
    private String nomFichier;

    @Column(name = "nom_original")
    private String nomOriginal;

    @Column(name = "type_mime")
    private String typeMime;

    @Column(name = "taille_octets")
    private Long tailleOctets;

    @Column(name = "chemin_stockage", nullable = false)
    private String cheminStockage;

    @Column(name = "url_acces")
    private String urlAcces;

    @Column(name = "collecte_id")
    private Long collecteId;

    @Column(name = "projet_id")
    private Long projetId;

    @Column(name = "uploade_par", nullable = false)
    private Long uploadePar;

    @Column(name = "uploade_at", nullable = false)
    private LocalDateTime uploadeAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String metadonnees;

    @PrePersist
    protected void onCreate() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
        }
        if (this.uploadeAt == null) {
            this.uploadeAt = LocalDateTime.now();
        }
    }

    public Media() {}

    public Long getId() { return id; }
    public UUID getUuid() { return uuid; }
    public String getNomFichier() { return nomFichier; }
    public String getNomOriginal() { return nomOriginal; }
    public String getTypeMime() { return typeMime; }
    public Long getTailleOctets() { return tailleOctets; }
    public String getCheminStockage() { return cheminStockage; }
    public String getUrlAcces() { return urlAcces; }
    public Long getCollecteId() { return collecteId; }
    public Long getProjetId() { return projetId; }
    public Long getUploadePar() { return uploadePar; }
    public LocalDateTime getUploadeAt() { return uploadeAt; }
    public String getMetadonnees() { return metadonnees; }

    public void setNomFichier(String nomFichier) { this.nomFichier = nomFichier; }
    public void setNomOriginal(String nomOriginal) { this.nomOriginal = nomOriginal; }
    public void setTypeMime(String typeMime) { this.typeMime = typeMime; }
    public void setTailleOctets(Long tailleOctets) { this.tailleOctets = tailleOctets; }
    public void setCheminStockage(String cheminStockage) { this.cheminStockage = cheminStockage; }
    public void setUrlAcces(String urlAcces) { this.urlAcces = urlAcces; }
    public void setCollecteId(Long collecteId) { this.collecteId = collecteId; }
    public void setProjetId(Long projetId) { this.projetId = projetId; }
    public void setUploadePar(Long uploadePar) { this.uploadePar = uploadePar; }
    public void setUploadeAt(LocalDateTime uploadeAt) { this.uploadeAt = uploadeAt; }
    public void setMetadonnees(String metadonnees) { this.metadonnees = metadonnees; }
}
