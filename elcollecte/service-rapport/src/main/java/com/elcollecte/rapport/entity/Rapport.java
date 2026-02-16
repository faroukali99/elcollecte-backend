package com.elcollecte.rapport.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "rapports")
public class Rapport {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "projet_id",    nullable = false) private Long   projetId;
    @Column(name = "demandeur_id", nullable = false) private Long   demandeurId;
    @Column(nullable = false, length = 200)          private String titre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false) private Format format = Format.PDF;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false) private Statut statut = Statut.EN_ATTENTE;

    @Column(name = "url_fichier")   private String urlFichier;
    @Column(name = "taille_bytes")  private Long   tailleBytes;
    @Column(name = "message_erreur", columnDefinition = "TEXT") private String messageErreur;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb") private Map<String, Object> parametres;

    @Column(name = "created_at",   updatable = false) private LocalDateTime createdAt;
    @Column(name = "generated_at")                    private LocalDateTime generatedAt;

    @PrePersist protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    public enum Format { PDF, XLSX, CSV }
    public enum Statut { EN_ATTENTE, EN_COURS, TERMINE, ERREUR }

    public Rapport() {}

    public Long   getId()           { return id; }
    public Long   getProjetId()     { return projetId; }
    public Long   getDemandeurId()  { return demandeurId; }
    public String getTitre()        { return titre; }
    public Format getFormat()       { return format; }
    public Statut getStatut()       { return statut; }
    public String getUrlFichier()   { return urlFichier; }
    public Long   getTailleBytes()  { return tailleBytes; }
    public String getMessageErreur(){ return messageErreur; }
    public Map<String, Object> getParametres() { return parametres; }
    public LocalDateTime getCreatedAt()   { return createdAt; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }

    public void setProjetId(Long v)     { this.projetId = v; }
    public void setDemandeurId(Long v)  { this.demandeurId = v; }
    public void setTitre(String v)      { this.titre = v; }
    public void setFormat(Format v)     { this.format = v; }
    public void setStatut(Statut v)     { this.statut = v; }
    public void setUrlFichier(String v) { this.urlFichier = v; }
    public void setTailleBytes(Long v)  { this.tailleBytes = v; }
    public void setMessageErreur(String v) { this.messageErreur = v; }
    public void setParametres(Map<String, Object> v) { this.parametres = v; }
    public void setGeneratedAt(LocalDateTime v)      { this.generatedAt = v; }
}