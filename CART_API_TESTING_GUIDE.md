# Cart API Testing Guide

## Prerequisites
- Application running on: http://localhost:8082
- You must have a JWT token (login first)
- You must have products in the database

---

## 1. Register a User (if not already done)

**POST** `http://localhost:8082/api/auth/register`

```json
{
  "username": "testuser",
  "email": "testuser@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "testuser",
  "email": "testuser@example.com"
}
```

---

## 2. Login

**POST** `http://localhost:8082/api/auth/login`

```json
{
  "email": "testuser@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "testuser",
  "email": "testuser@example.com"
}
```

**Copy the token from the response!**

---

## 3. Create a Product (Admin/User with permissions)

**POST** `http://localhost:8082/api/products`

**Headers:**
```
Authorization: Bearer <your-jwt-token>
Content-Type: application/json
```

**Body:**
```json
{
  "name": "Fresh Milk",
  "description": "Organic whole milk",
  "price": 45.50,
  "stock": 100
}
```

**Response:**
```json
{
  "id": 1,
  "name": "Fresh Milk",
  "description": "Organic whole milk",
  "price": 45.50,
  "stock": 100
}
```

---

## 4. Add Product to Cart

**POST** `http://localhost:8082/api/cart/add`

**Headers:**
```
Authorization: Bearer <your-jwt-token>
Content-Type: application/json
```

**Body:**
```json
{
  "productId": 1,
  "quantity": 2
}
```

**Response:**
```json
{
  "message": "Product added to cart successfully",
  "cartItem": {
    "id": 1,
    "userId": 1,
    "productId": 1,
    "quantity": 2
  }
}
```

---

## 5. View Cart Items

**GET** `http://localhost:8082/api/cart`

**Headers:**
```
Authorization: Bearer <your-jwt-token>
```

**Response:**
```json
{
  "cartItems": [
    {
      "cartId": 1,
      "quantity": 2,
      "productId": 1,
      "productName": "Fresh Milk",
      "productPrice": 45.50,
      "productDescription": "Organic whole milk",
      "subtotal": 91.0
    }
  ],
  "total": 91.0,
  "itemCount": 1
}
```

---

## 6. Update Cart Item Quantity

**PUT** `http://localhost:8082/api/cart/1`

**Headers:**
```
Authorization: Bearer <your-jwt-token>
Content-Type: application/json
```

**Body:**
```json
{
  "quantity": 5
}
```

**Response:**
```json
{
  "message": "Cart item updated successfully",
  "cartItem": {
    "id": 1,
    "userId": 1,
    "productId": 1,
    "quantity": 5
  }
}
```

---

## 7. Remove Item from Cart

**DELETE** `http://localhost:8082/api/cart/1`

**Headers:**
```
Authorization: Bearer <your-jwt-token>
```

**Response:**
```json
{
  "message": "Item removed from cart successfully"
}
```

---

## 8. Clear Entire Cart

**DELETE** `http://localhost:8082/api/cart/clear`

**Headers:**
```
Authorization: Bearer <your-jwt-token>
```

**Response:**
```json
{
  "message": "Cart cleared successfully"
}
```

---

## Complete Postman Workflow

1. **Register** → Get token
2. **Create Product** → Note the product ID
3. **Add to Cart** → Use product ID and quantity
4. **View Cart** → See all cart items with totals
5. **Update Cart** → Change quantities
6. **Remove from Cart** → Delete specific items
7. **Clear Cart** → Remove all items

---

## Common Errors

### 403 Forbidden
- Token is missing or invalid
- Make sure to add: `Authorization: Bearer <token>` in headers

### 404 Not Found
- Product doesn't exist
- Cart item doesn't exist

### 400 Bad Request
- Invalid product ID
- Quantity less than 1
- Product not found

---

## Features Implemented

✅ Add products to cart
✅ View cart with product details and totals
✅ Update cart item quantities
✅ Remove individual items from cart
✅ Clear entire cart
✅ Automatic cart item merging (if product already in cart, quantity increases)
✅ User-specific carts (each user has their own cart)
✅ Product validation (ensures product exists before adding)


