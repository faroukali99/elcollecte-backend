@echo off
REM Script de build et déploiement du service-collecte

setlocal enabledelayedexpansion

REM Chemin du projet
set PROJECT_DIR=C:\Semestre 5\Projet tuteure\Diagrammes UML\elcollecte-backend\elcollecte

REM Chemin Maven
set MAVEN_HOME=%USERPROFILE%\apache-maven-3.9.5
set MVN_CMD=%MAVEN_HOME%\bin\mvn.cmd

echo ========================================
echo BUILD: Compilation du module service-collecte
echo ========================================

cd /d "%PROJECT_DIR%"

REM Compiler le module
call %MVN_CMD% clean package -DskipTests -pl service-collecte -am

if %ERRORLEVEL% neq 0 (
    echo.
    echo ERREUR: Compilation échouée!
    pause
    exit /b 1
)

echo.
echo ========================================
echo DEPLOY: Le JAR a été créé avec succès
echo ========================================
echo.
echo JAR location:
echo %PROJECT_DIR%\service-collecte\target\service-collecte-1.0.0-SNAPSHOT.jar
echo.
echo Étapes suivantes:
echo 1. Arrêtez l'instance actuelle de service-collecte (Ctrl+C si en foreground)
echo 2. Lancez le nouveau JAR avec:
echo    java -jar "%PROJECT_DIR%\service-collecte\target\service-collecte-1.0.0-SNAPSHOT.jar"
echo 3. Testez avec un POST à http://localhost:8084/api/collectes
echo.
pause

