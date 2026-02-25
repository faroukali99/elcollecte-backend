# Script de redéploiement du service-collecte

$PROJECT_DIR = "C:\Semestre 5\Projet tuteure\Diagrammes UML\elcollecte-backend\elcollecte"
$MVN_HOME = "$env:USERPROFILE\apache-maven-3.9.5"
$MVN = "$MVN_HOME\bin\mvn.cmd"

Write-Host "========================================" -ForegroundColor Green
Write-Host "ÉTAPE 1: Compilation du module" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green

cd $PROJECT_DIR

# Compiler
& $MVN clean package -DskipTests -pl service-collecte -am

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "✓ Compilation réussie!" -ForegroundColor Green
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "ÉTAPE 2: JAR créé avec succès" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "JAR: $PROJECT_DIR\service-collecte\target\service-collecte-1.0.0-SNAPSHOT.jar" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "ACTIONS REQUISES:" -ForegroundColor Cyan
    Write-Host "1. Arrêtez l'instance actuelle (Ctrl+C)" -ForegroundColor Cyan
    Write-Host "2. Lancez le nouveau JAR avec:" -ForegroundColor Cyan
    Write-Host "   java -jar `"$PROJECT_DIR\service-collecte\target\service-collecte-1.0.0-SNAPSHOT.jar`"" -ForegroundColor Yellow
    Write-Host "3. Testez POST à http://localhost:8084/api/collectes" -ForegroundColor Cyan
    Write-Host ""
} else {
    Write-Host ""
    Write-Host "✗ Erreur de compilation!" -ForegroundColor Red
    exit 1
}

Write-Host "Appuyez sur une touche pour fermer..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

