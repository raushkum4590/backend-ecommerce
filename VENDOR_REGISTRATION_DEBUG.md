# 🔧 Troubleshooting: "An unexpected error occurred" - Vendor Registration

## ✅ What I Fixed

I've improved the error handling in your `VendorController.java` to provide **detailed error messages and logging**. The backend will now tell you exactly what went wrong instead of just saying "An unexpected error occurred".

---

## 🚀 Steps to Fix the Issue

### **Step 1: Restart Your Spring Boot Application**

Stop the current server and restart it to load the updated code:

```bash
# Press Ctrl+C to stop
# Then restart
```

### **Step 2: Check the Console Logs**

After restarting, try registering as a vendor again. Now look at your **Spring Boot console** for detailed error messages like:

```
2025-10-11 10:30:15 INFO  VendorController - Vendor registration attempt by user: user@example.com
2025-10-11 10:30:15 INFO  VendorController - Request data: storeName=My Store, businessEmail=store@example.com
2025-10-11 10:30:15 ERROR VendorController - Vendor registration failed for user: user@example.com
java.lang.RuntimeException: Store name already taken
```

The logs will now show you the **exact error** that's causing the problem.

---

## 🔍 Common Issues & Solutions

### **Issue 1: Vendor Table Not Created**

**Symptom:** Error like "Table 'ecommerce.vendors' doesn't exist"

**Solution:** Run the SQL script I created:

```sql
-- In MySQL Workbench or command line:
mysql -u root -p ecommerce < create_vendor_tables.sql
```

Or manually run:
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

ALTER TABLE products ADD COLUMN vendor_id BIGINT;
ALTER TABLE products ADD CONSTRAINT fk_products_vendor 
    FOREIGN KEY (vendor_id) REFERENCES vendors(id);
```

---

### **Issue 2: Store Name Already Taken**

**Symptom:** "Store name already taken"

**Solution:** Choose a different store name or delete existing vendor:

```sql
-- Check existing vendors
SELECT id, store_name, user_id FROM vendors;

-- Delete if needed
DELETE FROM vendors WHERE store_name = 'My Store';
```

---

### **Issue 3: User Already Has Vendor Profile**

**Symptom:** "User already has a vendor profile"

**Solution:** Check if you already registered:

```sql
-- Check if you have a vendor profile
SELECT v.*, u.email 
FROM vendors v 
JOIN users u ON v.user_id = u.id 
WHERE u.email = 'your-email@example.com';

-- If exists and you want to re-register, delete it:
DELETE FROM vendors WHERE user_id = (SELECT id FROM users WHERE email = 'your-email@example.com');
```

---

### **Issue 4: JSON Serialization Error**

**Symptom:** "Could not write JSON" or circular reference error

**Solution:** The Vendor entity already has `@JsonIgnore` on the User relationship, but verify:

```java
// In Vendor.java - should have:
@JsonIgnore  
@OneToOne
private User user;
```

---

### **Issue 5: Authentication/Token Issues**

**Symptom:** 401 or 403 errors

**Solution:** 
1. Make sure you're logged in
2. Check your token is valid:
   ```javascript
   const token = localStorage.getItem('token');
   console.log('Token:', token);
   ```
3. If token expired, login again

---

## 📊 Debug Your Frontend Request

Add this to your frontend `app/vendor/register/page.js`:

```javascript
const handleSubmit = async (e) => {
  e.preventDefault();
  
  console.log('🔵 Sending vendor registration request...');
  console.log('📦 Request data:', formData);
  
  const token = localStorage.getItem('token');
  console.log('🔑 Token exists:', !!token);
  
  try {
    const response = await fetch('http://localhost:8082/api/vendor/register', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(formData)
    });
    
    console.log('📊 Response status:', response.status);
    
    const data = await response.json();
    console.log('📦 Response data:', data);
    
    if (data.success) {
      alert('✅ Vendor registration successful!');
    } else {
      alert('❌ Error: ' + data.message);
      console.error('Error details:', data);
    }
  } catch (error) {
    console.error('❌ Network error:', error);
  }
};
```

---

## 🧪 Test the API Directly

Use this cURL command to test without the frontend:

```bash
# 1. Login first to get token
curl -X POST http://localhost:8082/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password"}'

# Copy the token from response, then:

# 2. Register as vendor
curl -X POST http://localhost:8082/api/vendor/register \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "storeName": "Test Store",
    "description": "Test description",
    "businessEmail": "store@example.com",
    "businessPhone": "1234567890",
    "businessAddress": "123 Main St"
  }'
```

---

## 📋 Expected Response (Success)

```json
{
  "success": true,
  "message": "Vendor registration successful! Awaiting admin verification.",
  "vendor": {
    "id": 1,
    "storeName": "Test Store",
    "description": "Test description",
    "isVerified": false,
    "isActive": true,
    "rating": 0.0,
    "reviewCount": 0
  }
}
```

---

## 📋 Expected Response (Error)

```json
{
  "success": false,
  "message": "Store name already taken",
  "error": "RuntimeException"
}
```

---

## 🔄 Complete Testing Flow

1. **Restart Spring Boot** (to load new error handling)
2. **Check database** (run `create_vendor_tables.sql` if needed)
3. **Try registration again**
4. **Check Spring Boot console** for detailed logs
5. **Check browser console** for frontend errors
6. **Read the error message** (now much more helpful!)

---

## 📞 What to Check Next

After restarting, the error message will tell you exactly what's wrong. Come back with:

1. **The exact error message from Spring Boot console** (look for lines starting with "ERROR")
2. **The response data from browser console** (will now include error type)
3. **Any database errors** (if table doesn't exist)

---

## ✅ Files Updated

- ✅ `VendorController.java` - Added detailed logging and error messages
- ✅ `application.properties` - Already has file upload limits
- ✅ `create_vendor_tables.sql` - SQL script to create vendor tables

---

## 🎯 Next Steps

1. **Restart your Spring Boot application NOW**
2. **Try registering as vendor again**
3. **Look at the Spring Boot console output**
4. **The error message will tell you exactly what to fix!**

The improved error handling will guide you to the exact problem! 🚀

