-- SQL script to update existing users with email column
-- Run this in your MySQL database if you have existing users

-- Check if email column exists, if not it will be added automatically by Hibernate
-- But if you have existing users without emails, you need to add emails manually

-- Option 1: Add email to existing users (replace with actual emails)
-- UPDATE user SET email = CONCAT(username, '@example.com') WHERE email IS NULL OR email = '';

-- Option 2: Delete all existing users and start fresh
-- TRUNCATE TABLE user;

-- To see all users in your database:
SELECT * FROM user;

