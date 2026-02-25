#!/usr/bin/env pwsh

# ============================================================
# COMPILATION ET DÉMARRAGE DU SERVICE COLLECTE - CORRIGÉ
# ============================================================

$ProjectRoot = "C:\Semestre 5\Projet tuteure\Diagrammes UML\elcollecte-backend\elcollecte"
$ServicePath = Join-Path $ProjectRoot "service-collecte"
$JarPath = Join-Path $ServicePath "target\service-collecte-1.0.0-SNAPSHOT.jar"

Write-Host "`n============================================================" -ForegroundColor Cyan
Write-Host " COMPILATION COLLECTE SERVICE - ENUM STATUT CORRIGÉ" -ForegroundColor Cyan
Write-Host "============================================================`n" -ForegroundColor Cyan

# 1. Tuer les anciens processus
Write-Host "[1/3] Arrêt des anciens processus..." -ForegroundColor Green
Get-Process java -ErrorAction SilentlyContinue | ForEach-Object {
    Write-Host "  Arrêt de PID $($_.Id)" -ForegroundColor Gray
    Stop-Process -Id $_.Id -Force -ErrorAction SilentlyContinue
}
Start-Sleep -Seconds 2

# 2. Nettoyer et compiler avec Maven
Write-Host "`n[2/3] Compilation du service-collecte..." -ForegroundColor Green

# Essayer d'utiliser Maven depuis différents chemins
$MavenPaths = @(
    "C:\Maven\bin\mvn.cmd",
    "C:\Program Files\Maven\bin\mvn.cmd",
    "mvn.cmd"
)

$MavenFound = $false
$MavenCmd = $null

foreach ($path in $MavenPaths) {
    if (Test-Path $path) {
        $MavenCmd = $path
        $MavenFound = $true
        Write-Host "  Maven trouvé: $path" -ForegroundColor Gray
        break
    }
}

if (-not $MavenFound) {
    Write-Host "  ERREUR: Maven non trouvé" -ForegroundColor Red
    Write-Host "  Veuillez installer Maven ou le mettre dans le PATH" -ForegroundColor Yellow
    Write-Host "  Sinon, contactez votre administrateur système." -ForegroundColor Yellow
    exit 1
}

# Compiler
Write-Host "  Exécution: $MavenCmd clean package -DskipTests" -ForegroundColor Gray
& $MavenCmd -f (Join-Path $ProjectRoot "pom.xml") clean package -DskipTests -q

if ($LASTEXITCODE -ne 0) {
    Write-Host "  ERREUR: Compilation échouée (code: $LASTEXITCODE)" -ForegroundColor Red
    exit 1
}

Write-Host "  Compilation réussie!" -ForegroundColor Green

# 3. Vérifier le JAR et démarrer
Write-Host "`n[3/3] Démarrage du service..." -ForegroundColor Green

if (-not (Test-Path $JarPath)) {
    Write-Host "  ERREUR: JAR non trouvé après compilation: $JarPath" -ForegroundColor Red
    exit 1
}

Write-Host "  JAR: $JarPath" -ForegroundColor Gray
Write-Host "  Port: 8084" -ForegroundColor Gray
Write-Host "`n" -ForegroundColor Green

# Lancer le service
java -jar $JarPath `
  --spring.application.name=service-collecte `
  --server.port=8084 `
  --logging.level.root=INFO


