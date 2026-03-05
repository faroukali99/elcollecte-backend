#!/usr/bin/env powershell

<#
.SYNOPSIS
Script de vérification de Docker et prérequis

.DESCRIPTION
Vérifie que Docker est correctement installé et configuré
pour exécuter le projet eLcollecte

.EXAMPLE
./docker-check.ps1
#>

$ErrorActionPreference = "Continue"

Write-Host ""
Write-Host "╔════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║     eLcollecte Docker Prerequisites Checker              ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

# Couleurs
$SUCCESS = "Green"
$ERROR = "Red"
$WARNING = "Yellow"
$INFO = "Cyan"

# Compteurs
$checksTotal = 0
$checksPass = 0
$checksFail = 0

function Test-Requirement {
    param(
        [string]$Name,
        [scriptblock]$Test,
        [string]$MinVersion = "",
        [string]$FixSuggestion = ""
    )

    $checksTotal++
    Write-Host "🔍 Vérification: $Name" -NoNewline
    Write-Host " ... " -NoNewline

    try {
        $result = & $Test

        if ($result -eq $true) {
            Write-Host "✅ OK" -ForegroundColor $SUCCESS
            if ($MinVersion) {
                Write-Host "   Version acceptée: $MinVersion" -ForegroundColor Gray
            }
            $checksPass++
            return $true
        } else {
            Write-Host "❌ ÉCHOUÉ" -ForegroundColor $ERROR
            if ($FixSuggestion) {
                Write-Host "   💡 Solution: $FixSuggestion" -ForegroundColor $WARNING
            }
            $checksFail++
            return $false
        }
    }
    catch {
        Write-Host "❌ ERREUR" -ForegroundColor $ERROR
        Write-Host "   Erreur: $($_.Exception.Message)" -ForegroundColor Gray
        if ($FixSuggestion) {
            Write-Host "   💡 Solution: $FixSuggestion" -ForegroundColor $WARNING
        }
        $checksFail++
        return $false
    }
}

# Tests
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host "SYSTÈME D'EXPLOITATION" -ForegroundColor $INFO
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray

$osInfo = Get-CimInstance Win32_OperatingSystem
Write-Host "Système: $($osInfo.Caption)" -ForegroundColor Gray
Write-Host "Version: $($osInfo.Version)" -ForegroundColor Gray

Test-Requirement -Name "Windows 10 ou plus récent" -Test {
    [Version]$ver = [System.Environment]::OSVersion.Version
    return $ver.Major -ge 10
} -FixSuggestion "Mettre à jour Windows"

Write-Host ""
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host "RESSOURCES SYSTÈME" -ForegroundColor $INFO
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray

$memory = Get-CimInstance Win32_ComputerSystem | Select-Object -ExpandProperty TotalPhysicalMemory
$memoryGB = [math]::Round($memory / 1GB, 2)
Write-Host "RAM disponible: $memoryGB GB" -ForegroundColor Gray

Test-Requirement -Name "Minimum 8 GB de RAM" -Test {
    $memory = Get-CimInstance Win32_ComputerSystem | Select-Object -ExpandProperty TotalPhysicalMemory
    return ($memory -ge 8GB)
} -FixSuggestion "Augmenter la RAM ou fermer les applications gourmandes"

$disk = Get-Volume C: | Select-Object SizeRemaining
$diskFreeGB = [math]::Round($disk.SizeRemaining / 1GB, 2)
Write-Host "Espace disque libre: $diskFreeGB GB" -ForegroundColor Gray

Test-Requirement -Name "Minimum 20 GB d'espace disque" -Test {
    $disk = Get-Volume C: | Select-Object SizeRemaining
    return ($disk.SizeRemaining -ge 20GB)
} -FixSuggestion "Libérer de l'espace disque ou augmenter la partition"

Write-Host ""
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host "OUTILS REQUIS" -ForegroundColor $INFO
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray

Test-Requirement -Name "Docker Desktop" -Test {
    $docker = Get-Command docker -ErrorAction SilentlyContinue
    if ($null -eq $docker) { return $false }

    $version = docker --version
    Write-Host " ($version)" -NoNewline -ForegroundColor Gray
    return $true
} -FixSuggestion "Télécharger et installer Docker Desktop depuis https://docker.com/products/docker-desktop"

Test-Requirement -Name "Docker Daemon en cours d'exécution" -Test {
    try {
        $out = docker info 2>&1
        return ($LASTEXITCODE -eq 0)
    } catch {
        return $false
    }
} -FixSuggestion "Démarrer Docker Desktop (voir dans la barre de tâches)"

Test-Requirement -Name "Docker Compose" -Test {
    $compose = Get-Command docker-compose -ErrorAction SilentlyContinue
    if ($null -ne $compose) {
        $version = docker-compose --version
        Write-Host " ($version)" -NoNewline -ForegroundColor Gray
        return $true
    }

    # Vérifier si intégré dans docker
    $version = docker compose version 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host " (intégré dans Docker)" -NoNewline -ForegroundColor Gray
        return $true
    }

    return $false
} -FixSuggestion "Installer docker-compose ou utiliser 'docker compose' (intégré)"

Test-Requirement -Name "Git (optionnel)" -Test {
    $git = Get-Command git -ErrorAction SilentlyContinue
    if ($null -eq $git) { return $false }

    $version = git --version
    Write-Host " ($version)" -NoNewline -ForegroundColor Gray
    return $true
} -FixSuggestion "Optionnel - Télécharger depuis https://git-scm.com"

Write-Host ""
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host "CONFIGURATION DOCKER" -ForegroundColor $INFO
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray

try {
    $dockerInfo = docker info --format "json" | ConvertFrom-Json

    $memoryLimit = $dockerInfo.MemoryLimit
    $memoryGB = [math]::Round($memoryLimit / 1GB, 2)
    Write-Host "Mémoire allouée à Docker: $memoryGB GB" -ForegroundColor Gray

    Test-Requirement -Name "Mémoire Docker suffisante (8+ GB)" -Test {
        $memoryLimit = docker info --format "{{.MemoryLimit}}"
        return ([long]$memoryLimit -ge 8GB)
    } -FixSuggestion "Docker Desktop Settings > Resources > Memory - Augmenter à 8-12 GB"

    $cpus = $dockerInfo.NCPU
    Write-Host "CPUs alloués à Docker: $cpus" -ForegroundColor Gray

    Test-Requirement -Name "CPUs suffisants (4+)" -Test {
        $cpus = docker info --format "{{.NCPU}}"
        return ([int]$cpus -ge 4)
    } -FixSuggestion "Docker Desktop Settings > Resources > CPUs - Augmenter à 4-8"
}
catch {
    Write-Host "⚠️  Impossible de lire la configuration Docker" -ForegroundColor $WARNING
}

Write-Host ""
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host "PORTS DISPONIBLES" -ForegroundColor $INFO
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray

$requiredPorts = @(
    @{ Port = 5173; Name = "Frontend"; },
    @{ Port = 8080; Name = "API Gateway"; },
    @{ Port = 8761; Name = "Eureka Discovery"; },
    @{ Port = 5432; Name = "PostgreSQL"; },
    @{ Port = 6379; Name = "Redis"; },
    @{ Port = 9092; Name = "Kafka"; },
    @{ Port = 9090; Name = "Kafka UI"; },
    @{ Port = 9000; Name = "MinIO API"; },
    @{ Port = 9001; Name = "MinIO Console"; }
)

$portsFree = 0
$portsBusy = 0

foreach ($port in $requiredPorts) {
    $listener = [System.Net.NetworkInformation.IPGlobalProperties]::GetIPGlobalProperties().GetActiveTcpListeners()
    $inUse = $listener.Port -contains $port.Port

    if ($inUse) {
        Write-Host "⚠️  Port $($port.Port) ($($port.Name)): EN UTILISATION" -ForegroundColor $WARNING
        $portsBusy++
    } else {
        Write-Host "✅ Port $($port.Port) ($($port.Name)): DISPONIBLE" -ForegroundColor $SUCCESS
        $portsFree++
    }
}

Write-Host ""
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host "RÉSUMÉ" -ForegroundColor $INFO
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray

$percentage = if ($checksTotal -gt 0) { [math]::Round(($checksPass / $checksTotal) * 100) } else { 0 }

Write-Host ""
Write-Host "Vérifications: $checksPass/$checksTotal réussies ($percentage%)" -ForegroundColor $(if ($checksFail -eq 0) { $SUCCESS } else { $WARNING })
Write-Host ""

if ($checksFail -eq 0 -and $portsBusy -eq 0) {
    Write-Host "╔════════════════════════════════════════════════════════════╗" -ForegroundColor $SUCCESS
    Write-Host "║  ✅ SYSTÈME PRÊT POUR LE DÉPLOIEMENT DOCKER               ║" -ForegroundColor $SUCCESS
    Write-Host "╚════════════════════════════════════════════════════════════╝" -ForegroundColor $SUCCESS
    Write-Host ""
    Write-Host "Prochaines étapes:" -ForegroundColor $SUCCESS
    Write-Host "  1. cd elcollecte" -ForegroundColor Gray
    Write-Host "  2. Copy-Item .env.example .env" -ForegroundColor Gray
    Write-Host "  3. .\deploy-docker.ps1 -Action start" -ForegroundColor Gray
    Write-Host ""
} else {
    Write-Host "╔════════════════════════════════════════════════════════════╗" -ForegroundColor $WARNING
    Write-Host "║  ⚠️  DES PROBLÈMES ONT ÉTÉ DÉTECTÉS                       ║" -ForegroundColor $WARNING
    Write-Host "╚════════════════════════════════════════════════════════════╝" -ForegroundColor $WARNING
    Write-Host ""
    Write-Host "Veuillez résoudre les problèmes mentionnés ci-dessus avant de continuer." -ForegroundColor $WARNING
    Write-Host ""
    exit 1
}

