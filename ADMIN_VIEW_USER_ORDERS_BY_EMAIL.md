# 🔐 Admin Features - View User Orders by Email

## 📋 Overview

As an admin, you can now:
1. **Search for users by email**
2. **View all orders for a specific user**
3. **See user details and statistics**
4. **Reset user passwords**

## 🎯 Admin Endpoints

### 1. Get User Details by Email

**Endpoint:** `GET /api/admin/users/email/{email}`

**Example:**
```bash
GET http://localhost:8082/api/admin/users/email/user@example.com
Authorization: Bearer YOUR_ADMIN_TOKEN
```

**Response:**
```json
{
  "id": 5,
  "username": "johndoe",
  "email": "user@example.com",
  "role": "USER",
  "phoneNumber": "1234567890",
  "isActive": true,
  "createdAt": "2025-01-15T10:30:00",
  "lastLogin": "2025-10-10T08:45:00"
}
```

---

### 2. Get All Orders for a User by Email ⭐ MAIN FEATURE

**Endpoint:** `GET /api/admin/users/email/{email}/orders`

**Example:**
```bash
GET http://localhost:8082/api/admin/users/email/user@example.com/orders
Authorization: Bearer YOUR_ADMIN_TOKEN
```

**Response:**
```json
{
  "user": {
    "id": 5,
    "username": "johndoe",
    "email": "user@example.com",
    "role": "USER"
  },
  "totalOrders": 3,
  "totalSpent": 156.99,
  "orders": [
    {
      "id": 101,
      "orderNumber": "ORD-2025-001",
      "orderDate": "2025-10-05T14:30:00",
      "orderStatus": "DELIVERED",
      "paymentStatus": "COMPLETED",
      "paymentMethod": "PAYPAL",
      "totalAmount": 59.99,
      "shippingFee": 5.00,
      "taxAmount": 4.80,
      "trackingNumber": "TRACK123456",
      "items": [
        {
          "id": 201,
          "product": {
            "id": 10,
            "name": "Organic Apples",
            "price": 4.99
          },
          "quantity": 2,
          "price": 4.99
        }
      ]
    },
    {
      "id": 102,
      "orderNumber": "ORD-2025-002",
      "orderDate": "2025-10-08T10:15:00",
      "orderStatus": "SHIPPED",
      "paymentStatus": "COMPLETED",
      "totalAmount": 97.00
    }
  ]
}
```

---

### 3. Search Users

**Endpoint:** `GET /api/admin/users/search?query={searchTerm}`

**Example:**
```bash
GET http://localhost:8082/api/admin/users/search?query=john
Authorization: Bearer YOUR_ADMIN_TOKEN
```

Searches by email or username.

---

### 4. Reset User Password

**Endpoint:** `PUT /api/admin/users/{userId}/reset-password`

**Example:**
```bash
PUT http://localhost:8082/api/admin/users/5/reset-password
Authorization: Bearer YOUR_ADMIN_TOKEN
Content-Type: application/json

{
  "newPassword": "newSecurePassword123"
}
```

**Response:**
```json
{
  "message": "Password reset successfully for user: user@example.com"
}
```

---

### 5. Get All Users

**Endpoint:** `GET /api/admin/users`

```bash
GET http://localhost:8082/api/admin/users
Authorization: Bearer YOUR_ADMIN_TOKEN
```

Returns list of all users with their details.

---

### 6. Get All Orders

**Endpoint:** `GET /api/admin/orders`

```bash
GET http://localhost:8082/api/admin/orders
Authorization: Bearer YOUR_ADMIN_TOKEN
```

Returns all orders from all users.

---

## 💻 Frontend Implementation

### React/Next.js Example - Search User and View Orders

```jsx
'use client';
import { useState } from 'react';

export default function AdminUserOrders() {
  const [email, setEmail] = useState('');
  const [userData, setUserData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const searchUserOrders = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    
    try {
      const token = localStorage.getItem('token');
      
      const response = await fetch(
        `http://localhost:8082/api/admin/users/email/${email}/orders`,
        {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        }
      );
      
      if (!response.ok) {
        throw new Error('User not found or unauthorized');
      }
      
      const data = await response.json();
      setUserData(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="admin-user-orders">
      <h1>View User Orders</h1>
      
      <form onSubmit={searchUserOrders}>
        <input
          type="email"
          placeholder="Enter user email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <button type="submit" disabled={loading}>
          {loading ? 'Searching...' : 'Search'}
        </button>
      </form>
      
      {error && <div className="error">{error}</div>}
      
      {userData && (
        <div className="results">
          <div className="user-info">
            <h2>User Information</h2>
            <p><strong>Username:</strong> {userData.user.username}</p>
            <p><strong>Email:</strong> {userData.user.email}</p>
            <p><strong>Role:</strong> {userData.user.role}</p>
            <p><strong>Total Orders:</strong> {userData.totalOrders}</p>
            <p><strong>Total Spent:</strong> ${userData.totalSpent.toFixed(2)}</p>
          </div>
          
          <div className="orders-list">
            <h2>Order History</h2>
            {userData.orders.length === 0 ? (
              <p>No orders found</p>
            ) : (
              userData.orders.map(order => (
                <div key={order.id} className="order-card">
                  <h3>Order #{order.orderNumber || order.id}</h3>
                  <p><strong>Date:</strong> {new Date(order.orderDate).toLocaleDateString()}</p>
                  <p><strong>Status:</strong> {order.orderStatus}</p>
                  <p><strong>Payment:</strong> {order.paymentStatus}</p>
                  <p><strong>Total:</strong> ${order.totalAmount.toFixed(2)}</p>
                  
                  <div className="order-items">
                    <h4>Items:</h4>
                    {order.items?.map(item => (
                      <div key={item.id} className="item">
                        <span>{item.product.name}</span>
                        <span>Qty: {item.quantity}</span>
                        <span>${(item.price * item.quantity).toFixed(2)}</span>
                      </div>
                    ))}
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      )}
    </div>
  );
}
```

---

## 🎨 Complete Admin Dashboard Example

```jsx
'use client';
import { useState, useEffect } from 'react';

export default function AdminDashboard() {
  const [searchEmail, setSearchEmail] = useState('');
  const [userData, setUserData] = useState(null);
  const [allUsers, setAllUsers] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);

  // Fetch all users on load
  useEffect(() => {
    fetchAllUsers();
  }, []);

  const fetchAllUsers = async () => {
    const token = localStorage.getItem('token');
    const response = await fetch('http://localhost:8082/api/admin/users', {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    const data = await response.json();
    setAllUsers(data);
  };

  const viewUserOrders = async (email) => {
    const token = localStorage.getItem('token');
    const response = await fetch(
      `http://localhost:8082/api/admin/users/email/${email}/orders`,
      {
        headers: { 'Authorization': `Bearer ${token}` }
      }
    );
    const data = await response.json();
    setUserData(data);
    setSelectedUser(email);
  };

  const resetPassword = async (userId, newPassword) => {
    const token = localStorage.getItem('token');
    const response = await fetch(
      `http://localhost:8082/api/admin/users/${userId}/reset-password`,
      {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ newPassword })
      }
    );
    const result = await response.json();
    alert(result.message);
  };

  return (
    <div className="admin-dashboard">
      <h1>Admin Dashboard</h1>
      
      {/* Search by Email */}
      <section className="search-section">
        <h2>Search User Orders by Email</h2>
        <div className="search-box">
          <input
            type="email"
            placeholder="user@example.com"
            value={searchEmail}
            onChange={(e) => setSearchEmail(e.target.value)}
          />
          <button onClick={() => viewUserOrders(searchEmail)}>
            Search Orders
          </button>
        </div>
      </section>

      {/* All Users List */}
      <section className="users-section">
        <h2>All Users</h2>
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Username</th>
              <th>Email</th>
              <th>Role</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {allUsers.map(user => (
              <tr key={user.id}>
                <td>{user.id}</td>
                <td>{user.username}</td>
                <td>{user.email}</td>
                <td>{user.role}</td>
                <td>
                  <button onClick={() => viewUserOrders(user.email)}>
                    View Orders
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>

      {/* User Orders Display */}
      {userData && (
        <section className="user-orders-section">
          <h2>Orders for {selectedUser}</h2>
          <div className="stats">
            <div>Total Orders: {userData.totalOrders}</div>
            <div>Total Spent: ${userData.totalSpent.toFixed(2)}</div>
          </div>
          
          <div className="orders-grid">
            {userData.orders.map(order => (
              <div key={order.id} className="order-card">
                <h3>Order #{order.id}</h3>
                <p>Date: {new Date(order.orderDate).toLocaleString()}</p>
                <p>Status: {order.orderStatus}</p>
                <p>Payment: {order.paymentStatus}</p>
                <p>Total: ${order.totalAmount.toFixed(2)}</p>
                
                {order.items && (
                  <div className="items">
                    {order.items.map(item => (
                      <div key={item.id}>
                        {item.product.name} x {item.quantity}
                      </div>
                    ))}
                  </div>
                )}
              </div>
            ))}
          </div>
        </section>
      )}
    </div>
  );
}
```

---

## 🗄️ Database Query (Alternative Method)

If you want to query directly in MySQL:

```sql
-- Get all orders for a specific user by email
SELECT 
    o.id AS order_id,
    o.order_number,
    o.order_date,
    o.order_status,
    o.payment_status,
    o.total_amount,
    u.username,
    u.email
FROM orders o
JOIN users u ON o.user_id = u.id
WHERE u.email = 'user@example.com'
ORDER BY o.order_date DESC;

-- Get detailed order items
SELECT 
    o.id AS order_id,
    o.order_date,
    o.total_amount,
    oi.quantity,
    oi.price,
    p.name AS product_name,
    u.email
FROM orders o
JOIN users u ON o.user_id = u.id
JOIN order_items oi ON oi.order_id = o.id
JOIN products p ON oi.product_id = p.id
WHERE u.email = 'user@example.com'
ORDER BY o.order_date DESC;

-- Calculate total spent by user
SELECT 
    u.email,
    COUNT(o.id) AS total_orders,
    SUM(o.total_amount) AS total_spent
FROM users u
LEFT JOIN orders o ON o.user_id = u.id
WHERE u.email = 'user@example.com'
GROUP BY u.email;
```

---

## 🔒 Security

**Important:** All these endpoints require:
1. ✅ Valid JWT token
2. ✅ Admin role (`ROLE_ADMIN`)
3. ✅ `@PreAuthorize("hasRole('ADMIN')")` annotation

Non-admin users will get **403 Forbidden** when accessing these endpoints.

---

## 🧪 Testing with Postman/cURL

### 1. Login as Admin
```bash
POST http://localhost:8082/api/auth/login
Content-Type: application/json

{
  "email": "admin@example.com",
  "password": "adminpassword"
}
```

Save the token from response.

### 2. Get User Orders
```bash
GET http://localhost:8082/api/admin/users/email/user@example.com/orders
Authorization: Bearer YOUR_ADMIN_TOKEN
```

### 3. Search Users
```bash
GET http://localhost:8082/api/admin/users/search?query=john
Authorization: Bearer YOUR_ADMIN_TOKEN
```

---

## 📊 Quick Reference

| Task | Endpoint | Method |
|------|----------|--------|
| **View user orders by email** | `/api/admin/users/email/{email}/orders` | GET |
| Get user details by email | `/api/admin/users/email/{email}` | GET |
| Search users | `/api/admin/users/search?query={term}` | GET |
| Reset user password | `/api/admin/users/{id}/reset-password` | PUT |
| Get all users | `/api/admin/users` | GET |
| Get all orders | `/api/admin/orders` | GET |
| Update order status | `/api/admin/orders/{id}/status` | PUT |

---

## ✅ Your Backend is Ready!

After restarting, you can:
1. Login as admin
2. Enter any user's email
3. See all their orders with full details
4. View total spent and order count
5. Reset passwords if needed

**Make sure you have at least one admin user in the database!**

To create an admin user:
```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'your-email@example.com';
```

