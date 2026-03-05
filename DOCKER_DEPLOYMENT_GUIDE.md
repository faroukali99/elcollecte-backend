# 🐳 Guide de Déploiement Docker - eLcollecte

## Table des matières
1. [Prérequis](#prérequis)
2. [Installation](#installation)
3. [Configuration](#configuration)
4. [Déploiement](#déploiement)
5. [Utilisation](#utilisation)
6. [Dépannage](#dépannage)
7. [Architecture](#architecture)

---

## 📋 Prérequis

### Système d'exploitation
- Windows 10/11, macOS 10.15+, ou Linux (Ubuntu 20.04+)
- Minimum 8 GB RAM
- Minimum 20 GB d'espace disque libre

### Logiciels requis
1. **Docker Desktop** (v20.10+)
   - Télécharger: https://www.docker.com/products/docker-desktop
   - Installer et redémarrer le système

2. **Docker Compose** (v1.29+ ou intégré dans Docker Desktop)
   - Vérifier: `docker-compose --version`

3. **Git** (optionnel, pour cloner le repo)
   - Télécharger: https://git-scm.com

### Vérification des prérequis
```powershell
# Vérifier Docker
docker --version
# Output: Docker version 20.10.x ou plus

# Vérifier Docker Compose
docker-compose --version
# Output: Docker Compose version 1.29.x ou plus

# Vérifier l'espace disque
Get-Volume C: | Select-Object SizeRemaining
```

---

## 🚀 Installation

### 1. Cloner le repository
```powershell
git clone <repository-url>
cd elcollecte-backend
```

### 2. Naviguer dans le répertoire projet
```powershell
cd elcollecte
```

### 3. Vérifier la structure des fichiers
```powershell
# Les fichiers suivants doivent exister:
ls -Path .\docker-compose.full.yml
ls -Path .\docker-compose.infra.yml
ls -Path .\service-collecte\Dockerfile
ls -Path .\api-gateway\Dockerfile
# etc...
```

---

## ⚙️ Configuration

### 1. Créer le fichier .env
```powershell
# Copier le fichier d'exemple
Copy-Item .env.example .env

# Éditer le fichier .env avec vos paramètres
notepad .env
```

### 2. Fichier .env - Paramètres importants
```env
# BASE DE DONNÉES
DB_HOST=postgres
DB_PASSWORD=postgres123

# JWT SECRET - ⚠️ CHANGER EN PRODUCTION
JWT_SECRET=your-super-secure-secret-key-here

# MINIO (Stockage objet)
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin123

# EUREKA (Service Discovery)
EUREKA_HOST=service-discovery
EUREKA_DEFAULT_ZONE=http://service-discovery:8761/eureka/
```

### 3. Configuration avancée (optionnel)

#### Limiter les ressources des conteneurs
Modifier `docker-compose.full.yml` pour chaque service:
```yaml
service-collecte:
  # ... autres paramètres ...
  deploy:
    resources:
      limits:
        cpus: '1'
        memory: 1024M
      reservations:
        cpus: '0.5'
        memory: 512M
```

#### Configurer la base de données
Modifier `infra/init-db.sql`:
```sql
-- Ajouter vos scripts d'initialisation
CREATE DATABASE elcollecte_custom;
CREATE USER custom_user WITH PASSWORD 'custom_password';
GRANT ALL PRIVILEGES ON DATABASE elcollecte_custom TO custom_user;
```

---

## 🎯 Déploiement

### Option 1: Utiliser le script PowerShell (Recommandé)

#### Démarrer tous les services
```powershell
Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope Process -Force
.\deploy-docker.ps1 -Action start
```

#### Arrêter tous les services
```powershell
.\deploy-docker.ps1 -Action stop
```

#### Voir le statut des services
```powershell
.\deploy-docker.ps1 -Action status
```

#### Voir les logs
```powershell
# Tous les services
.\deploy-docker.ps1 -Action logs

# Un service spécifique
.\deploy-docker.ps1 -Action logs -Service service-collecte -Lines 50
```

#### Redémarrer les services
```powershell
.\deploy-docker.ps1 -Action restart
```

### Option 2: Commandes manuelles Docker Compose

#### Construire les images
```powershell
docker-compose -f docker-compose.full.yml build
```

#### Démarrer les services
```powershell
docker-compose -f docker-compose.full.yml up -d
```

#### Voir les services en cours d'exécution
```powershell
docker-compose -f docker-compose.full.yml ps
```

#### Voir les logs
```powershell
# Tous les logs
docker-compose -f docker-compose.full.yml logs -f

# Logs d'un service spécifique
docker-compose -f docker-compose.full.yml logs -f service-collecte
```

#### Arrêter les services
```powershell
docker-compose -f docker-compose.full.yml down
```

#### Nettoyer les données
```powershell
# Supprimer les conteneurs et volumes
docker-compose -f docker-compose.full.yml down -v
```

---

## 📱 Utilisation

### Accès aux services après déploiement

#### Frontend
- **URL**: http://localhost:5173
- **Description**: Interface web eLcollecte
- **Statut**: ✅ Prêt quand tous les services backend sont sains

#### API Gateway
- **URL**: http://localhost:8080
- **Description**: Point d'entrée principal des APIs
- **Health**: http://localhost:8080/actuator/health

#### Eureka (Service Discovery)
- **URL**: http://localhost:8761
- **Description**: Tableau de bord de découverte des services
- **Utilisateur**: eureka
- **Mot de passe**: eureka

#### Kafka UI
- **URL**: http://localhost:9090
- **Description**: Interface de gestion Kafka
- **Fonctionnalité**: Monitorer les messages Kafka en temps réel

#### MinIO (Stockage objet)
- **URL**: http://localhost:9001
- **Utilisateur**: minioadmin
- **Mot de passe**: minioadmin123
- **Description**: Interface de gestion du stockage objet

#### PostgreSQL
- **Host**: localhost
- **Port**: 5432
- **Utilisateur**: postgres
- **Mot de passe**: postgres123
- **Description**: Base de données principal

#### Redis
- **Host**: localhost
- **Port**: 6379
- **Mot de passe**: redis123
- **Description**: Cache en mémoire

### Connecter une application client
```java
// Configuration Spring Boot
server.servlet.context-path=/api
spring.application.name=my-app
eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/
```

### Vérifier la santé des services
```powershell
# Via curl
curl http://localhost:8080/actuator/health | ConvertFrom-Json | Format-List

# Via PowerShell
$response = Invoke-RestMethod -Uri http://localhost:8080/actuator/health
$response | ConvertTo-Json -Depth 3 | Write-Host
```

---

## 🔧 Dépannage

### Problème: "Port already in use"

**Solution**:
```powershell
# Trouver quel processus utilise le port 8080
netstat -ano | findstr :8080

# Terminer le processus
taskkill /PID <PID> /F

# Ou utiliser un port différent dans docker-compose.yml
# Changer "8080:8080" par "8081:8080"
```

### Problème: "docker-compose: command not found"

**Solution**:
```powershell
# Utiliser docker compose (intégré)
docker compose -f docker-compose.full.yml up -d

# Ou installer docker-compose via Chocolatey
choco install docker-compose
```

### Problème: "Cannot connect to Docker daemon"

**Solution**:
1. Redémarrer Docker Desktop
2. Vérifier que Docker Desktop est en cours d'exécution
3. Sur Linux: `sudo service docker start`

### Problème: "Out of memory"

**Solution**:
```powershell
# Dans Docker Desktop Settings:
# Settings > Resources > Memory: Augmenter à 8-12 GB
# Settings > Resources > Swap: Augmenter à 2 GB

# Ou arrêter les services inutiles
docker-compose -f docker-compose.full.yml down

# Nettoyer les images non utilisées
docker image prune -a
```

### Problème: Services qui ne démarrent pas

**Solution**:
```powershell
# 1. Vérifier les logs
docker-compose -f docker-compose.full.yml logs service-collecte

# 2. Vérifier que les dépendances sont saines
docker-compose -f docker-compose.full.yml ps

# 3. Reconstruire les images
docker-compose -f docker-compose.full.yml build --no-cache

# 4. Redémarrer
docker-compose -f docker-compose.full.yml restart
```

### Problème: Connexion à la base de données échouée

**Solution**:
```powershell
# 1. Vérifier que postgres est en cours d'exécution
docker-compose -f docker-compose.full.yml ps | findstr postgres

# 2. Vérifier les logs postgres
docker-compose -f docker-compose.full.yml logs postgres

# 3. Attendre plus longtemps (ajouter un délai)
Start-Sleep -Seconds 10

# 4. Réinitialiser la base de données
docker-compose -f docker-compose.full.yml down -v
docker-compose -f docker-compose.full.yml up -d
```

---

## 🏗️ Architecture

### Diagramme des services
```
┌─────────────────────────────────────────────────────────────┐
│                    eLcollecte System                        │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌──────────────┐                                           │
│  │  Frontend    │                                           │
│  │ (Nginx:5173) │                                           │
│  └──────┬───────┘                                           │
│         │                                                   │
│  ┌──────▼──────────────┐                                    │
│  │   API Gateway       │                                    │
│  │  (Spring:8080)      │                                    │
│  └──────┬──────────────┘                                    │
│         │                                                   │
│  ┌──────▼──────────────────────────────────────────┐        │
│  │        Service Discovery (Eureka:8761)        │        │
│  └──────┬──────────────────────────────────────────┘        │
│         │                                                   │
│  ┌──────┴──────────────┬──────────────────┬────────────┐   │
│  │                     │                  │            │   │
│  ▼                     ▼                  ▼            ▼   │
│ [Service-Utilisateur] [Service-Projet] [Service-Collecte] │
│ [Service-Formulaire] [Service-Média]    [Service-Validation]
│ [Service-Analytique] [Service-Audit]    [Service-Rapport]  │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐   │
│  │          PostgreSQL (DB centralisée)               │   │
│  └──────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌──────────────┐  ┌───────────┐  ┌──────────────┐         │
│  │   Kafka      │  │   Redis   │  │    MinIO     │         │
│  │  (Message)   │  │  (Cache)  │  │  (Storage)   │         │
│  └──────────────┘  └───────────┘  └──────────────┘         │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### Stack Technologique

| Composant | Technologie | Port | Rôle |
|-----------|-------------|------|------|
| Frontend | Nginx Alpine | 5173 | Interface utilisateur |
| API Gateway | Spring Boot 3.3 | 8080 | Routage des requêtes |
| Discovery | Spring Cloud Eureka | 8761 | Service Discovery |
| Services | Spring Boot 3.3 | 8081-8091 | Microservices métier |
| Database | PostgreSQL 15 + PostGIS | 5432 | Données persistantes |
| Cache | Redis 7 | 6379 | Cache distribué |
| Message Queue | Kafka 7.5 | 9092 | Événements asynchrones |
| Storage | MinIO Latest | 9000/9001 | Stockage objet S3 |
| Coordination | Zookeeper 7.5 | 2181 | Coordination Kafka |

---

## 📊 Monitoring et Maintenance

### Voir les métriques des conteneurs
```powershell
docker stats
```

### Vérifier l'utilisation du disque
```powershell
docker system df
```

### Nettoyer les ressources inutilisées
```powershell
# Supprimer les images inutilisées
docker image prune -a

# Supprimer les conteneurs arrêtés
docker container prune

# Nettoyer complètement
docker system prune -a -v
```

### Sauvegarder les données
```powershell
# Créer une sauvegarde de la base de données
docker-compose -f docker-compose.full.yml exec postgres pg_dump -U postgres elcollecte_users > backup.sql

# Restaurer depuis la sauvegarde
docker-compose -f docker-compose.full.yml exec -T postgres psql -U postgres < backup.sql
```

---

## 📝 Notes supplémentaires

### Pour le développement local
- Modifier les ports mapping dans `docker-compose.yml` si besoin
- Utiliser `docker-compose logs -f service-name` pour déboguer
- Les volumes rendent les changements de code automatiquement visibles

### Pour la production
- ⚠️ Changer tous les mots de passe par défaut
- ⚠️ Configurer HTTPS/TLS
- ⚠️ Mettre en place des sauvegardes régulières
- ⚠️ Utiliser un orchestrateur (Kubernetes) pour la scalabilité
- ⚠️ Mettre en place du monitoring (Prometheus, Grafana)
- ⚠️ Configurer les limites de ressources
- ⚠️ Utiliser un registre Docker privé

### Support et aide
- Vérifier les logs: `docker-compose logs <service-name>`
- Consulter la documentation: https://docs.docker.com
- Forum support: [Lien du forum]
- Issues GitHub: [Lien du repo]

---

**Dernière mise à jour**: 2026-02-26
**Version**: 1.0.0

