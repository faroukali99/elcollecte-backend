---
name: testing-elcollecte
description: Test the eLcollecte local microservices stack and browser UI end-to-end. Use when verifying startup fixes, auth flows, dashboard routes, or frontend/backend integration.
---

# eLcollecte local testing

## Devin Secrets Needed

- None for local Docker-based testing. The local stack uses development credentials from `docker-compose.infra.yml` such as Postgres `postgres/postgres123` and MinIO `minioadmin/minioadmin123`.

## Start the local stack

1. Start infrastructure from the backend module directory:
   ```bash
   cd elcollecte
   docker-compose -f docker-compose.infra.yml up -d
   ```
2. Start Eureka with `SERVER_PORT=8761` even though `service-discovery/src/main/resources/application.yml` may default to `8762`; the other services expect Eureka at `localhost:8761/eureka`.
3. Start backend service jars with local development env vars:
   ```bash
   DB_PASSWORD=postgres123 SPRING_DATASOURCE_PASSWORD=postgres123 KAFKA_SERVERS=localhost:9092 EUREKA_HOST=localhost java -jar <service>/target/<service>-1.0.0-SNAPSHOT.jar
   ```
4. Start the frontend:
   ```bash
   cd elcollecte-frontend
   npm run dev -- --host 0.0.0.0
   ```

## Local auth setup

- The registration page submits `organisationId: 1`. If local registration returns HTTP 500 with no app-level message, check whether `elcollecte_users.organisations` contains `id=1`.
- For local UI testing only, seed a development organisation if missing:
  ```bash
  docker exec elcollecte-postgres psql -U postgres -d elcollecte_users -c "INSERT INTO organisations (id, nom, email_contact, pays) VALUES (1, 'Organisation Test Devin', 'devin-test@example.com', 'SN') ON CONFLICT (id) DO NOTHING;"
  ```
- Use a unique email per run, e.g. `devin$(date +%s)@example.com`, with password `Password123`. These are local-only test credentials.

## Health checks

Before browser testing, verify:

```bash
for p in 8761 8080 8081 8082 8084 8085 8087 8088 8089 8090 8091; do echo -n "$p "; curl -fsS "http://localhost:$p/actuator/health"; echo; done
curl -fsS http://localhost:5173/ | head -c 80
```

Expected backend response for every port is `{"status":"UP"}` and the frontend should serve HTML.

## Browser flow for auth + dashboard route

1. Open `http://localhost:5173/register`.
2. Register with a unique email and `Password123`.
3. Wait for the success alert `Compte créé avec succès ! Redirection...`, then login.
4. Confirm the authenticated layout/sidebar is visible.
5. Click sidebar `Analytique` and verify the URL is `/analytics` and the dashboard renders `Tableau de bord`, KPI cards, `Volume des collectes`, and `Répartition`.

## Build/lint checks

- Backend package: `cd elcollecte && mvn -B -DskipTests package`
- Frontend build: `cd elcollecte-frontend && npm run build`
- Frontend lint: `cd elcollecte-frontend && npm run lint`
- Full backend tests may fail on a pre-existing `service-analytique` `StatsServiceTest` expectation; verify against `master` before treating it as caused by a PR.
