package com.elcollecte.utilisateur.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "is_active")
    private boolean active = true;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public enum Role {
        ADMIN, CHEF_PROJET, ENQUETEUR, ANALYSTE
    }

    // ── Constructors ─────────────────────────────────────────────────────────

    public User() {}

    public User(String nom, String prenom, String email, String password,
                Role role, Organisation organisation) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.role = role;
        this.organisation = organisation;
        this.active = true;
    }

    // ── UserDetails ──────────────────────────────────────────────────────────

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override public String   getUsername()              { return email; }
    @Override public boolean  isEnabled()                { return active; }
    @Override public boolean  isAccountNonExpired()      { return true; }
    @Override public boolean  isAccountNonLocked()       { return true; }
    @Override public boolean  isCredentialsNonExpired()  { return true; }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public Long         getId()            { return id; }
    public Organisation getOrganisation()  { return organisation; }
    public String       getNom()           { return nom; }
    public String       getPrenom()        { return prenom; }
    public String       getEmail()         { return email; }
    public String       getPassword()      { return password; }
    public Role         getRole()          { return role; }
    public boolean      isActive()         { return active; }
    public String       getRefreshToken()  { return refreshToken; }
    public LocalDateTime getCreatedAt()    { return createdAt; }
    public LocalDateTime getLastLogin()    { return lastLogin; }

    public void setId(Long id)                       { this.id = id; }
    public void setOrganisation(Organisation o)      { this.organisation = o; }
    public void setNom(String nom)                   { this.nom = nom; }
    public void setPrenom(String prenom)             { this.prenom = prenom; }
    public void setEmail(String email)               { this.email = email; }
    public void setPassword(String password)         { this.password = password; }
    public void setRole(Role role)                   { this.role = role; }
    public void setActive(boolean active)            { this.active = active; }
    public void setRefreshToken(String token)        { this.refreshToken = token; }
    public void setCreatedAt(LocalDateTime t)        { this.createdAt = t; }
    public void setLastLogin(LocalDateTime t)        { this.lastLogin = t; }
}
