@echo off
setlocal
cd /d "%~dp0"

set "APP_JAR=target\hotel-reservation-system-1.0.0.jar"
if not exist "%APP_JAR%" (
  echo Runnable JAR not found. Building the project from source...
  call build.bat
  if errorlevel 1 (
    echo Build failed. Install JDK 17+, Maven 3.9+, and allow Maven to download dependencies on the first build.
    pause
    exit /b 1
  )
)

if defined JAVA_HOME (
  if not exist "%JAVA_HOME%\bin\java.exe" (
    echo JAVA_HOME does not point to a valid JDK: %JAVA_HOME%
    pause
    exit /b 1
  )
  set "HOTEL_JAVA=%JAVA_HOME%\bin\java.exe"
) else (
  where java >nul 2>&1
  if errorlevel 1 (
    echo Java 17 or newer was not found. Install a JDK or set JAVA_HOME.
    pause
    exit /b 1
  )
  set "HOTEL_JAVA=java"
)
if not defined PORT set "PORT=8095"
echo Starting Serene Hotel at http://localhost:%PORT%...
"%HOTEL_JAVA%" -jar "%APP_JAR%"
set "APP_EXIT=%ERRORLEVEL%"
if not "%APP_EXIT%"=="0" (
  echo Startup failed. Check Java 17+, the port, and the README.
  pause
)
exit /b %APP_EXIT%
