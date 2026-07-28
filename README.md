# 🌍 eLcollecte - Plateforme de Collecte de Données Environnementales et Sociales

![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![Status](https://img.shields.io/badge/status-production--ready-green.svg)
![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-green.svg)
![React](https://img.shields.io/badge/React-18-blue.svg)

**eLcollecte** est une plateforme complète de collecte de données environnementales et sociales basée sur une architecture microservices moderne avec Spring Boot et React.

---

## 📋 Table des Matières

- [Aperçu](#-aperçu)
- [Architecture](#-architecture)
- [Stack Technique](#-stack-technique)
- [Services](#-services)
- [Prérequis](#-prérequis)
- [Installation](#-installation)
- [Démarrage Rapide](#-démarrage-rapide)
- [Documentation API](#-documentation-api)
- [Développement](#-développement)
- [Déploiement](#-déploiement)
- [Structure du Projet](#-structure-du-projet)
- [Contributing](#-contributing)
- [Licence](#-licence)

---

## 🎯 Aperçu

eLcollecte permet aux organisations de:
- **Gérer des projets** de collecte de données environnementales
- **Créer des formulaires** dynamiques pour les études d'impact (EIES)
- **Collecter des données** terrain avec support offline
- **Valider** les soumissions avec des règles métier complexes
- **Analyser** les données avec des dashboards en temps réel
- **Générer des rapports** PDF automatisés
- **Auditer** toutes les opérations pour traçabilité complète

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Frontend (React)                         │
│                   Port: 5173                                │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                  API Gateway (8080)                         │
│              JWT Auth + Routing + CORS                       │
└────────────────────┬────────────────────────────────────────┘
                     │
        ┌────────────┼────────────┬────────────┐
        ▼            ▼            ▼            ▼
┌──────────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐
│   Service    │ │ Service  │ │ Service  │ │ Service  │
│  Utilisateur │ │  Projet  │ │ Formulaire│ │ Collecte │
│    8081      │ │   8082   │ │   8091   │ │   8084   │
└──────────────┘ └──────────┘ └──────────┘ └──────────┘
        │            │            │            │
        └────────────┼────────────┼────────────┘
                     ▼            ▼
              ┌──────────┐ ┌──────────┐
              │ Service  │ │ Service  │
              │  Media   │ │Validation│
              │   8085   │ │   8090   │
              └──────────┘ └──────────┘
                     │            │
        ┌────────────┼────────────┼────────────┐
        ▼            ▼            ▼            ▼
┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐
│ Service  │ │ Service  │ │ Service  │ │ Service  │
│Analytique│ │ Rapport  │ │  Audit   │ │Discovery │
│   8087   │ │   8088   │ │   8089   │ │   8761   │
└──────────┘ └──────────┘ └──────────┘ └──────────┘
                     │
        ┌────────────┼────────────┐
        ▼            ▼            ▼
┌──────────┐ ┌──────────┐ ┌──────────┐
│PostgreSQL│ │  Kafka   │ │  MinIO   │
│   5432   │ │   9092   │ │   9000   │
└──────────┘ └──────────┘ └──────────┘
```

---

## 🛠️ Stack Technique

### Backend
- **Java 17** avec Spring Boot 3.3
- **Spring Cloud 2023** pour microservices
- **Spring Data JPA** avec PostgreSQL
- **Spring Security** avec JWT
- **Apache Kafka** pour événements asynchrones
- **Eureka** pour service discovery
- **Redis** pour cache
- **MinIO** pour stockage S3-compatible
- **Flyway** pour migrations DB
- **iText** pour génération PDF

### Frontend
- **React 18** avec Vite
- **Material-UI (MUI)** pour composants
- **Redux Toolkit** pour state management
- **React Router** pour routing
- **Axios** pour API calls
- **Recharts** pour graphiques
- **Tailwind CSS** pour styling

### Infrastructure
- **Docker** & **Docker Compose**
- **PostgreSQL 15** avec PostGIS
- **Kafka 7.5** + Zookeeper
- **Redis 7**
- **MinIO** (S3-compatible)

---

## 🚀 Services

| Service | Port | Description | Endpoint Clé |
|---------|------|-------------|--------------|
| **API Gateway** | 8080 | Passerelle centralisée | `/api/*` |
| **Service Discovery** | 8761 | Eureka Server | `/eureka` |
| **Service Utilisateur** | 8081 | Auth & gestion users | `/api/utilisateurs` |
| **Service Projet** | 8082 | Gestion projets | `/api/projets` |
| **Service Formulaire** | 8091 | Formulaires EIES | `/api/formulaires` |
| **Service Collecte** | 8084 | Données terrain | `/api/collectes` |
| **Service Media** | 8085 | Upload fichiers | `/api/medias` |
| **Service Validation** | 8090 | Validation métier | `/api/validations` |
| **Service Analytique** | 8087 | Statistiques | `/api/analytiques` |
| **Service Rapport** | 8088 | Génération PDF | `/api/rapports` |
| **Service Audit** | 8089 | Journal d'audit | `/api/audit` |
| **Frontend** | 5173 | Interface React | `/` |

---

## 📦 Prérequis

- **Docker Desktop** v20.10+
- **Docker Compose** v1.29+
- **8 GB RAM** minimum
- **20 GB** espace disque
- **Git** (optionnel)

---

## 🚀 Installation

```bash
# Cloner le repository
git clone https://github.com/votre-org/elcollecte-backend.git
cd elcollecte-backend

# Naviguer dans le répertoire elcollecte
cd elcollecte
```

---

## ⚡ Démarrage Rapide

### Option 1: Docker Compose (Recommandé)

```bash
# Démarrer l'infrastructure
docker-compose -f docker-compose.infra.yml up -d

# Attendre 30 secondes puis démarrer tous les services
docker-compose -f docker-compose.full.yml up -d

# Ou utiliser le script PowerShell
.\START_SERVICES_DOCKER.ps1
```

### Option 2: Développement Local

```bash
# Démarrer PostgreSQL, Redis, Kafka, MinIO
docker-compose -f docker-compose.infra.yml up -d

# Démarrer chaque service (nécessite Java 17 et Maven)
cd service-discovery && mvn spring-boot:run
cd ../api-gateway && mvn spring-boot:run
cd ../service-utilisateur && mvn spring-boot:run
# ... etc pour chaque service

# Démarrer le frontend
cd ../elcollecte-frontend
npm install
npm run dev
```

### Vérification

```bash
# Vérifier les conteneurs
docker-compose ps

# Vérifier les logs
docker-compose logs -f api-gateway

# Accéder à Eureka
open http://localhost:8761

# Accéder au frontend
open http://localhost:5173
```

---

## 📚 Documentation API

### Authentification

```bash
# Inscription
curl -X POST http://localhost:8080/api/utilisateurs/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "SecurePassword123!",
    "nom": "Dupont",
    "prenom": "Jean",
    "role": "COLLECTEUR"
  }'

# Connexion
curl -X POST http://localhost:8080/api/utilisateurs/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "SecurePassword123!"
  }'
```

### Projets

```bash
# Créer un projet
curl -X POST http://localhost:8080/api/projets \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Enquête Satisfaction 2026",
    "description": "Collecte de données de satisfaction",
    "dateDebut": "2026-03-01",
    "dateFin": "2026-03-31",
    "responsable": "jean.dupont@company.com"
  }'

# Lister les projets
curl http://localhost:8080/api/projets \
  -H "Authorization: Bearer <token>"
```

### Collecte

```bash
# Soumettre une collecte
curl -X POST http://localhost:8080/api/collectes \
  -H "Authorization: Bearer <token>" \
  -H "X-User-Id: 1" \
  -H "Content-Type: application/json" \
  -d '{
    "formulaireId": 1,
    "donnees": {"nom_client": "Michel Martin", "satisfaction": "Satisfait"},
    "latitude": 48.8566,
    "longitude": 2.3522
  }'
```

### Swagger UI

Chaque service expose sa documentation Swagger:
- API Gateway: http://localhost:8080/swagger-ui.html
- Service Utilisateur: http://localhost:8081/swagger-ui.html
- Service Projet: http://localhost:8082/swagger-ui.html
- ... etc pour chaque service

---

## 💻 Développement

### Structure Backend

```
elcollecte/
├── api-gateway/              # API Gateway
├── service-discovery/        # Eureka Server
├── service-utilisateur/      # Gestion utilisateurs
├── service-projet/           # Gestion projets
├── service-formulaire/       # Formulaires EIES
├── service-collecte/         # Collecte données
├── service-media/            # Upload fichiers
├── service-validation/       # Validation métier
├── service-analytique/       # Analytics
├── service-rapport/          # Génération rapports
├── service-audit/            # Journal audit
├── common-lib/               # Bibliothèque commune
└── docker-compose*.yml       # Configuration Docker
```

### Structure Frontend

```
elcollecte-frontend/
├── src/
│   ├── api/                  # API clients
│   ├── components/           # Composants réutilisables
│   ├── context/              # Context providers
│   ├── features/             # Redux slices
│   ├── hooks/                # Custom hooks
│   ├── pages/                # Pages de l'application
│   ├── App.jsx               # Composant principal
│   └── main.jsx              # Point d'entrée
├── package.json
├── vite.config.js
└── tailwind.config.js
```

### Ajouter un Nouveau Service

```bash
# Créer le module Maven
cd elcollecte
mkdir service-nouveau
cd service-nouveau

# Structure de base
mkdir -p src/main/java/com/elcollecte/nouveau/{controller,service,repository,entity,dto,config}
mkdir -p src/main/resources/db/migration
mkdir -p src/test/java/com/elcollecte/nouveau

# Copier les fichiers depuis un service existant et adapter
```

---

## 🚢 Déploiement

### Docker Compose

```bash
# Build les images
docker-compose -f docker-compose.full.yml build

# Démarrer tous les services
docker-compose -f docker-compose.full.yml up -d

# Vérifier le statut
docker-compose ps

# Voir les logs
docker-compose logs -f
```

### Scripts de Déploiement

```bash
# PowerShell
.\deploy-docker.ps1 start
.\deploy-docker.ps1 status
.\deploy-docker.ps1 logs
.\deploy-docker.ps1 stop

# Batch
deploy-docker.bat start
deploy-docker.bat status
deploy-docker.bat logs
deploy-docker.bat stop
```

### Variables d'Environnement

Créer un fichier `.env` dans `elcollecte/`:

```env
# Base de données
DB_HOST=postgres
DB_PASSWORD=postgres123

# JWT
JWT_SECRET=votre-secret-jwt-min-32-chars

# MinIO
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin123

# Kafka
KAFKA_SERVERS=kafka:29092

# Eureka
EUREKA_HOST=service-discovery
```

---

## 📊 Monitoring

### Services de Monitoring

- **Eureka Dashboard**: http://localhost:8761
- **Kafka UI**: http://localhost:9090
- **MinIO Console**: http://localhost:9001
- **Actuator Endpoints**: `/actuator/health` sur chaque service

### Logs

```bash
# Logs d'un service spécifique
docker-compose logs -f service-collecte

# Tous les logs
docker-compose logs

# Logs des 100 dernières lignes
docker-compose logs --tail=100
```

---

## 🔧 Configuration

### Ports Utilisés

| Service | Port |
|---------|------|
| Frontend | 5173 |
| API Gateway | 8080 |
| Service Utilisateur | 8081 |
| Service Projet | 8082 |
| Service Collecte | 8084 |
| Service Media | 8085 |
| Service Analytique | 8087 |
| Service Rapport | 8088 |
| Service Audit | 8089 |
| Service Validation | 8090 |
| Service Formulaire | 8091 |
| Eureka | 8761 |
| PostgreSQL | 5432 |
| Redis | 6379 |
| Kafka | 9092 |
| Kafka UI | 9090 |
| MinIO | 9000 |
| MinIO Console | 9001 |

---

## 📖 Documentation Complémentaire

- [Guide Docker Détaillé](elcollecte/DOCKER_DEPLOYMENT_GUIDE.md)
- [Architecture Kafka](elcollecte/KAFKA_ARCHITECTURE.md)
- [Guide Gestion Projets](elcollecte/GESTION_PROJETS_GUIDE.md)
- [Rapport Complet](RAPPORT_COMPLET.md)
- [Résumé Final](PROJET_FINAL_SUMMARY.md)

---

## 🤝 Contributing

Les contributions sont les bienvenues! Voici comment contribuer:

1. Fork le projet
2. Créer une branche (`git checkout -b feature/AmazingFeature`)
3. Commit vos changements (`git commit -m 'Add AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

---

## 📝 Licence

Ce projet est sous licence MIT. Voir le fichier LICENSE pour plus de détails.

---

## 👥 Équipe

- **Chef de Projet**: [Nom]
- **Développeurs Backend**: [Noms]
- **Développeurs Frontend**: [Noms]
- **DevOps**: [Noms]

---

## 📞 Support

Pour toute question ou problème:
- **Email**: support@elcollecte.com
- **Documentation**: [Wiki](https://github.com/votre-org/elcollecte-backend/wiki)
- **Issues**: [GitHub Issues](https://github.com/votre-org/elcollecte-backend/issues)

---

## 🎉 Remerciements

Merci à tous les contributeurs et aux technologies open-source utilisées dans ce projet.

---

**Version**: 1.0.0  
**Dernière mise à jour**: 17 Juin 2026  
**Statut**: ✅ Production Ready


<img width="1920" height="919" alt="image" src="https://github.com/user-attachments/assets/cebb0737-b4d6-4d4d-a605-64800e15efca" />

<img width="1913" height="914" alt="image" src="https://github.com/user-attachments/assets/16ead5c2-7afb-4d4f-8610-c9d7be48a12f" />

<img width="1919" height="908" alt="image" src="https://github.com/user-attachments/assets/4b73ef0a-0331-4a87-a86e-74f207bf1f11" />

<img width="1920" height="911" alt="image" src="https://github.com/user-attachments/assets/b4f83d7c-27cb-4dda-970f-1041f4fb0ca9" />

<img width="1920" height="923" alt="image" src="https://github.com/user-attachments/assets/1697ad95-a165-43ad-aad8-b6054dc17830" />

<img width="1920" height="921" alt="image" src="https://github.com/user-attachments/assets/9c494e03-f434-405e-ad09-a69397232a70" />
