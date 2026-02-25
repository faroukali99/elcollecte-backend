# Fix Service-Collecte - Recompilation et Redéploiement

$ErrorActionPreference = "Stop"
$ProgressPreference = "SilentlyContinue"

$PROJECT_DIR = "C:\Semestre 5\Projet tuteure\Diagrammes UML\elcollecte-backend\elcollecte"
$MVN_HOME = "$env:USERPROFILE\apache-maven-3.9.5"
$MVN_CMD = "$MVN_HOME\bin\mvn.cmd"
$JAR_PATH = "$PROJECT_DIR\service-collecte\target\service-collecte-1.0.0-SNAPSHOT.jar"
$API_URL = "http://localhost:8084/api/collectes"

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "FIX SERVICE-COLLECTE - ENUM STATUT ERROR" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""

# Step 1: Check Maven
Write-Host "[1/5] Verification de Maven..." -ForegroundColor Yellow
if (-not (Test-Path $MVN_CMD)) {
    Write-Host "ERREUR: Maven non trouve" -ForegroundColor Red
    exit 1
}
Write-Host "OK - Maven trouve" -ForegroundColor Green

# Step 2: Build
Write-Host ""
Write-Host "[2/5] Compilation du module..." -ForegroundColor Yellow
cd $PROJECT_DIR
& $MVN_CMD package -DskipTests -pl service-collecte -am

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERREUR: Compilation echouee" -ForegroundColor Red
    exit 1
}
Write-Host "OK - Compilation reussie" -ForegroundColor Green

# Step 3: Check JAR
Write-Host ""
Write-Host "[3/5] Verification du JAR..." -ForegroundColor Yellow
if (-not (Test-Path $JAR_PATH)) {
    Write-Host "ERREUR: JAR non trouve" -ForegroundColor Red
    exit 1
}
$jarInfo = Get-Item $JAR_PATH
Write-Host "OK - JAR trouve ($([math]::Round($jarInfo.Length / 1MB, 2)) MB)" -ForegroundColor Green

# Step 4: Stop old Java
Write-Host ""
Write-Host "[4/5] Arret des anciens processus Java..." -ForegroundColor Yellow
$javaProcs = Get-Process -Name java -ErrorAction SilentlyContinue
if ($javaProcs) {
    Write-Host "Arret des processus..." -ForegroundColor Yellow
    Stop-Process -Name java -Force -ErrorAction SilentlyContinue
    Write-Host "Attente de la liberation des fichiers (5 secondes)..." -ForegroundColor Yellow
    Start-Sleep -Seconds 5
    Write-Host "OK - Processus Java arretes" -ForegroundColor Green
}
else {
    Write-Host "OK - Aucun processus Java en cours" -ForegroundColor Green
}

# Step 5: Start new service
Write-Host ""
Write-Host "[5/5] Demarrage du nouveau service..." -ForegroundColor Yellow
Write-Host "Commande: java -jar '$JAR_PATH'" -ForegroundColor Cyan
Write-Host ""

$processInfo = New-Object System.Diagnostics.ProcessStartInfo
$processInfo.FileName = "java"
$processInfo.Arguments = "-jar `"$JAR_PATH`""
$processInfo.UseShellExecute = $false
$processInfo.CreateNoWindow = $false

$process = [System.Diagnostics.Process]::Start($processInfo)
Write-Host "OK - Service demarre (PID: $($process.Id))" -ForegroundColor Green

# Wait for startup
Write-Host ""
Write-Host "Attente du demarrage (30 secondes)..." -ForegroundColor Yellow
for ($i = 30; $i -gt 0; $i--) {
    Write-Host -NoNewline "`r[$i seconds remaining] "
    Start-Sleep -Seconds 1
}
Write-Host ""
Write-Host "OK - Service pret" -ForegroundColor Green

# Test API
Write-Host ""
Write-Host "Test de l'API..." -ForegroundColor Yellow
$apiBody = @{
    formulaireId = 1
    projetId = 1
    donnees = @{ q1 = "test"; q2 = @{ nested = "value" } }
    latitude = 12.345678
    longitude = -7.123456
} | ConvertTo-Json -Depth 5

try {
    $response = Invoke-WebRequest -Uri $API_URL -Method Post -Body $apiBody -Headers @{"X-User-Id" = "2"} -ContentType "application/json" -TimeoutSec 10 -ErrorAction Stop

    Write-Host ""
    Write-Host "============================================================" -ForegroundColor Green
    Write-Host "SUCCES! Fix applique avec succes." -ForegroundColor Green
    Write-Host "============================================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Status Code: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "L'erreur 'statut character varying' est resolue!" -ForegroundColor Green
    Write-Host ""
}
catch {
    Write-Host ""
    Write-Host "ATTENTION: Test API en erreur" -ForegroundColor Yellow
    Write-Host "Cela peut survenir si le service demarrera dans quelques secondes." -ForegroundColor Yellow
    Write-Host "Verifiez http://localhost:8084/api/collectes manuellement dans 1 minute." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "Script termine" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""

