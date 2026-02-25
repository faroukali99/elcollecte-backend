#!/usr/bin/env pwsh

# ============================================================
# DÉMARRAGE DE TOUS LES SERVICES - CORRIGÉ
# ============================================================

$ProjectRoot = "C:\Semestre 5\Projet tuteure\Diagrammes UML\elcollecte-backend\elcollecte"

Write-Host "`n============================================================" -ForegroundColor Cyan
Write-Host " DÉMARRAGE TOUS LES SERVICES" -ForegroundColor Cyan
Write-Host "============================================================`n" -ForegroundColor Cyan

# Services à démarrer dans l'ordre
$services = @(
    "service-discovery",      # Eureka - Doit être en premier
    "api-gateway",            # API Gateway
    "service-utilisateur",    # Service Utilisateur
    "service-projet",         # Service Projet
    "service-formulaire",     # Service Formulaire
    "service-collecte",       # Service Collecte (CORRIGÉ)
    "service-validation",     # Service Validation
    "service-media",          # Service Media
    "service-rapport",        # Service Rapport
    "service-analytique",     # Service Analytique
    "service-audit"           # Service Audit
)

$jars = @()

# Construire la liste des JARs
foreach ($service in $services) {
    $jarPath = Join-Path $ProjectRoot "$service\target\$service-1.0.0-SNAPSHOT.jar"

    if (Test-Path $jarPath) {
        $jars += @{
            name = $service
            path = $jarPath
            port = if ($service -eq "service-discovery") { 8761 } else { 8080 + $services.IndexOf($service) }
        }
        Write-Host "✅ $service trouvé" -ForegroundColor Green
    } else {
        Write-Host "⚠️  $service non trouvé ($jarPath)" -ForegroundColor Yellow
    }
}

if ($jars.Count -eq 0) {
    Write-Host "`n❌ ERREUR: Aucun JAR trouvé!" -ForegroundColor Red
    Write-Host "Veuillez d'abord compiler le projet:" -ForegroundColor Yellow
    Write-Host "  cd '$ProjectRoot'" -ForegroundColor Gray
    Write-Host "  mvn clean install -DskipTests" -ForegroundColor Gray
    exit 1
}

# Arrêter les anciens processus
Write-Host "`n[1/3] Arrêt des anciens processus Java..." -ForegroundColor Green
Get-Process java -ErrorAction SilentlyContinue | ForEach-Object {
    Write-Host "  Arrêt du PID $($_.Id)" -ForegroundColor Gray
    Stop-Process -Id $_.Id -Force -ErrorAction SilentlyContinue
}
Start-Sleep -Seconds 3

# Attendre que les ports se libèrent
Write-Host "`n[2/3] Attente de libération des ports..." -ForegroundColor Green
Start-Sleep -Seconds 2

# Démarrer les services
Write-Host "`n[3/3] Démarrage des services..." -ForegroundColor Green

# Ordre de démarrage - Eureka en premier
$discoveryJar = $jars | Where-Object { $_.name -eq "service-discovery" }
if ($discoveryJar) {
    Write-Host "`n  📍 Démarrage de service-discovery (Eureka) sur le port $($discoveryJar.port)..." -ForegroundColor Cyan
    Start-Process java -ArgumentList @(
        "-jar", $discoveryJar.path,
        "--spring.application.name=service-discovery",
        "--server.port=$($discoveryJar.port)",
        "--logging.level.root=WARN"
    ) -NoNewWindow
    Start-Sleep -Seconds 5
}

# Puis les autres services
foreach ($jar in $jars | Where-Object { $_.name -ne "service-discovery" }) {
    Write-Host "`n  🚀 Démarrage de $($jar.name) sur le port $($jar.port)..." -ForegroundColor Cyan
    Start-Process java -ArgumentList @(
        "-jar", $jar.path,
        "--spring.application.name=$($jar.name)",
        "--server.port=$($jar.port)",
        "--logging.level.root=WARN"
    ) -NoNewWindow
    Start-Sleep -Seconds 2
}

Write-Host "`n============================================================" -ForegroundColor Cyan
Write-Host " ✅ TOUS LES SERVICES DÉMARRÉS" -ForegroundColor Green
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "📍 Service Discovery (Eureka): http://localhost:8761" -ForegroundColor Yellow
Write-Host "🔌 API Gateway: http://localhost:8081" -ForegroundColor Yellow
Write-Host "🔐 Service Utilisateur: http://localhost:8082" -ForegroundColor Yellow
Write-Host "📋 Service Projet: http://localhost:8083" -ForegroundColor Yellow
Write-Host "✏️  Service Formulaire: http://localhost:8085" -ForegroundColor Yellow
Write-Host "📦 Service Collecte: http://localhost:8084" -ForegroundColor Yellow
Write-Host ""
Write-Host "Attente du démarrage complet (~15-20 secondes)..." -ForegroundColor Gray
Write-Host "Vérifiez http://localhost:8761 pour voir tous les services enregistrés" -ForegroundColor Gray


