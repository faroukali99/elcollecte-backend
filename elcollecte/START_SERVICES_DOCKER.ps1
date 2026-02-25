# Script PowerShell pour builder et démarrer l'ensemble avec Docker Compose
# Exécuter depuis le dossier elcollecte :
# Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope Process -Force ; .\START_SERVICES_DOCKER.ps1

$compose = "docker-compose.full.yml"
Write-Host "1) Build des images Maven/Node via 'docker compose build' (peut take long)" -ForegroundColor Cyan
docker compose -f $compose build --parallel

Write-Host "2) Démarrage des services en arrière-plan.." -ForegroundColor Cyan
docker compose -f $compose up -d

Write-Host "3) Attente 10s pour laisser aux services le temps de démarrer" -ForegroundColor Cyan
Start-Sleep -Seconds 10

Write-Host "4) Vérification: lister les containers" -ForegroundColor Green
docker ps --format "table {{.Names}}	{{.Status}}	{{.Ports}}"

Write-Host "Si tout ne démarre pas, voir 'docker compose -f $compose logs <service>' ou 'docker compose -f $compose ps'" -ForegroundColor Yellow

