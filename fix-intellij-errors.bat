@echo off
echo ========================================
echo    Force Reload Maven in IntelliJ IDEA
echo ========================================
echo.
echo This script will help you fix the compilation errors.
echo.
echo The dependencies are already downloaded (Maven build succeeded),
echo but IntelliJ needs to reload them.
echo.
echo ========================================
echo STEP 1: Invalidate Caches and Restart
echo ========================================
echo.
echo In IntelliJ IDEA:
echo 1. Go to "File" ^> "Invalidate Caches..."
echo 2. Check ALL boxes:
echo    - [x] Invalidate and Restart
echo    - [x] Clear file system cache and Local History
echo    - [x] Clear downloaded shared indexes
echo 3. Click "Invalidate and Restart"
echo 4. Wait for IntelliJ to restart and reindex
echo.
echo ========================================
echo STEP 2: Reload Maven Project
echo ========================================
echo.
echo After IntelliJ restarts:
echo 1. Open Maven tool window (View ^> Tool Windows ^> Maven)
echo 2. Click "Reload All Maven Projects" (circular arrows icon)
echo.
echo OR simply press: Ctrl+Shift+O
echo.
echo ========================================
echo STEP 3: Reimport Project (If still not working)
echo ========================================
echo.
echo If errors persist:
echo 1. Right-click on pom.xml
echo 2. Select "Maven" ^> "Reimport"
echo.
echo ========================================
echo.
pause

