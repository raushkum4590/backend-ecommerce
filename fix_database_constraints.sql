-- Fix database foreign key constraint issues
-- Run this script in MySQL to clean up orphaned records

USE ecommerce;

-- Disable foreign key checks temporarily
SET FOREIGN_KEY_CHECKS = 0;

-- Delete orders with invalid user_id (not in users table)
DELETE FROM orders
WHERE user_id IS NOT NULL
  AND user_id NOT IN (SELECT id FROM users);

-- Delete order_items for deleted orders
DELETE FROM order_items
WHERE order_id NOT IN (SELECT id FROM orders);

-- Delete cart_items with invalid user_id
DELETE FROM cart_items
WHERE user_id IS NOT NULL
  AND user_id NOT IN (SELECT id FROM users);

-- Delete reviews with invalid user_id
DELETE FROM reviews
WHERE user_id IS NOT NULL
  AND user_id NOT IN (SELECT id FROM users);

-- Delete wishlist with invalid user_id
DELETE FROM wishlist
WHERE user_id IS NOT NULL
  AND user_id NOT IN (SELECT id FROM users);

-- Delete addresses with invalid user_id
DELETE FROM addresses
WHERE user_id IS NOT NULL
  AND user_id NOT IN (SELECT id FROM users);

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- Verify no orphaned records remain
SELECT 'Orders with invalid user_id:' AS check_name, COUNT(*) AS count
FROM orders
WHERE user_id IS NOT NULL AND user_id NOT IN (SELECT id FROM users)
UNION ALL
SELECT 'Cart items with invalid user_id:', COUNT(*)
FROM cart_items
WHERE user_id IS NOT NULL AND user_id NOT IN (SELECT id FROM users)
UNION ALL
SELECT 'Reviews with invalid user_id:', COUNT(*)
FROM reviews
WHERE user_id IS NOT NULL AND user_id NOT IN (SELECT id FROM users)
UNION ALL
SELECT 'Wishlist with invalid user_id:', COUNT(*)
FROM wishlist
WHERE user_id IS NOT NULL AND user_id NOT IN (SELECT id FROM users)
UNION ALL
SELECT 'Addresses with invalid user_id:', COUNT(*)
FROM addresses
WHERE user_id IS NOT NULL AND user_id NOT IN (SELECT id FROM users);

SELECT '✅ Database cleanup complete!' AS status;

