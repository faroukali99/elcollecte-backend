@echo off
REM Script de test de l'API POST /api/collectes

echo ========================================
echo TEST: POST /api/collectes
echo ========================================
echo.

REM Créer un fichier JSON temporaire
set TEMP_JSON=%TEMP%\collecte_test.json

(
    echo {
    echo   "formulaireId": 1,
    echo   "projetId": 1,
    echo   "donnees": {
    echo     "q1": "valeur1",
    echo     "q2": {
    echo       "nested": "valeur2"
    echo     }
    echo   },
    echo   "latitude": 12.345678,
    echo   "longitude": -7.123456
    echo }
) > "%TEMP_JSON%"

echo Payload:
type "%TEMP_JSON%"
echo.
echo Envoi du POST à http://localhost:8084/api/collectes...
echo.

REM Utiliser PowerShell pour faire la requête
powershell -Command ^
  "$body = Get-Content '%TEMP_JSON%' -Raw; " ^
  "$result = Invoke-WebRequest -Uri 'http://localhost:8084/api/collectes' -Method Post -Body $body -ContentType 'application/json' -Headers @{'X-User-Id'='2'} -ErrorAction SilentlyContinue -PassThru; " ^
  "Write-Output ('Status: ' + $result.StatusCode); " ^
  "Write-Output ('Response: ' + $result.Content)"

echo.
echo ========================================
echo TEST TERMINÉ
echo ========================================
echo.
echo Si vous voyez un status 201 et un ID de collecte dans la réponse: SUCCESS!
echo Si vous voyez toujours l'erreur sur 'statut': Le JAR n'a pas été redéployé correctement
echo.
pause

