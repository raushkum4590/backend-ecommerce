@echo off
echo ========================================
echo ADMIN VENDORS ENDPOINT TEST
echo ========================================
echo.

echo This script will test the admin vendor endpoints.
echo You need an ADMIN token to test these endpoints.
echo.
echo ----------------------------------------
echo Step 1: Testing PUBLIC vendors endpoint (should fail with 403/401)
echo ----------------------------------------
curl -v http://localhost:8082/api/vendors 2>&1 | findstr "403\|401\|404"
echo.
echo This should return 403 or 404 - endpoint doesn't exist or is protected
echo.

echo ----------------------------------------
echo Step 2: Testing ADMIN vendors endpoint WITHOUT token (should fail)
echo ----------------------------------------
curl -v http://localhost:8082/api/admin/vendors 2>&1 | findstr "403\|401"
echo.
echo This should return 401 or 403 - endpoint requires authentication
echo.

echo ----------------------------------------
echo Step 3: Get Admin Token
echo ----------------------------------------
echo.
echo To test with authentication, you need to:
echo 1. Login as admin: admin@gmail.com / admin123
echo 2. Copy the JWT token from the response
echo 3. Run: curl -H "Authorization: Bearer YOUR_TOKEN" http://localhost:8082/api/admin/vendors
echo.

echo ----------------------------------------
echo Testing with Admin Login
echo ----------------------------------------
echo Attempting to login as admin...
curl -X POST http://localhost:8082/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"admin@gmail.com\",\"password\":\"admin123\"}" > admin_login.json 2>&1

echo.
echo Login response saved to admin_login.json
type admin_login.json
echo.

echo ----------------------------------------
echo Extracting Token (if login successful)
echo ----------------------------------------
echo Check admin_login.json for the token
echo Look for: "token": "eyJ..."
echo.

echo ========================================
echo SUMMARY
echo ========================================
echo.
echo The correct endpoint for admin to get vendors is:
echo   GET http://localhost:8082/api/admin/vendors
echo.
echo Required header:
echo   Authorization: Bearer YOUR_ADMIN_TOKEN
echo.
echo Frontend Fix Required:
echo   1. Change endpoint from /api/vendors to /api/admin/vendors
echo   2. Add Authorization header with admin token
echo   3. Ensure user is logged in as admin
echo.
echo ========================================
pause

