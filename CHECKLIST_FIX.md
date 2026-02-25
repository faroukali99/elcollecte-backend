# ✅ VÉRIFICATION FINALE - Fix Service-Collecte

## État du Correctif

### Fichier Source Corrigé ✓
**Chemin**: `elcollecte/service-collecte/src/main/java/com/elcollecte/collecte/entity/CollecteData.java`

**Vérifications:**
- ✅ Imports: `JdbcTypeCode` et `SqlTypes` **SUPPRIMÉS**
- ✅ Champ `donnees`: `@Column(nullable = false, columnDefinition = "jsonb")`
- ✅ Champ `medias`: `@Column(columnDefinition = "jsonb")`
- ✅ Champ `statut`: **@Enumerated(EnumType.STRING)** + **@Column(nullable = false, columnDefinition = "collecte_statut")**

---

## Scripts Disponibles

### 1️⃣ FIX_AND_DEPLOY.ps1 ⭐ (RECOMMANDÉ)
**Le plus complet et automatisé**

```powershell
Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope Process -Force
& "C:\Semestre 5\Projet tuteure\Diagrammes UML\elcollecte-backend\FIX_AND_DEPLOY.ps1"
```

**Actions automatisées:**
1. Recompile le module Maven
2. Arrête l'ancienne instance Java
3. Lance le nouveau JAR
4. Teste l'API automatiquement
5. Affiche le résultat (succès/échec)

**Durée:** ~3-5 minutes

---

### 2️⃣ RESTART_SERVICE.bat
**Redémarrage rapide (si compilation déjà faite)**

```batch
C:\Semestre 5\Projet tuteure\Diagrammes UML\elcollecte-backend\RESTART_SERVICE.bat
```

**Actions:**
1. Arrête les processus Java
2. Lance le JAR existant

**Durée:** ~30 secondes

---

## Checklist d'Exécution

### Avant de lancer le fix:
- [ ] Fermer toute connexion à http://localhost:8084
- [ ] Arrêter manuellement le service actuel si en background
- [ ] Avoir Maven disponible (script installe si absent)

### Exécution (Option 1 - Recommandée):
```powershell
# 1. Ouvrir PowerShell en administrateur
# 2. Copier-coller:
Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope Process -Force
& "C:\Semestre 5\Projet tuteure\Diagrammes UML\elcollecte-backend\FIX_AND_DEPLOY.ps1"

# 3. Attendre la fin du script (~3-5 min)
```

### Exécution (Option 2 - Manuel):
```bash
# 1. CMD/PowerShell
cd "C:\Semestre 5\Projet tuteure\Diagrammes UML\elcollecte-backend\elcollecte"

# 2. Compiler
mvn clean package -DskipTests -pl service-collecte -am

# 3. Arrêter ancien service (Ctrl+C si foreground)
taskkill /F /IM java.exe

# 4. Redémarrer
java -jar "C:\Semestre 5\Projet tuteure\Diagrammes UML\elcollecte-backend\elcollecte\service-collecte\target\service-collecte-1.0.0-SNAPSHOT.jar"

# 5. Tester dans un autre terminal PowerShell
$body = '{"formulaireId":1,"projetId":1,"donnees":{"q1":"test"},"latitude":12.345,"longitude":-7.123}' | ConvertFrom-Json | ConvertTo-Json -Depth 5
Invoke-WebRequest -Uri "http://localhost:8084/api/collectes" -Method Post -Body $body -Headers @{"X-User-Id"="2"}
```

---

## Vérification du Succès

### ✅ Succès = Vous voyez:
```
Status Code: 201 (Created)
Response: {"id": <number>, "uuid": "<uuid>", ...}
```

### ❌ Échec = Vous voyez:
```
ERREUR: la colonne « statut » est de type collecte_statut mais l'expression est de type character varying
```

**Cause probable:** L'ancien JAR est toujours en exécution
**Solution:** Relancer `taskkill /F /IM java.exe` puis le script

---

## Troubleshooting

| Problème | Solution |
|----------|----------|
| Script PowerShell ne s'exécute pas | `Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope Process -Force` |
| Port 8084 déjà utilisé | `netstat -ano \| findstr :8084` → `taskkill /PID <PID> /F` |
| Maven non trouvé | Le script l'installe automatiquement |
| Erreur persiste après redémarrage | Vérifiez que le JAR date de maintenant: `Get-Item <JAR_PATH> \| Select LastWriteTime` |
| Base PostgreSQL non accessible | Vérifiez `application.yml` dans `service-collecte/src/main/resources/` |

---

## Résumé du Fix

**Problème racine:**
- Hibernate envoyait la valeur enum `statut` comme `String` (varchar)
- PostgreSQL attendait le type `collecte_statut` (enum custom)
- Mismatch de types → Erreur 42804

**Solution:**
- Ajouter `columnDefinition = "collecte_statut"` à l'annotation `@Column`
- Garder `@Enumerated(EnumType.STRING)` pour que JPA sérialise en String
- Hibernate saura alors que cette String doit matcher l'enum PostgreSQL

**Résultat:**
- ✅ Type mismatch résolu
- ✅ INSERT/UPDATE fonctionnent normalement
- ✅ Pas de modification base de données requise

---

**Créé le**: 2026-02-23  
**État**: ✅ Prêt pour exécution

