# Microservices API Testing Guide

## Using Postman

### 1. User Registration and Login

**Register User**
```
POST http://localhost:8080/api/users/register
Content-Type: application/json

{
  "email": "john@example.com",
  "username": "johndoe",
  "password": "password123",
  "phoneNumber": "9876543210"
}
```

**Login**
```
POST http://localhost:8080/api/users/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}

Response: { "token": "eyJhbGciOiJIUzI1NiJ9..." }
```

### 2. Product Management

**Create Product**
```
POST http://localhost:8080/api/products
Content-Type: application/json

{
  "name": "Organic Apples",
  "description": "Fresh organic apples",
  "price": 150.0,
  "stock": 100,
  "imageUrl": "https://example.com/apple.jpg",
  "brand": "FreshFarm",
  "unit": "kg",
  "weight": 1.0,
  "isAvailable": true,
  "isFeatured": true,
  "discount": 10.0
}
```

**Get All Products**
```
GET http://localhost:8080/api/products
```

**Search Products**
```
GET http://localhost:8080/api/products/search?keyword=apple
```

### 3. Cart Operations

**Add to Cart**
```
POST http://localhost:8080/api/cart/add
Content-Type: application/json

{
  "userId": 1,
  "productId": 1,
  "quantity": 2,
  "price": 150.0
}
```

**Get User Cart**
```
GET http://localhost:8080/api/cart/user/1
```

### 4. Order Management

**Create Order** (Updated - All Fields)
```
POST http://localhost:8080/api/orders
Content-Type: application/json

{
  "userId": 1,
  "shippingAddressId": 1,
  "paymentMethod": "CASH_ON_DELIVERY",
  "paymentStatus": "PENDING",
  "orderStatus": "PENDING",
  "totalAmount": 300.0,
  "shippingFee": 50.0,
  "discount": 0.0,
  "taxAmount": 0.0,
  "items": [
    {
      "productId": 1,
      "productName": "Organic Apples",
      "quantity": 2,
      "price": 150.0,
      "subtotal": 300.0
    }
  ]
}
```

**Get User Orders**
```
GET http://localhost:8080/api/orders/user/1
```

**Update Order Status**
```
PATCH http://localhost:8080/api/orders/1/status?status=SHIPPED
```

**Cancel Order**
```
POST http://localhost:8080/api/orders/1/cancel?reason=Customer%20requested%20cancellation
```

### 5. Payment Processing

**Create Payment**
```
POST http://localhost:8080/api/payments
Content-Type: application/json

{
  "orderId": 1,
  "paymentMethod": "PAYPAL",
  "amount": 300.0
}
```

**Process COD**
```
POST http://localhost:8080/api/payments/cod/1
```

## Testing Flow

1. **Register User** → Save user ID
2. **Login** → Save JWT token (for future use)
3. **Create Address** → Save address ID
4. **Create Products** → Save product IDs
5. **Add to Cart** → Verify cart total
6. **Create Order** (with all required fields) → Save order ID
7. **Process Payment** → Complete transaction
8. **Check Order Status** → Verify order created

## Important Notes

### Order Creation Fields
When creating an order, make sure to include:
- `userId` - The user placing the order
- `shippingAddressId` - Delivery address ID
- `paymentMethod` - Payment method (e.g., "CASH_ON_DELIVERY", "PAYPAL")
- `paymentStatus` - Payment status enum: PENDING, COMPLETED, FAILED, REFUNDED
- `orderStatus` - Order status enum: PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED
- `totalAmount` - Total order amount
- `shippingFee` - Shipping charges
- `discount` - Discount amount
- `taxAmount` - Tax amount
- `items` - Array of order items with product details

### Order Status Values
- `PENDING` - Order placed but not confirmed
- `CONFIRMED` - Order confirmed
- `PROCESSING` - Order being prepared
- `SHIPPED` - Order shipped
- `DELIVERED` - Order delivered
- `CANCELLED` - Order cancelled

### Payment Status Values
- `PENDING` - Payment not yet completed
- `COMPLETED` - Payment successful
- `FAILED` - Payment failed
- `REFUNDED` - Payment refunded

## Service Health Check

Check Eureka Dashboard:
```
http://localhost:8761
```

All 6 services should show as UP:
- USER-SERVICE
- PRODUCT-SERVICE
- CART-SERVICE
- ORDER-SERVICE
- PAYMENT-SERVICE
- API-GATEWAY
# Docker Compose for E-Commerce Microservices

version: '3.8'

services:
  # MySQL Database for User Service
  mysql-users:
    image: mysql:8.0
    container_name: mysql-users
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: ecommerce_users
    ports:
      - "3307:3306"
    volumes:
      - mysql-users-data:/var/lib/mysql

  # MySQL Database for Product Service
  mysql-products:
    image: mysql:8.0
    container_name: mysql-products
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: ecommerce_products
    ports:
      - "3308:3306"
    volumes:
      - mysql-products-data:/var/lib/mysql

  # MySQL Database for Cart Service
  mysql-carts:
    image: mysql:8.0
    container_name: mysql-carts
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: ecommerce_carts
    ports:
      - "3309:3306"
    volumes:
      - mysql-carts-data:/var/lib/mysql

  # MySQL Database for Order Service
  mysql-orders:
    image: mysql:8.0
    container_name: mysql-orders
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: ecommerce_orders
    ports:
      - "3310:3306"
    volumes:
      - mysql-orders-data:/var/lib/mysql

  # MySQL Database for Payment Service
  mysql-payments:
    image: mysql:8.0
    container_name: mysql-payments
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: ecommerce_payments
    ports:
      - "3311:3306"
    volumes:
      - mysql-payments-data:/var/lib/mysql

  # Eureka Service Discovery
  eureka-server:
    build: ./eureka-server
    container_name: eureka-server
    ports:
      - "8761:8761"
    environment:
      - SPRING_PROFILES_ACTIVE=docker

  # User Service
  user-service:
    build: ./user-service
    container_name: user-service
    ports:
      - "8081:8081"
    depends_on:
      - mysql-users
      - eureka-server
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql-users:3306/ecommerce_users
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/

  # Product Service
  product-service:
    build: ./product-service
    container_name: product-service
    ports:
      - "8082:8082"
    depends_on:
      - mysql-products
      - eureka-server
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql-products:3306/ecommerce_products
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/

  # Cart Service
  cart-service:
    build: ./cart-service
    container_name: cart-service
    ports:
      - "8083:8083"
    depends_on:
      - mysql-carts
      - eureka-server
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql-carts:3306/ecommerce_carts
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/

  # Order Service
  order-service:
    build: ./order-service
    container_name: order-service
    ports:
      - "8084:8084"
    depends_on:
      - mysql-orders
      - eureka-server
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql-orders:3306/ecommerce_orders
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/

  # Payment Service
  payment-service:
    build: ./payment-service
    container_name: payment-service
    ports:
      - "8085:8085"
    depends_on:
      - mysql-payments
      - eureka-server
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql-payments:3306/ecommerce_payments
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/

  # API Gateway
  api-gateway:
    build: ./api-gateway
    container_name: api-gateway
    ports:
      - "8080:8080"
    depends_on:
      - eureka-server
      - user-service
      - product-service
      - cart-service
      - order-service
      - payment-service
    environment:
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/

volumes:
  mysql-users-data:
  mysql-products-data:
  mysql-carts-data:
  mysql-orders-data:
  mysql-payments-data:

