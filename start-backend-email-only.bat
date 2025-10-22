@echo off
echo ========================================
echo E-commerce Backend - Email Notifications
echo ========================================
echo.

echo Step 1: Stopping any running backend instances...
taskkill /F /FI "WINDOWTITLE eq Spring Boot Backend*" 2>nul
timeout /t 2 /nobreak >nul
echo.

echo Step 2: Fixing database schema...
mysql -u root -pCoder@3570 -e "USE ecommerce; ALTER TABLE orders MODIFY COLUMN payment_status VARCHAR(50); ALTER TABLE orders MODIFY COLUMN order_status VARCHAR(50);"
if %errorlevel% equ 0 (
    echo ✅ Database schema updated
) else (
    echo ⚠️ Warning: Database update had issues
)
echo.

echo Step 3: Cleaning and rebuilding...
call mvn clean install -DskipTests
if %errorlevel% neq 0 (
    echo ❌ ERROR: Maven build failed
    pause
    exit /b 1
)
echo ✅ Build completed
echo.

echo Step 4: Starting backend...
echo.
echo ========================================
echo 📧 EMAIL NOTIFICATIONS ACTIVE
echo ========================================
echo Emails will be sent to users for:
echo   ✅ Order Creation (Thank You)
echo   ✅ Order Confirmation
echo   ✅ Payment Completion
echo   ✅ Order Status Updates
echo.
echo Note: Emails are sent ASYNCHRONOUSLY
echo Orders will be created immediately even
echo if email sending fails or is slow.
echo ========================================
echo.

start "Spring Boot Backend" cmd /k "mvn spring-boot:run"

echo ✅ Backend starting...
echo.
echo Please wait 30 seconds for startup
echo Then test from frontend: http://localhost:3000
echo.
pause

