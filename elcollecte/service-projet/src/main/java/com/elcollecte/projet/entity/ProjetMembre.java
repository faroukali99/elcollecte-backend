package com.elcollecte.projet.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "projet_membres")
public class ProjetMembre {

    @EmbeddedId
    private ProjetMembreId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("projetId")
    @JoinColumn(name = "projet_id")
    private Projet projet;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    @Column(name = "role_membre", length = 50)
    private String roleMembre;

    @Column(name = "assigned_at", updatable = false)
    private LocalDateTime assignedAt;

    @Column(name = "is_active")
    private boolean active = true;

    @PrePersist
    protected void onCreate() { this.assignedAt = LocalDateTime.now(); }

    public ProjetMembre() {}

    public ProjetMembre(Projet projet, Long userId, String roleMembre) {
        this.id = new ProjetMembreId(projet.getId(), userId);
        this.projet = projet;
        this.userId = userId;
        this.roleMembre = roleMembre;
        this.active = true;
    }

    public ProjetMembreId getId()          { return id; }
    public Projet getProjet()               { return projet; }
    public Long getUserId()                 { return userId; }
    public String getRoleMembre()           { return roleMembre; }
    public LocalDateTime getAssignedAt()    { return assignedAt; }
    public boolean isActive()               { return active; }

    public void setActive(boolean active)   { this.active = active; }
    public void setRoleMembre(String role)  { this.roleMembre = role; }

    @Embeddable
    public static class ProjetMembreId implements java.io.Serializable {
        @Column(name = "projet_id")
        private Long projetId;
        @Column(name = "user_id")
        private Long userId;

        public ProjetMembreId() {}
        public ProjetMembreId(Long projetId, Long userId) {
            this.projetId = projetId;
            this.userId   = userId;
        }
        public Long getProjetId() { return projetId; }
        public Long getUserId()   { return userId; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ProjetMembreId that)) return false;
            return java.util.Objects.equals(projetId, that.projetId)
                && java.util.Objects.equals(userId, that.userId);
        }
        @Override
        public int hashCode() {
            return java.util.Objects.hash(projetId, userId);
        }
    }
}
