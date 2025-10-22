# 👥 User Management in Admin Panel - Complete Guide

## ✅ ALREADY IMPLEMENTED!

Your backend already has **complete user management functionality**! Here's everything you can do:

---

## 📋 Available User Management Endpoints

### 1. **Get All Users**
**Endpoint:** `GET /api/admin/users`

**Description:** Retrieves all users from the database

**Headers:**
```json
{
  "Authorization": "Bearer {ADMIN_TOKEN}"
}
```

**Response:**
```json
[
  {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "role": "USER",
    "phoneNumber": "1234567890",
    "isActive": true,
    "createdAt": "2025-10-01T10:00:00",
    "lastLogin": "2025-10-11T08:30:00"
  },
  {
    "id": 2,
    "username": "admin",
    "email": "admin@example.com",
    "role": "ADMIN",
    "phoneNumber": "9876543210",
    "isActive": true,
    "createdAt": "2025-09-15T09:00:00",
    "lastLogin": "2025-10-11T09:45:00"
  }
]
```

**Test Command:**
```bash
curl -X GET http://localhost:8082/api/admin/users ^
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

---

### 2. **Get User by ID**
**Endpoint:** `GET /api/admin/users/{id}`

**Example:** `GET /api/admin/users/1`

**Response:**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "role": "USER",
  "phoneNumber": "1234567890",
  "isActive": true,
  "createdAt": "2025-10-01T10:00:00",
  "lastLogin": "2025-10-11T08:30:00"
}
```

**Test Command:**
```bash
curl -X GET http://localhost:8082/api/admin/users/1 ^
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

---

### 3. **Get User by Email**
**Endpoint:** `GET /api/admin/users/email/{email}`

**Example:** `GET /api/admin/users/email/john@example.com`

**Response:**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "role": "USER",
  "phoneNumber": "1234567890",
  "isActive": true,
  "createdAt": "2025-10-01T10:00:00",
  "lastLogin": "2025-10-11T08:30:00"
}
```

**Test Command:**
```bash
curl -X GET http://localhost:8082/api/admin/users/email/john@example.com ^
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

---

### 4. **Get User's Order History**
**Endpoint:** `GET /api/admin/users/email/{email}/orders`

**Description:** Get complete order history for a specific user

**Response:**
```json
{
  "user": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "role": "USER"
  },
  "totalOrders": 5,
  "totalSpent": 425.50,
  "orders": [
    {
      "id": 15,
      "orderDate": "2025-10-10T14:30:00",
      "totalAmount": 125.50,
      "orderStatus": "DELIVERED",
      "paymentStatus": "COMPLETED",
      "paymentMethod": "cash",
      "items": [...]
    },
    {
      "id": 12,
      "orderDate": "2025-10-05T11:20:00",
      "totalAmount": 300.00,
      "orderStatus": "SHIPPED",
      "paymentStatus": "COMPLETED",
      "paymentMethod": "paypal"
    }
  ]
}
```

**Test Command:**
```bash
curl -X GET http://localhost:8082/api/admin/users/email/john@example.com/orders ^
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

---

### 5. **Search Users**
**Endpoint:** `GET /api/admin/users/search?query={searchTerm}`

**Description:** Search users by email or username

**Example:** `GET /api/admin/users/search?query=john`

**Response:**
```json
[
  {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "role": "USER",
    "isActive": true
  },
  {
    "id": 5,
    "username": "johnny_smith",
    "email": "johnny@example.com",
    "role": "USER",
    "isActive": true
  }
]
```

**Test Command:**
```bash
curl -X GET "http://localhost:8082/api/admin/users/search?query=john" ^
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

---

### 6. **Activate User**
**Endpoint:** `PUT /api/admin/users/{id}/activate`

**Description:** Enable a user account

**Response:**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "isActive": true
}
```

**Test Command:**
```bash
curl -X PUT http://localhost:8082/api/admin/users/1/activate ^
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

---

### 7. **Deactivate User**
**Endpoint:** `PUT /api/admin/users/{id}/deactivate`

**Description:** Disable a user account

**Response:**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "isActive": false
}
```

**Test Command:**
```bash
curl -X PUT http://localhost:8082/api/admin/users/1/deactivate ^
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

---

### 8. **Reset User Password**
**Endpoint:** `PUT /api/admin/users/{id}/reset-password`

**Description:** Admin can reset any user's password

**Request Body:**
```json
{
  "newPassword": "newSecurePassword123"
}
```

**Response:**
```json
{
  "message": "Password reset successfully for user: john@example.com"
}
```

**Test Command:**
```bash
curl -X PUT http://localhost:8082/api/admin/users/1/reset-password ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" ^
  -d "{\"newPassword\":\"newPassword123\"}"
```

---

### 9. **Get Dashboard Statistics** (includes user stats)
**Endpoint:** `GET /api/admin/dashboard/stats`

**Response:**
```json
{
  "totalRevenue": 15420.50,
  "totalOrders": 245,
  "totalProducts": 150,
  "totalUsers": 89,
  "pendingOrders": 12
}
```

**Test Command:**
```bash
curl -X GET http://localhost:8082/api/admin/dashboard/stats ^
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

---

### 10. **Get Comprehensive Statistics** (detailed user analytics)
**Endpoint:** `GET /api/admin/stats/comprehensive`

**Response:**
```json
{
  "totalUsers": 89,
  "activeUsers": 75,
  "totalVendors": 12,
  "verifiedVendors": 8,
  "pendingVendors": 4,
  "totalProducts": 150,
  "availableProducts": 142,
  "totalOrders": 245,
  "totalRevenue": 15420.50,
  "ordersByStatus": {
    "PENDING": 5,
    "CONFIRMED": 8,
    "PROCESSING": 12,
    "SHIPPED": 15,
    "DELIVERED": 195,
    "CANCELLED": 10
  }
}
```

**Test Command:**
```bash
curl -X GET http://localhost:8082/api/admin/stats/comprehensive ^
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

---

## 💻 Frontend Integration Examples

### React/Next.js - Fetch All Users

```javascript
// In your Admin Users Page component
import { useState, useEffect } from 'react';

const AdminUsersPage = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch('http://localhost:8082/api/admin/users', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      
      const data = await response.json();
      setUsers(data);
      setLoading(false);
    } catch (error) {
      console.error('Error fetching users:', error);
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    if (!searchQuery.trim()) {
      fetchUsers();
      return;
    }

    try {
      const token = localStorage.getItem('token');
      const response = await fetch(
        `http://localhost:8082/api/admin/users/search?query=${searchQuery}`,
        {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        }
      );
      const data = await response.json();
      setUsers(data);
    } catch (error) {
      console.error('Error searching users:', error);
    }
  };

  const toggleUserStatus = async (userId, currentStatus) => {
    const endpoint = currentStatus ? 'deactivate' : 'activate';
    
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(
        `http://localhost:8082/api/admin/users/${userId}/${endpoint}`,
        {
          method: 'PUT',
          headers: {
            'Authorization': `Bearer ${token}`
          }
        }
      );

      if (response.ok) {
        alert(`User ${currentStatus ? 'deactivated' : 'activated'} successfully`);
        fetchUsers();
      }
    } catch (error) {
      console.error('Error toggling user status:', error);
    }
  };

  const viewUserOrders = async (email) => {
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
      const data = await response.json();
      console.log('User orders:', data);
      // Display in modal or navigate to orders page
    } catch (error) {
      console.error('Error fetching user orders:', error);
    }
  };

  if (loading) return <div>Loading users...</div>;

  return (
    <div className="admin-users-page">
      <h1>User Management</h1>
      
      {/* Search Bar */}
      <div className="search-bar">
        <input
          type="text"
          placeholder="Search by name or email..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
        />
        <button onClick={handleSearch}>Search</button>
        <button onClick={() => { setSearchQuery(''); fetchUsers(); }}>
          Clear
        </button>
      </div>

      {/* User Statistics */}
      <div className="stats-summary">
        <div className="stat-card">
          <h3>Total Users</h3>
          <p>{users.length}</p>
        </div>
        <div className="stat-card">
          <h3>Active Users</h3>
          <p>{users.filter(u => u.isActive).length}</p>
        </div>
        <div className="stat-card">
          <h3>Inactive Users</h3>
          <p>{users.filter(u => !u.isActive).length}</p>
        </div>
      </div>

      {/* Users Table */}
      <table className="users-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Username</th>
            <th>Email</th>
            <th>Role</th>
            <th>Phone</th>
            <th>Status</th>
            <th>Created</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {users.map(user => (
            <tr key={user.id}>
              <td>{user.id}</td>
              <td>{user.username}</td>
              <td>{user.email}</td>
              <td>
                <span className={`role-badge ${user.role.toLowerCase()}`}>
                  {user.role}
                </span>
              </td>
              <td>{user.phoneNumber || 'N/A'}</td>
              <td>
                <span className={`status-badge ${user.isActive ? 'active' : 'inactive'}`}>
                  {user.isActive ? '✅ Active' : '❌ Inactive'}
                </span>
              </td>
              <td>{new Date(user.createdAt).toLocaleDateString()}</td>
              <td>
                <button 
                  onClick={() => viewUserOrders(user.email)}
                  className="btn-view"
                >
                  📦 Orders
                </button>
                <button 
                  onClick={() => toggleUserStatus(user.id, user.isActive)}
                  className={user.isActive ? 'btn-deactivate' : 'btn-activate'}
                >
                  {user.isActive ? '🔒 Deactivate' : '🔓 Activate'}
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {users.length === 0 && (
        <div className="no-data">No users found</div>
      )}
    </div>
  );
};

export default AdminUsersPage;
```

---

## 🎨 CSS Styling Example

```css
/* Admin Users Page Styles */
.admin-users-page {
  padding: 20px;
}

.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.search-bar input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.search-bar button {
  padding: 10px 20px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.stats-summary {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  text-align: center;
}

.stat-card h3 {
  margin: 0 0 10px 0;
  color: #666;
  font-size: 14px;
}

.stat-card p {
  margin: 0;
  font-size: 32px;
  font-weight: bold;
  color: #333;
}

.users-table {
  width: 100%;
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.users-table th {
  background: #f8f9fa;
  padding: 15px;
  text-align: left;
  font-weight: 600;
  border-bottom: 2px solid #dee2e6;
}

.users-table td {
  padding: 15px;
  border-bottom: 1px solid #dee2e6;
}

.role-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.role-badge.admin {
  background: #dc3545;
  color: white;
}

.role-badge.vendor {
  background: #ffc107;
  color: #000;
}

.role-badge.user {
  background: #28a745;
  color: white;
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.status-badge.active {
  background: #d4edda;
  color: #155724;
}

.status-badge.inactive {
  background: #f8d7da;
  color: #721c24;
}

.btn-view,
.btn-activate,
.btn-deactivate {
  padding: 6px 12px;
  margin: 0 5px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}

.btn-view {
  background: #17a2b8;
  color: white;
}

.btn-activate {
  background: #28a745;
  color: white;
}

.btn-deactivate {
  background: #dc3545;
  color: white;
}

.no-data {
  text-align: center;
  padding: 40px;
  color: #999;
}
```

---

## 📊 User Analytics Display

```javascript
// User Statistics Component
const UserStatistics = () => {
  const [stats, setStats] = useState(null);

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(
        'http://localhost:8082/api/admin/stats/comprehensive',
        {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        }
      );
      const data = await response.json();
      setStats(data);
    } catch (error) {
      console.error('Error fetching stats:', error);
    }
  };

  if (!stats) return <div>Loading statistics...</div>;

  return (
    <div className="user-statistics">
      <h2>User Analytics</h2>
      
      <div className="stats-grid">
        <div className="stat-item">
          <span className="stat-label">Total Users</span>
          <span className="stat-value">{stats.totalUsers}</span>
        </div>
        
        <div className="stat-item">
          <span className="stat-label">Active Users</span>
          <span className="stat-value">{stats.activeUsers}</span>
        </div>
        
        <div className="stat-item">
          <span className="stat-label">Total Vendors</span>
          <span className="stat-value">{stats.totalVendors}</span>
        </div>
        
        <div className="stat-item">
          <span className="stat-label">Total Revenue</span>
          <span className="stat-value">${stats.totalRevenue.toFixed(2)}</span>
        </div>
      </div>
    </div>
  );
};
```

---

## ✅ Complete Feature Checklist

| Feature | Status | Endpoint |
|---------|--------|----------|
| ✅ View All Users | **IMPLEMENTED** | `GET /api/admin/users` |
| ✅ Search Users | **IMPLEMENTED** | `GET /api/admin/users/search?query=...` |
| ✅ View User Details | **IMPLEMENTED** | `GET /api/admin/users/{id}` |
| ✅ View by Email | **IMPLEMENTED** | `GET /api/admin/users/email/{email}` |
| ✅ Order History | **IMPLEMENTED** | `GET /api/admin/users/email/{email}/orders` |
| ✅ Activate User | **IMPLEMENTED** | `PUT /api/admin/users/{id}/activate` |
| ✅ Deactivate User | **IMPLEMENTED** | `PUT /api/admin/users/{id}/deactivate` |
| ✅ Reset Password | **IMPLEMENTED** | `PUT /api/admin/users/{id}/reset-password` |
| ✅ User Statistics | **IMPLEMENTED** | `GET /api/admin/stats/comprehensive` |

**ALL FEATURES ARE ALREADY IMPLEMENTED IN YOUR BACKEND!** 🎉

---

## 🚀 Getting Started

1. **Make sure your backend is running:**
   ```bash
   mvn spring-boot:run
   ```

2. **Test the endpoints using the provided test script** (see next section)

3. **Integrate the frontend code** into your Next.js admin panel

4. **Customize the UI** to match your design requirements

---

Your backend is fully ready for user management! Just create the frontend UI using the examples above. 🎨

