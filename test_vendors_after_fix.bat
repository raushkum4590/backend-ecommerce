@echo off
echo ========================================
echo TEST ADMIN VENDORS AFTER FIX
echo ========================================
echo.
echo Make sure you restarted Spring Boot backend!
echo.

echo [1/2] Login as Admin...
echo ----------------------------------------
curl -s -X POST http://localhost:8082/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"admin@gmail.com\",\"password\":\"admin123\"}" > admin_token.json

for /f "tokens=2 delims=:, " %%a in ('type admin_token.json ^| findstr "token"') do set TOKEN=%%a
set TOKEN=%TOKEN:"=%

echo Admin token obtained
echo.

echo [2/2] Testing /api/admin/vendors endpoint...
echo ----------------------------------------
curl -s -H "Authorization: Bearer %TOKEN%" http://localhost:8082/api/admin/vendors

echo.
echo.
echo ========================================
echo EXPECTED RESULT:
echo ========================================
echo You should see vendor data like:
echo [
echo   {
echo     "id": 3,
echo     "storeName": "hh",
echo     "businessEmail": "rt@gmail.com",
echo     "isVerified": true,
echo     "isActive": true
echo   }
echo ]
echo.
echo If you see vendors above, the backend is FIXED! ✅
echo.
echo Next step: Update your frontend to use:
echo   - Endpoint: /api/admin/vendors (not /api/vendors)
echo   - Header: Authorization: Bearer ADMIN_TOKEN
echo.
pause

