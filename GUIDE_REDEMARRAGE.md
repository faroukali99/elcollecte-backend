# 🔧 GUIDE DE REDÉMARRAGE - SERVICE COLLECTE CORRIGÉ

## ✅ Corrections Appliquées

Le problème que vous rencontriez était dû à:

1. **Erreur de type PostgreSQL ENUM**
   - Vous utilisiez `columnDefinition = "collecte_statut"` qui forçait un type custom
   - PostgreSQL n'acceptait que ce type, mais Hibernate envoyait des strings
   - **Solution**: Utiliser `@Enumerated(EnumType.STRING)` sans columnDefinition

2. **Erreur Hibernate JSON Map**
   - Les champs `donnees` et `medias` n'étaient pas initialisés
   - Hibernate ne savait pas comment traiter des Maps nulles
   - **Solution**: Initialiser avec `new HashMap<>()`

## 📋 Fichier Modifié

```
service-collecte/src/main/java/com/elcollecte/collecte/entity/CollecteData.java
```

### Changements Principaux:
```java
// ❌ AVANT
private Map<String, Object> donnees;
private Map<String, Object> medias;
@Column(nullable = false, columnDefinition = "collecte_statut")
private Statut statut = Statut.SOUMIS;

// ✅ APRÈS
private Map<String, Object> donnees = new HashMap<>();
private Map<String, Object> medias = new HashMap<>();
@Column(nullable = false)
private Statut statut = Statut.SOUMIS;
```

## 🚀 Comment Redémarrer

### **OPTION 1: Avec Maven (RECOMMANDÉ)**

Si Maven est installé:

```powershell
cd "C:\Semestre 5\Projet tuteure\Diagrammes UML\elcollecte-backend"
.\COMPILE_AND_RUN_COLLECTE.ps1
```

Ce script va:
- ✅ Arrêter tous les anciens processus Java
- ✅ Compiler le projet avec Maven
- ✅ Démarrer le service sur le port 8084

### **OPTION 2: Sans Maven (JAR Pré-compilé)**

Si Maven n'est pas installé ou ne fonctionne pas:

```powershell
cd "C:\Semestre 5\Projet tuteure\Diagrammes UML\elcollecte-backend"
.\START_SERVICE_COLLECTE.ps1
```

Cela démarrera directement le JAR déjà compilé.

### **OPTION 3: Démarrage Manuel**

```powershell
# Arrêter les anciens processus
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force
Start-Sleep -Seconds 2

# Démarrer le service
cd "C:\Semestre 5\Projet tuteure\Diagrammes UML\elcollecte-backend\elcollecte\service-collecte"
java -jar .\target\service-collecte-1.0.0-SNAPSHOT.jar `
  --spring.application.name=service-collecte `
  --server.port=8084
```

## 🌐 Accès au Service

Une fois démarré:
- **URL**: http://localhost:8084
- **Port**: 8084
- **Statut**: Vérifiez les logs pour "Started CollecteApplication"

## 🔍 Vérification

Après le démarrage, vous devriez voir:
```
✅ Service démarre sans erreurs Hibernate
✅ Logs sans "ClassCastException" ou "collecte_statut"
✅ Logs sans "JdbcTypeRecommendationException"
```

## ⚠️ Dépannage

### Si Maven n'est pas trouvé:
1. Installez Maven: https://maven.apache.org/download.cgi
2. Ajoutez le chemin Maven au PATH système
3. Relancez le PowerShell

### Si le JAR n'existe pas:
```powershell
# Compilez manuellement avec Maven
cd "C:\Semestre 5\Projet tuteure\Diagrammes UML\elcollecte-backend\elcollecte"
mvn clean package -DskipTests
```

### Si Java n'est pas trouvé:
```powershell
# Vérifiez Java
java -version

# Ou installez Java 21+
```

## 📝 Notes Importantes

- **Base de données**: Assurez-vous que PostgreSQL est démarré
- **Eureka**: Le service Eureka doit être disponible sur http://localhost:8761
- **Ports**: Assurez-vous que le port 8084 est libre

## ✨ Une Fois Démarré

Vous pouvez maintenant:
1. ✅ Accéder à la page de login
2. ✅ Valider le formulaire de collecte
3. ✅ Soumettre des données sans erreur PostgreSQL
4. ✅ Les données seront correctement sauvegardées en base

---

**Besoin d'aide?** Vérifiez les logs du service pour plus de détails sur les erreurs.


