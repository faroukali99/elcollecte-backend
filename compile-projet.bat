@echo off
cd /d "C:\Semestre 5\Projet tuteure\Diagrammes UML\elcollecte-backend\elcollecte"
echo Compilation du service-projet...
call mvn clean package -DskipTests -pl service-projet -q
if %ERRORLEVEL% EQU 0 (
    echo ✓ Compilation réussie!
    REM Chercher le fichier JAR
    for /f "delims=" %%A in ('dir /b /s service-projet\target\service-projet-*.jar 2^>nul ^| findstr /V ".original"') do (
        echo ✓ JAR créé: %%A
    )
) else (
    echo ✗ Erreur de compilation
    mvn clean package -DskipTests -pl service-projet 2>&1 | tail -300
)
pause

