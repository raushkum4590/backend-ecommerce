@echo off
echo ========================================
echo Fixing E-commerce Backend Issues
echo ========================================
echo.

echo Step 1: Fixing database payment_status column...
mysql -u root -pCoder@3570 < fix_payment_status_column.sql
if %errorlevel% neq 0 (
    echo ERROR: Failed to update database schema
    pause
    exit /b 1
)
echo SUCCESS: Database schema updated
echo.

echo Step 2: Cleaning and rebuilding Maven project...
call mvn clean install -DskipTests
if %errorlevel% neq 0 (
    echo ERROR: Maven build failed
    pause
    exit /b 1
)
echo SUCCESS: Maven build completed
echo.

echo Step 3: Starting Spring Boot backend...
echo Backend will start on port 8082
echo.
start "Spring Boot Backend" cmd /k "mvn spring-boot:run"

echo.
echo ========================================
echo All fixes applied successfully!
echo ========================================
echo.
echo Backend is starting on http://localhost:8082
echo Wait 30 seconds for the backend to fully start
echo Then test your order creation from the frontend
echo.
echo Email notifications will be sent to user emails
echo Check the backend console for email sending logs
echo.
pause

