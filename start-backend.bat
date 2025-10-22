@echo off
echo ========================================
echo    Starting Spring Boot Backend Server
echo ========================================
echo.
echo Starting on port: 8082
echo.
echo Please wait 30-60 seconds for the server to fully start...
echo.
echo You'll see:
echo - Tomcat started on port(s): 8082
echo - Started GroceryApplication in X seconds
echo.
echo ========================================
echo.

cd /d "%~dp0"
call mvnw.cmd spring-boot:run

pause

