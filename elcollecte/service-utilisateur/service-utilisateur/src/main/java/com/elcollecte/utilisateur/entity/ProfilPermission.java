package com.elcollecte.utilisateur.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "profil_permissions",
       uniqueConstraints = @UniqueConstraint(columnNames = {"profil_id", "permission_id"}))
public class ProfilPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profil_id", nullable = false)
    private Profil profil;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;

    @Column(name = "granted_at", updatable = false)
    private LocalDateTime grantedAt;

    @PrePersist
    protected void onCreate() {
        this.grantedAt = LocalDateTime.now();
    }

    public ProfilPermission() {}

    public ProfilPermission(Profil profil, Permission permission) {
        this.profil = profil;
        this.permission = permission;
    }

    public Long          getId()         { return id; }
    public Profil        getProfil()     { return profil; }
    public Permission    getPermission() { return permission; }
    public LocalDateTime getGrantedAt()  { return grantedAt; }

    public void setId(Long id)                  { this.id = id; }
    public void setProfil(Profil profil)        { this.profil = profil; }
    public void setPermission(Permission p)     { this.permission = p; }
    public void setGrantedAt(LocalDateTime t)   { this.grantedAt = t; }
}
