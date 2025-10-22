-- Multi-Vendor E-Commerce Database Setup
-- Run this script to ensure all tables are created properly

-- Create vendors table
CREATE TABLE IF NOT EXISTS vendors (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT UNIQUE NOT NULL,
    store_name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,
    business_license VARCHAR(255),
    tax_id VARCHAR(255),
    logo_url VARCHAR(500),
    banner_url VARCHAR(500),
    business_email VARCHAR(255),
    business_phone VARCHAR(50),
    business_address VARCHAR(500),
    is_verified BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    rating DOUBLE DEFAULT 0.0,
    review_count INT DEFAULT 0,
    commission DOUBLE DEFAULT 10.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Add vendor_id to products table (if not exists)
ALTER TABLE products
ADD COLUMN IF NOT EXISTS vendor_id BIGINT,
ADD CONSTRAINT fk_products_vendor
    FOREIGN KEY (vendor_id) REFERENCES vendors(id) ON DELETE SET NULL;

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_vendors_user ON vendors(user_id);
CREATE INDEX IF NOT EXISTS idx_vendors_verified ON vendors(is_verified);
CREATE INDEX IF NOT EXISTS idx_vendors_active ON vendors(is_active);
CREATE INDEX IF NOT EXISTS idx_products_vendor ON products(vendor_id);

-- Verify tables
SELECT 'vendors table created' AS status;
SELECT 'products table updated' AS status;

