@echo off
echo ========================================
echo Starting Remaining E-Commerce Microservices
echo ========================================
echo.

cd E:\New folder (12)\demo\microservices

echo [1/6] Starting Eureka Server (Service Discovery)...
start "Eureka Server" cmd /k "cd eureka-server && mvn spring-boot:run"
echo Waiting 40 seconds for Eureka to be ready...
timeout /t 40 /nobreak

echo.
echo [2/6] Starting User Service...
start "User Service" cmd /k "cd user-service && mvn spring-boot:run"
timeout /t 15 /nobreak

echo.
echo [3/6] Product Service already running on port 8082 - SKIPPED
echo.

echo [4/6] Starting Cart Service...
start "Cart Service" cmd /k "cd cart-service && mvn spring-boot:run"
timeout /t 15 /nobreak

echo.
echo [5/6] Starting Order Service...
start "Order Service" cmd /k "cd order-service && mvn spring-boot:run"
timeout /t 15 /nobreak

echo.
echo [6/6] Starting Payment Service...
start "Payment Service" cmd /k "cd payment-service && mvn spring-boot:run"
timeout /t 15 /nobreak

echo.
echo [7/7] Starting API Gateway...
start "API Gateway" cmd /k "cd api-gateway && mvn spring-boot:run"

echo.
echo ========================================
echo All services are starting!
echo ========================================
echo.
echo Service Status:
echo [RUNNING] Product Service: http://localhost:8082
echo [STARTING] Eureka Server: http://localhost:8761
echo [STARTING] User Service: http://localhost:8081
echo [STARTING] Cart Service: http://localhost:8083
echo [STARTING] Order Service: http://localhost:8084
echo [STARTING] Payment Service: http://localhost:8085
echo [STARTING] API Gateway: http://localhost:8080
echo.
echo Please wait 2-3 minutes for all services to register with Eureka.
echo Then check: http://localhost:8761 (Eureka Dashboard)
echo.
echo All API requests should go through: http://localhost:8080
echo ========================================
pause

