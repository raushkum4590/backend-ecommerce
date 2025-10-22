@echo off
echo ========================================
echo E-commerce Backend with Kafka + Email
echo ========================================
echo.

echo Step 1: Stopping any running instances...
taskkill /F /FI "WINDOWTITLE eq Spring Boot Backend*" 2>nul
taskkill /F /FI "WINDOWTITLE eq Kafka*" 2>nul
timeout /t 2 /nobreak >nul
echo.

echo Step 2: Fixing database schema...
echo Running SQL fix...
mysql -u root -pCoder@3570 -e "USE ecommerce; ALTER TABLE orders MODIFY COLUMN payment_status VARCHAR(50); ALTER TABLE orders MODIFY COLUMN order_status VARCHAR(50);"
if %errorlevel% equ 0 (
    echo ✅ SUCCESS: Database schema updated
) else (
    echo ⚠️ Warning: Database update had issues, but continuing...
)
echo.

echo Step 3: Starting Kafka services...
echo This may take 30-45 seconds...
cd /d "%~dp0"
if exist docker-compose-kafka.yml (
    start "Kafka Services" cmd /k "docker-compose -f docker-compose-kafka.yml up"
    echo ✅ Kafka starting in separate window...
    echo Waiting 30 seconds for Kafka to initialize...
    timeout /t 30 /nobreak
) else (
    echo ⚠️ docker-compose-kafka.yml not found. Kafka will not start.
    echo Backend will run without Kafka support.
    timeout /t 3 /nobreak
)
echo.

echo Step 4: Cleaning Maven project...
call mvn clean
echo.

echo Step 5: Rebuilding project...
call mvn install -DskipTests
if %errorlevel% neq 0 (
    echo ❌ ERROR: Maven build failed
    pause
    exit /b 1
)
echo ✅ SUCCESS: Maven build completed
echo.

echo Step 6: Starting Spring Boot backend...
echo Backend starting on http://localhost:8082
echo.
echo ========================================
echo 📧 EMAIL + 🔔 KAFKA NOTIFICATIONS ENABLED
echo ========================================
echo.
echo Emails will be sent to users for:
echo   ✅ Order Creation (Thank You email)
echo   ✅ Order Confirmation
echo   ✅ Payment Completion
echo   ✅ Order Status Changes
echo.
echo Real-time notifications via Kafka:
echo   🔔 Order events published to Kafka topics
echo   📨 Email notifications triggered by events
echo.
echo ========================================
echo.

start "Spring Boot Backend" cmd /k "mvn spring-boot:run"

echo.
echo ✅ All services are starting...
echo.
echo Please wait 20-30 seconds for complete startup
echo Then test order creation from your frontend
echo.
echo Watch the backend window for logs
echo.
pause

