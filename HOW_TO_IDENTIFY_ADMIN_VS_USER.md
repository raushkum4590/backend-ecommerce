# 🔐 How to Identify Admin vs User

Your application has two roles: **USER** and **ADMIN**

## 🎯 Backend - How to Check User Role

### 1. **In the Database**

Check the `users` table:
```sql
SELECT id, username, email, role FROM users;
```

**Roles:**
- `USER` - Regular user (default)
- `ADMIN` - Administrator with elevated permissions

### 2. **Update a User to Admin**

```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@example.com';
```

### 3. **API Endpoints to Get User Role**

#### A. Login Response (Automatic)
```bash
POST http://localhost:8082/api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMi...",
  "user": {
    "id": 1,
    "username": "testuser",
    "email": "user@example.com",
    "role": "USER"    ← Check this field
  }
}
```

#### B. Get Current User Info
```bash
GET http://localhost:8082/api/auth/me
Authorization: Bearer YOUR_TOKEN
```

**Response:**
```json
{
  "id": 1,
  "username": "testuser",
  "email": "user@example.com",
  "role": "ADMIN",       ← "USER" or "ADMIN"
  "isAdmin": true,       ← Convenient boolean
  "phoneNumber": "1234567890"
}
```

### 4. **In Your Controllers (Backend)**

Use `@PreAuthorize` annotation to restrict endpoints:

```java
// Only admins can access
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/admin/dashboard")
public ResponseEntity<?> getAdminDashboard() {
    return ResponseEntity.ok("Admin Dashboard");
}

// Both users and admins can access
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@GetMapping("/profile")
public ResponseEntity<?> getProfile() {
    return ResponseEntity.ok("User Profile");
}

// Get current user's role programmatically
@GetMapping("/check-role")
public ResponseEntity<?> checkRole(Authentication auth) {
    User user = userService.findByEmail(auth.getName());
    boolean isAdmin = user.getRole().name().equals("ADMIN");
    
    return ResponseEntity.ok(Map.of(
        "role", user.getRole().name(),
        "isAdmin", isAdmin
    ));
}
```

## 💻 Frontend - How to Check User Role

### 1. **After Login/Register**

```javascript
// When user logs in
const response = await fetch('http://localhost:8082/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ email, password })
});

const data = await response.json();

// Store user role
localStorage.setItem('token', data.token);
localStorage.setItem('userRole', data.user.role);  // "USER" or "ADMIN"

// Check if admin
const isAdmin = data.user.role === 'ADMIN';
console.log('Is Admin:', isAdmin);
```

### 2. **Check Current User Role**

```javascript
// Fetch current user's role
async function getCurrentUserRole() {
  const token = localStorage.getItem('token');
  
  const response = await fetch('http://localhost:8082/api/auth/me', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  const user = await response.json();
  
  console.log('Role:', user.role);        // "USER" or "ADMIN"
  console.log('Is Admin:', user.isAdmin); // true or false
  
  return user;
}
```

### 3. **Conditional Rendering (React/Next.js)**

```jsx
import { useState, useEffect } from 'react';

function Dashboard() {
  const [isAdmin, setIsAdmin] = useState(false);
  
  useEffect(() => {
    // Check user role
    const userRole = localStorage.getItem('userRole');
    setIsAdmin(userRole === 'ADMIN');
  }, []);
  
  return (
    <div>
      <h1>Dashboard</h1>
      
      {/* Show only to admins */}
      {isAdmin && (
        <div className="admin-panel">
          <h2>Admin Panel</h2>
          <button>Manage Users</button>
          <button>View Orders</button>
          <button>Manage Products</button>
        </div>
      )}
      
      {/* Show only to regular users */}
      {!isAdmin && (
        <div className="user-panel">
          <h2>User Dashboard</h2>
          <button>My Orders</button>
          <button>My Profile</button>
        </div>
      )}
    </div>
  );
}
```

### 4. **Protected Routes (Next.js)**

```javascript
// middleware.js or in your component
export function requireAdmin(Component) {
  return function ProtectedRoute(props) {
    const [authorized, setAuthorized] = useState(false);
    const router = useRouter();
    
    useEffect(() => {
      const checkAuth = async () => {
        const token = localStorage.getItem('token');
        
        if (!token) {
          router.push('/login');
          return;
        }
        
        const response = await fetch('http://localhost:8082/api/auth/me', {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        
        const user = await response.json();
        
        if (user.role !== 'ADMIN') {
          router.push('/unauthorized');
        } else {
          setAuthorized(true);
        }
      };
      
      checkAuth();
    }, []);
    
    if (!authorized) return <div>Loading...</div>;
    
    return <Component {...props} />;
  };
}

// Usage
export default requireAdmin(AdminDashboard);
```

## 🛠️ How to Create an Admin User

### Method 1: Direct Database Update
```sql
-- Make an existing user an admin
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@example.com';

-- Or insert a new admin user (password must be BCrypt hashed)
INSERT INTO users (username, email, password, role, is_active, created_at) 
VALUES (
  'admin',
  'admin@example.com',
  '$2a$10$YOUR_BCRYPT_HASHED_PASSWORD',
  'ADMIN',
  1,
  NOW()
);
```

### Method 2: Create Admin via API (requires admin endpoint)
```java
@PostMapping("/admin/create-admin")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> createAdmin(@RequestBody RegisterRequest request) {
    User user = new User();
    user.setUsername(request.getUsername());
    user.setEmail(request.getEmail());
    user.setPassword(request.getPassword());
    user.setRole(UserRole.ADMIN);  // Set as admin
    
    User registeredUser = userService.register(user);
    return ResponseEntity.ok(registeredUser);
}
```

## 📊 Quick Reference Table

| Scenario | How to Check |
|----------|-------------|
| **Backend Controller** | `@PreAuthorize("hasRole('ADMIN')")` |
| **Backend Service** | `user.getRole().name().equals("ADMIN")` |
| **Frontend after Login** | `data.user.role === 'ADMIN'` |
| **Frontend anytime** | `localStorage.getItem('userRole') === 'ADMIN'` |
| **API Call** | `GET /api/auth/me` and check `isAdmin` field |
| **Database** | `SELECT role FROM users WHERE email = ?` |

## 🎨 UI Elements Examples

### Admin Navigation
```jsx
{isAdmin && (
  <nav className="admin-nav">
    <Link href="/admin/dashboard">Dashboard</Link>
    <Link href="/admin/users">Manage Users</Link>
    <Link href="/admin/products">Manage Products</Link>
    <Link href="/admin/orders">All Orders</Link>
  </nav>
)}
```

### User Badge
```jsx
<div className="user-info">
  <span>{username}</span>
  {isAdmin && <span className="badge badge-admin">ADMIN</span>}
</div>
```

## 🔒 Security Best Practices

1. **Never trust frontend checks alone** - Always validate roles on the backend
2. **Use `@PreAuthorize`** on sensitive endpoints
3. **Check role in JWT token** - Role is encoded in the token
4. **Don't expose admin features in UI** for non-admins (security through obscurity + backend validation)
5. **Log admin actions** for audit trails

## 🚀 Testing

### Test as User
1. Register: `POST /api/auth/register`
2. Login: Check `role: "USER"` in response
3. Try admin endpoint: Should get 403 Forbidden

### Test as Admin
1. Update user to admin in database
2. Login: Check `role: "ADMIN"` in response
3. Access admin endpoints: Should succeed

---

**Your backend now returns role information in all auth responses!**

