@echo off
REM Fix Address Null Error - Test Script
echo ============================================
echo    FIX: Address ID Null Error
echo ============================================
echo.

REM Get user token
set /p USER_TOKEN="Enter your USER token: "
echo.

echo Step 1: Checking if user has addresses
echo ========================================
echo GET /api/addresses
echo.

curl -X GET http://localhost:8082/api/addresses ^
  -H "Authorization: Bearer %USER_TOKEN%"

echo.
echo.
echo Check the response above:
echo - If you see [], it means NO ADDRESSES EXIST
echo - If you see address objects, note the "id" field
echo.
pause

echo.
echo Step 2: Create a new address (if needed)
echo =========================================
set /p CREATE_ADDRESS="Do you want to create a new address? (y/n): "

if /i "%CREATE_ADDRESS%"=="y" (
    echo.
    echo Creating default address...
    curl -X POST http://localhost:8082/api/addresses ^
      -H "Content-Type: application/json" ^
      -H "Authorization: Bearer %USER_TOKEN%" ^
      -d "{\"street\":\"123 Main Street\",\"city\":\"New York\",\"state\":\"NY\",\"zipCode\":\"10001\",\"country\":\"USA\",\"phoneNumber\":\"1234567890\",\"isDefault\":true}"

    echo.
    echo.
    echo Address created! Note the "id" from the response above.
    echo.
    pause
)

echo.
echo Step 3: Place COD Order with Address
echo ======================================
set /p ADDRESS_ID="Enter the address ID to use (from Step 1 or 2): "
echo.
echo Creating COD order with addressId: %ADDRESS_ID%
echo.

curl -X POST http://localhost:8082/api/orders ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer %USER_TOKEN%" ^
  -d "{\"addressId\":%ADDRESS_ID%,\"paymentMethod\":\"cash\"}"

echo.
echo.
echo ============================================
echo         COD ORDER TEST COMPLETE
echo ============================================
echo.
echo Check the response above:
echo - Success: You should see order details with paymentStatus: CASH_ON_DELIVERY
echo - Error: Check the error message
echo.
pause

