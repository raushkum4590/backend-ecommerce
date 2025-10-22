# 🔧 Admin Panel - Vendor Management Fix

## 🚨 Issue Identified

Your frontend is getting a **403 Forbidden** error when calling `/api/vendors`.

**Problem:** The endpoint path is wrong or authentication is missing.

**Correct Endpoint:** `/api/admin/vendors` (requires admin authentication)

---

## ✅ Available Admin Endpoints

Your backend already has these endpoints configured:

### 1. Get All Vendors
```http
GET /api/admin/vendors
Authorization: Bearer ADMIN_TOKEN
```

**Response:**
```json
[
  {
    "id": 3,
    "storeName": "hh",
    "businessEmail": "rt@gmail.com",
    "isVerified": true,
    "isActive": true,
    "description": "dfd",
    "businessPhone": "+911111111111",
    "businessAddress": "rtt"
  }
]
```

### 2. Get Pending Vendors
```http
GET /api/admin/vendors/pending
Authorization: Bearer ADMIN_TOKEN
```

### 3. Get Vendor by ID
```http
GET /api/admin/vendors/{id}
Authorization: Bearer ADMIN_TOKEN
```

### 4. Verify Vendor
```http
PUT /api/admin/vendors/{id}/verify
Authorization: Bearer ADMIN_TOKEN
Content-Type: application/json

{
  "verified": true
}
```

### 5. Toggle Vendor Status
```http
PUT /api/admin/vendors/{id}/status
Authorization: Bearer ADMIN_TOKEN
Content-Type: application/json

{
  "active": true
}
```

---

## 🔧 Frontend Fix Required

Your frontend code needs to:

1. **Use correct endpoint:** `/api/admin/vendors` (not `/api/vendors`)
2. **Include admin token:** Add `Authorization: Bearer TOKEN` header
3. **Check user role:** Verify user is admin before calling

### Frontend Code Example:

```javascript
// In your admin products page
const loadVendors = async () => {
  try {
    const token = localStorage.getItem('token');
    
    // Correct endpoint with /admin prefix
    const response = await fetch('http://localhost:8082/api/admin/vendors', {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    }

    const vendors = await response.json();
    console.log('✅ Vendors loaded:', vendors);
    return vendors;
    
  } catch (error) {
    console.error('❌ Failed to load vendors:', error);
    throw error;
  }
};
```

---

## 🧪 Test the Endpoints

### Test 1: Check if admin token is valid
```bash
# Get your admin token first (login as admin)
# Then test:
curl -H "Authorization: Bearer YOUR_ADMIN_TOKEN" http://localhost:8082/api/admin/vendors
```

**Expected:** Returns array of vendors  
**If 403:** Token is invalid or user is not admin  
**If 401:** Token is missing or expired

### Test 2: Test without token
```bash
curl http://localhost:8082/api/admin/vendors
```

**Expected:** 401 or 403 error (this is correct - endpoint is protected)

---

## 🔍 Troubleshooting

### Error: 403 Forbidden

**Cause 1: Wrong endpoint path**
- ❌ `/api/vendors` 
- ✅ `/api/admin/vendors`

**Cause 2: Missing admin role**
```sql
-- Check user role in database
SELECT id, email, role FROM users WHERE email = 'admin@gmail.com';
-- Should show role = 'ADMIN'
```

**Cause 3: Invalid token**
- Token expired
- Token from non-admin user
- Token not included in request

### Error: 401 Unauthorized

**Cause:** No token in request headers

**Fix:** Add authorization header:
```javascript
headers: {
  'Authorization': `Bearer ${token}`
}
```

---

## 📋 Quick Checklist

Before calling admin endpoints, verify:

- [ ] User is logged in as admin
- [ ] Admin token is stored in localStorage
- [ ] Endpoint path includes `/admin` prefix
- [ ] Authorization header is included
- [ ] Token is not expired

---

## 🎯 Summary

**Your Backend:** ✅ Working correctly - endpoints exist and are protected  
**Your Frontend:** ❌ Needs fix - using wrong endpoint or missing auth

**Fix Required:**
1. Change endpoint from `/api/vendors` to `/api/admin/vendors`
2. Include `Authorization: Bearer TOKEN` header
3. Verify user is admin before making request

The admin panel vendor management feature is fully implemented in the backend - your frontend just needs to call the correct endpoints with proper authentication!

