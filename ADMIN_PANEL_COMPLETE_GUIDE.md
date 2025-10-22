# 📚 Complete Admin Panel API Documentation

## 🔐 Admin Login Credentials

```
Email:    admin@gmail.com
Password: admin123
```

---

## 🎯 Complete Admin Panel Features

### 1️⃣ **Dashboard Statistics**

#### Get Comprehensive Stats
```http
GET /api/admin/stats/comprehensive
Authorization: Bearer ADMIN_TOKEN
```

**Response:**
```json
{
  "totalUsers": 5,
  "activeUsers": 4,
  "totalVendors": 2,
  "verifiedVendors": 1,
  "pendingVendors": 1,
  "totalProducts": 50,
  "availableProducts": 45,
  "totalOrders": 100,
  "totalRevenue": 5000.00,
  "ordersByStatus": {
    "PENDING": 10,
    "CONFIRMED": 20,
    "SHIPPED": 30,
    "DELIVERED": 35,
    "CANCELLED": 5
  }
}
```

---

### 2️⃣ **User Management**

#### Get All Users
```http
GET /api/admin/users
Authorization: Bearer ADMIN_TOKEN
```

#### Get User by ID
```http
GET /api/admin/users/{userId}
Authorization: Bearer ADMIN_TOKEN
```

#### Get User by Email
```http
GET /api/admin/users/email/user@example.com
Authorization: Bearer ADMIN_TOKEN
```

#### Get User's Orders
```http
GET /api/admin/users/email/user@example.com/orders
Authorization: Bearer ADMIN_TOKEN
```

**Response:**
```json
{
  "user": {
    "id": 1,
    "username": "john",
    "email": "user@example.com",
    "role": "USER"
  },
  "totalOrders": 5,
  "totalSpent": 250.50,
  "orders": [...]
}
```

#### Search Users
```http
GET /api/admin/users/search?query=john
Authorization: Bearer ADMIN_TOKEN
```

#### Activate User
```http
PUT /api/admin/users/{userId}/activate
Authorization: Bearer ADMIN_TOKEN
```

#### Deactivate User
```http
PUT /api/admin/users/{userId}/deactivate
Authorization: Bearer ADMIN_TOKEN
```

#### Reset User Password
```http
PUT /api/admin/users/{userId}/reset-password
Authorization: Bearer ADMIN_TOKEN
Content-Type: application/json

{
  "newPassword": "newpassword123"
}
```

---

### 3️⃣ **Vendor Management (Accept/Reject Requests)**

#### Get All Vendors
```http
GET /api/admin/vendors
Authorization: Bearer ADMIN_TOKEN
```

#### Get Pending Vendor Requests
```http
GET /api/admin/vendors/pending
Authorization: Bearer ADMIN_TOKEN
```

**Response:**
```json
[
  {
    "id": 3,
    "storeName": "Fresh Grocery Store",
    "businessEmail": "vendor@example.com",
    "businessPhone": "+1234567890",
    "isVerified": false,
    "isActive": true,
    "createdAt": "2025-10-10T10:00:00",
    "user": {
      "id": 10,
      "username": "vendor1",
      "email": "vendor@example.com"
    }
  }
]
```

#### Get Vendor Details
```http
GET /api/admin/vendors/{vendorId}
Authorization: Bearer ADMIN_TOKEN
```

#### ✅ Accept Vendor Request (VERIFY)
```http
PUT /api/admin/vendors/{vendorId}/verify
Authorization: Bearer ADMIN_TOKEN
Content-Type: application/json

{
  "verified": true
}
```

**Response:**
```json
{
  "success": true,
  "message": "Vendor verified successfully",
  "vendor": {
    "id": 3,
    "storeName": "Fresh Grocery Store",
    "isVerified": true,
    "isActive": true
  }
}
```

#### ❌ Reject Vendor Request
```http
PUT /api/admin/vendors/{vendorId}/verify
Authorization: Bearer ADMIN_TOKEN
Content-Type: application/json

{
  "verified": false
}
```

#### Activate/Deactivate Vendor
```http
PUT /api/admin/vendors/{vendorId}/status
Authorization: Bearer ADMIN_TOKEN
Content-Type: application/json

{
  "active": true
}
```

#### Delete Vendor (Soft Delete)
```http
DELETE /api/admin/vendors/{vendorId}
Authorization: Bearer ADMIN_TOKEN
```

---

### 4️⃣ **Product Management**

#### Get All Products (Admin + Vendor Products)
```http
GET /api/admin/products
Authorization: Bearer ADMIN_TOKEN
```

**Returns:** ALL products from the system (both admin-added and vendor-added)

#### 🆕 Get Products Grouped by Source
```http
GET /api/admin/products/by-source
Authorization: Bearer ADMIN_TOKEN
```

**Response:**
```json
{
  "totalProducts": 50,
  "adminProducts": 20,
  "vendorProducts": 30,
  "adminProductList": [
    {
      "id": 1,
      "name": "Admin Product 1",
      "price": 9.99,
      "vendor": null
    }
  ],
  "vendorProductList": [
    {
      "id": 2,
      "name": "Vendor Product 1",
      "price": 14.99,
      "vendor": {
        "id": 3,
        "storeName": "Fresh Store",
        "businessEmail": "vendor@example.com"
      }
    }
  ]
}
```

#### Get Product by ID (with Source Info)
```http
GET /api/admin/products/{productId}
Authorization: Bearer ADMIN_TOKEN
```

**Response for Admin Product:**
```json
{
  "product": {
    "id": 1,
    "name": "Organic Apples",
    "price": 4.99,
    "vendor": null
  },
  "source": "ADMIN"
}
```

**Response for Vendor Product:**
```json
{
  "product": {
    "id": 2,
    "name": "Fresh Tomatoes",
    "price": 3.99,
    "vendor": {
      "id": 5,
      "storeName": "Green Farm",
      "businessEmail": "vendor@example.com"
    }
  },
  "source": "VENDOR",
  "vendorInfo": {
    "id": 5,
    "storeName": "Green Farm",
    "businessEmail": "vendor@example.com"
  }
}
```

#### ➕ Add New Product (ADMIN)
```http
POST /api/admin/products
Authorization: Bearer ADMIN_TOKEN
Content-Type: application/json

{
  "name": "Organic Apples",
  "description": "Fresh organic apples from local farms",
  "price": 4.99,
  "stock": 100,
  "category": {
    "id": 1
  },
  "brand": "FreshFarm",
  "unit": "kg",
  "weight": 1.0,
  "imageUrl": "https://example.com/apple.jpg",
  "isAvailable": true,
  "isFeatured": false,
  "discount": 0.0
}
```

**Response:**
```json
{
  "success": true,
  "message": "Product created successfully",
  "product": {
    "id": 51,
    "name": "Organic Apples",
    "price": 4.99,
    "stock": 100,
    "isAvailable": true,
    "createdAt": "2025-10-11T10:00:00"
  }
}
```

#### Update Product
```http
PUT /api/admin/products/{productId}
Authorization: Bearer ADMIN_TOKEN
Content-Type: application/json

{
  "name": "Updated Product Name",
  "price": 9.99,
  "stock": 50,
  "isAvailable": true
}
```

**Response:**
```json
{
  "success": true,
  "message": "Product updated successfully",
  "product": {
    "id": 51,
    "name": "Updated Product Name",
    "price": 9.99
  }
}
```

#### Delete Product
```http
DELETE /api/admin/products/{productId}
Authorization: Bearer ADMIN_TOKEN
```

**Response:**
```json
{
  "success": true,
  "message": "Product deleted successfully"
}
```

---

### 5️⃣ **Order Management**

#### Get All Orders
```http
GET /api/admin/orders
Authorization: Bearer ADMIN_TOKEN
```

#### Get Orders by Status
```http
GET /api/admin/orders/status/PENDING
Authorization: Bearer ADMIN_TOKEN
```

**Available Statuses:** `PENDING`, `CONFIRMED`, `SHIPPED`, `DELIVERED`, `CANCELLED`

#### Get Order Details
```http
GET /api/admin/orders/{orderId}
Authorization: Bearer ADMIN_TOKEN
```

#### Update Order Status
```http
PUT /api/admin/orders/{orderId}/status
Authorization: Bearer ADMIN_TOKEN
Content-Type: application/json

{
  "status": "SHIPPED"
}
```

#### Update Payment Status
```http
PUT /api/admin/orders/{orderId}/payment-status
Authorization: Bearer ADMIN_TOKEN
Content-Type: application/json

{
  "status": "COMPLETED"
}
```

#### Update Tracking Number
```http
PUT /api/admin/orders/{orderId}/tracking
Authorization: Bearer ADMIN_TOKEN
Content-Type: application/json

{
  "trackingNumber": "TRACK123456789"
}
```

---

## 🧪 Testing Examples

### Example 1: Add Product Using cURL

```bash
curl -X POST http://localhost:8082/api/admin/products \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Fresh Tomatoes",
    "description": "Organic red tomatoes",
    "price": 3.99,
    "stock": 200,
    "category": {"id": 1},
    "brand": "LocalFarm",
    "unit": "kg",
    "weight": 1.0,
    "imageUrl": "https://example.com/tomato.jpg",
    "isAvailable": true,
    "isFeatured": false,
    "discount": 0.0
  }'
```

### Example 2: Accept Vendor Request

```bash
curl -X PUT http://localhost:8082/api/admin/vendors/3/verify \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"verified": true}'
```

### Example 3: Get All Pending Vendor Requests

```bash
curl -X GET http://localhost:8082/api/admin/vendors/pending \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

---

## 📊 Complete Admin Workflow

### Workflow 1: Managing Vendor Requests

1. **Get pending requests:** `GET /api/admin/vendors/pending`
2. **Review vendor details:** `GET /api/admin/vendors/{id}`
3. **Accept vendor:** `PUT /api/admin/vendors/{id}/verify` with `{"verified": true}`
4. **Or reject:** `PUT /api/admin/vendors/{id}/verify` with `{"verified": false}`

### Workflow 2: Adding Products

1. **Get categories:** `GET /api/categories`
2. **Create product:** `POST /api/admin/products` with product details
3. **Verify creation:** `GET /api/admin/products/{id}`
4. **Make featured (optional):** `PUT /api/admin/products/{id}` with `{"isFeatured": true}`

### Workflow 3: Managing Orders

1. **View all orders:** `GET /api/admin/orders`
2. **Filter by status:** `GET /api/admin/orders/status/PENDING`
3. **Update status:** `PUT /api/admin/orders/{id}/status`
4. **Add tracking:** `PUT /api/admin/orders/{id}/tracking`

---

## 🔑 Getting Admin Token

### Step 1: Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@gmail.com",
  "password": "admin123"
}
```

### Step 2: Use Token
```http
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1...
```

---

## ✅ Summary

Your admin panel has **complete functionality** for:

- ✅ **Dashboard with comprehensive statistics**
- ✅ **User management** (view, search, activate/deactivate, reset password)
- ✅ **Vendor management** (view, accept/reject requests, activate/deactivate)
- ✅ **Product management** (view, **add**, update, delete)
- ✅ **Order management** (view, update status, track shipments)

**All endpoints are secured with `@PreAuthorize("hasRole('ADMIN')")` - only admin users can access them!**

---

## 🎉 Ready to Use!

Your admin panel is **fully functional** and ready to manage your multi-vendor e-commerce platform!

**Login as admin and start managing your platform:** `admin@gmail.com` / `admin123`
