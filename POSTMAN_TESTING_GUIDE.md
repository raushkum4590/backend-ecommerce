# Complete Postman Testing Guide

## Server Information
- **Base URL**: `http://localhost:8082`
- **Server Port**: 8082

---

## 1. REGISTER A NEW USER

### Endpoint
```
POST http://localhost:8082/api/auth/register
```

### Headers
```
Content-Type: application/json
```

### Body (raw JSON)
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123"
}
```

### Expected Response (200 OK)
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "john_doe",
  "email": "john@example.com"
}
```

### Error Response (400 Bad Request)
```json
{
  "error": "Username already exists"
}
```

**⚠️ IMPORTANT**: Copy the `token` value from the response - you'll need it for all other requests!

---

## 2. LOGIN

### Endpoint
```
POST http://localhost:8082/api/auth/login
```

### Headers
```
Content-Type: application/json
```

### Body (raw JSON)
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

### Expected Response (200 OK)
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "john_doe",
  "email": "john@example.com"
}
```

### Error Response (401 Unauthorized)
```json
{
  "error": "Invalid email or password"
}
```

**⚠️ IMPORTANT**: Copy the `token` value - you need it for authenticated requests!

---

## 3. CREATE/ADD PRODUCTS (Admin Only)

### Endpoint
```
POST http://localhost:8082/api/products
```

### Headers
```
Authorization: Bearer <paste-your-JWT-token-here>
Content-Type: application/json
```

### Body (raw JSON)
```json
{
  "name": "Fresh Milk",
  "category": "Dairy",
  "price": 45.0,
  "stock": 100,
  "imageUrl": "https://via.placeholder.com/150",
  "description": "Pure cow milk, 1 liter"
}
```

### More Product Examples
```json
{
  "name": "Brown Bread",
  "category": "Bakery",
  "price": 30.0,
  "stock": 50,
  "imageUrl": "https://via.placeholder.com/150",
  "description": "Whole wheat brown bread"
}
```

```json
{
  "name": "Basmati Rice",
  "category": "Grains",
  "price": 120.0,
  "stock": 200,
  "imageUrl": "https://via.placeholder.com/150",
  "description": "Premium quality basmati rice, 1kg"
}
```

---

## 4. GET ALL PRODUCTS

### Endpoint
```
GET http://localhost:8082/api/products
```

### Headers
```
Authorization: Bearer <paste-your-JWT-token-here>
```

### Expected Response (200 OK)
```json
[
  {
    "id": 1,
    "name": "Fresh Milk",
    "category": "Dairy",
    "price": 45.0,
    "stock": 100,
    "imageUrl": "https://via.placeholder.com/150",
    "description": "Pure cow milk, 1 liter"
  },
  {
    "id": 2,
    "name": "Brown Bread",
    "category": "Bakery",
    "price": 30.0,
    "stock": 50,
    "imageUrl": "https://via.placeholder.com/150",
    "description": "Whole wheat brown bread"
  }
]
```

---

## 5. ADD PRODUCT TO CART

### Endpoint
```
POST http://localhost:8082/api/cart/add
```

### Headers
```
Authorization: Bearer <paste-your-JWT-token-here>
Content-Type: application/json
```

### Body (raw JSON)
```json
{
  "productId": 1,
  "quantity": 2
}
```

### Expected Response (200 OK)
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

### Error Response (400 Bad Request)
```json
{
  "error": "Product not found"
}
```

---

## 6. VIEW CART ITEMS

### Endpoint
```
GET http://localhost:8082/api/cart
```

### Headers
```
Authorization: Bearer <paste-your-JWT-token-here>
```

### Expected Response (200 OK)
```json
{
  "cartItems": [
    {
      "cartId": 1,
      "quantity": 2,
      "productId": 1,
      "productName": "Fresh Milk",
      "productPrice": 45.0,
      "productDescription": "Pure cow milk, 1 liter",
      "subtotal": 90.0
    },
    {
      "cartId": 2,
      "quantity": 1,
      "productId": 2,
      "productName": "Brown Bread",
      "productPrice": 30.0,
      "productDescription": "Whole wheat brown bread",
      "subtotal": 30.0
    }
  ],
  "total": 120.0,
  "itemCount": 2
}
```

---

## 7. UPDATE CART ITEM QUANTITY

### Endpoint
```
PUT http://localhost:8082/api/cart/{cartId}
```

**Example**: `PUT http://localhost:8082/api/cart/1`

### Headers
```
Authorization: Bearer <paste-your-JWT-token-here>
Content-Type: application/json
```

### Body (raw JSON)
```json
{
  "quantity": 5
}
```

### Expected Response (200 OK)
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

## 8. INCREMENT CART ITEM (+ Button)

### Endpoint
```
PUT http://localhost:8082/api/cart/{cartId}/increment
```

**Example**: `PUT http://localhost:8082/api/cart/1/increment`

### Headers
```
Authorization: Bearer <paste-your-JWT-token-here>
```

### Expected Response (200 OK)
```json
{
  "message": "Quantity increased successfully",
  "cartItem": {
    "id": 1,
    "userId": 1,
    "productId": 1,
    "quantity": 3
  }
}
```

---

## 9. DECREMENT CART ITEM (- Button)

### Endpoint
```
PUT http://localhost:8082/api/cart/{cartId}/decrement
```

**Example**: `PUT http://localhost:8082/api/cart/1/decrement`

### Headers
```
Authorization: Bearer <paste-your-JWT-token-here>
```

### Expected Response (200 OK)
```json
{
  "message": "Quantity decreased successfully",
  "cartItem": {
    "id": 1,
    "userId": 1,
    "productId": 1,
    "quantity": 1
  }
}
```

**Note**: If quantity reaches 0, the item will be removed from cart:
```json
{
  "message": "Item removed from cart (quantity reached 0)"
}
```

---

## 10. REMOVE ITEM FROM CART (🗑️ Button)

### Endpoint
```
DELETE http://localhost:8082/api/cart/{cartId}
```

**Example**: `DELETE http://localhost:8082/api/cart/1`

### Headers
```
Authorization: Bearer <paste-your-JWT-token-here>
```

### Expected Response (200 OK)
```json
{
  "message": "Item removed from cart successfully"
}
```

---

## 11. CLEAR ENTIRE CART

### Endpoint
```
DELETE http://localhost:8082/api/cart/clear
```

### Headers
```
Authorization: Bearer <paste-your-JWT-token-here>
```

### Expected Response (200 OK)
```json
{
  "message": "Cart cleared successfully"
}
```

---

## 12. CREATE ORDER FROM CART

### Endpoint
```
POST http://localhost:8082/api/orders
```

### Headers
```
Authorization: Bearer <paste-your-JWT-token-here>
Content-Type: application/json
```

### Body (raw JSON)
```json
{
  "shippingAddress": "123 Main Street, Bangalore, Karnataka, 560001",
  "paymentMethod": "COD",
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 2,
      "quantity": 1
    }
  ]
}
```

### Payment Method Options
- `"COD"` - Cash on Delivery
- `"CARD"` - Credit/Debit Card
- `"UPI"` - UPI Payment
- `"NET_BANKING"` - Net Banking

### Expected Response (200 OK)
```json
{
  "orderId": 1,
  "totalAmount": 120.0,
  "status": "PENDING",
  "orderDate": "2025-10-07T13:45:30"
}
```

### Error Responses
```json
{
  "error": "Product not found with ID: 1"
}
```

```json
{
  "error": "Insufficient stock for product: Fresh Milk"
}
```

---

## 13. GET ALL ORDERS (User's Orders)

### Endpoint
```
GET http://localhost:8082/api/orders
```

### Headers
```
Authorization: Bearer <paste-your-JWT-token-here>
```

### Expected Response (200 OK)
```json
[
  {
    "id": 1,
    "orderDate": "2025-10-07T13:45:30",
    "totalAmount": 120.0,
    "status": "PENDING",
    "shippingAddress": "123 Main Street, Bangalore, Karnataka, 560001",
    "paymentMethod": "COD",
    "items": [
      {
        "productName": "Fresh Milk",
        "quantity": 2,
        "price": 45.0,
        "subtotal": 90.0
      },
      {
        "productName": "Brown Bread",
        "quantity": 1,
        "price": 30.0,
        "subtotal": 30.0
      }
    ]
  }
]
```

---

## 14. GET ORDER BY ID

### Endpoint
```
GET http://localhost:8082/api/orders/{orderId}
```

**Example**: `GET http://localhost:8082/api/orders/1`

### Headers
```
Authorization: Bearer <paste-your-JWT-token-here>
```

### Expected Response (200 OK)
```json
{
  "id": 1,
  "orderDate": "2025-10-07T13:45:30",
  "totalAmount": 120.0,
  "status": "PENDING",
  "shippingAddress": "123 Main Street, Bangalore, Karnataka, 560001",
  "paymentMethod": "COD",
  "items": [
    {
      "productName": "Fresh Milk",
      "quantity": 2,
      "price": 45.0,
      "subtotal": 90.0
    }
  ]
}
```

---

## COMMON ERRORS AND SOLUTIONS

### Error: 404 Not Found
```json
{
  "timestamp": "2025-10-07T07:39:32.921+00:00",
  "status": 404,
  "error": "Not Found",
  "path": "/api/auth/register"
}
```
**Solution**: Check your URL. Make sure you're using port **8082**: `http://localhost:8082`

---

### Error: 403 Forbidden
```json
{
  "timestamp": "2025-10-07T08:02:12.821+00:00",
  "status": 403,
  "error": "Forbidden",
  "path": "/api/products"
}
```
**Solution**: 
1. Make sure you've added the Authorization header
2. Copy the JWT token from login/register response
3. Add header: `Authorization: Bearer <your-token>`
4. Make sure there's a space after "Bearer"

---

### Error: JWT Signature Does Not Match
```
JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.
```
**Solution**: 
1. Login again to get a fresh token
2. The token may have been generated by a different server instance
3. Clear your old tokens and use the new one

---

### Error: Connection Refused
```
Error: connect ECONNREFUSED 127.0.0.1:8082
```
**Solution**: 
1. Make sure your Spring Boot application is running
2. Check if it's running on port 8082
3. Start the application if it's not running

---

## TESTING WORKFLOW

### Complete Testing Flow:

1. **Register** a new user → Save the token
2. **Login** with the same user → Verify token works
3. **Add Products** (create at least 2-3 products)
4. **View Products** → Get product IDs
5. **Add to Cart** → Add 2-3 products with different quantities
6. **View Cart** → Verify cart total and items
7. **Update Cart** → Test increment/decrement/remove
8. **Create Order** → Convert cart items to order
9. **View Orders** → Check order history
10. **View Order Details** → Check specific order

---

## TIPS FOR POSTMAN

### Setting Up Environment Variables:
1. Create a new environment in Postman
2. Add variable `baseUrl` = `http://localhost:8082`
3. Add variable `token` = `<empty initially>`
4. After login, manually copy token to this variable
5. Use `{{baseUrl}}` and `{{token}}` in your requests

### Example with Variables:
```
URL: {{baseUrl}}/api/cart
Header: Authorization: Bearer {{token}}
```

### Quick Token Setup:
After login/register, add this to the "Tests" tab:
```javascript
pm.environment.set("token", pm.response.json().token);
```
This will automatically save the token to your environment!

---

## STATUS CODES

- **200 OK** - Request successful
- **201 Created** - Resource created successfully
- **400 Bad Request** - Invalid request data
- **401 Unauthorized** - Missing or invalid token
- **403 Forbidden** - No permission to access
- **404 Not Found** - Resource not found
- **500 Internal Server Error** - Server error

---

**Happy Testing! 🚀**

