@echo off
echo ========================================
echo Fixing E-commerce Backend - Complete Solution
echo ========================================
echo.

echo Step 1: Stopping any running backend instances...
taskkill /F /FI "WINDOWTITLE eq Spring Boot Backend*" 2>nul
timeout /t 2 /nobreak >nul
echo.

echo Step 2: Fixing database payment_status column...
echo Running SQL fix...
mysql -u root -pCoder@3570 -e "USE ecommerce; ALTER TABLE orders MODIFY COLUMN payment_status VARCHAR(50); ALTER TABLE orders MODIFY COLUMN order_status VARCHAR(50);"
if %errorlevel% equ 0 (
    echo ✅ SUCCESS: Database schema updated
) else (
    echo ⚠️ Warning: Database update had issues, but continuing...
)
echo.

echo Step 3: Cleaning Maven project...
call mvn clean
echo.

echo Step 4: Rebuilding project with dependencies...
call mvn install -DskipTests
if %errorlevel% neq 0 (
    echo ❌ ERROR: Maven build failed
    echo Please check the errors above
    pause
    exit /b 1
)
echo ✅ SUCCESS: Maven build completed
echo.

echo Step 5: Starting Spring Boot backend...
echo Backend starting on http://localhost:8082
echo.
echo ========================================
echo 📧 EMAIL NOTIFICATIONS ENABLED
echo ========================================
echo Emails will be sent to users at:
echo   - Order Creation (Thank You email)
echo   - Order Confirmation (COD orders)
echo   - Payment Completion
echo   - Order Status Changes
echo.
echo Check backend console for email logs
echo ========================================
echo.

start "Spring Boot Backend" cmd /k "mvn spring-boot:run"

echo.
echo ✅ Backend is starting...
echo.
echo Please wait 30-45 seconds for complete startup
echo Then test order creation from your frontend
echo.
echo Watch this window and the backend window for logs
echo.
pause

