-- Fix foreign key constraint issue in orders table
-- This script cleans up orphaned records and ensures data integrity

USE ecommerce;

-- Step 1: Check for orphaned orders (orders with user_id not in users table)
SELECT COUNT(*) as orphaned_orders
FROM orders o
LEFT JOIN users u ON o.user_id = u.id
WHERE u.id IS NULL;

-- Step 2: Option A - Delete orphaned orders (RECOMMENDED)
-- Uncomment the line below to delete orphaned records
DELETE FROM orders WHERE user_id NOT IN (SELECT id FROM users);

-- Step 3: Option B - Assign orphaned orders to a default admin user (if exists)
-- First, check if admin user exists
-- SELECT id FROM users WHERE role = 'ADMIN' LIMIT 1;
-- UPDATE orders SET user_id = (SELECT id FROM users WHERE role = 'ADMIN' LIMIT 1)
-- WHERE user_id NOT IN (SELECT id FROM users);

-- Step 4: Drop existing foreign key if it exists (to recreate it)
SET @foreign_key_name = (
    SELECT CONSTRAINT_NAME
    FROM information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = 'ecommerce'
    AND TABLE_NAME = 'orders'
    AND COLUMN_NAME = 'user_id'
    AND REFERENCED_TABLE_NAME = 'users'
    LIMIT 1
);

SET @sql = IF(@foreign_key_name IS NOT NULL,
    CONCAT('ALTER TABLE orders DROP FOREIGN KEY ', @foreign_key_name),
    'SELECT "No foreign key to drop"');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Step 5: Verify all user_id values in orders exist in users table
SELECT
    CASE
        WHEN COUNT(*) = 0 THEN 'All orders have valid user_id references'
        ELSE CONCAT('WARNING: ', COUNT(*), ' orphaned orders found!')
    END as status
FROM orders o
LEFT JOIN users u ON o.user_id = u.id
WHERE u.id IS NULL;

-- Step 6: Recreate the foreign key constraint
ALTER TABLE orders
ADD CONSTRAINT FK32ql8ubntj5uh44ph9659tiih
FOREIGN KEY (user_id) REFERENCES users (id);

-- Step 7: Verify the constraint was created
SELECT
    CONSTRAINT_NAME,
    TABLE_NAME,
    COLUMN_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'ecommerce'
AND TABLE_NAME = 'orders'
AND COLUMN_NAME = 'user_id';

