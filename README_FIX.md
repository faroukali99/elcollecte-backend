# FIX SERVICE-COLLECTE - Erreur Enum PostgreSQL

## Problème
```
ERREUR: la colonne « statut » est de type collecte_statut mais l'expression est de type character varying
```

## Solution Appliquée

Le fichier `CollecteData.java` a été corrigé:
- ✅ Imports nettoyés (suppression de `JdbcTypeCode`, `SqlTypes`)
- ✅ Champ `donnees`: `@Column(nullable = false, columnDefinition = "jsonb")`
- ✅ Champ `medias`: `@Column(columnDefinition = "jsonb")`
- ✅ Champ `statut`: `@Enumerated(EnumType.STRING)` + `@Column(nullable = false, columnDefinition = "collecte_statut")`

## Exécution du Fix

### Option 1: Script PowerShell Complet (Recommandé) ⭐

Exécutez ce script qui automatise tout (recompile, redéploie, teste):

```powershell
# Ouvrir PowerShell et exécuter:
Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope Process -Force
& "C:\Semestre 5\Projet tuteure\Diagrammes UML\elcollecte-backend\FIX_AND_DEPLOY.ps1"
```

**Ce script:**
- ✓ Recompile le module avec Maven
- ✓ Arrête l'ancienne instance Java
- ✓ Lance le nouveau JAR
- ✓ Teste l'API automatiquement
- ✓ Affiche les résultats

### Option 2: Commandes Manuelles (Si le script ne fonctionne pas)

**1. Recompiler:**
```bash
cd "C:\Semestre 5\Projet tuteure\Diagrammes UML\elcollecte-backend\elcollecte"
mvn clean package -DskipTests -pl service-collecte -am
```

**2. Arrêter l'instance actuellement en exécution:**
```bash
taskkill /F /IM java.exe
```
Ou si en foreground: `Ctrl+C`

**3. Redémarrer le service:**
```bash
java -jar "C:\Semestre 5\Projet tuteure\Diagrammes UML\elcollecte-backend\elcollecte\service-collecte\target\service-collecte-1.0.0-SNAPSHOT.jar"
```

**4. Tester (ouvrir PowerShell):**
```powershell
$body = @{
  formulaireId = 1
  projetId     = 1
  donnees      = @{ q1 = "test"; q2 = @{ nested = "value" } }
  latitude     = 12.345678
  longitude    = -7.123456
} | ConvertTo-Json -Depth 5

Invoke-WebRequest -Uri "http://localhost:8084/api/collectes" `
  -Method Post `
  -Body $body `
  -Headers @{"X-User-Id"="2"; "Content-Type"="application/json"}
```

## Vérification du Fix

Le test réussit quand vous voyez:
- ✓ Status Code: **201** (Created)
- ✓ Une réponse JSON avec `id` de la nouvelle collecte
- ✓ **PAS d'erreur PostgreSQL sur le statut**

## Fichiers Modifiés

```
elcollecte/service-collecte/src/main/java/com/elcollecte/collecte/entity/CollecteData.java
```

## Notes Importantes

1. Le source est corrigé, mais le JAR doit être **recompilé** pour refléter les changements
2. Si l'erreur persiste après redéploiement, c'est que l'ancien JAR est toujours en exécution
3. Vérifiez que `taskkill` a bien arrêté tous les processus Java avant de relancer

## Support

Si le problème persiste:
1. Vérifiez que le JAR date de maintenant (`Get-Item` et regarder `LastWriteTime`)
2. Consultez les logs du service pour voir l'erreur complète
3. Assurez-vous que PostgreSQL est accessible

---

**Créé**: 2026-02-23  
**Problème Résolu**: Enum PostgreSQL `collecte_statut` type mismatch

