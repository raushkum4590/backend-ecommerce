    businessEmail: '',
    businessPhone: '',
    businessAddress: '',
    businessLicense: '',
    taxId: ''
  });

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    const token = localStorage.getItem('token');
    
    const response = await fetch('http://localhost:8082/api/vendor/register', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(formData)
    });
    
    const data = await response.json();
    
    if (data.success) {
      alert('Registration successful! Awaiting admin verification.');
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <h2>Become a Vendor</h2>
      
      <input
        type="text"
        placeholder="Store Name"
        value={formData.storeName}
        onChange={(e) => setFormData({...formData, storeName: e.target.value})}
        required
      />
      
      <textarea
        placeholder="Store Description"
        value={formData.description}
        onChange={(e) => setFormData({...formData, description: e.target.value})}
      />
      
      <input
        type="email"
        placeholder="Business Email"
        value={formData.businessEmail}
        onChange={(e) => setFormData({...formData, businessEmail: e.target.value})}
      />
      
      <input
        type="tel"
        placeholder="Business Phone"
        value={formData.businessPhone}
        onChange={(e) => setFormData({...formData, businessPhone: e.target.value})}
      />
      
      <input
        type="text"
        placeholder="Business License"
        value={formData.businessLicense}
        onChange={(e) => setFormData({...formData, businessLicense: e.target.value})}
      />
      
      <button type="submit">Register as Vendor</button>
    </form>
  );
}
```

---

### 2. Vendor Dashboard - Manage Products

```jsx
'use client';
import { useState, useEffect } from 'react';

export default function VendorDashboard() {
  const [products, setProducts] = useState([]);
  const [stats, setStats] = useState({});
  const [newProduct, setNewProduct] = useState({
    name: '',
    price: 0,
    stock: 0
  });

  useEffect(() => {
    fetchProducts();
    fetchStats();
  }, []);

  const fetchProducts = async () => {
    const token = localStorage.getItem('token');
    const response = await fetch('http://localhost:8082/api/vendor/products', {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    const data = await response.json();
    setProducts(data);
  };

  const fetchStats = async () => {
    const token = localStorage.getItem('token');
    const response = await fetch('http://localhost:8082/api/vendor/products/stats', {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    const data = await response.json();
    setStats(data);
  };

  const addProduct = async (e) => {
    e.preventDefault();
    
    const token = localStorage.getItem('token');
    
    const response = await fetch('http://localhost:8082/api/vendor/products', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(newProduct)
    });
    
    const data = await response.json();
    
    if (data.success) {
      alert('Product added!');
      fetchProducts();
      fetchStats();
    }
  };

  const deleteProduct = async (productId) => {
    const token = localStorage.getItem('token');
    
    const response = await fetch(`http://localhost:8082/api/vendor/products/${productId}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${token}` }
    });
    
    const data = await response.json();
    
    if (data.success) {
      alert('Product deleted!');
      fetchProducts();
      fetchStats();
    }
  };

  return (
    <div className="vendor-dashboard">
      <h1>Vendor Dashboard</h1>
      
      {/* Statistics */}
      <div className="stats">
        <div className="stat-card">
          <h3>Total Products</h3>
          <p>{stats.totalProducts}</p>
        </div>
        <div className="stat-card">
          <h3>Available</h3>
          <p>{stats.availableProducts}</p>
        </div>
        <div className="stat-card">
          <h3>Out of Stock</h3>
          <p>{stats.outOfStock}</p>
        </div>
      </div>
      
      {/* Add Product Form */}
      <div className="add-product">
        <h2>Add New Product</h2>
        <form onSubmit={addProduct}>
          <input
            type="text"
            placeholder="Product Name"
            value={newProduct.name}
            onChange={(e) => setNewProduct({...newProduct, name: e.target.value})}
            required
          />
          <input
            type="number"
            placeholder="Price"
            value={newProduct.price}
            onChange={(e) => setNewProduct({...newProduct, price: parseFloat(e.target.value)})}
            required
          />
          <input
            type="number"
            placeholder="Stock"
            value={newProduct.stock}
            onChange={(e) => setNewProduct({...newProduct, stock: parseInt(e.target.value)})}
            required
          />
          <button type="submit">Add Product</button>
        </form>
      </div>
      
      {/* Products List */}
      <div className="products-list">
        <h2>My Products</h2>
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Price</th>
              <th>Stock</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {products.map(product => (
              <tr key={product.id}>
                <td>{product.id}</td>
                <td>{product.name}</td>
                <td>${product.price}</td>
                <td>{product.stock}</td>
                <td>{product.isAvailable ? 'Active' : 'Inactive'}</td>
                <td>
                  <button onClick={() => deleteProduct(product.id)}>Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
```

---

### 3. Admin Panel - Verify Vendors

```jsx
'use client';
import { useState, useEffect } from 'react';

export default function AdminVendorManagement() {
  const [pendingVendors, setPendingVendors] = useState([]);
  const [allVendors, setAllVendors] = useState([]);

  useEffect(() => {
    fetchPendingVendors();
    fetchAllVendors();
  }, []);

  const fetchPendingVendors = async () => {
    const token = localStorage.getItem('token');
    const response = await fetch('http://localhost:8082/api/admin/vendors/pending', {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    const data = await response.json();
    setPendingVendors(data);
  };

  const fetchAllVendors = async () => {
    const token = localStorage.getItem('token');
    const response = await fetch('http://localhost:8082/api/admin/vendors', {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    const data = await response.json();
    setAllVendors(data);
  };

  const verifyVendor = async (vendorId, verified) => {
    const token = localStorage.getItem('token');
    
    const response = await fetch(`http://localhost:8082/api/admin/vendors/${vendorId}/verify`, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ verified })
    });
    
    const data = await response.json();
    
    if (data.success) {
      alert(data.message);
      fetchPendingVendors();
      fetchAllVendors();
    }
  };

  const toggleStatus = async (vendorId, active) => {
    const token = localStorage.getItem('token');
    
    const response = await fetch(`http://localhost:8082/api/admin/vendors/${vendorId}/status`, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ active })
    });
    
    const data = await response.json();
    
    if (data.success) {
      alert(data.message);
      fetchAllVendors();
    }
  };

  return (
    <div className="admin-vendor-management">
      <h1>Vendor Management</h1>
      
      {/* Pending Verifications */}
      <section>
        <h2>Pending Verifications ({pendingVendors.length})</h2>
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Store Name</th>
              <th>Email</th>
              <th>Created</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {pendingVendors.map(vendor => (
              <tr key={vendor.id}>
                <td>{vendor.id}</td>
                <td>{vendor.storeName}</td>
                <td>{vendor.businessEmail}</td>
                <td>{new Date(vendor.createdAt).toLocaleDateString()}</td>
                <td>
                  <button onClick={() => verifyVendor(vendor.id, true)}>
                    Verify
                  </button>
                  <button onClick={() => verifyVendor(vendor.id, false)}>
                    Reject
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>
      
      {/* All Vendors */}
      <section>
        <h2>All Vendors</h2>
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Store Name</th>
              <th>Verified</th>
              <th>Active</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {allVendors.map(vendor => (
              <tr key={vendor.id}>
                <td>{vendor.id}</td>
                <td>{vendor.storeName}</td>
                <td>{vendor.isVerified ? '✅' : '❌'}</td>
                <td>{vendor.isActive ? '✅' : '❌'}</td>
                <td>
                  <button onClick={() => toggleStatus(vendor.id, !vendor.isActive)}>
                    {vendor.isActive ? 'Deactivate' : 'Activate'}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>
    </div>
  );
}
```

---

## 🔒 Security Configuration

Make sure your `SecurityConfig.java` allows these endpoints:

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http
        .csrf().disable()
        .authorizeRequests()
        // Public endpoints
        .antMatchers("/api/auth/**").permitAll()
        .antMatchers("/api/products/**").permitAll()
        .antMatchers("/api/vendor/{id}").permitAll()
        .antMatchers("/api/vendor/all").permitAll()
        
        // Vendor endpoints
        .antMatchers("/api/vendor/**").hasRole("VENDOR")
        
        // Admin endpoints
        .antMatchers("/api/admin/**").hasRole("ADMIN")
        
        // Authenticated endpoints
        .anyRequest().authenticated();
}
```

---

## 🧪 Testing Flow

### 1. **User Registers as Vendor**
```bash
# Login as user
POST /api/auth/login
{"email": "user@example.com", "password": "password"}

# Register as vendor
POST /api/vendor/register
Authorization: Bearer USER_TOKEN
{"storeName": "My Store", ...}
```

### 2. **Admin Verifies Vendor**
```bash
# Login as admin
POST /api/auth/login
{"email": "admin@example.com", "password": "admin"}

# Get pending vendors
GET /api/admin/vendors/pending
Authorization: Bearer ADMIN_TOKEN

# Verify vendor
PUT /api/admin/vendors/1/verify
{"verified": true}
```

### 3. **Vendor Adds Products**
```bash
# Login as vendor (same token as before, role updated)
POST /api/auth/login
{"email": "user@example.com", "password": "password"}

# Add product
POST /api/vendor/products
Authorization: Bearer VENDOR_TOKEN
{"name": "Product 1", "price": 10.99, ...}
```

### 4. **Customer Browses Products**
```bash
# Get all products (includes vendor info)
GET /api/products

# Get products by specific vendor
GET /api/products/vendor/1
```

---

## 📊 Database Migrations

Run these commands to update your database:

```sql
-- Add vendor_id to products table
ALTER TABLE products ADD COLUMN vendor_id BIGINT;
ALTER TABLE products ADD CONSTRAINT fk_products_vendor 
    FOREIGN KEY (vendor_id) REFERENCES vendors(id);

-- Create index for better performance
CREATE INDEX idx_products_vendor ON products(vendor_id);
CREATE INDEX idx_vendors_user ON vendors(user_id);
```

---

## ✅ Features Implemented

✅ Vendor registration and profile management  
✅ Admin vendor verification system  
✅ Vendor product CRUD operations  
✅ Products linked to vendors  
✅ Vendor-specific product queries  
✅ Public vendor browsing  
✅ Commission tracking  
✅ Vendor statistics  
✅ Multi-vendor order support  

---

## 🚀 Next Steps

1. **Restart your Spring Boot application**
2. **Test vendor registration**
3. **Create an admin user** (if not exists):
   ```sql
   UPDATE users SET role = 'ADMIN' WHERE email = 'admin@example.com';
   ```
4. **Test the complete flow**

---

## 📧 Support

Your multi-vendor e-commerce system is now complete! All orders will automatically track which vendor each product comes from.

**Enjoy your multi-vendor marketplace! 🎉**
# 🏪 Multi-Vendor E-Commerce System - Complete Guide

## 📋 Overview

Your Spring Boot application now supports a **complete multi-vendor e-commerce platform** where:
- **Users** can browse and purchase products from multiple vendors
- **Vendors** can register, manage their store, and sell products
- **Admins** can verify vendors and manage the entire platform

---

## 🎯 User Roles

### 1. **USER** (Regular Customer)
- Browse products from all vendors
- Add to cart and place orders
- Write reviews
- View order history

### 2. **VENDOR** (Seller)
- Register as a vendor
- Manage store profile
- Add/edit/delete own products
- View sales and statistics
- Cannot sell until admin verifies

### 3. **ADMIN** (Platform Manager)
- Manage all users and vendors
- Verify/unverify vendors
- Activate/deactivate vendor accounts
- Manage all products
- View all orders

---

## 🚀 API Endpoints

### **Vendor Registration & Management**

#### 1. Register as Vendor
```http
POST /api/vendor/register
Authorization: Bearer USER_TOKEN
Content-Type: application/json

{
  "storeName": "Fresh Organic Store",
  "description": "We sell fresh organic products",
  "businessLicense": "BL-12345",
  "taxId": "TAX-67890",
  "businessEmail": "contact@freshorganic.com",
  "businessPhone": "+1234567890",
  "businessAddress": "123 Market Street, City",
  "logoUrl": "https://example.com/logo.jpg",
  "bannerUrl": "https://example.com/banner.jpg"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Vendor registration successful! Awaiting admin verification.",
  "vendor": {
    "id": 1,
    "storeName": "Fresh Organic Store",
    "isVerified": false,
    "isActive": true
  }
}
```

---

#### 2. Get My Vendor Profile
```http
GET /api/vendor/profile
Authorization: Bearer VENDOR_TOKEN
```

**Response:**
```json
{
  "id": 1,
  "storeName": "Fresh Organic Store",
  "description": "We sell fresh organic products",
  "businessEmail": "contact@freshorganic.com",
  "isVerified": true,
  "isActive": true,
  "rating": 4.5,
  "reviewCount": 120,
  "commission": 10.0,
  "createdAt": "2025-01-15T10:00:00"
}
```

---

#### 3. Update Vendor Profile
```http
PUT /api/vendor/profile
Authorization: Bearer VENDOR_TOKEN
Content-Type: application/json

{
  "storeName": "Fresh Organic Market",
  "description": "Updated description",
  "businessPhone": "+9876543210"
}
```

---

#### 4. Get Vendor by ID (Public)
```http
GET /api/vendor/{vendorId}
```

---

#### 5. Get All Verified Vendors (Public)
```http
GET /api/vendor/all
```

---

### **Vendor Product Management**

#### 1. Get My Products
```http
GET /api/vendor/products
Authorization: Bearer VENDOR_TOKEN
```

**Response:**
```json
[
  {
    "id": 10,
    "name": "Organic Apples",
    "price": 4.99,
    "stock": 100,
    "vendor": {
      "id": 1,
      "storeName": "Fresh Organic Store"
    },
    "isAvailable": true
  }
]
```

---

#### 2. Add New Product
```http
POST /api/vendor/products
Authorization: Bearer VENDOR_TOKEN
Content-Type: application/json

{
  "name": "Organic Bananas",
  "description": "Fresh organic bananas",
  "price": 3.99,
  "stock": 50,
  "imageUrl": "https://example.com/banana.jpg",
  "category": {
    "id": 2
  },
  "brand": "Farm Fresh",
  "unit": "kg",
  "weight": 1.0,
  "isAvailable": true,
  "discount": 0.0
}
```

**Response:**
```json
{
  "success": true,
  "message": "Product added successfully",
  "product": {
    "id": 15,
    "name": "Organic Bananas",
    "price": 3.99,
    "vendor": {
      "id": 1,
      "storeName": "Fresh Organic Store"
    }
  }
}
```

---

#### 3. Update My Product
```http
PUT /api/vendor/products/{productId}
Authorization: Bearer VENDOR_TOKEN
Content-Type: application/json

{
  "price": 4.49,
  "stock": 75,
  "discount": 10.0
}
```

---

#### 4. Delete My Product
```http
DELETE /api/vendor/products/{productId}
Authorization: Bearer VENDOR_TOKEN
```

---

#### 5. Get Product Statistics
```http
GET /api/vendor/products/stats
Authorization: Bearer VENDOR_TOKEN
```

**Response:**
```json
{
  "totalProducts": 25,
  "availableProducts": 20,
  "outOfStock": 3,
  "inactiveProducts": 5
}
```

---

### **Admin - Vendor Management**

#### 1. Get All Vendors
```http
GET /api/admin/vendors
Authorization: Bearer ADMIN_TOKEN
```

---

#### 2. Get Pending Vendor Verifications
```http
GET /api/admin/vendors/pending
Authorization: Bearer ADMIN_TOKEN
```

**Response:**
```json
[
  {
    "id": 5,
    "storeName": "New Vendor Store",
    "businessEmail": "newvendor@example.com",
    "isVerified": false,
    "isActive": true,
    "createdAt": "2025-10-10T14:30:00"
  }
]
```

---

#### 3. Verify Vendor
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
    "id": 5,
    "storeName": "New Vendor Store",
    "isVerified": true
  }
}
```

---

#### 4. Activate/Deactivate Vendor
```http
PUT /api/admin/vendors/{vendorId}/status
Authorization: Bearer ADMIN_TOKEN
Content-Type: application/json

{
  "active": false
}
```

---

### **Public Product Endpoints (Updated)**

#### Get Products by Vendor
```http
GET /api/products/vendor/{vendorId}
```

All existing product endpoints (`/api/products`) now include vendor information in responses.

---

## 🗄️ Database Schema

### New Table: `vendors`
```sql
CREATE TABLE vendors (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT UNIQUE NOT NULL,
    store_name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,
    business_license VARCHAR(255),
    tax_id VARCHAR(255),
    logo_url VARCHAR(500),
    banner_url VARCHAR(500),
    business_email VARCHAR(255),
    business_phone VARCHAR(50),
    business_address VARCHAR(500),
    is_verified BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    rating DOUBLE DEFAULT 0.0,
    review_count INT DEFAULT 0,
    commission DOUBLE DEFAULT 10.0,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### Updated Table: `products`
```sql
ALTER TABLE products
ADD COLUMN vendor_id BIGINT,
ADD FOREIGN KEY (vendor_id) REFERENCES vendors(id);
```

---

## 📱 Frontend Implementation Examples

### 1. Register as Vendor (React/Next.js)

```jsx
'use client';
import { useState } from 'react';

export default function VendorRegistration() {
  const [formData, setFormData] = useState({
    storeName: '',
    description: '',

