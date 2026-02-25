#!/usr/bin/env pwsh

# Script de démarrage du service collecte corrigé
$ServicePath = "C:\Semestre 5\Projet tuteure\Diagrammes UML\elcollecte-backend\elcollecte\service-collecte"
$JarPath = Join-Path $ServicePath "target\service-collecte-1.0.0-SNAPSHOT.jar"

# Vérifier que le JAR existe
if (-not (Test-Path $JarPath)) {
    Write-Host "JAR non trouvé: $JarPath" -ForegroundColor Red
    Write-Host "Veuillez compiler le projet d'abord." -ForegroundColor Yellow
    exit 1
}

# Tuer les anciens processus Java si existants
Write-Host "Arrêt des anciens processus..." -ForegroundColor Cyan
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 2

# Démarrer le service
Write-Host "Démarrage du service-collecte..." -ForegroundColor Green
Write-Host "JAR: $JarPath" -ForegroundColor Gray

java -jar $JarPath `
  --spring.application.name=service-collecte `
  --server.port=8084 `
  --logging.level.root=INFO


