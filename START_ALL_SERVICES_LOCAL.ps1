# Demarrage de tous les services elcollecte (sans Docker)
# Lance chaque service dans une fenetre PowerShell separate

$ProjectDir = "C:\Semestre 5\Projet tuteure\Diagrammes UML\elcollecte-backend\elcollecte"
$Services = @(
    "service-discovery",
    "api-gateway",
    "service-utilisateur",
    "service-projet",
    "service-formulaire",
    "service-collecte",
    "service-media",
    "service-validation",
    "service-analytique",
    "service-rapport",
    "service-audit"
)

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "DEMARRAGE DE TOUS LES SERVICES ELCOLLECTE" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Services a demarrer:" -ForegroundColor Yellow
$Services | ForEach-Object { Write-Host "  - $_" }
Write-Host ""

Write-Host "Demarrage de chaque service dans sa propre fenetre..." -ForegroundColor Yellow
Write-Host ""

$Ports = @{
    "service-discovery" = "8761"
    "api-gateway" = "8080"
    "service-utilisateur" = "8081"
    "service-projet" = "8082"
    "service-formulaire" = "8083"
    "service-collecte" = "8084"
    "service-media" = "8085"
    "service-validation" = "8086"
    "service-analytique" = "8087"
    "service-rapport" = "8088"
    "service-audit" = "8089"
}

foreach ($service in $Services) {
    $jarPath = "$ProjectDir\$service\target\$service-1.0.0-SNAPSHOT.jar"
    $port = $Ports[$service]

    if (Test-Path $jarPath) {
        Write-Host "Lancement de $service (port $port)..." -ForegroundColor Green

        # Lancer le service dans une nouvelle fenetre PowerShell
        $title = "elcollecte - $service"
        $cmd = "cd '$ProjectDir'; java -jar '$jarPath'; Read-Host 'Appuyez sur Entree pour fermer'"

        Start-Process PowerShell -ArgumentList "-NoExit", "-Command", $cmd -WindowStyle Normal -PassThru | Out-Null

        # Petit delai entre les lancements
        Start-Sleep -Seconds 2
    } else {
        Write-Host "ATTENTION: JAR non trouve pour $service" -ForegroundColor Yellow
    }
}

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "Tous les services ont ete lances dans des fenetres separate." -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Acces aux services:" -ForegroundColor Green
Write-Host "  - Eureka (Service Discovery): http://localhost:8761" -ForegroundColor Cyan
Write-Host "  - API Gateway: http://localhost:8080" -ForegroundColor Cyan
Write-Host "  - Service Collecte: http://localhost:8084/api/collectes" -ForegroundColor Cyan
Write-Host ""
Write-Host "Pour arreter tous les services: fermer les fenetres PowerShell" -ForegroundColor Yellow
Write-Host ""

