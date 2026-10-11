@echo off
setlocal
cd /d "%~dp0"
call mvn clean package
if errorlevel 1 pause
