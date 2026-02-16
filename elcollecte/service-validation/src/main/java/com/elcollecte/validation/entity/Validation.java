package com.elcollecte.validation.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "validations")
public class Validation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "collecte_id", nullable = false, unique = true)
    private Long collecteId;

    @Column(name = "projet_id", nullable = false)
    private Long projetId;

    @Column(name = "validateur_id")
    private Long validateurId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Statut statut = Statut.EN_ATTENTE;

    @Column(columnDefinition = "TEXT") private String commentaire;
    @Column(name = "motif_rejet", columnDefinition = "TEXT") private String motifRejet;

    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at")                    private LocalDateTime updatedAt;

    @PrePersist  protected void onCreate() { this.createdAt = LocalDateTime.now(); }
    @PreUpdate   protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    public enum Statut { EN_ATTENTE, VALIDE, REJETE, REVISION }

    public Validation() {}

    public Long         getId()            { return id; }
    public Long         getCollecteId()    { return collecteId; }
    public Long         getProjetId()      { return projetId; }
    public Long         getValidateurId()  { return validateurId; }
    public Statut       getStatut()        { return statut; }
    public String       getCommentaire()   { return commentaire; }
    public String       getMotifRejet()    { return motifRejet; }
    public LocalDateTime getCreatedAt()   { return createdAt; }
    public LocalDateTime getUpdatedAt()   { return updatedAt; }

    public void setCollecteId(Long v)    { this.collecteId = v; }
    public void setProjetId(Long v)      { this.projetId = v; }
    public void setValidateurId(Long v)  { this.validateurId = v; }
    public void setStatut(Statut v)      { this.statut = v; }
    public void setCommentaire(String v) { this.commentaire = v; }
    public void setMotifRejet(String v)  { this.motifRejet = v; }
}