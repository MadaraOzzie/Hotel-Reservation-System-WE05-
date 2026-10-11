@echo off
setlocal
cd /d "%~dp0"
if defined JAVA_HOME (
  set "HOTEL_JAVA=%JAVA_HOME%\bin\java.exe"
) else (
  set "HOTEL_JAVA=java"
)
if not defined PORT set "PORT=8095"
echo Starting Serene Hotel at http://localhost:%PORT%...
"%HOTEL_JAVA%" -jar target\hotel-reservation-system-1.0.0.jar
if errorlevel 1 (
  echo Startup failed. Check Java 17+, the port, and the README.
  pause
)
