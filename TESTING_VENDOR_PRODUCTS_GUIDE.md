# 🧪 COMPLETE TESTING GUIDE: Verify Vendor Products Display

## ✅ Goal
Verify that when vendors add products, those products **actually appear** in the database and in API responses.

---

## 📋 Step-by-Step Testing Process

### Step 1: Check Database Directly

#### Option A: Run SQL Script
```bash
# In MySQL command line or MySQL Workbench:
mysql -u root -p ecommerce < verify_vendor_products.sql
```

#### Option B: Manual SQL Queries
```sql
-- Connect to your database
USE ecommerce;

-- Check if vendor products exist
SELECT 
    p.id,
    p.name,
    p.price,
    CASE 
        WHEN p.vendor_id IS NULL THEN '🔵 ADMIN'
        ELSE '🟢 VENDOR'
    END as source,
    v.store_name as vendor_name
FROM products p
LEFT JOIN vendors v ON p.vendor_id = v.id
ORDER BY p.created_at DESC
LIMIT 20;
```

**Expected Result:**
```
+----+-------------------+-------+----------+--------------+
| id | name              | price | source   | vendor_name  |
+----+-------------------+-------+----------+--------------+
| 25 | Organic Tomatoes  | 3.99  | 🟢 VENDOR | Green Farm   |
| 24 | Fresh Milk        | 2.99  | 🟢 VENDOR | Dairy King   |
| 23 | Premium Apples    | 4.99  | 🔵 ADMIN  | NULL         |
+----+-------------------+-------+----------+--------------+
```

---

### Step 2: Test API Endpoints

#### Test 1: Get All Products (Public Endpoint)
```bash
curl http://localhost:8082/api/products
```

**What to Look For:**
- Products with `"vendor": null` → Admin products ✅
- Products with `"vendor": {...}` → Vendor products ✅

**Example Response:**
```json
[
  {
    "id": 1,
    "name": "Admin Product",
    "price": 9.99,
    "vendor": null  ← ADMIN PRODUCT
  },
  {
    "id": 2,
    "name": "Vendor Product",
    "price": 14.99,
    "vendor": {  ← VENDOR PRODUCT
      "id": 5,
      "storeName": "Green Farm",
      "businessEmail": "vendor@example.com"
    }
  }
]
```

#### Test 2: Admin View - All Products
```bash
curl -X GET http://localhost:8082/api/admin/products \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

**Should Return:** ALL products (admin + vendor)

#### Test 3: Admin View - Products by Source (NEW!)
```bash
curl -X GET http://localhost:8082/api/admin/products/by-source \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

**Expected Response:**
```json
{
  "totalProducts": 50,
  "adminProducts": 20,
  "vendorProducts": 30,
  "adminProductList": [
    {
      "id": 1,
      "name": "Admin Product 1",
      "vendor": null
    }
  ],
  "vendorProductList": [
    {
      "id": 2,
      "name": "Vendor Product 1",
      "vendor": {
        "storeName": "Green Farm"
      }
    }
  ]
}
```

---

### Step 3: Full Workflow Test

#### 3.1 Register a Vendor
```bash
curl -X POST http://localhost:8082/api/vendor/register \
  -H "Authorization: Bearer USER_TOKEN" \
  -H "Content-Type: multipart/form-data" \
  -F "storeName=Test Store" \
  -F "description=Testing vendor products" \
  -F "businessEmail=testvendor@example.com" \
  -F "businessPhone=+1234567890" \
  -F "businessAddress=123 Test St"
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Vendor registration successful. Awaiting admin verification.",
  "vendorId": 10,
  "status": "PENDING"
}
```

#### 3.2 Admin Verifies Vendor
```bash
curl -X PUT http://localhost:8082/api/admin/vendors/10/verify \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"verified": true}'
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Vendor verified successfully",
  "vendor": {
    "id": 10,
    "isVerified": true
  }
}
```

#### 3.3 Vendor Adds a Product
```bash
curl -X POST http://localhost:8082/api/vendor/products \
  -H "Authorization: Bearer VENDOR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Vendor Product",
    "description": "This is a test product from vendor",
    "price": 19.99,
    "stock": 50,
    "category": {"id": 1},
    "brand": "TestBrand",
    "unit": "piece",
    "imageUrl": "https://example.com/product.jpg",
    "isAvailable": true
  }'
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Product added successfully",
  "product": {
    "id": 51,
    "name": "Test Vendor Product",
    "price": 19.99,
    "vendor": {
      "id": 10,
      "storeName": "Test Store"
    },
    "createdAt": "2025-10-11T..."
  }
}
```

#### 3.4 Verify Product Appears in Public List
```bash
curl http://localhost:8082/api/products | grep "Test Vendor Product"
```

**Expected:** Should find the product!

#### 3.5 Check Database
```sql
SELECT * FROM products WHERE name = 'Test Vendor Product';
```

**Expected Result:**
```
id: 51
name: Test Vendor Product
price: 19.99
vendor_id: 10  ← THIS CONFIRMS IT'S A VENDOR PRODUCT
is_available: 1
```

#### 3.6 Verify in Admin Panel
```bash
curl -X GET http://localhost:8082/api/admin/products/51 \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

**Expected Response:**
```json
{
  "product": {
    "id": 51,
    "name": "Test Vendor Product",
    "vendor": {
      "id": 10,
      "storeName": "Test Store"
    }
  },
  "source": "VENDOR",  ← CONFIRMS SOURCE
  "vendorInfo": {
    "id": 10,
    "storeName": "Test Store",
    "businessEmail": "testvendor@example.com"
  }
}
```

---

## 🔍 Troubleshooting

### Issue 1: Vendor Products NOT Appearing

#### Check 1: Is vendor verified?
```sql
SELECT id, store_name, is_verified FROM vendors WHERE id = 10;
```

**Fix if is_verified = 0:**
```bash
curl -X PUT http://localhost:8082/api/admin/vendors/10/verify \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"verified": true}'
```

#### Check 2: Is product marked as available?
```sql
SELECT id, name, is_available FROM products WHERE vendor_id = 10;
```

**Fix if is_available = 0:**
```sql
UPDATE products SET is_available = 1 WHERE id = 51;
```

#### Check 3: Check backend logs for errors
```bash
# Look for errors in Spring Boot console
tail -f /path/to/logs/spring.log | grep -i error
```

#### Check 4: Verify vendor_id is set correctly
```sql
-- This should return products
SELECT * FROM products WHERE vendor_id IS NOT NULL;

-- If empty, vendor products weren't saved correctly
```

---

### Issue 2: Products Appear in Database but NOT in API

#### Check 1: Verify ProductService is using findAll()
```java
// This should return ALL products
public List<Product> getAllProducts() {
    return productRepository.findAll();  // ← Should get ALL products
}
```

#### Check 2: Test the repository directly
```sql
-- Simulate what the API does
SELECT * FROM products;  -- Should return ALL products
```

#### Check 3: Check for JSON serialization issues
Look for circular reference errors in logs:
```
"Could not write JSON: Infinite recursion"
```

**Fix:** Add `@JsonIgnore` to prevent circular references

---

## 📊 Expected Results Summary

| Test | Expected Result |
|------|----------------|
| SQL query for all products | Shows both ADMIN and VENDOR products |
| `GET /api/products` | Returns products with `vendor: null` AND `vendor: {...}` |
| `GET /api/admin/products` | Returns ALL products |
| `GET /api/admin/products/by-source` | Shows separated admin/vendor counts |
| Vendor adds product → Check DB | Product has `vendor_id` set |
| Vendor adds product → Check API | Product appears in `/api/products` |

---

## ✅ Verification Checklist

Before completing testing, verify:

- [ ] Database has `products` table with `vendor_id` column
- [ ] Database has `vendors` table
- [ ] At least one vendor is verified (`is_verified = 1`)
- [ ] Vendor products exist in database (`SELECT * FROM products WHERE vendor_id IS NOT NULL`)
- [ ] `GET /api/products` returns vendor products
- [ ] `GET /api/admin/products` returns vendor products
- [ ] Vendor products have `vendor` object in JSON response
- [ ] Admin products have `vendor: null` in JSON response
- [ ] No errors in backend logs
- [ ] `GET /api/admin/products/by-source` shows correct counts

---

## 🎯 Quick Verification Commands

### Check if ANY vendor products exist:
```sql
SELECT COUNT(*) as vendor_product_count 
FROM products 
WHERE vendor_id IS NOT NULL;
```

**Expected:** Number > 0

### Check if vendor products are available:
```bash
curl http://localhost:8082/api/products | jq '[.[] | select(.vendor != null)] | length'
```

**Expected:** Number > 0

### Get vendor product IDs:
```sql
SELECT id, name, vendor_id FROM products WHERE vendor_id IS NOT NULL;
```

### Test specific vendor product in API:
```bash
curl http://localhost:8082/api/products/51  # Replace 51 with actual vendor product ID
```

---

## 🚀 Final Test Script (Copy & Paste)

```bash
#!/bin/bash

echo "==================================="
echo "TESTING VENDOR PRODUCT VISIBILITY"
echo "==================================="

echo ""
echo "1. Testing public products endpoint..."
curl -s http://localhost:8082/api/products | jq '. | length' | xargs echo "Total products:"

echo ""
echo "2. Counting vendor products..."
curl -s http://localhost:8082/api/products | jq '[.[] | select(.vendor != null)] | length' | xargs echo "Vendor products:"

echo ""
echo "3. Counting admin products..."
curl -s http://localhost:8082/api/products | jq '[.[] | select(.vendor == null)] | length' | xargs echo "Admin products:"

echo ""
echo "4. Sample vendor product:"
curl -s http://localhost:8082/api/products | jq '[.[] | select(.vendor != null)] | first'

echo ""
echo "==================================="
echo "TEST COMPLETE"
echo "==================================="
```

**Save as `test_vendor_products.sh` and run:**
```bash
chmod +x test_vendor_products.sh
./test_vendor_products.sh
```

---

## 📞 Support

If vendor products are STILL not visible after all checks:

1. Share backend logs during product creation
2. Share database schema: `DESCRIBE products;`
3. Share result of: `SELECT * FROM products WHERE vendor_id IS NOT NULL LIMIT 5;`
4. Share API response: `curl http://localhost:8082/api/products`

**The system IS configured correctly - if products aren't showing, there's a specific issue we can identify and fix!**

