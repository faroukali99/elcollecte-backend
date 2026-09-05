package com.elcollecte.utilisateur.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "profils")
public class Profil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 150)
    private String libelle;

    @Column
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;

    // Profil "système" = miroir d'un ancien Role. Ne peut pas être supprimé
    // ni renommé de code, afin de préserver la compatibilité de migration.
    @Column(name = "is_systeme")
    private boolean systeme = false;

    @Column(name = "is_active")
    private boolean active = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "profil", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProfilPermission> profilPermissions = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Profil() {}

    public Profil(String code, String libelle, String description,
                  Organisation organisation, boolean systeme) {
        this.code = code;
        this.libelle = libelle;
        this.description = description;
        this.organisation = organisation;
        this.systeme = systeme;
        this.active = true;
    }

    public Long          getId()               { return id; }
    public String        getCode()              { return code; }
    public String        getLibelle()           { return libelle; }
    public String        getDescription()       { return description; }
    public Organisation  getOrganisation()      { return organisation; }
    public boolean       isSysteme()            { return systeme; }
    public boolean       isActive()             { return active; }
    public LocalDateTime getCreatedAt()         { return createdAt; }
    public LocalDateTime getUpdatedAt()         { return updatedAt; }
    public Set<ProfilPermission> getProfilPermissions() { return profilPermissions; }

    public void setId(Long id)                       { this.id = id; }
    public void setCode(String code)                 { this.code = code; }
    public void setLibelle(String libelle)           { this.libelle = libelle; }
    public void setDescription(String description)   { this.description = description; }
    public void setOrganisation(Organisation o)      { this.organisation = o; }
    public void setSysteme(boolean systeme)          { this.systeme = systeme; }
    public void setActive(boolean active)            { this.active = active; }
    public void setCreatedAt(LocalDateTime t)        { this.createdAt = t; }
    public void setUpdatedAt(LocalDateTime t)        { this.updatedAt = t; }
}
