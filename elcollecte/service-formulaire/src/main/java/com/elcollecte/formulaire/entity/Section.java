package com.elcollecte.formulaire.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sections")
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "version_id", nullable = false)
    private FormulaireVersion version;

    @Column(nullable = false, length = 200)
    private String titre;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Integer ordre;

    @Column(name = "is_repeatable")
    private boolean repeatable = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Question> questions = new HashSet<>();

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    public Section() {}

    public Long getId() { return id; }
    public FormulaireVersion getVersion() { return version; }
    public String getTitre() { return titre; }
    public String getDescription() { return description; }
    public Integer getOrdre() { return ordre; }
    public boolean isRepeatable() { return repeatable; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Set<Question> getQuestions() { return questions; }

    public void setId(Long id) { this.id = id; }
    public void setVersion(FormulaireVersion version) { this.version = version; }
    public void setTitre(String titre) { this.titre = titre; }
    public void setDescription(String description) { this.description = description; }
    public void setOrdre(Integer ordre) { this.ordre = ordre; }
    public void setRepeatable(boolean repeatable) { this.repeatable = repeatable; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setQuestions(Set<Question> questions) { this.questions = questions; }
}
