#!/usr/bin/env pwsh

# Script de validation des corrections
$file = "C:\Semestre 5\Projet tuteure\Diagrammes UML\elcollecte-backend\elcollecte\service-collecte\src\main\java\com\elcollecte\collecte\entity\CollecteData.java"

Write-Host "`n============================================================" -ForegroundColor Cyan
Write-Host " VALIDATION DES CORRECTIONS" -ForegroundColor Cyan
Write-Host "============================================================`n" -ForegroundColor Cyan

$content = Get-Content $file -Raw

$checks = @(
    @{
        name = "Initialisation de donnees"
        pattern = "private Map<String, Object> donnees = new HashMap<>"
        shouldExist = $true
    },
    @{
        name = "Initialisation de medias"
        pattern = "private Map<String, Object> medias = new HashMap<>"
        shouldExist = $true
    },
    @{
        name = "Suppression du columnDefinition errone"
        pattern = 'columnDefinition = "collecte_statut"'
        shouldExist = $false
    },
    @{
        name = "Statut enum correct"
        pattern = "@Column(nullable = false)\s+private Statut statut"
        shouldExist = $true
    }
)

$allGood = $true

foreach ($check in $checks) {
    $found = $content -match $check.pattern

    if ($found -eq $check.shouldExist) {
        Write-Host "✅ $($check.name)" -ForegroundColor Green
    } else {
        Write-Host "❌ $($check.name)" -ForegroundColor Red
        $allGood = $false
    }
}

Write-Host ""
if ($allGood) {
    Write-Host "✅ Toutes les corrections sont appliquées!" -ForegroundColor Green
    Write-Host "`nProchaine étape: Relancez le service avec:" -ForegroundColor Yellow
    Write-Host "  cd 'C:\Semestre 5\Projet tuteure\Diagrammes UML\elcollecte-backend'" -ForegroundColor Gray
    Write-Host "  .\COMPILE_AND_RUN_COLLECTE.ps1" -ForegroundColor Gray
} else {
    Write-Host "❌ Des corrections ne sont pas appliquées correctement!" -ForegroundColor Red
    exit 1
}


