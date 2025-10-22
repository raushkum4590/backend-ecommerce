-- ========================================
-- VERIFY VENDOR PRODUCTS IN DATABASE
-- ========================================

-- 1. Check if vendors table exists and has data
SELECT 'VENDORS TABLE' as 'CHECK';
SELECT
    id,
    store_name,
    business_email,
    is_verified,
    is_active,
    created_at
FROM vendors
ORDER BY created_at DESC;

-- 2. Check if products table has vendor_id column
SELECT 'PRODUCTS TABLE STRUCTURE' as 'CHECK';
DESCRIBE products;

-- 3. View ALL products with their source (ADMIN vs VENDOR)
SELECT 'ALL PRODUCTS WITH SOURCE' as 'CHECK';
SELECT
    p.id,
    p.name,
    p.price,
    p.stock,
    p.is_available,
    CASE
        WHEN p.vendor_id IS NULL THEN 'ADMIN'
        ELSE 'VENDOR'
    END as source,
    v.store_name as vendor_name,
    v.business_email as vendor_email,
    p.created_at
FROM products p
LEFT JOIN vendors v ON p.vendor_id = v.id
ORDER BY p.created_at DESC
LIMIT 50;

-- 4. Count products by source
SELECT 'PRODUCT COUNT BY SOURCE' as 'CHECK';
SELECT
    CASE
        WHEN vendor_id IS NULL THEN 'ADMIN'
        ELSE 'VENDOR'
    END as source,
    COUNT(*) as total_products,
    SUM(CASE WHEN is_available = 1 THEN 1 ELSE 0 END) as available_products
FROM products
GROUP BY source;

-- 5. List ONLY vendor products
SELECT 'VENDOR PRODUCTS ONLY' as 'CHECK';
SELECT
    p.id,
    p.name,
    p.price,
    v.store_name as vendor_name,
    p.is_available,
    p.created_at
FROM products p
INNER JOIN vendors v ON p.vendor_id = v.id
ORDER BY p.created_at DESC;

-- 6. List ONLY admin products
SELECT 'ADMIN PRODUCTS ONLY' as 'CHECK';
SELECT
    id,
    name,
    price,
    is_available,
    created_at
FROM products
WHERE vendor_id IS NULL
ORDER BY created_at DESC;

-- 7. Check for any products with invalid vendor references
SELECT 'PRODUCTS WITH INVALID VENDOR REFERENCES' as 'CHECK';
SELECT
    p.id,
    p.name,
    p.vendor_id
FROM products p
LEFT JOIN vendors v ON p.vendor_id = v.id
WHERE p.vendor_id IS NOT NULL AND v.id IS NULL;

-- 8. Vendor products summary by vendor
SELECT 'PRODUCTS PER VENDOR' as 'CHECK';
SELECT
    v.id as vendor_id,
    v.store_name,
    v.business_email,
    v.is_verified,
    COUNT(p.id) as total_products,
    SUM(CASE WHEN p.is_available = 1 THEN 1 ELSE 0 END) as available_products
FROM vendors v
LEFT JOIN products p ON v.id = p.vendor_id
GROUP BY v.id, v.store_name, v.business_email, v.is_verified
ORDER BY total_products DESC;

-- 9. Recently added products (last 10)
SELECT 'RECENTLY ADDED PRODUCTS' as 'CHECK';
SELECT
    p.id,
    p.name,
    p.price,
    CASE
        WHEN p.vendor_id IS NULL THEN 'ADMIN'
        ELSE CONCAT('VENDOR: ', v.store_name)
    END as added_by,
    p.created_at
FROM products p
LEFT JOIN vendors v ON p.vendor_id = v.id
ORDER BY p.created_at DESC
LIMIT 10;

-- 10. Check if vendor products are available for purchase
SELECT 'VENDOR PRODUCTS AVAILABLE FOR PURCHASE' as 'CHECK';
SELECT
    p.id,
    p.name,
    p.price,
    p.stock,
    v.store_name as vendor_name,
    v.is_verified as vendor_verified,
    p.is_available as product_available
FROM products p
INNER JOIN vendors v ON p.vendor_id = v.id
WHERE p.is_available = 1 AND p.stock > 0
ORDER BY p.created_at DESC;

