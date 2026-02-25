feat: ajout architecture Kafka et améliorations multi-services

- Documentation complète de l'architecture Kafka avec topics et flux événementiels
- Configuration des converters JSON et sécurité pour tous les services
- Scripts de déploiement et compilation automatisés
- Gestion des statuts de projets avec motifs de rejet
- Améliorations frontend pour la gestion de projets
- Corrections diverses et guides d'utilisation# Démarrage Docker pour ElCollecte

Ce fichier explique comment démarrer l'ensemble du système (infra + backend + frontend) via Docker Compose.

Prérequis:
- Docker Desktop installé
- (optionnel) Docker Compose v2 intégré
- Port 5432, 9092, 8761, 8080..8091, 5173 disponibles

Commandes rapides (PowerShell):

```powershell
# depuis le dossier elcollecte
Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope Process -Force
# Build & start (images seront construites à partir des Dockerfile des microservices)
.\START_SERVICES_DOCKER.ps1

# Vérifier les logs d'un service (ex: kafka)
docker compose -f docker-compose.full.yml logs -f kafka

# Pour arrêter et supprimer containers/volumes
docker compose -f docker-compose.full.yml down -v
```

Tests rapides:
- Accéder à Eureka: http://localhost:8761
- Frontend: http://localhost:5173
- Kafka UI: http://localhost:9090
- MinIO Console: http://localhost:9001 (user: minioadmin / pass: minioadmin123)

Dépannage:
- Si une application Java ne démarre pas -> voir `docker compose logs -f <service>`.
- Si Flyway échoue (migrations), vérifier la base de données: `docker exec -it elcollecte-postgres psql -U postgres -d elcollecte_projets` etc.
- Si Maven requiert JDK version 21, les Dockerfiles utilisent Temurin 24 JRE pour runtime; la compilation est faite localement par le script d'origine. Si vous voulez builder à l'intérieur du conteneur, adaptez les Dockerfiles pour build stage (Multi-stage avec maven).


