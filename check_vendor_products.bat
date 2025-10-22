@echo off
echo ===============================================
echo VENDOR PRODUCTS DATABASE VERIFICATION
echo ===============================================
echo.

echo Connecting to MySQL database...
echo.

mysql -u root -pCoder@3570 ecommerce -e "SELECT 'CHECKING VENDOR PRODUCTS IN DATABASE' as '';"
echo.

echo ----------------------------------------
echo 1. ALL PRODUCTS WITH SOURCE
echo ----------------------------------------
mysql -u root -pCoder@3570 ecommerce -e "SELECT p.id, p.name, p.price, CASE WHEN p.vendor_id IS NULL THEN 'ADMIN' ELSE 'VENDOR' END as source, v.store_name as vendor_name FROM products p LEFT JOIN vendors v ON p.vendor_id = v.id ORDER BY p.created_at DESC LIMIT 20;"
echo.

echo ----------------------------------------
echo 2. COUNT BY SOURCE
echo ----------------------------------------
mysql -u root -pCoder@3570 ecommerce -e "SELECT CASE WHEN vendor_id IS NULL THEN 'ADMIN' ELSE 'VENDOR' END as source, COUNT(*) as total_products FROM products GROUP BY source;"
echo.

echo ----------------------------------------
echo 3. VENDOR PRODUCTS ONLY
echo ----------------------------------------
mysql -u root -pCoder@3570 ecommerce -e "SELECT p.id, p.name, p.price, v.store_name as vendor_name, p.is_available FROM products p INNER JOIN vendors v ON p.vendor_id = v.id ORDER BY p.created_at DESC LIMIT 10;"
echo.

echo ----------------------------------------
echo 4. ADMIN PRODUCTS ONLY
echo ----------------------------------------
mysql -u root -pCoder@3570 ecommerce -e "SELECT id, name, price, is_available FROM products WHERE vendor_id IS NULL ORDER BY created_at DESC LIMIT 10;"
echo.

echo ----------------------------------------
echo 5. VENDORS SUMMARY
echo ----------------------------------------
mysql -u root -pCoder@3570 ecommerce -e "SELECT v.id, v.store_name, v.is_verified, COUNT(p.id) as total_products FROM vendors v LEFT JOIN products p ON v.id = p.vendor_id GROUP BY v.id, v.store_name, v.is_verified;"
echo.

echo ===============================================
echo VERIFICATION COMPLETE
echo ===============================================
echo.
echo INTERPRETATION:
echo - If you see VENDOR products above, they ARE in the database
echo - If vendor_id is NOT NULL, it's a vendor product
echo - If vendor_id IS NULL, it's an admin product
echo.
pause

