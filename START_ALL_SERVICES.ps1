#!/bin/bash
# Demarrage complet de tous les services elcollecte

echo ""
echo "============================================================"
echo "DEMARRAGE COMPLET - TOUS LES SERVICES ELCOLLECTE"
echo "============================================================"
echo ""

PROJECT_DIR="C:\Semestre 5\Projet tuteure\Diagrammes UML\elcollecte-backend\elcollecte"
MVN_HOME="$USERPROFILE\apache-maven-3.9.5"
MVN_CMD="$MVN_HOME\bin\mvn.cmd"

# Etape 1: Compiler tous les services
Write-Host "[1/3] Compilation de tous les modules..." -ForegroundColor Yellow
cd $PROJECT_DIR

& $MVN_CMD clean package -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERREUR: Compilation echouee" -ForegroundColor Red
    exit 1
}

Write-Host "OK - Tous les modules compiles" -ForegroundColor Green

# Etape 2: Arreter les services existants
Write-Host ""
Write-Host "[2/3] Arret des services existants..." -ForegroundColor Yellow

# Arreter les processus Java
$javaProcs = Get-Process -Name java -ErrorAction SilentlyContinue
if ($javaProcs) {
    Stop-Process -Name java -Force -ErrorAction SilentlyContinue
    Start-Sleep -Seconds 3
    Write-Host "OK - Processus Java arretes" -ForegroundColor Green
}

# Arreter Docker Compose si en cours
Write-Host "Arret de Docker Compose..." -ForegroundColor Yellow
cd $PROJECT_DIR
docker-compose down 2>$null
Start-Sleep -Seconds 2
Write-Host "OK - Services Docker arretes" -ForegroundColor Green

# Etape 3: Demarrer tous les services via Docker Compose
Write-Host ""
Write-Host "[3/3] Demarrage de tous les services via Docker..." -ForegroundColor Yellow
Write-Host ""

cd $PROJECT_DIR
docker-compose up -d

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "============================================================" -ForegroundColor Green
    Write-Host "SUCCES! Tous les services sont demarres." -ForegroundColor Green
    Write-Host "============================================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Services actifs:" -ForegroundColor Cyan
    Write-Host "  - Service Discovery (Eureka): http://localhost:8761" -ForegroundColor Green
    Write-Host "  - API Gateway: http://localhost:8080" -ForegroundColor Green
    Write-Host "  - Service Utilisateur: http://localhost:8081" -ForegroundColor Green
    Write-Host "  - Service Projet: http://localhost:8082" -ForegroundColor Green
    Write-Host "  - Service Formulaire: http://localhost:8083" -ForegroundColor Green
    Write-Host "  - Service Collecte: http://localhost:8084" -ForegroundColor Green
    Write-Host "  - Service Media: http://localhost:8085" -ForegroundColor Green
    Write-Host "  - Service Validation: http://localhost:8086" -ForegroundColor Green
    Write-Host "  - Service Analytique: http://localhost:8087" -ForegroundColor Green
    Write-Host ""
    Write-Host "Affichage des logs (Ctrl+C pour arreter):" -ForegroundColor Yellow
    Write-Host ""

    docker-compose logs -f
} else {
    Write-Host ""
    Write-Host "ERREUR: Le demarrage via Docker a echoue" -ForegroundColor Red
    Write-Host "Assurez-vous que Docker et Docker Compose sont installes et en cours d'execution." -ForegroundColor Red
    exit 1
}

Write-Host ""

