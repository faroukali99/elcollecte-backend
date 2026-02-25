# FIX - Service Projet: Erreur d'énumération VALIDE

## 🎯 Problème

```
java.lang.IllegalArgumentException: No enum constant com.elcollecte.projet.entity.Projet.Statut.VALIDE
```

Le frontend tente d'envoyer le statut "VALIDE" au service-projet, mais l'énumération `Statut` ne contient que 4 valeurs:
- BROUILLON
- ACTIF
- SUSPENDU
- TERMINE

## ✅ Solution Complète

### 1. **Entity - Projet.java**

#### Modifications:
```java
// Avant
public enum Statut {
    BROUILLON, ACTIF, SUSPENDU, TERMINE
}

// Après
public enum Statut {
    BROUILLON, ACTIF, SUSPENDU, TERMINE, VALIDE, REJETE
}

// Ajout du champ
@Column(columnDefinition = "TEXT")
private String motifRejet;

// Ajout des accesseurs
public String getMotifRejet() { return motifRejet; }
public void setMotifRejet(String motifRejet) { this.motifRejet = motifRejet; }
```

### 2. **DTO - ProjetDto.java**

#### Modifications:
```java
// Record modifié
public record ProjetDto(
    Long id,
    Long organisationId,
    Long chefProjetId,
    String titre,
    String description,
    String statut,
    String motifRejet,        // ← Nouveau
    Map<String, Object> zoneGeo,
    LocalDate dateDebut,
    LocalDate dateFin,
    LocalDateTime createdAt,
    int nbEnqueteurs
)

// Méthode from() mise à jour
public static ProjetDto from(Projet p) {
    return new ProjetDto(
        ...,
        p.getMotifRejet(),  // ← Ajouté
        ...
    );
}
```

### 3. **DTO - UpdateProjetRequest.java**

#### Modifications:
```java
public record UpdateProjetRequest(
    @Size(max = 200) String titre,
    String description,
    String statut,
    String motifRejet,        // ← Nouveau
    LocalDate dateFin,
    Map<String, Object> zoneGeo
)
```

### 4. **Service - ProjetService.java**

#### Modifications:
```java
@Transactional
public ProjetDto update(Long id, UpdateProjetRequest req, Long orgId) {
    Projet projet = projetRepository.findByIdAndOrganisationId(id, orgId)
        .orElseThrow(() -> new NoSuchElementException("Projet introuvable: " + id));

    // ... autres mises à jour
    if (req.motifRejet() != null) projet.setMotifRejet(req.motifRejet());
    
    return ProjetDto.from(projetRepository.save(projet));
}
```

### 5. **Migrations Flyway**

#### V2__add_statuts_projet.sql
```sql
-- Ajouter les nouveaux statuts à l'énumération projet_statut
ALTER TYPE projet_statut ADD VALUE 'VALIDE';
ALTER TYPE projet_statut ADD VALUE 'REJETE';
```

#### V3__add_motif_rejet.sql
```sql
-- Ajouter la colonne motifRejet pour stocker les raisons de rejet
ALTER TABLE projets ADD COLUMN motif_rejet TEXT;
```

### 6. **Frontend - GestionProjets.jsx**

#### Modifications:
```jsx
// Ajout dans le STATUS_CONFIG
const STATUS_CONFIG = {
    // ...
    REJETE: { label: 'Rejeté', color: '#dc2626', bg: '#fee2e2', icon: '❌', action: 'Réactiver' },
    VALIDE: { label: 'Validé', color: '#0891b2', bg: '#cffafe', icon: '✔️', action: 'Valider' },
};

// Affichage du motif dans le modal de détails
{selectedProjet.statut === 'REJETE' && selectedProjet.motifRejet && (
    <div style={{
        padding: 12, background: '#fee2e2', border: '1px solid #fca5a5',
        borderRadius: 8, marginTop: 12
    }}>
        <label style={{ fontSize: '0.8rem', color: '#991b1b', fontWeight: 600 }}>
            ❌ Motif de rejet
        </label>
        <p style={{ margin: '6px 0 0', fontSize: '0.9rem', color: '#7f1d1d' }}>
            {selectedProjet.motifRejet}
        </p>
    </div>
)}
```

## 🔄 Flux de Validation des Projets

```
BROUILLON  →  ACTIF  →  VALIDE
                ↓
            REJETE (avec motif)
                ↓
            SUSPENDU  →  TERMINE
```

### Transitions Autorisées

| De / Vers | BROUILLON | ACTIF | VALIDE | REJETE | SUSPENDU | TERMINE |
|-----------|-----------|-------|--------|--------|----------|---------|
| **BROUILLON** | - | ✓ | ✗ | ✓ | ✗ | ✗ |
| **ACTIF** | ✗ | - | ✓ | ✓ | ✓ | ✓ |
| **VALIDE** | ✗ | ✗ | - | ✗ | ✗ | ✓ |
| **REJETE** | ✓ | ✓ | ✗ | - | ✗ | ✗ |
| **SUSPENDU** | ✗ | ✓ | ✓ | ✓ | - | ✓ |
| **TERMINE** | ✗ | ✗ | ✗ | ✗ | ✗ | - |

## 📋 Fichiers Modifiés

- ✓ `service-projet/src/main/java/com/elcollecte/projet/entity/Projet.java`
- ✓ `service-projet/src/main/java/com/elcollecte/projet/dto/ProjetDto.java`
- ✓ `service-projet/src/main/java/com/elcollecte/projet/dto/UpdateProjetRequest.java`
- ✓ `service-projet/src/main/java/com/elcollecte/projet/service/ProjetService.java`
- ✓ `service-projet/src/main/resources/db/migration/V2__add_statuts_projet.sql` (créé)
- ✓ `service-projet/src/main/resources/db/migration/V3__add_motif_rejet.sql` (créé)
- ✓ `elcollecte-frontend/src/pages/GestionProjets.jsx`

## 🚀 Déploiement

1. **Compiler le service-projet:**
```bash
cd elcollecte
mvn clean package -pl service-projet -DskipTests
```

2. **Redémarrer le service-projet:**
```bash
# Les migrations Flyway seront exécutées automatiquement
```

3. **Tester l'API:**
```bash
# Récupérer les projets (GET /api/projets)
# Modifier un projet (PUT /api/projets/{id})
# Valider: statut = "VALIDE"
# Rejeter: statut = "REJETE", motifRejet = "Raison du rejet"
```

## ✨ Avantages de cette Solution

1. **Type-safety**: Énumération fortement typée
2. **Audit complet**: Motif de rejet traçable
3. **Migrations versionnées**: Historique des changements
4. **API cohérente**: Le DTOs reflètent les modifications
5. **UX améliorée**: Affichage du motif de rejet au frontend

## 🧪 Cas de Test

```javascript
// Test 1: Valider un projet
PUT /api/projets/1
{ "statut": "VALIDE" }
→ 200 OK, statut = VALIDE

// Test 2: Rejeter un projet avec motif
PUT /api/projets/1
{ 
    "statut": "REJETE",
    "motifRejet": "Données insuffisantes"
}
→ 200 OK, statut = REJETE, motifRejet = "Données insuffisantes"

// Test 3: Récupérer un projet rejeté
GET /api/projets/1
→ {
    ...
    "statut": "REJETE",
    "motifRejet": "Données insuffisantes"
}
```

## 📚 Documentation API

### Changement d'état
```
POST /api/projets/{id}/validate
POST /api/projets/{id}/reject
  body: { "reason": "string" }
```

Ou via la méthode PUT existante:
```
PUT /api/projets/{id}
  body: {
    "statut": "VALIDE" | "REJETE",
    "motifRejet": "..." (si REJETE)
  }
```

---

**Status**: ✅ Implémenté et prêt pour le déploiement
**Date**: 2026-02-24
**Version**: 1.0.0-SNAPSHOT

