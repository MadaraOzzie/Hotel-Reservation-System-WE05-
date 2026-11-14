@echo off
setlocal
cd /d "%~dp0"
where mvn >nul 2>&1
if errorlevel 1 (
  echo Maven 3.9 or newer was not found. Install Maven and add it to PATH.
  exit /b 1
)
call mvn clean package
exit /b %ERRORLEVEL%
