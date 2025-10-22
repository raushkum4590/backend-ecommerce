-- SIMPLE FIX: Clean up orphaned orders before adding foreign key constraint
-- Run this script in MySQL Workbench or command line

USE ecommerce;

-- 1. First, let's see if there are any orphaned orders
SELECT 'Checking for orphaned orders...' as step;
SELECT o.id, o.user_id
FROM orders o
LEFT JOIN users u ON o.user_id = u.id
WHERE u.id IS NULL
LIMIT 10;

-- 2. Delete orphaned orders (orders with invalid user_id)
DELETE FROM orders
WHERE user_id NOT IN (SELECT id FROM users);

-- 3. Show how many orders remain
SELECT COUNT(*) as total_orders FROM orders;

-- 4. Verify all orders now have valid user references
SELECT
    CASE
        WHEN COUNT(*) = 0 THEN '✓ All orders have valid user_id'
        ELSE CONCAT('✗ Still ', COUNT(*), ' orphaned orders!')
    END as validation_result
FROM orders o
LEFT JOIN users u ON o.user_id = u.id
WHERE u.id IS NULL;

SELECT 'Database cleaned successfully! Now restart your Spring Boot application.' as message;

