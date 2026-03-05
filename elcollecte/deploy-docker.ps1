#!/usr/bin/env powershell

<#
.SYNOPSIS
Script de déploiement Docker pour le projet eLcollecte

.DESCRIPTION
Ce script gère le déploiement complet du système eLcollecte via Docker

.EXAMPLE
./deploy-docker.ps1 -Action "start"
./deploy-docker.ps1 -Action "stop"
./deploy-docker.ps1 -Action "logs" -Service "service-collecte"
#>

param(
    [Parameter(Mandatory=$true)]
    [ValidateSet("start", "stop", "restart", "logs", "build", "status", "clean", "shell")]
    [string]$Action,

    [Parameter(Mandatory=$false)]
    [string]$Service = "",

    [Parameter(Mandatory=$false)]
    [string]$Lines = "100"
)

$DOCKER_COMPOSE_FILE = "docker-compose.full.yml"
$PROJECT_NAME = "elcollecte"

function Write-Header {
    param([string]$Text)
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host $Text -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Cyan
}

function Test-DockerInstalled {
    $docker = Get-Command docker -ErrorAction SilentlyContinue
    if (-not $docker) {
        Write-Host "❌ Docker n'est pas installé ou n'est pas dans le PATH" -ForegroundColor Red
        Write-Host "Veuillez installer Docker Desktop depuis: https://www.docker.com/products/docker-desktop" -ForegroundColor Yellow
        exit 1
    }
    Write-Host "✅ Docker trouvé: $(docker --version)" -ForegroundColor Green
}

function Test-DockerCompose {
    $compose = Get-Command docker-compose -ErrorAction SilentlyContinue
    if (-not $compose) {
        Write-Host "ℹ️ Utilisation de docker compose (intégré)" -ForegroundColor Cyan
        $global:DOCKER_COMPOSE_CMD = "docker compose"
    } else {
        $global:DOCKER_COMPOSE_CMD = "docker-compose"
    }
    Write-Host "✅ Docker Compose disponible: $($global:DOCKER_COMPOSE_CMD)" -ForegroundColor Green
}

function Start-Services {
    Write-Header "🚀 Démarrage des services eLcollecte"

    Write-Host "📦 Construction des images..." -ForegroundColor Yellow
    & $global:DOCKER_COMPOSE_CMD -f $DOCKER_COMPOSE_FILE -p $PROJECT_NAME build

    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ La construction a échoué" -ForegroundColor Red
        exit 1
    }

    Write-Host "🔧 Démarrage des conteneurs..." -ForegroundColor Yellow
    & $global:DOCKER_COMPOSE_CMD -f $DOCKER_COMPOSE_FILE -p $PROJECT_NAME up -d

    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Le démarrage a échoué" -ForegroundColor Red
        exit 1
    }

    Write-Host "⏳ Attente du démarrage des services (30 secondes)..." -ForegroundColor Yellow
    Start-Sleep -Seconds 30

    Write-Host "✅ Services démarrés avec succès!" -ForegroundColor Green
    Show-ServiceStatus
}

function Stop-Services {
    Write-Header "🛑 Arrêt des services eLcollecte"

    & $global:DOCKER_COMPOSE_CMD -f $DOCKER_COMPOSE_FILE -p $PROJECT_NAME down

    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Services arrêtés avec succès!" -ForegroundColor Green
    } else {
        Write-Host "❌ Erreur lors de l'arrêt" -ForegroundColor Red
    }
}

function Restart-Services {
    Write-Header "🔄 Redémarrage des services eLcollecte"

    Stop-Services
    Write-Host "⏳ Attente avant redémarrage..." -ForegroundColor Yellow
    Start-Sleep -Seconds 5
    Start-Services
}

function Show-Logs {
    Write-Header "📋 Logs des services eLcollecte"

    if ($Service -eq "") {
        Write-Host "Affichage des logs de tous les services (dernières $Lines lignes)..." -ForegroundColor Yellow
        & $global:DOCKER_COMPOSE_CMD -f $DOCKER_COMPOSE_FILE -p $PROJECT_NAME logs -f --tail $Lines
    } else {
        Write-Host "Affichage des logs de $Service (dernières $Lines lignes)..." -ForegroundColor Yellow
        & $global:DOCKER_COMPOSE_CMD -f $DOCKER_COMPOSE_FILE -p $PROJECT_NAME logs -f --tail $Lines $Service
    }
}

function Build-Images {
    Write-Header "🔨 Construction des images Docker"

    & $global:DOCKER_COMPOSE_CMD -f $DOCKER_COMPOSE_FILE -p $PROJECT_NAME build --no-cache

    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Images construites avec succès!" -ForegroundColor Green
    } else {
        Write-Host "❌ La construction a échoué" -ForegroundColor Red
    }
}

function Show-ServiceStatus {
    Write-Header "📊 État des services"

    & $global:DOCKER_COMPOSE_CMD -f $DOCKER_COMPOSE_FILE -p $PROJECT_NAME ps

    Write-Host ""
    Write-Host "Accès aux services:" -ForegroundColor Green
    Write-Host "  🌐 Frontend: http://localhost:5173" -ForegroundColor Cyan
    Write-Host "  🚪 API Gateway: http://localhost:8080" -ForegroundColor Cyan
    Write-Host "  🎯 Service Découverte (Eureka): http://localhost:8761" -ForegroundColor Cyan
    Write-Host "  📊 Kafka UI: http://localhost:9090" -ForegroundColor Cyan
    Write-Host "  📦 MinIO: http://localhost:9001 (minioadmin/minioadmin123)" -ForegroundColor Cyan
    Write-Host "  🔴 PostgreSQL: localhost:5432 (postgres/postgres123)" -ForegroundColor Cyan
    Write-Host ""
}

function Clean-All {
    Write-Header "🧹 Nettoyage complet"

    Write-Host "⚠️ Cela supprimera tous les conteneurs, images et volumes" -ForegroundColor Red
    Write-Host "Êtes-vous sûr ? (yes/no)" -ForegroundColor Yellow
    $response = Read-Host

    if ($response -eq "yes") {
        Write-Host "Arrêt des services..." -ForegroundColor Yellow
        & $global:DOCKER_COMPOSE_CMD -f $DOCKER_COMPOSE_FILE -p $PROJECT_NAME down -v

        Write-Host "Suppression des images..." -ForegroundColor Yellow
        & docker image prune -af

        Write-Host "✅ Nettoyage terminé!" -ForegroundColor Green
    } else {
        Write-Host "Nettoyage annulé" -ForegroundColor Yellow
    }
}

function Open-Shell {
    if ($Service -eq "") {
        Write-Host "⚠️ Vous devez spécifier le service avec -Service" -ForegroundColor Red
        Write-Host "Exemple: ./deploy-docker.ps1 -Action shell -Service service-collecte" -ForegroundColor Yellow
        exit 1
    }

    Write-Host "🔌 Connexion au shell du service $Service..." -ForegroundColor Yellow
    & $global:DOCKER_COMPOSE_CMD -f $DOCKER_COMPOSE_FILE -p $PROJECT_NAME exec $Service /bin/bash
}

# Main
Write-Host ""
Test-DockerInstalled
Test-DockerCompose

switch ($Action) {
    "start" { Start-Services }
    "stop" { Stop-Services }
    "restart" { Restart-Services }
    "logs" { Show-Logs }
    "build" { Build-Images }
    "status" { Show-ServiceStatus }
    "clean" { Clean-All }
    "shell" { Open-Shell }
    default { Write-Host "Action inconnue: $Action" -ForegroundColor Red }
}

Write-Host ""

