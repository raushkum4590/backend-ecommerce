# Quick Postman Testing Reference

## 🚀 Quick Start (3 Steps)

### Step 1: Register/Login
```
POST http://localhost:8082/api/auth/register

Body:
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123"
}

→ Copy the "token" from response
```

### Step 2: Add Products
```
POST http://localhost:8082/api/products
Authorization: Bearer <your-token>

Body:
{
  "name": "Milk",
  "category": "Dairy",
  "price": 45.0,
  "stock": 100,
  "description": "Fresh milk"
}

→ Note the product "id" from response
```

### Step 3: Add to Cart
```
POST http://localhost:8082/api/cart/add
Authorization: Bearer <your-token>

Body:
{
  "productId": 1,
  "quantity": 2
}
```

---

## 📋 All Endpoints (Copy-Paste Ready)

### Authentication
- Register: `POST http://localhost:8082/api/auth/register`
- Login: `POST http://localhost:8082/api/auth/login`

### Products
- Get All: `GET http://localhost:8082/api/products`
- Create: `POST http://localhost:8082/api/products`
- Get By ID: `GET http://localhost:8082/api/products/{id}`

### Cart
- Add Item: `POST http://localhost:8082/api/cart/add`
- View Cart: `GET http://localhost:8082/api/cart`
- Update: `PUT http://localhost:8082/api/cart/{cartId}`
- Increment: `PUT http://localhost:8082/api/cart/{cartId}/increment`
- Decrement: `PUT http://localhost:8082/api/cart/{cartId}/decrement`
- Remove Item: `DELETE http://localhost:8082/api/cart/{cartId}`
- Clear Cart: `DELETE http://localhost:8082/api/cart/clear`

### Orders
- Create Order: `POST http://localhost:8082/api/orders`
- Get All Orders: `GET http://localhost:8082/api/orders`
- Get Order By ID: `GET http://localhost:8082/api/orders/{orderId}`

---

## 🔑 Important Headers

For all authenticated requests (after login):
```
Authorization: Bearer <paste-token-here>
Content-Type: application/json
```

---

## ✅ Testing Checklist

- [ ] Register new user
- [ ] Login with email + password
- [ ] Create 2-3 products
- [ ] View all products
- [ ] Add products to cart
- [ ] View cart items
- [ ] Increment quantity (+ button)
- [ ] Decrement quantity (- button)
- [ ] Remove item from cart
- [ ] Create order from cart
- [ ] View order history
- [ ] View specific order details

---

## ⚠️ Common Issues

### 403 Forbidden?
→ Add Authorization header with Bearer token

### 404 Not Found?
→ Use port 8082: `http://localhost:8082`

### Connection Refused?
→ Start your Spring Boot application

### "Invalid email or password"?
→ Use email (not username) for login

---

## 💡 Sample Test Data

### User Registration
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123"
}
```

### Product Creation
```json
{
  "name": "Fresh Milk",
  "category": "Dairy",
  "price": 45.0,
  "stock": 100,
  "description": "Pure cow milk, 1 liter"
}
```

### Add to Cart
```json
{
  "productId": 1,
  "quantity": 2
}
```

### Create Order
```json
{
  "shippingAddress": "123 Main St, Bangalore, 560001",
  "paymentMethod": "COD",
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ]
}
```

---

**Server**: http://localhost:8082
**Port**: 8082

