@echo off
echo ========================================
echo TESTING API RESPONSE
echo ========================================
echo.
echo Testing: http://localhost:8082/api/products
echo.
echo Looking for vendor data in API response...
echo.

curl -s http://localhost:8082/api/products > temp_api_response.json

echo API Response saved to temp_api_response.json
echo.

findstr /i "vendor" temp_api_response.json

echo.
echo ========================================
echo Check above for vendor data!
echo ========================================
echo.
echo If you see "vendor":null - backend needs restart
echo If you see "vendor":{...} - SUCCESS! ✅
echo.
pause

