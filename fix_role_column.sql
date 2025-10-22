-- FIX: Data truncated for column 'role' at row 1
-- This script fixes the role column size issue

USE ecommerce;

-- Check current role column definition
DESCRIBE users;

-- Alter the role column to accommodate 'VENDOR' (and future roles)
ALTER TABLE users MODIFY COLUMN role VARCHAR(20);

-- Verify the change
DESCRIBE users;

-- Check if there are any users with problematic roles
SELECT id, email, role FROM users;

-- Now the role column can store: USER, ADMIN, VENDOR, and future roles

