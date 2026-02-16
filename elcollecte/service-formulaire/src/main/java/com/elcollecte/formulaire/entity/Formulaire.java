package com.elcollecte.formulaire.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "formulaires")
public class Formulaire {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "projet_id",   nullable = false) private Long projetId;
    @Column(name = "createur_id", nullable = false) private Long createurId;
    @Column(nullable = false, length = 200)          private String titre;
    @Column(columnDefinition = "TEXT")               private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "schema_questions", columnDefinition = "jsonb")
    private List<Object> schemaQuestions;

    @Column(nullable = false) private int     version   = 1;
    @Column(name = "is_active")private boolean active   = true;
    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;

    @PrePersist  protected void onCreate() { this.createdAt = LocalDateTime.now(); }
    @PreUpdate   protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    public Formulaire() {}

    public Long         getId()              { return id; }
    public Long         getProjetId()        { return projetId; }
    public Long         getCreateurId()      { return createurId; }
    public String       getTitre()           { return titre; }
    public String       getDescription()     { return description; }
    public List<Object> getSchemaQuestions() { return schemaQuestions; }
    public int          getVersion()         { return version; }
    public boolean      isActive()           { return active; }
    public LocalDateTime getCreatedAt()      { return createdAt; }
    public LocalDateTime getUpdatedAt()      { return updatedAt; }

    public void setProjetId(Long v)              { this.projetId = v; }
    public void setCreateurId(Long v)            { this.createurId = v; }
    public void setTitre(String v)               { this.titre = v; }
    public void setDescription(String v)         { this.description = v; }
    public void setSchemaQuestions(List<Object> v){ this.schemaQuestions = v; }
    public void setVersion(int v)                { this.version = v; }
    public void setActive(boolean v)             { this.active = v; }
}