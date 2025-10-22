# E-Commerce Microservices Architecture

## Services Overview

### 1. User Service (Port: 8081)
- User registration and authentication
- JWT token generation
- User profile management
- Address management
- Admin user management

### 2. Product Service (Port: 8082)
- Product CRUD operations
- Category management
- Vendor management
- Product reviews
- Wishlist management

### 3. Cart Service (Port: 8083)
- Cart management
- Cart item operations
- Integration with Product Service for product details

### 4. Order Service (Port: 8084)
- Order creation and management
- Order status tracking
- Order cancellation
- Integration with Cart, User, and Payment services

### 5. Payment Service (Port: 8085)
- PayPal payment processing
- Cash on delivery handling
- Payment status management

### 6. API Gateway (Port: 8080)
- Single entry point
- Request routing
- Load balancing
- Authentication verification

## Communication
- REST APIs for synchronous communication
- Spring Cloud OpenFeign for inter-service communication
- JWT tokens for authentication across services

## Database Strategy
- Each service has its own database schema
- Separate database instances for true microservices architecture

## Technology Stack
- Spring Boot 3.1.5
- Spring Cloud (Gateway, OpenFeign, Eureka)
- MySQL for databases
- JWT for authentication
- Docker for containerization

