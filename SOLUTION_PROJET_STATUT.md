# Résumé des modifications - Service Projet

## Problème résolu
L'erreur "No enum constant com.elcollecte.projet.entity.Projet.Statut.VALIDE" a été corrigée en ajoutant les statuts manquants.

## Modifications effectuées

### 1. **Entity Projet.java** ✓
- Ajout de l'énumération `VALIDE` et `REJETE` aux statuts
- Ajout du champ `motifRejet` (String) pour stocker le motif de rejet
- Ajout du getter `getMotifRejet()`
- Ajout du setter `setMotifRejet()`

### 2. **DTO ProjetDto.java** ✓
- Ajout du champ `motifRejet` au record
- Mise à jour de la méthode `from()` pour inclure `p.getMotifRejet()`

### 3. **DTO UpdateProjetRequest.java** ✓
- Ajout du paramètre `motifRejet` au record

### 4. **Service ProjetService.java** ✓
- Mise à jour de la méthode `update()` pour gérer le champ `motifRejet`

### 5. **Migrations Flyway** ✓
- **V2__add_statuts_projet.sql** : Ajoute les statuts VALIDE et REJETE à l'énumération PostgreSQL
- **V3__add_motif_rejet.sql** : Ajoute la colonne `motif_rejet` à la table projets

## Énumération complète des statuts

```java
public enum Statut {
    BROUILLON,  // Projet en cours de création
    ACTIF,      // Projet actif avec collectes en cours
    SUSPENDU,   // Projet temporairement suspendu
    TERMINE,    // Projet terminé
    VALIDE,     // Projet validé par un administrateur
    REJETE      // Projet rejeté avec motif
}
```

## Flux de validation des projets

1. **Création** : État initial = BROUILLON
2. **Activation** : Un chef de projet change le statut à ACTIF
3. **Validation** : Un administrateur peut changer le statut à VALIDE
4. **Rejet** : Un administrateur peut changer le statut à REJETE avec motif
5. **Suspension** : Peut être appliqué à tout moment
6. **Fin** : Le statut devient TERMINE

## Architecture des endpoints

### GET /api/projets
Récupère tous les projets avec leurs statuts et motifs de rejet

### PUT /api/projets/{id}
Met à jour un projet avec:
- `titre`, `description`, `dateFin`, `zoneGeo` : modifiables à tout moment
- `statut` : changement de statut
- `motifRejet` : ajouté lors du rejet (statut = "REJETE")

## Structure du frontend

La page de gestion des projets (ProjetManagement.jsx) doit afficher:
- Liste des projets
- État de chaque projet
- Boutons d'action selon l'état:
  - **BROUILLON** : Activer, Rejeter
  - **ACTIF** : Valider, Rejeter, Suspendre
  - **SUSPENDING** : Réactiver, Terminer
  - **REJETE** : Afficher le motif, Réactiver
  - **VALIDE** : Aucune action
  - **TERMINE** : Aucune action

## Tests effectués

- ✓ Compilation du service-projet
- ✓ Vérification de la migration Flyway
- ✓ Vérification des DTOs et services

## Points importants

1. L'énumération PostgreSQL a été étendue avec ALTER TYPE
2. La colonne `motif_rejet` accepte NULL (projets non rejetés)
3. Les migrations sont versionnées et seront appliquées au démarrage
4. Le ProjetDto inclut le motifRejet pour les réponses API

