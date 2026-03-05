#!/usr/bin/env powershell

# ============================================
# Guide de démarrage rapide - eLcollecte
# ============================================

Write-Host ""
Write-Host "╔════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║     Démarrage du Système eLcollecte - Guide Rapide    ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

Write-Host "📋 ÉTAPES À SUIVRE:" -ForegroundColor Green
Write-Host ""

Write-Host "1️⃣  LANCER DOCKER DESKTOP" -ForegroundColor Yellow
Write-Host "   → Ouvrir Docker Desktop (depuis le menu Démarrer)" -ForegroundColor Gray
Write-Host "   → Attendre le message: 'Docker Desktop is running'" -ForegroundColor Gray
Write-Host "   → Attendre 30-60 secondes que le daemon démarre" -ForegroundColor Gray
Write-Host ""

Write-Host "2️⃣  VÉRIFIER QUE DOCKER FONCTIONNE" -ForegroundColor Yellow
Write-Host "   $ docker ps" -ForegroundColor Cyan
Write-Host "   (Devrait afficher une liste vide, pas d'erreur)" -ForegroundColor Gray
Write-Host ""

Write-Host "3️⃣  NAVIGUER DANS LE DOSSIER ELCOLLECTE" -ForegroundColor Yellow
Write-Host "   $ cd elcollecte" -ForegroundColor Cyan
Write-Host ""

Write-Host "4️⃣  VÉRIFIER LA CONFIGURATION" -ForegroundColor Yellow
Write-Host "   $ Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope Process -Force" -ForegroundColor Cyan
Write-Host "   $ .\docker-check.ps1" -ForegroundColor Cyan
Write-Host ""

Write-Host "5️⃣  CONFIGURER L'ENVIRONNEMENT" -ForegroundColor Yellow
Write-Host "   $ Copy-Item .env.example .env" -ForegroundColor Cyan
Write-Host "   (Modifier .env si nécessaire)" -ForegroundColor Gray
Write-Host ""

Write-Host "6️⃣  LANCER LE SYSTÈME" -ForegroundColor Yellow
Write-Host "   $ .\deploy-docker.ps1 -Action start" -ForegroundColor Cyan
Write-Host "   (Première fois: ~2-5 minutes, images téléchargées et compilées)" -ForegroundColor Gray
Write-Host ""

Write-Host "✅ ACCÈS AUX SERVICES:" -ForegroundColor Green
Write-Host "   🌐 Frontend:     http://localhost:5173" -ForegroundColor Cyan
Write-Host "   🚪 API Gateway:  http://localhost:8080" -ForegroundColor Cyan
Write-Host "   🎯 Eureka:       http://localhost:8761" -ForegroundColor Cyan
Write-Host "   📊 Kafka UI:     http://localhost:9090" -ForegroundColor Cyan
Write-Host ""

Write-Host "⚠️  IMPORTANT:" -ForegroundColor Red
Write-Host "   • Laisser Docker Desktop OUVERT et EN COURS D'EXÉCUTION" -ForegroundColor Gray
Write-Host "   • La première démonstration peut prendre 5-10 minutes" -ForegroundColor Gray
Write-Host "   • Garder au moins 8-10 GB de RAM disponible" -ForegroundColor Gray
Write-Host ""

Write-Host "🆘 EN CAS DE PROBLÈME:" -ForegroundColor Yellow
Write-Host "   $ .\docker-check.ps1              # Vérifier configuration" -ForegroundColor Cyan
Write-Host "   $ .\deploy-docker.ps1 -Action logs # Voir les logs" -ForegroundColor Cyan
Write-Host "   $ .\deploy-docker.ps1 -Action stop # Arrêter" -ForegroundColor Cyan
Write-Host ""

Write-Host "📚 DOCUMENTATION:" -ForegroundColor Green
Write-Host "   • README_DOCKER.md" -ForegroundColor Gray
Write-Host "   • DOCKER_DEPLOYMENT_GUIDE.md" -ForegroundColor Gray
Write-Host "   • DOCKER_SUMMARY.md" -ForegroundColor Gray
Write-Host ""

