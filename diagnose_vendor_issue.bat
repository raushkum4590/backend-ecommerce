@echo off
echo ========================================
echo COMPLETE VENDOR PRODUCTS DIAGNOSIS
echo ========================================
echo.

echo [1/5] Checking Database...
echo ----------------------------------------
mysql -u root -pCoder@3570 ecommerce -e "SELECT 'Database Check:' as ''; SELECT COUNT(*) as total_products, SUM(CASE WHEN vendor_id IS NULL THEN 1 ELSE 0 END) as without_vendor, SUM(CASE WHEN vendor_id IS NOT NULL THEN 1 ELSE 0 END) as with_vendor FROM products;"

echo.
echo [2/5] Showing Sample Products in Database...
echo ----------------------------------------
mysql -u root -pCoder@3570 ecommerce -e "SELECT p.id, p.name, p.price, p.vendor_id, v.store_name FROM products p LEFT JOIN vendors v ON p.vendor_id = v.id LIMIT 5;"

echo.
echo [3/5] Testing Backend API...
echo ----------------------------------------
echo Calling: http://localhost:8082/api/products
curl -s http://localhost:8082/api/products > api_test.json
echo Response saved to api_test.json
echo.
echo Checking for vendor data:
findstr /i "vendor" api_test.json | findstr /v "null"
if errorlevel 1 (
    echo ❌ NO VENDOR DATA IN API RESPONSE!
    echo Problem: Backend is returning vendor as null
) else (
    echo ✅ Vendor data found in API response
)

echo.
echo [4/5] Sample API Response (First Product):
echo ----------------------------------------
type api_test.json | findstr /i /c:"\"id\"" /c:"\"name\"" /c:"\"vendor\"" | more

echo.
echo [5/5] Diagnosis Summary...
echo ========================================
echo.

mysql -u root -pCoder@3570 ecommerce -e "SELECT COUNT(*) FROM products WHERE vendor_id IS NOT NULL;" > vendor_count.txt
set /p vendor_count=<vendor_count.txt

if "%vendor_count%"=="0" (
    echo ❌ ISSUE: No products have vendor_id in database
    echo.
    echo FIX: Run fix_vendors_now.bat to assign vendors to products
) else (
    echo ✅ Database has products with vendors
    echo.
    echo Checking API response...
    findstr /i "storeName" api_test.json > nul
    if errorlevel 1 (
        echo ❌ ISSUE: API not returning vendor data
        echo.
        echo FIX:
        echo 1. Make sure you updated Product.java with FetchType.EAGER
        echo 2. Restart Spring Boot backend
        echo 3. Run test_api_response.bat again
    ) else (
        echo ✅ API returning vendor data correctly
        echo.
        echo If frontend still not showing vendors:
        echo 1. Check frontend is calling http://localhost:8082/api/products
        echo 2. Check browser console for errors (F12)
        echo 3. Verify frontend code is reading product.vendor field
    )
)

echo.
echo ========================================
echo Full API response saved to: api_test.json
echo Open this file to see complete response
echo ========================================
echo.
pause

