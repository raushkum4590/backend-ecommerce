@echo off
echo ========================================
echo    Installing Kafka and Mail Dependencies
echo ========================================
echo.

cd /d "%~dp0"

echo Running Maven clean install...
echo.

call mvnw.cmd clean install -DskipTests

echo.
if %ERRORLEVEL% EQU 0 (
    echo ✅ Dependencies installed successfully!
    echo.
    echo All Kafka and Mail dependencies have been downloaded.
    echo You can now run the application.
) else (
    echo ❌ Build failed. Please check the errors above.
)

pause

