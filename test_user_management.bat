@echo off
REM Test User Management Features in Admin Panel
echo ============================================
echo    ADMIN USER MANAGEMENT TESTING
echo ============================================
echo.

REM Get admin token
set /p ADMIN_TOKEN="Enter your ADMIN token: "
echo.

echo ============================================
echo Step 1: Get All Users
echo ============================================
echo GET /api/admin/users
echo.

curl -X GET http://localhost:8082/api/admin/users ^
  -H "Authorization: Bearer %ADMIN_TOKEN%"

echo.
echo.
pause

echo ============================================
echo Step 2: Search Users
echo ============================================
set /p SEARCH_QUERY="Enter search term (username or email): "
echo.
echo GET /api/admin/users/search?query=%SEARCH_QUERY%
echo.

curl -X GET "http://localhost:8082/api/admin/users/search?query=%SEARCH_QUERY%" ^
  -H "Authorization: Bearer %ADMIN_TOKEN%"

echo.
echo.
pause

echo ============================================
echo Step 3: Get User by ID
echo ============================================
set /p USER_ID="Enter user ID: "
echo.
echo GET /api/admin/users/%USER_ID%
echo.

curl -X GET http://localhost:8082/api/admin/users/%USER_ID% ^
  -H "Authorization: Bearer %ADMIN_TOKEN%"

echo.
echo.
pause

echo ============================================
echo Step 4: Get User by Email
echo ============================================
set /p USER_EMAIL="Enter user email: "
echo.
echo GET /api/admin/users/email/%USER_EMAIL%
echo.

curl -X GET http://localhost:8082/api/admin/users/email/%USER_EMAIL% ^
  -H "Authorization: Bearer %ADMIN_TOKEN%"

echo.
echo.
pause

echo ============================================
echo Step 5: Get User's Order History
echo ============================================
echo Using email: %USER_EMAIL%
echo.
echo GET /api/admin/users/email/%USER_EMAIL%/orders
echo.

curl -X GET http://localhost:8082/api/admin/users/email/%USER_EMAIL%/orders ^
  -H "Authorization: Bearer %ADMIN_TOKEN%"

echo.
echo.
pause

echo ============================================
echo Step 6: User Management Actions
echo ============================================
echo.
echo What would you like to do?
echo 1. Activate a user
echo 2. Deactivate a user
echo 3. Reset user password
echo 4. Skip
echo.
set /p ACTION="Enter choice (1-4): "

if "%ACTION%"=="1" (
    set /p USER_ID_ACTION="Enter user ID to activate: "
    echo.
    echo PUT /api/admin/users/!USER_ID_ACTION!/activate
    curl -X PUT http://localhost:8082/api/admin/users/!USER_ID_ACTION!/activate ^
      -H "Authorization: Bearer %ADMIN_TOKEN%"
    echo.
)

if "%ACTION%"=="2" (
    set /p USER_ID_ACTION="Enter user ID to deactivate: "
    echo.
    echo PUT /api/admin/users/!USER_ID_ACTION!/deactivate
    curl -X PUT http://localhost:8082/api/admin/users/!USER_ID_ACTION!/deactivate ^
      -H "Authorization: Bearer %ADMIN_TOKEN%"
    echo.
)

if "%ACTION%"=="3" (
    set /p USER_ID_ACTION="Enter user ID for password reset: "
    set /p NEW_PASSWORD="Enter new password: "
    echo.
    echo PUT /api/admin/users/!USER_ID_ACTION!/reset-password
    curl -X PUT http://localhost:8082/api/admin/users/!USER_ID_ACTION!/reset-password ^
      -H "Content-Type: application/json" ^
      -H "Authorization: Bearer %ADMIN_TOKEN%" ^
      -d "{\"newPassword\":\"!NEW_PASSWORD!\"}"
    echo.
)

echo.
pause

echo ============================================
echo Step 7: Get Dashboard Statistics
echo ============================================
echo GET /api/admin/dashboard/stats
echo.

curl -X GET http://localhost:8082/api/admin/dashboard/stats ^
  -H "Authorization: Bearer %ADMIN_TOKEN%"

echo.
echo.
pause

echo ============================================
echo Step 8: Get Comprehensive Statistics
echo ============================================
echo GET /api/admin/stats/comprehensive
echo.

curl -X GET http://localhost:8082/api/admin/stats/comprehensive ^
  -H "Authorization: Bearer %ADMIN_TOKEN%"

echo.
echo.
echo ============================================
echo     USER MANAGEMENT TESTING COMPLETE
echo ============================================
echo.
echo All user management features have been tested!
echo.
echo Available Features:
echo - View all users
echo - Search users by name/email
echo - View user details
echo - View user order history
echo - Activate/Deactivate users
echo - Reset user passwords
echo - View user statistics
echo.
pause

