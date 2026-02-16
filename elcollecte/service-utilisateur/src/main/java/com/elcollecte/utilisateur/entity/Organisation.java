package com.elcollecte.utilisateur.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "organisations")
public class Organisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nom;

    @Column(name = "email_contact", unique = true, length = 150)
    private String emailContact;

    @Column(length = 20)
    private String telephone;

    @Column(length = 100)
    private String pays;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "is_active")
    private boolean active = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Organisation() {}

    public Organisation(Long id, String nom, String emailContact, String telephone,
                        String pays, String logoUrl, boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.nom = nom;
        this.emailContact = emailContact;
        this.telephone = telephone;
        this.pays = pays;
        this.logoUrl = logoUrl;
        this.active = active;
        this.createdAt = createdAt;
    }

    public Long getId()                   { return id; }
    public String getNom()                { return nom; }
    public String getEmailContact()       { return emailContact; }
    public String getTelephone()          { return telephone; }
    public String getPays()               { return pays; }
    public String getLogoUrl()            { return logoUrl; }
    public boolean isActive()             { return active; }
    public LocalDateTime getCreatedAt()   { return createdAt; }

    public void setId(Long id)            { this.id = id; }
    public void setNom(String nom)        { this.nom = nom; }
    public void setEmailContact(String v) { this.emailContact = v; }
    public void setTelephone(String v)    { this.telephone = v; }
    public void setPays(String pays)      { this.pays = pays; }
    public void setLogoUrl(String v)      { this.logoUrl = v; }
    public void setActive(boolean v)      { this.active = v; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
}
