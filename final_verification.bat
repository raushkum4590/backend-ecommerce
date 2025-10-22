@echo off
echo ========================================
echo FINAL VENDOR DISPLAY VERIFICATION
echo ========================================
echo.

echo [1/3] Testing Backend API...
echo ----------------------------------------
curl -s http://localhost:8082/api/products > final_test.json

echo Checking for vendor data in API response...
findstr /i "storeName" final_test.json > nul
if errorlevel 1 (
    echo ❌ Backend NOT returning vendor data
    echo.
    echo Fix: Restart Spring Boot backend
    goto :end
) else (
    echo ✅ Backend returning vendor data correctly
    echo.
    type final_test.json | findstr /i /c:"storeName" /c:"businessEmail"
)

echo.
echo [2/3] Sample Product with Vendor:
echo ----------------------------------------
type final_test.json

echo.
echo [3/3] Frontend Checklist:
echo ========================================
echo.
echo Next steps to verify frontend display:
echo.
echo 1. Open browser: http://localhost:3000/products
echo.
echo 2. Press F12 to open Console
echo.
echo 3. Look for these logs:
echo    📦 Products fetched: 1
echo    🏪 Vendor info: {id: 3, storeName: "hh"}
echo    ✅ Product has vendor - badge will display
echo.
echo 4. Visual check - You should see:
echo    ┌─────────────────────────┐
echo    │ Fresh Milk              │
echo    │ 🟣 Sold by: hh         │ ← Purple badge
echo    │ Farm fresh milk         │
echo    │ $45.00                  │
echo    └─────────────────────────┘
echo.
echo ========================================
echo.
echo ✅ If backend shows vendor data above:
echo    - Frontend code is ready
echo    - Badge should display automatically
echo.
echo ❌ If badge still not showing:
echo    1. Hard refresh: Ctrl + Shift + R
echo    2. Clear Next.js cache: rm -r .next (in ecommerce dir)
echo    3. Restart Next.js: npm run dev
echo.
echo ========================================
echo.

:end
pause

