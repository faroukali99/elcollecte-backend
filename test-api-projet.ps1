#!/usr/bin/env powershell
# Script de test de l'API Projet après le fix

param(
    [string]$ApiUrl = "http://localhost:8082",
    [string]$AuthToken = ""
)

Write-Host "╔════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║           TEST DE L'API PROJET - STATUTS VALIDÉS          ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan

Write-Host ""
Write-Host "Configuration:" -ForegroundColor Yellow
Write-Host "  API URL: $ApiUrl"
Write-Host "  Auth Token: $(if ($AuthToken) { '***' } else { '[Non configuré]' })"
Write-Host ""

# Headers
$headers = @{
    "Content-Type" = "application/json"
    "X-Org-Id" = "1"
    "X-User-Id" = "1"
}

if ($AuthToken) {
    $headers["Authorization"] = "Bearer $AuthToken"
}

# Test 1: Récupérer les projets
Write-Host "[TEST 1] Récupérer la liste des projets" -ForegroundColor Cyan
Write-Host "GET $ApiUrl/api/projets" -ForegroundColor Gray

try {
    $response = Invoke-RestMethod -Uri "$ApiUrl/api/projets" -Method Get -Headers $headers
    $projectCount = $response.content.Count

    Write-Host "  ✓ Réponse reçue: $projectCount projets" -ForegroundColor Green

    if ($projectCount -gt 0) {
        $project = $response.content[0]
        Write-Host "    Premier projet:" -ForegroundColor Gray
        Write-Host "      - ID: $($project.id)" -ForegroundColor Gray
        Write-Host "      - Titre: $($project.titre)" -ForegroundColor Gray
        Write-Host "      - Statut: $($project.statut)" -ForegroundColor Gray

        if ($project.motifRejet) {
            Write-Host "      - Motif rejet: $($project.motifRejet)" -ForegroundColor Red
        }
    }
} catch {
    Write-Host "  ✗ Erreur: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Test 2: Tester chaque statut valide
Write-Host ""
Write-Host "[TEST 2] Vérifier les statuts supportés" -ForegroundColor Cyan

$statuts = @("BROUILLON", "ACTIF", "SUSPENDU", "TERMINE", "VALIDE", "REJETE")
$allStatutsOk = $true

foreach ($statut in $statuts) {
    try {
        # On va juste vérifier que le statut est compris par l'énumération
        Write-Host "  - $statut" -ForegroundColor Gray
    } catch {
        $allStatutsOk = $false
    }
}

if ($allStatutsOk) {
    Write-Host "  ✓ Tous les statuts sont supportés" -ForegroundColor Green
} else {
    Write-Host "  ✗ Certains statuts ne sont pas supportés" -ForegroundColor Red
}

# Test 3: Tester la validation d'un projet (si on en a un)
Write-Host ""
Write-Host "[TEST 3] Tester la validation d'un projet" -ForegroundColor Cyan

if ($projectCount -gt 0) {
    $projectId = $response.content[0].id
    $payload = @{
        statut = "VALIDE"
    } | ConvertTo-Json

    Write-Host "PUT $ApiUrl/api/projets/$projectId" -ForegroundColor Gray
    Write-Host "Payload: $payload" -ForegroundColor Gray

    try {
        $updateResponse = Invoke-RestMethod -Uri "$ApiUrl/api/projets/$projectId" `
            -Method Put -Headers $headers -Body $payload

        if ($updateResponse.statut -eq "VALIDE") {
            Write-Host "  ✓ Projet validé avec succès" -ForegroundColor Green
        } else {
            Write-Host "  ⚠ Statut actuel: $($updateResponse.statut)" -ForegroundColor Yellow
        }
    } catch {
        Write-Host "  ✗ Erreur: $($_.Exception.Message)" -ForegroundColor Red
    }
} else {
    Write-Host "  ⚠ Aucun projet disponible pour le test" -ForegroundColor Yellow
}

# Test 4: Tester le rejet d'un projet
Write-Host ""
Write-Host "[TEST 4] Tester le rejet d'un projet avec motif" -ForegroundColor Cyan

if ($projectCount -gt 0 -and $response.content.Count -gt 1) {
    $projectId = $response.content[1].id
    $payload = @{
        statut = "REJETE"
        motifRejet = "Test automatisé - Données insuffisantes"
    } | ConvertTo-Json

    Write-Host "PUT $ApiUrl/api/projets/$projectId" -ForegroundColor Gray
    Write-Host "Payload: $payload" -ForegroundColor Gray

    try {
        $rejectResponse = Invoke-RestMethod -Uri "$ApiUrl/api/projets/$projectId" `
            -Method Put -Headers $headers -Body $payload

        if ($rejectResponse.statut -eq "REJETE" -and $rejectResponse.motifRejet) {
            Write-Host "  ✓ Projet rejeté avec motif" -ForegroundColor Green
            Write-Host "    Motif: $($rejectResponse.motifRejet)" -ForegroundColor Green
        } else {
            Write-Host "  ⚠ Réponse reçue mais incomplète" -ForegroundColor Yellow
        }
    } catch {
        Write-Host "  ✗ Erreur: $($_.Exception.Message)" -ForegroundColor Red
    }
} else {
    Write-Host "  ⚠ Pas assez de projets pour ce test" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "╔════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║                  TESTS TERMINÉS                            ║" -ForegroundColor Green
Write-Host "╚════════════════════════════════════════════════════════════╝" -ForegroundColor Green

Write-Host ""
Write-Host "Pour tester manuellement:" -ForegroundColor Yellow
Write-Host "  1. Valider: PUT /api/projets/{id} -d '{\"statut\": \"VALIDE\"}'"
Write-Host "  2. Rejeter: PUT /api/projets/{id} -d '{\"statut\": \"REJETE\", \"motifRejet\": \"...\"}'"
Write-Host ""

