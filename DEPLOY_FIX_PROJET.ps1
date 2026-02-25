#!/usr/bin/env powershell
# Script de déploiement du fix pour le service-projet

Write-Host "╔════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║       DÉPLOIEMENT - FIX SERVICE-PROJET (ENUM STATUTS)      ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan

$ErrorActionPreference = "Continue"

# Configuration
$projectPath = "C:\Semestre 5\Projet tuteure\Diagrammes UML\elcollecte-backend\elcollecte"
$javaHome = $env:JAVA_HOME

Write-Host ""
Write-Host "[INFO] Configuration du déploiement" -ForegroundColor Yellow
Write-Host "  - Répertoire projet: $projectPath"
Write-Host "  - JAVA_HOME: $javaHome"

if (-not (Test-Path $projectPath)) {
    Write-Host "[ERREUR] Répertoire du projet non trouvé!" -ForegroundColor Red
    exit 1
}

Set-Location $projectPath

Write-Host ""
Write-Host "[1/5] Nettoyage des compilations précédentes..." -ForegroundColor Yellow

try {
    mvn clean -q -pl common-lib,service-projet 2>$null
    Write-Host "  ✓ Nettoyage effectué" -ForegroundColor Green
} catch {
    Write-Host "  ⚠ Le nettoyage a échoué, continuant quand même..." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "[2/5] Compilation du common-lib..." -ForegroundColor Yellow

try {
    mvn compile -q -pl common-lib -DskipTests
    Write-Host "  ✓ common-lib compilé" -ForegroundColor Green
} catch {
    Write-Host "  ✗ Erreur lors de la compilation du common-lib" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "[3/5] Compilation du service-projet..." -ForegroundColor Yellow

try {
    mvn compile -q -pl service-projet -DskipTests
    Write-Host "  ✓ service-projet compilé" -ForegroundColor Green
} catch {
    Write-Host "  ✗ Erreur lors de la compilation du service-projet" -ForegroundColor Red
    mvn compile -pl service-projet -DskipTests 2>&1 | tail -50
    exit 1
}

Write-Host ""
Write-Host "[4/5] Packaging du service-projet..." -ForegroundColor Yellow

try {
    mvn package -q -pl service-projet -DskipTests
    $jarFile = Get-Item "service-projet/target/service-projet-*.jar" -Exclude "*.original" 2>$null | Select-Object -First 1
    if ($jarFile) {
        Write-Host "  ✓ JAR créé: $($jarFile.Name)" -ForegroundColor Green
    }
} catch {
    Write-Host "  ✗ Erreur lors du packaging" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "[5/5] Vérification des modifications..." -ForegroundColor Yellow

$modifications = @{
    "Entity Projet.java" = "getMotifRejet|setMotifRejet|VALIDE|REJETE"
    "DTO ProjetDto.java" = "motifRejet"
    "DTO UpdateProjetRequest.java" = "motifRejet"
    "Service ProjetService.java" = "req.motifRejet"
    "Migration V2__add_statuts_projet.sql" = "VALIDE.*REJETE"
    "Migration V3__add_motif_rejet.sql" = "motif_rejet"
    "GestionProjets.jsx" = "motifRejet|Motif de rejet"
}

Write-Host ""

$allOk = $true
foreach ($file in $modifications.Keys) {
    $pattern = $modifications[$file]

    if ($file -like "*.sql") {
        $fullPath = "service-projet/src/main/resources/db/migration/$file"
    } elseif ($file -like "*.jsx") {
        $fullPath = "../elcollecte-frontend/src/pages/$file"
    } else {
        $fullPath = "service-projet/src/main/java/com/elcollecte/projet/**/$file"
    }

    $found = Get-Content -Path $fullPath -ErrorAction SilentlyContinue | Select-String -Pattern $pattern

    if ($found) {
        Write-Host "  ✓ $file" -ForegroundColor Green
    } else {
        Write-Host "  ✗ $file - Modifications non trouvées!" -ForegroundColor Red
        $allOk = $false
    }
}

Write-Host ""
Write-Host "╔════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan

if ($allOk) {
    Write-Host "║       ✓ DÉPLOIEMENT RÉUSSI                              ║" -ForegroundColor Green
    Write-Host "╚════════════════════════════════════════════════════════════╝" -ForegroundColor Green

    Write-Host ""
    Write-Host "Résumé des modifications:" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "1. Entity Projet.java" -ForegroundColor Cyan
    Write-Host "   - Ajout: Enum VALIDE et REJETE"
    Write-Host "   - Ajout: Champ motifRejet (String)"
    Write-Host "   - Ajout: getter/setter pour motifRejet"
    Write-Host ""
    Write-Host "2. DTOs et Service" -ForegroundColor Cyan
    Write-Host "   - ProjetDto: Ajout de motifRejet"
    Write-Host "   - UpdateProjetRequest: Ajout de motifRejet"
    Write-Host "   - ProjetService.update(): Gestion de motifRejet"
    Write-Host ""
    Write-Host "3. Migrations Flyway" -ForegroundColor Cyan
    Write-Host "   - V2: ALTER TYPE pour ajouter VALIDE et REJETE"
    Write-Host "   - V3: ALTER TABLE pour ajouter motif_rejet"
    Write-Host ""
    Write-Host "4. Frontend" -ForegroundColor Cyan
    Write-Host "   - GestionProjets.jsx: Affichage du motif de rejet"
    Write-Host ""

} else {
    Write-Host "║       ✗ ERREURS DÉTECTÉES                              ║" -ForegroundColor Red
    Write-Host "╚════════════════════════════════════════════════════════════╝" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "Prochaines étapes:" -ForegroundColor Yellow
Write-Host "1. Redémarrer le service-projet"
Write-Host "2. Les migrations Flyway seront exécutées automatiquement au démarrage"
Write-Host "3. Tester la validation/rejet des projets via l'API"
Write-Host ""

