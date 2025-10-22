@echo off
echo ========================================
echo E-Commerce Microservices Startup
echo ========================================
echo.

cd microservices

echo Starting Eureka Server (Service Discovery)...
start "Eureka Server" cmd /k "cd eureka-server && mvn spring-boot:run"
timeout /t 30

echo Starting User Service...
start "User Service" cmd /k "cd user-service && mvn spring-boot:run"
timeout /t 10

echo Starting Product Service...
start "Product Service" cmd /k "cd product-service && mvn spring-boot:run"
timeout /t 10

echo Starting Cart Service...
start "Cart Service" cmd /k "cd cart-service && mvn spring-boot:run"
timeout /t 10

echo Starting Order Service...
start "Order Service" cmd /k "cd order-service && mvn spring-boot:run"
timeout /t 10

echo Starting Payment Service...
start "Payment Service" cmd /k "cd payment-service && mvn spring-boot:run"
timeout /t 10

echo Starting API Gateway...
start "API Gateway" cmd /k "cd api-gateway && mvn spring-boot:run"

echo.
echo ========================================
echo All services are starting up!
echo ========================================
echo.
echo Service URLs:
echo - Eureka Dashboard: http://localhost:8761
echo - API Gateway: http://localhost:8080
echo - User Service: http://localhost:8081
echo - Product Service: http://localhost:8082
echo - Cart Service: http://localhost:8083
echo - Order Service: http://localhost:8084
echo - Payment Service: http://localhost:8085
echo.
echo All requests should go through API Gateway (port 8080)
echo ========================================

