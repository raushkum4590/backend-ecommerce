-- ========================================
-- FIX ADMIN ROLE FOR VENDORS ENDPOINT
-- ========================================

USE ecommerce;

-- Check current user role
SELECT 'Current admin user role:' as '';
SELECT id, email, username, role FROM users WHERE email = 'admin@gmail.com';

-- Update user role to ADMIN if it's not
UPDATE users
SET role = 'ADMIN'
WHERE email = 'admin@gmail.com';

-- Verify the fix
SELECT 'After fix - admin user role:' as '';
SELECT id, email, username, role FROM users WHERE email = 'admin@gmail.com';

SELECT '========================================' as '';
SELECT 'Admin role fixed! Now logout and login again in frontend to get new token with ADMIN role.' as '';
SELECT '========================================' as '';

