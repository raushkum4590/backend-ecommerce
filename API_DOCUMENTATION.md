# E-Commerce Grocery Application - API Documentation

## Overview
This is a complete backend for an e-commerce grocery application built with Spring Boot, featuring:
- User Authentication & Authorization (JWT)
- Product Management with Categories
- Shopping Cart
- Order Management with Status Tracking
- Wishlist
- Product Reviews & Ratings
- Address Management
- Admin Dashboard
- Search & Filter Capabilities

## Base URL
```
http://localhost:8082/api
```

---

## Authentication Endpoints

### Register User
```
POST /auth/register
```
**Request Body:**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123"
}
```

### Login
```
POST /auth/login
```
**Request Body:**
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```
**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

## Product Endpoints

### Get All Products
```
GET /products
```

### Get Available Products
```
GET /products/available
```

### Get Featured Products
```
GET /products/featured
```

### Get Top Rated Products
```
GET /products/top-rated
```

### Get New Products
```
GET /products/new
```

### Get Product by ID
```
GET /products/{id}
```

### Search Products
```
GET /products/search?keyword=apple
```

### Filter Products
```
GET /products/filter?categoryId=1&minPrice=0&maxPrice=100
```

### Get Products by Category
```
GET /products/category/{categoryId}
```

### Create Product (Admin Only)
```
POST /products
Authorization: Bearer {token}
```
**Request Body:**
```json
{
  "name": "Fresh Apples",
  "category": {"id": 1},
  "price": 3.99,
  "stock": 100,
  "description": "Fresh organic apples",
  "brand": "FreshFarms",
  "unit": "kg",
  "weight": 1.0,
  "imageUrl": "http://example.com/apple.jpg",
  "isAvailable": true,
  "isFeatured": false,
  "discount": 0
}
```

### Update Product (Admin Only)
```
PUT /products/{id}
Authorization: Bearer {token}
```

### Delete Product (Admin Only)
```
DELETE /products/{id}
Authorization: Bearer {token}
```

---

## Category Endpoints

### Get All Categories
```
GET /categories
```

### Get Category by ID
```
GET /categories/{id}
```

### Create Category (Admin Only)
```
POST /categories
Authorization: Bearer {token}
```
**Request Body:**
```json
{
  "name": "Fruits",
  "description": "Fresh fruits",
  "imageUrl": "http://example.com/fruits.jpg"
}
```

### Update Category (Admin Only)
```
PUT /categories/{id}
Authorization: Bearer {token}
```

### Delete Category (Admin Only)
```
DELETE /categories/{id}
Authorization: Bearer {token}
```

---

## Cart Endpoints

### Get User Cart
```
GET /cart
Authorization: Bearer {token}
```

### Get Cart Summary
```
GET /cart/summary
Authorization: Bearer {token}
```
**Response:**
```json
{
  "totalItems": 5,
  "subtotal": 45.50,
  "shippingFee": 0.0,
  "tax": 3.64,
  "total": 49.14
}
```

### Add to Cart
```
POST /cart/add
Authorization: Bearer {token}
```
**Request Body:**
```json
{
  "productId": 1,
  "quantity": 2
}
```

### Update Cart Item Quantity
```
PUT /cart/{cartItemId}
Authorization: Bearer {token}
```
**Request Body:**
```json
{
  "quantity": 3
}
```

### Increment Quantity
```
PUT /cart/{cartItemId}/increment
Authorization: Bearer {token}
```

### Decrement Quantity
```
PUT /cart/{cartItemId}/decrement
Authorization: Bearer {token}
```

### Remove from Cart
```
DELETE /cart/{cartItemId}
Authorization: Bearer {token}
```

### Clear Cart
```
DELETE /cart/clear
Authorization: Bearer {token}
```

---

## Order Endpoints

### Create Order
```
POST /orders
Authorization: Bearer {token}
```
**Request Body:**
```json
{
  "addressId": 1,
  "paymentMethod": "Credit Card"
}
```

### Get User Orders
```
GET /orders
Authorization: Bearer {token}
```

### Get Order by ID
```
GET /orders/{id}
Authorization: Bearer {token}
```

### Cancel Order
```
POST /orders/{id}/cancel
Authorization: Bearer {token}
```
**Request Body (Optional):**
```json
{
  "reason": "Changed my mind"
}
```

---

## Address Endpoints

### Get User Addresses
```
GET /addresses
Authorization: Bearer {token}
```

### Get Default Address
```
GET /addresses/default
Authorization: Bearer {token}
```

### Get Address by ID
```
GET /addresses/{id}
Authorization: Bearer {token}
```

### Add Address
```
POST /addresses
Authorization: Bearer {token}
```
**Request Body:**
```json
{
  "fullName": "John Doe",
  "phoneNumber": "+1234567890",
  "addressLine1": "123 Main St",
  "addressLine2": "Apt 4B",
  "city": "New York",
  "state": "NY",
  "postalCode": "10001",
  "country": "USA",
  "isDefault": true
}
```

### Update Address
```
PUT /addresses/{id}
Authorization: Bearer {token}
```

### Delete Address
```
DELETE /addresses/{id}
Authorization: Bearer {token}
```

### Set Default Address
```
PUT /addresses/{id}/set-default
Authorization: Bearer {token}
```

---

## Wishlist Endpoints

### Get User Wishlist
```
GET /wishlist
Authorization: Bearer {token}
```

### Add to Wishlist
```
POST /wishlist/add/{productId}
Authorization: Bearer {token}
```

### Remove from Wishlist
```
DELETE /wishlist/{wishlistId}
Authorization: Bearer {token}
```

### Remove Product from Wishlist
```
DELETE /wishlist/product/{productId}
Authorization: Bearer {token}
```

### Check if Product in Wishlist
```
GET /wishlist/check/{productId}
Authorization: Bearer {token}
```

---

## Review Endpoints

### Get Product Reviews
```
GET /reviews/product/{productId}
```

### Get User Reviews
```
GET /reviews/user
Authorization: Bearer {token}
```

### Add Review
```
POST /reviews
Authorization: Bearer {token}
```
**Request Body:**
```json
{
  "productId": 1,
  "rating": 5,
  "comment": "Great product!"
}
```

### Update Review
```
PUT /reviews/{id}
Authorization: Bearer {token}
```

### Delete Review
```
DELETE /reviews/{id}
Authorization: Bearer {token}
```

---

## Admin Endpoints

### Get Dashboard Statistics
```
GET /admin/dashboard/stats
Authorization: Bearer {admin-token}
```

### Get All Orders
```
GET /admin/orders
Authorization: Bearer {admin-token}
```

### Get Orders by Status
```
GET /admin/orders/status/{status}
Authorization: Bearer {admin-token}
```
Status values: `PENDING`, `CONFIRMED`, `PROCESSING`, `SHIPPED`, `OUT_FOR_DELIVERY`, `DELIVERED`, `CANCELLED`, `REFUNDED`

### Update Order Status
```
PUT /admin/orders/{id}/status
Authorization: Bearer {admin-token}
```
**Request Body:**
```json
{
  "status": "SHIPPED"
}
```

### Update Payment Status
```
PUT /admin/orders/{id}/payment-status
Authorization: Bearer {admin-token}
```
**Request Body:**
```json
{
  "status": "COMPLETED"
}
```

### Update Tracking Number
```
PUT /admin/orders/{id}/tracking
Authorization: Bearer {admin-token}
```
**Request Body:**
```json
{
  "trackingNumber": "TRACK123456"
}
```

### Get All Users
```
GET /admin/users
Authorization: Bearer {admin-token}
```

### Deactivate User
```
PUT /admin/users/{id}/deactivate
Authorization: Bearer {admin-token}
```

### Activate User
```
PUT /admin/users/{id}/activate
Authorization: Bearer {admin-token}
```

---

## Features

### 1. **Authentication & Authorization**
- JWT-based authentication
- Role-based access control (USER, ADMIN)
- Secure password encryption

### 2. **Product Management**
- Product CRUD operations
- Category management
- Search and filter capabilities
- Featured products
- Top-rated products
- New arrivals

### 3. **Shopping Cart**
- Add/remove products
- Update quantities
- Cart summary with pricing
- Automatic cart clearing after order

### 4. **Order Management**
- Create orders from cart
- Order status tracking (8 statuses)
- Payment status tracking
- Order cancellation with reason
- Automatic stock management
- Shipping fee calculation
- Tax calculation

### 5. **Wishlist**
- Add/remove products
- Check if product is in wishlist

### 6. **Reviews & Ratings**
- Product reviews
- 1-5 star ratings
- Average rating calculation
- One review per product per user

### 7. **Address Management**
- Multiple shipping addresses
- Default address selection
- Full address details

### 8. **Admin Dashboard**
- View statistics (revenue, orders, products, users)
- Order management
- User management
- Product management

---

## Database Schema

### Tables:
- `users` - User accounts
- `products` - Product catalog
- `categories` - Product categories
- `cart_items` - Shopping cart items
- `orders` - Customer orders
- `order_items` - Order line items
- `addresses` - Shipping addresses
- `reviews` - Product reviews
- `wishlist` - User wishlists

---

## Technologies Used
- Spring Boot 3.1.5
- Spring Security with JWT
- Spring Data JPA
- MySQL Database
- Lombok
- Hibernate Validator

---

## Setup Instructions

1. Update database configuration in `application.properties`
2. Run the application: `mvn spring-boot:run`
3. Application runs on: `http://localhost:8082`

## Default Credentials
Create admin user manually in database with role='ADMIN'

