#!/usr/bin/env pwsh

$ErrorActionPreference = "Stop"
$WarningPreference = "SilentlyContinue"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Compilation du Service-Projet" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan

$projectPath = "C:\Semestre 5\Projet tuteure\Diagrammes UML\elcollecte-backend\elcollecte"
Set-Location $projectPath

# Nettoyer les compilations précédentes
Write-Host "[1/3] Nettoyage des fichiers de compilation..." -ForegroundColor Yellow
mvn clean -pl service-projet -q

# Compiler
Write-Host "[2/3] Compilation du module service-projet..." -ForegroundColor Yellow
$compileOutput = mvn compile -pl service-projet -DskipTests 2>&1

if ($LASTEXITCODE -ne 0) {
    Write-Host "[✗] Erreur de compilation!" -ForegroundColor Red
    Write-Host $compileOutput | tail -100
    exit 1
}

Write-Host "[✓] Compilation réussie!" -ForegroundColor Green

# Packager
Write-Host "[3/3] Création du JAR..." -ForegroundColor Yellow
mvn package -pl service-projet -DskipTests -q

if ($LASTEXITCODE -eq 0) {
    Write-Host "[✓] Package créé avec succès!" -ForegroundColor Green
    Get-Item "service-projet/target/service-projet-*.jar" -Exclude "*.original" | ForEach-Object {
        Write-Host "  → $($_.Name)" -ForegroundColor Cyan
    }
} else {
    Write-Host "[✗] Erreur lors du packaging!" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "Compilation terminée avec succès!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green

