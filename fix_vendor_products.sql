-- ========================================
-- FIX: Assign Vendors to Products
-- ========================================
-- This script fixes the issue where vendor is NULL in API response

USE ecommerce;

-- ========================================
-- STEP 1: Check Current State
-- ========================================

SELECT '=== CURRENT PRODUCT STATE ===' as '';

SELECT
    id,
    name,
    price,
    vendor_id,
    CASE
        WHEN vendor_id IS NULL THEN '❌ NO VENDOR'
        ELSE '✅ HAS VENDOR'
    END as status
FROM products
LIMIT 10;

SELECT '=== PRODUCTS WITHOUT VENDOR ===' as '';
SELECT COUNT(*) as products_without_vendor FROM products WHERE vendor_id IS NULL;

-- ========================================
-- STEP 2: Check if Vendors Exist
-- ========================================

SELECT '=== EXISTING VENDORS ===' as '';
SELECT id, store_name, business_email, is_verified, is_active FROM vendors;

-- ========================================
-- STEP 3: Create Default Vendor (if needed)
-- ========================================

-- Check if any vendor exists
SELECT '=== CHECKING FOR VENDORS ===' as '';
SELECT COUNT(*) as vendor_count FROM vendors;

-- If no vendors exist, create one
INSERT INTO vendors (
    store_name,
    business_email,
    business_phone,
    business_address,
    is_verified,
    is_active,
    created_at,
    updated_at
)
SELECT
    'Default Store',
    'store@example.com',
    '+1234567890',
    '123 Main Street',
    1,
    1,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM vendors LIMIT 1);

-- Get the vendor ID (first vendor)
SET @vendor_id = (SELECT id FROM vendors ORDER BY id ASC LIMIT 1);

SELECT '=== USING VENDOR ===' as '';
SELECT
    @vendor_id as vendor_id,
    store_name,
    business_email
FROM vendors WHERE id = @vendor_id;

-- ========================================
-- STEP 4: Assign Vendor to Products
-- ========================================

SELECT '=== ASSIGNING VENDOR TO PRODUCTS ===' as '';

-- Update products that don't have a vendor
UPDATE products
SET vendor_id = @vendor_id
WHERE vendor_id IS NULL;

-- Show how many were updated
SELECT ROW_COUNT() as products_updated;

-- ========================================
-- STEP 5: Verify the Fix
-- ========================================

SELECT '=== VERIFICATION - PRODUCTS NOW HAVE VENDORS ===' as '';

SELECT
    p.id,
    p.name,
    p.price,
    p.vendor_id,
    v.store_name as vendor_name,
    '✅ FIXED' as status
FROM products p
LEFT JOIN vendors v ON p.vendor_id = v.id
LIMIT 10;

SELECT '=== FINAL COUNT ===' as '';
SELECT
    COUNT(*) as total_products,
    SUM(CASE WHEN vendor_id IS NULL THEN 1 ELSE 0 END) as without_vendor,
    SUM(CASE WHEN vendor_id IS NOT NULL THEN 1 ELSE 0 END) as with_vendor
FROM products;

-- ========================================
-- STEP 6: Test Query (What API Should Return)
-- ========================================

SELECT '=== API TEST - SAMPLE PRODUCT WITH VENDOR ===' as '';

SELECT
    p.id,
    p.name,
    p.price,
    p.stock,
    p.description,
    v.id as vendor_id,
    v.store_name as vendor_store_name,
    v.business_email as vendor_email,
    'This is what API should return now!' as note
FROM products p
INNER JOIN vendors v ON p.vendor_id = v.id
LIMIT 1;

SELECT '=== ✅ FIX COMPLETE ===' as '';
SELECT 'Now restart your Spring Boot backend and check API response' as next_step;

