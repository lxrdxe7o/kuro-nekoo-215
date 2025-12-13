@echo off
setlocal
title Vehicle Shop Management System Launcher
cls

echo ========================================
echo  Vehicle Shop Management System Launcher
echo ========================================

REM 1. Check for Java
echo.
echo Checking for Java...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [X] Java not found.
    echo Attempting to install OpenJDK 17 via Winget...
    
    where winget >nul 2>&1
    if %errorlevel% neq 0 (
        echo [ERROR] Winget not found. Please install JDK 17 manually.
        pause
        exit /b 1
    )
    
    echo Installing Microsoft OpenJDK 17...
    winget install Microsoft.OpenJDK.17 --accept-package-agreements --accept-source-agreements
    
    REM Refresh environment variables is tricky in batch, asking user to restart
    echo.
    echo [!] Java installed. You may need to restart this script or your terminal to recognize the new 'java' command.
    echo Press any key to try continuing anyway...
    pause
) else (
    echo [OK] Java found.
)

REM 2. Run Gradle
echo.
echo Building and Running Application...
echo.
call gradlew.bat run

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Application crashed or failed to build.
    pause
)
