@echo off
REM ============================================================================
REM SCRIPT RAPIDE - Redémarrage du service-collecte avec le JAR compilé
REM ============================================================================

setlocal enabledelayedexpansion

set JAR_PATH=C:\Semestre 5\Projet tuteure\Diagrammes UML\elcollecte-backend\elcollecte\service-collecte\target\service-collecte-1.0.0-SNAPSHOT.jar

echo.
echo ╔════════════════════════════════════════════════════════════════╗
echo ║           SERVICE-COLLECTE - REDÉMARRAGE                       ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.

REM Arrêter les processus Java existants
echo Arrêt des processus Java...
taskkill /F /IM java.exe >nul 2>&1
timeout /t 2 /nobreak

REM Vérifier que le JAR existe
if not exist "%JAR_PATH%" (
    echo.
    echo [ERREUR] JAR non trouvé : %JAR_PATH%
    echo.
    echo Assurez-vous d'avoir d'abord exécuté: FIX_AND_DEPLOY.ps1
    echo.
    pause
    exit /b 1
)

echo [OK] JAR trouvé
echo.
echo Démarrage du service...
echo  Commande: java -jar "%JAR_PATH%"
echo.

REM Lancer le service
java -jar "%JAR_PATH%"

pause

