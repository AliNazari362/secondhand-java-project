@echo off
title SecondHand Marketplace - Launcher
echo ========================================
echo  SecondHand Marketplace Launcher
echo ========================================
echo.

:: پیدا کردن مسیر Maven داخل IntelliJ
set IDEA_PATH=C:\Program Files\JetBrains\IntelliJ IDEA 2024.1
set MAVEN_HOME=%IDEA_PATH%\plugins\maven\lib\maven3
set PATH=%MAVEN_HOME%\bin;%PATH%

:: بررسی اینکه Maven پیدا شد یا نه
where mvn >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Maven not found in IntelliJ directory.
    echo Please update the IDEA_PATH variable in this script.
    echo Current path: %IDEA_PATH%
    pause
    exit /b 1
)

echo [1/2] Starting Backend Server...
start "Backend Server" cmd /k "cd /d %~dp0backend && mvn spring-boot:run"

echo Waiting 8 seconds for backend to initialize...
timeout /t 8 /nobreak >nul

echo [2/2] Starting Frontend Application...
start "Frontend Application" cmd /k "cd /d %~dp0frontend && mvn javafx:run"

echo.
echo ========================================
echo Both services are starting up.
echo ========================================
pause