@echo off
echo ========================================
echo FIXING VENDOR PRODUCTS - STEP BY STEP
echo ========================================
echo.

echo Step 1: Checking current database state...
echo.

mysql -u root -pCoder@3570 ecommerce -e "SELECT '=== CURRENT PRODUCTS ===' as ''; SELECT id, name, vendor_id, CASE WHEN vendor_id IS NULL THEN '❌ NO VENDOR' ELSE '✅ HAS VENDOR' END as status FROM products LIMIT 10;"

echo.
echo Step 2: Checking if vendors exist...
echo.

mysql -u root -pCoder@3570 ecommerce -e "SELECT '=== VENDORS IN DATABASE ===' as ''; SELECT id, store_name, business_email, is_verified FROM vendors;"

echo.
echo Step 3: Creating default vendor if needed...
echo.

mysql -u root -pCoder@3570 ecommerce -e "INSERT IGNORE INTO vendors (store_name, business_email, business_phone, business_address, is_verified, is_active, created_at, updated_at) VALUES ('Default Store', 'store@example.com', '+1234567890', '123 Main St', 1, 1, NOW(), NOW());"

echo.
echo Step 4: Assigning vendor to all products...
echo.

mysql -u root -pCoder@3570 ecommerce -e "UPDATE products SET vendor_id = (SELECT id FROM vendors ORDER BY id LIMIT 1) WHERE vendor_id IS NULL;"

echo.
echo Step 5: Verifying the fix...
echo.

mysql -u root -pCoder@3570 ecommerce -e "SELECT '=== PRODUCTS NOW HAVE VENDORS ===' as ''; SELECT p.id, p.name, p.vendor_id, v.store_name as vendor_name FROM products p LEFT JOIN vendors v ON p.vendor_id = v.id LIMIT 10;"

echo.
echo Step 6: Counting results...
echo.

mysql -u root -pCoder@3570 ecommerce -e "SELECT '=== SUMMARY ===' as ''; SELECT COUNT(*) as total_products, SUM(CASE WHEN vendor_id IS NULL THEN 1 ELSE 0 END) as without_vendor, SUM(CASE WHEN vendor_id IS NOT NULL THEN 1 ELSE 0 END) as with_vendor FROM products;"

echo.
echo ========================================
echo ✅ DATABASE FIX COMPLETE!
echo ========================================
echo.
echo NEXT STEPS:
echo 1. Stop your Spring Boot backend (Ctrl+C)
echo 2. Restart it
echo 3. Test API: http://localhost:8082/api/products
echo 4. Check frontend: http://localhost:3000/products
echo.
pause

