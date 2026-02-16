# ElCollecte — Backend Microservices

## Stack technique
- **Java 24** (sans Lombok, Records pour les DTOs)
- **Spring Boot 3.3** + Spring Cloud 2023
- **PostgreSQL 15** + PostGIS (géolocalisation)
- **Apache Kafka** (communication asynchrone inter-services)
- **Redis** (rate limiting API Gateway)
- **MinIO** (stockage objets S3-compatible)
- **Eureka** (service discovery)
- **Spring Cloud Gateway** (API Gateway réactif)

## Services

| Service            | Port | Description                          |
|--------------------|------|--------------------------------------|
| service-discovery  | 8761 | Eureka Server                        |
| api-gateway        | 8080 | Point d'entrée unique + auth JWT     |
| service-utilisateur| 8081 | Auth, users, organisations           |
| service-projet     | 8082 | Projets, équipes enquêteurs          |
| service-formulaire | 8083 | Builder formulaires JSONB            |
| service-collecte   | 8084 | Données terrain + sync offline       |
| service-media      | 8085 | Upload photos/signatures (MinIO)     |
| service-validation | 8086 | Workflow validation/rejet            |
| service-analytique | 8087 | KPIs, CQRS read side                 |
| service-rapport    | 8088 | Génération PDF/Word asynchrone       |
| service-audit      | 8089 | Journal immuable (Kafka consumer)    |

## Démarrage rapide

```bash
# 1. Infrastructure
docker compose -f docker-compose.infra.yml up -d

# 2. Attendre que PostgreSQL soit prêt, puis lancer les services
docker compose up -d

# 3. Accéder à Eureka Dashboard
open http://localhost:8761

# 4. API disponible via Gateway
curl http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@elcollecte.com","password":"Admin@123"}'
```

## Variables d'environnement requises
```env
JWT_SECRET=votre-secret-jwt-min-32-chars
DB_HOST=localhost
DB_PASSWORD=postgres123
KAFKA_SERVERS=localhost:9092
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin123
```

## Topics Kafka
| Topic              | Producteur          | Consommateurs                   |
|--------------------|--------------------|---------------------------------|
| audit.events       | Tous les services  | service-audit                   |
| collecte.soumise   | service-collecte   | service-analytique, service-audit |
| collecte.validee   | service-collecte   | service-analytique, service-audit |
| collecte.rejetee   | service-collecte   | service-analytique, service-audit |
| projet.cree        | service-projet     | service-analytique, service-audit |
| formulaire.publie  | service-formulaire | service-audit                   |
