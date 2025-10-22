@echo off
echo ========================================
echo Restarting Order Service
echo ========================================
echo.

cd "E:\New folder (12)\demo\microservices\order-service"

echo Building Order Service...
call mvn clean install -DskipTests
echo.

if %ERRORLEVEL% EQU 0 (
    echo Build successful! Starting Order Service on port 8084...
    start "Order Service - Port 8084" cmd /k "mvn spring-boot:run"
    echo.
    echo Order Service is starting...
    echo Wait 30 seconds for it to fully start and register with Eureka
    echo Then test your order creation again!
) else (
    echo Build failed! Please check the error messages above.
)

pause

