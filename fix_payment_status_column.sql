-- Fix payment_status column to support CASH_ON_DELIVERY enum value
USE ecommerce;

-- Alter the payment_status column to support longer enum values
ALTER TABLE orders MODIFY COLUMN payment_status VARCHAR(50);

-- Show the updated column definition
SHOW COLUMNS FROM orders LIKE 'payment_status';

