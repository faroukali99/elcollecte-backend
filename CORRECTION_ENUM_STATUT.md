# CORRECTION - SERVICE COLLECTE

## Problèmes Identifiés et Résolus

### 1. **Erreur PostgreSQL - Type ENUM statut**
**Problème:**
```
ERREUR: la colonne « statut » est de type collecte_statut mais l'expression est de type character varying
```

**Cause:** 
- L'annotation `@Column(columnDefinition = "collecte_statut")` force Hibernate à utiliser un type PostgreSQL custom au lieu de ENUM STRING
- Cela crée une incompatibilité entre ce que PostgreSQL attend et ce que Hibernate envoie

**Solution:**
```java
// ❌ AVANT (INCORRECT)
@Column(nullable = false, columnDefinition = "collecte_statut")
private Statut statut = Statut.SOUMIS;

// ✅ APRÈS (CORRECT)
@Column(nullable = false)
private Statut statut = Statut.SOUMIS;
```

### 2. **Erreur Hibernate - JSON Map Non Initialisée**
**Problème:**
```
org.hibernate.type.descriptor.java.spi.JdbcTypeRecommendationException: 
Could not determine recommended JdbcType for Java type 'java.util.Map<java.lang.String, java.lang.Object>'
```

**Cause:**
- Les champs `donnees` et `medias` n'étaient pas initialisés
- Hibernate ne savait pas comment gérer les Maps nulles avec PostgreSQL JSONB

**Solution:**
```java
// ❌ AVANT (INCORRECT)
@JdbcTypeCode(SqlTypes.JSON)
@Column(nullable = false, columnDefinition = "jsonb")
private Map<String, Object> donnees;

// ✅ APRÈS (CORRECT)
@JdbcTypeCode(SqlTypes.JSON)
@Column(nullable = false, columnDefinition = "jsonb")
private Map<String, Object> donnees = new HashMap<>();
```

### 3. **Initialisation des Valeurs Par Défaut**
- Tous les champs Map sont maintenant initialisés avec `new HashMap<>()`
- Cela évite les problèmes de sérialisation JSON lors de l'insertion en base

## Fichier Modifié
- `service-collecte/src/main/java/com/elcollecte/collecte/entity/CollecteData.java`

## Changements Techniques

### Avant
```java
private Map<String, Object> donnees;
private Map<String, Object> medias;
@Column(nullable = false, columnDefinition = "collecte_statut")
private Statut statut = Statut.SOUMIS;
```

### Après
```java
private Map<String, Object> donnees = new HashMap<>();
private Map<String, Object> medias = new HashMap<>();
@Column(nullable = false)
private Statut statut = Statut.SOUMIS;
```

## Instructions pour Relancer

### Option 1: Avec Maven (Recommandé)
```powershell
cd "C:\Semestre 5\Projet tuteure\Diagrammes UML\elcollecte-backend"
.\COMPILE_AND_RUN_COLLECTE.ps1
```

### Option 2: Sans Maven (JAR pré-compilé)
```powershell
cd "C:\Semestre 5\Projet tuteure\Diagrammes UML\elcollecte-backend"
.\START_SERVICE_COLLECTE.ps1
```

## Résultat Attendu
- ✅ Service démarre sans erreurs Hibernate
- ✅ Base de données PostgreSQL reconnaît les types correctement
- ✅ Les insertions de collectes fonctionnent sans TypeCastException
- ✅ Les enums statut sont correctement persistés
- ✅ Les données JSON sont correctement sérialisées

## Validation
Après le redémarrage, vérifiez que:
1. Le service démarre correctement sur le port 8084
2. Les logs ne contiennent pas d'erreurs PostgreSQL
3. Vous pouvez créer une nouvelle collecte sans erreur de validation
4. Les données sont correctement sauvegardées en base


