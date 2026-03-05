@echo off
REM ============================================
REM Script de deploiement Docker pour eLcollecte
REM ============================================

setlocal enabledelayedexpansion

if "%1"=="" (
    echo Usage: deploy-docker.bat [start^|stop^|restart^|logs^|build^|status^|clean]
    echo.
    echo Examples:
    echo   deploy-docker.bat start
    echo   deploy-docker.bat stop
    echo   deploy-docker.bat logs service-collecte
    exit /b 1
)

set ACTION=%1
set SERVICE=%2
set DOCKER_COMPOSE_FILE=docker-compose.full.yml
set PROJECT_NAME=elcollecte

goto %ACTION%

:start
echo ========================================
echo Starting eLcollecte Services
echo ========================================
echo.
echo Building images...
docker-compose -f %DOCKER_COMPOSE_FILE% -p %PROJECT_NAME% build
if errorlevel 1 (
    echo ERROR: Build failed
    exit /b 1
)

echo.
echo Starting containers...
docker-compose -f %DOCKER_COMPOSE_FILE% -p %PROJECT_NAME% up -d
if errorlevel 1 (
    echo ERROR: Start failed
    exit /b 1
)

echo.
echo Waiting for services to start ^(30 seconds^)...
timeout /t 30 /nobreak

echo.
call :status
exit /b 0

:stop
echo ========================================
echo Stopping eLcollecte Services
echo ========================================
echo.
docker-compose -f %DOCKER_COMPOSE_FILE% -p %PROJECT_NAME% down
if errorlevel 1 (
    echo ERROR: Stop failed
    exit /b 1
)
echo Services stopped successfully
exit /b 0

:restart
echo ========================================
echo Restarting eLcollecte Services
echo ========================================
echo.
call :stop
echo.
echo Waiting before restart ^(5 seconds^)...
timeout /t 5 /nobreak
echo.
call :start
exit /b 0

:logs
echo ========================================
echo eLcollecte Logs
echo ========================================
echo.
if "%SERVICE%"=="" (
    docker-compose -f %DOCKER_COMPOSE_FILE% -p %PROJECT_NAME% logs -f --tail 100
) else (
    docker-compose -f %DOCKER_COMPOSE_FILE% -p %PROJECT_NAME% logs -f --tail 100 %SERVICE%
)
exit /b %errorlevel%

:build
echo ========================================
echo Building Docker Images
echo ========================================
echo.
docker-compose -f %DOCKER_COMPOSE_FILE% -p %PROJECT_NAME% build --no-cache
exit /b %errorlevel%

:status
echo ========================================
echo eLcollecte Services Status
echo ========================================
echo.
docker-compose -f %DOCKER_COMPOSE_FILE% -p %PROJECT_NAME% ps
echo.
echo Service Access URLs:
echo   Frontend: http://localhost:5173
echo   API Gateway: http://localhost:8080
echo   Eureka Discovery: http://localhost:8761
echo   Kafka UI: http://localhost:9090
echo   MinIO: http://localhost:9001 ^(minioadmin/minioadmin123^)
echo   PostgreSQL: localhost:5432 ^(postgres/postgres123^)
echo.
exit /b 0

:clean
echo ========================================
echo Cleaning up eLcollecte
echo ========================================
echo.
echo WARNING: This will remove all containers, images, and volumes
setlocal
set /p CONFIRM="Are you sure? ^(yes/no^): "
if /i "%CONFIRM%"=="yes" (
    echo Stopping services...
    docker-compose -f %DOCKER_COMPOSE_FILE% -p %PROJECT_NAME% down -v

    echo.
    echo Removing images...
    docker image prune -af

    echo.
    echo Cleanup completed
) else (
    echo Cleanup cancelled
)
endlocal
exit /b 0

:unknown
echo ERROR: Unknown action "%ACTION%"
echo Valid actions: start, stop, restart, logs, build, status, clean
exit /b 1

