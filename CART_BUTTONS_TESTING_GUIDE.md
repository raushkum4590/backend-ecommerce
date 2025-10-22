# Cart Increment/Decrement API Testing Guide

## New Cart Button Features Implemented

### ✅ Three Button Operations:
1. **➕ Plus Button** - Increase quantity by 1
2. **➖ Minus Button** - Decrease quantity by 1 (auto-removes at quantity 1)
3. **🗑️ Remove Button** - Delete item completely

---

## API Endpoints

### 1️⃣ ➕ Plus Button - Increment Quantity

**PUT** `http://localhost:8082/api/cart/{cartId}/increment`

**Headers:**
```
Authorization: Bearer <your-jwt-token>
```

**Example:**
```
PUT http://localhost:8082/api/cart/1/increment
```

**Response:**
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

### 2️⃣ ➖ Minus Button - Decrement Quantity

**PUT** `http://localhost:8082/api/cart/{cartId}/decrement`

**Headers:**
```
Authorization: Bearer <your-jwt-token>
```

**Example:**
```
PUT http://localhost:8082/api/cart/1/decrement
```

**Response (when quantity > 1):**
```json
{
  "message": "Quantity decreased successfully",
  "cartItem": {
    "id": 1,
    "userId": 1,
    "productId": 1,
    "quantity": 2
  }
}
```

**Response (when quantity = 1):**
```json
{
  "message": "Item removed from cart (quantity reached 0)"
}
```

**Note:** When quantity reaches 1 and you click decrement, the item is automatically removed from the cart.

---

### 3️⃣ 🗑️ Remove Button - Delete Item

**DELETE** `http://localhost:8082/api/cart/{cartId}`

**Headers:**
```
Authorization: Bearer <your-jwt-token>
```

**Example:**
```
DELETE http://localhost:8082/api/cart/1
```

**Response:**
```json
{
  "message": "Item removed from cart successfully"
}
```

---

## Complete Workflow Example

### Step 1: Add Product to Cart
```
POST http://localhost:8082/api/cart/add
Headers: Authorization: Bearer <token>
Body:
{
  "productId": 1,
  "quantity": 1
}
```

### Step 2: View Cart (Get cartId)
```
GET http://localhost:8082/api/cart
Headers: Authorization: Bearer <token>
```

**Response:**
```json
{
  "cartItems": [
    {
      "cartId": 1,
      "quantity": 1,
      "productId": 1,
      "productName": "Fresh Milk",
      "productPrice": 45.50,
      "subtotal": 45.50
    }
  ],
  "total": 45.50,
  "itemCount": 1
}
```

### Step 3: Click Plus Button (Increment)
```
PUT http://localhost:8082/api/cart/1/increment
Headers: Authorization: Bearer <token>
```

Now quantity becomes 2

### Step 4: Click Plus Button Again
```
PUT http://localhost:8082/api/cart/1/increment
Headers: Authorization: Bearer <token>
```

Now quantity becomes 3

### Step 5: Click Minus Button (Decrement)
```
PUT http://localhost:8082/api/cart/1/decrement
Headers: Authorization: Bearer <token>
```

Now quantity becomes 2

### Step 6: View Updated Cart
```
GET http://localhost:8082/api/cart
Headers: Authorization: Bearer <token>
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
      "subtotal": 91.0
    }
  ],
  "total": 91.0,
  "itemCount": 1
}
```

### Step 7: Remove Item Completely
```
DELETE http://localhost:8082/api/cart/1
Headers: Authorization: Bearer <token>
```

Cart is now empty!

---

## Frontend Integration Guide

For your frontend developers, here's how to implement the buttons:

### Plus Button (➕)
```javascript
async function incrementQuantity(cartId, token) {
  const response = await fetch(`http://localhost:8082/api/cart/${cartId}/increment`, {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return await response.json();
}
```

### Minus Button (➖)
```javascript
async function decrementQuantity(cartId, token) {
  const response = await fetch(`http://localhost:8082/api/cart/${cartId}/decrement`, {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return await response.json();
}
```

### Remove Button (🗑️)
```javascript
async function removeItem(cartId, token) {
  const response = await fetch(`http://localhost:8082/api/cart/${cartId}`, {
    method: 'DELETE',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return await response.json();
}
```

---

## Features Implemented

✅ **Increment Quantity** - Adds 1 to quantity
✅ **Decrement Quantity** - Removes 1 from quantity
✅ **Auto-Remove** - Item is removed when quantity reaches 0
✅ **Security** - Validates user owns the cart item
✅ **Error Handling** - Proper error messages for invalid operations
✅ **Remove Button** - Deletes item regardless of quantity

---

## Security Features

- Users can only modify their own cart items
- Cart item ownership is verified before increment/decrement
- JWT authentication required for all operations
- Prevents unauthorized access to other users' carts

---

## Testing in Postman

1. **Login** to get your JWT token
2. **Add a product** to cart (note the cartId from response)
3. **Test Plus Button**: PUT to `/api/cart/{cartId}/increment`
4. **Test Minus Button**: PUT to `/api/cart/{cartId}/decrement`
5. **Test Remove Button**: DELETE to `/api/cart/{cartId}`
6. **View Cart**: GET to `/api/cart` after each operation to see changes

---

## Expected Behavior

### Plus Button:
- Quantity: 1 → Click + → Quantity: 2
- Quantity: 5 → Click + → Quantity: 6
- No limit on quantity

### Minus Button:
- Quantity: 5 → Click - → Quantity: 4
- Quantity: 2 → Click - → Quantity: 1
- Quantity: 1 → Click - → **Item Removed** (quantity cannot be 0)

### Remove Button:
- Immediately removes item from cart regardless of quantity
- Quantity: 10 → Click Remove → **Item Removed**


