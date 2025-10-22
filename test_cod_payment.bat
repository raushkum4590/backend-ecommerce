@echo off
REM Test Cash on Delivery (COD) Feature
echo ============================================
echo    TESTING CASH ON DELIVERY FEATURE
echo ============================================
echo.

REM Set your admin token here after logging in
set ADMIN_TOKEN=YOUR_ADMIN_TOKEN_HERE

echo Step 1: Testing COD Order Creation
echo -----------------------------------
echo POST /api/orders with paymentMethod: cash
echo.
echo Expected: Order created with CASH_ON_DELIVERY status
echo.

curl -X POST http://localhost:8082/api/orders ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer YOUR_USER_TOKEN" ^
  -d "{\"addressId\": 1, \"paymentMethod\": \"cash\"}"

echo.
echo.
pause

echo Step 2: Get All Orders to Find COD Order ID
echo -------------------------------------------
echo GET /api/admin/orders
echo.

curl -X GET http://localhost:8082/api/admin/orders ^
  -H "Authorization: Bearer %ADMIN_TOKEN%"

echo.
echo.
echo Note: Copy the ID of a COD order from above
pause

echo Step 3: Mark COD Order as Paid
echo --------------------------------
set /p ORDER_ID="Enter the COD Order ID: "
echo.
echo POST /api/admin/orders/%ORDER_ID%/mark-cod-paid
echo.

curl -X POST http://localhost:8082/api/admin/orders/%ORDER_ID%/mark-cod-paid ^
  -H "Authorization: Bearer %ADMIN_TOKEN%"

echo.
echo.
echo ============================================
echo         COD TESTING COMPLETE
echo ============================================
echo.
echo Check results above:
echo - Order should be created with paymentStatus: CASH_ON_DELIVERY
echo - After marking as paid, status should be: COMPLETED
echo.
pause

