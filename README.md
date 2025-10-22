# E-Commerce Grocery Application - Full Backend

A comprehensive, production-ready RESTful API for an e-commerce grocery store built with Spring Boot 3.

## 🚀 Features

### Core Features
- ✅ **User Authentication & Authorization** - JWT-based with role management (USER, ADMIN)
- ✅ **Product Management** - Full CRUD with categories, search, and filters
- ✅ **Shopping Cart** - Add, update, remove items with real-time summary
- ✅ **Order Management** - Complete order lifecycle with 8 status stages
- ✅ **Wishlist** - Save favorite products
- ✅ **Reviews & Ratings** - Product reviews with 5-star ratings
- ✅ **Address Management** - Multiple shipping addresses with default selection
- ✅ **Admin Dashboard** - Statistics, order management, user management

### Advanced Features
- 🔍 **Product Search** - Keyword-based search across name and description
- 🎯 **Product Filters** - Filter by category, price range, availability
- ⭐ **Featured Products** - Highlight special products
- 📊 **Top Rated Products** - Display best-reviewed items
- 🆕 **New Arrivals** - Show recently added products
- 💰 **Dynamic Pricing** - Discount support, tax calculation, shipping fees
- 📦 **Stock Management** - Automatic stock updates on orders
- 🔄 **Order Tracking** - Track orders with status updates and tracking numbers
- 🚫 **Order Cancellation** - Cancel orders with reason tracking

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Postman (for API testing)

## 🛠️ Setup Instructions

### 1. Database Setup

Create MySQL database:
```sql
CREATE DATABASE ecommerce;
```

### 2. Configure Application

Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
server.port=8082
```

### 3. Build & Run

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8082`

### 4. Load Sample Data (Optional)

Execute the `sample_data.sql` file to populate the database with sample products and categories.

## 📚 API Documentation

Comprehensive API documentation is available in [API_DOCUMENTATION.md](API_DOCUMENTATION.md)

### Quick Start Examples

#### 1. Register a User
```bash
POST http://localhost:8082/api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123"
}
```

#### 2. Login
```bash
POST http://localhost:8082/api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```

#### 3. Get Products
```bash
GET http://localhost:8082/api/products
```

#### 4. Add to Cart (Authenticated)
```bash
POST http://localhost:8082/api/cart/add
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "productId": 1,
  "quantity": 2
}
```

## 🗂️ Project Structure

```
src/main/java/com/example/demo/
├── controller/          # REST API Controllers
│   ├── AuthController.java
│   ├── ProductController.java
│   ├── CartController.java
│   ├── OrderController.java
│   ├── CategoryController.java
│   ├── ReviewController.java
│   ├── WishlistController.java
│   ├── AddressController.java
│   └── AdminController.java
├── entity/             # JPA Entities
│   ├── User.java
│   ├── Product.java
│   ├── Category.java
│   ├── Cart.java
│   ├── CartItem.java
│   ├── Order.java
│   ├── OrderItem.java
│   ├── Review.java
│   ├── Wishlist.java
│   ├── Address.java
│   ├── OrderStatus.java
│   └── PaymentStatus.java
├── repository/         # JPA Repositories
├── service/           # Business Logic
├── security/          # JWT & Security Config
├── dto/              # Data Transfer Objects
└── exception/        # Exception Handlers
```

## 🔐 Security

- **JWT Authentication** - Secure token-based authentication
- **BCrypt Password Encryption** - Passwords are securely hashed
- **Role-Based Access Control** - USER and ADMIN roles
- **Method-Level Security** - @PreAuthorize annotations for admin endpoints

## 📊 Database Schema

### Main Tables
- **users** - User accounts with roles
- **products** - Product catalog with pricing and stock
- **categories** - Product categories
- **cart_items** - Shopping cart entries
- **orders** - Customer orders with status tracking
- **order_items** - Individual items in orders
- **addresses** - User shipping addresses
- **reviews** - Product reviews and ratings
- **wishlist** - User wishlist items

### Relationships
- User → Many Addresses
- User → Many Orders
- User → Many Reviews
- User → Many Wishlist Items
- User → Many Cart Items
- Category → Many Products
- Product → Many Reviews
- Product → Many Cart Items
- Product → Many Order Items
- Order → Many Order Items

## 🎯 Key Endpoints Summary

| Category | Endpoint | Method | Auth Required |
|----------|----------|--------|---------------|
| Auth | /api/auth/register | POST | No |
| Auth | /api/auth/login | POST | No |
| Products | /api/products | GET | No |
| Products | /api/products/search | GET | No |
| Cart | /api/cart | GET | Yes |
| Cart | /api/cart/add | POST | Yes |
| Orders | /api/orders | POST | Yes |
| Wishlist | /api/wishlist | GET | Yes |
| Reviews | /api/reviews | POST | Yes |
| Admin | /api/admin/dashboard/stats | GET | Admin Only |

## 🧪 Testing

### Using Postman

1. Import the `Grocery_API_Collection.postman_collection.json` file
2. Set up environment variables:
   - `baseUrl`: http://localhost:8082
   - `token`: Your JWT token after login
3. Run the collection

### Test Flow

1. Register a new user
2. Login and save the JWT token
3. Browse products
4. Add products to cart
5. Create an address
6. Place an order
7. Track order status

## 🚀 Advanced Features

### 1. Product Search & Filter
```
GET /api/products/search?keyword=apple
GET /api/products/filter?categoryId=1&minPrice=0&maxPrice=50
```

### 2. Cart Summary with Calculations
```
GET /api/cart/summary
```
Returns:
- Total items count
- Subtotal
- Shipping fee (free over $50)
- Tax (8%)
- Grand total

### 3. Order Status Tracking
Orders go through these statuses:
- PENDING → CONFIRMED → PROCESSING → SHIPPED → OUT_FOR_DELIVERY → DELIVERED
- Can be CANCELLED or REFUNDED at any point

### 4. Admin Dashboard
```
GET /api/admin/dashboard/stats
```
Returns:
- Total revenue
- Total orders
- Total products
- Total users
- Pending orders count

## 🔧 Configuration Options

### Shipping Fee Logic
- Free shipping for orders ≥ $50
- $5 flat rate for orders < $50

### Tax Rate
- 8% tax on all orders

### Stock Management
- Automatic stock deduction on order placement
- Stock restoration on order cancellation

## 📝 Environment Variables

You can override properties using environment variables:
```
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/ecommerce
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=yourpassword
SERVER_PORT=8082
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 👨‍💻 Author

Your Name - Full Stack Developer

## 🙏 Acknowledgments

- Spring Boot Team
- JWT.io
- MySQL Community

## 📞 Support

For issues and questions:
- Email: support@grocery.com
- Documentation: [API_DOCUMENTATION.md](API_DOCUMENTATION.md)

---

**Happy Coding! 🎉**
-- Sample Data for E-Commerce Grocery Application

-- Insert Categories
INSERT INTO categories (name, description, image_url) VALUES
('Fruits', 'Fresh organic fruits', 'https://example.com/fruits.jpg'),
('Vegetables', 'Farm fresh vegetables', 'https://example.com/vegetables.jpg'),
('Dairy', 'Dairy products and milk', 'https://example.com/dairy.jpg'),
('Bakery', 'Fresh baked goods', 'https://example.com/bakery.jpg'),
('Beverages', 'Drinks and beverages', 'https://example.com/beverages.jpg'),
('Snacks', 'Snacks and munchies', 'https://example.com/snacks.jpg'),
('Meat & Seafood', 'Fresh meat and seafood', 'https://example.com/meat.jpg'),
('Pantry', 'Pantry essentials', 'https://example.com/pantry.jpg');

-- Insert Products
INSERT INTO products (name, category_id, price, stock, image_url, description, brand, unit, weight, is_available, is_featured, discount, average_rating, review_count, created_at, updated_at) VALUES
-- Fruits
('Fresh Apples', 1, 3.99, 150, 'https://example.com/apples.jpg', 'Crisp and sweet red apples', 'FreshFarms', 'kg', 1.0, true, true, 0, 4.5, 12, NOW(), NOW()),
('Organic Bananas', 1, 2.49, 200, 'https://example.com/bananas.jpg', 'Ripe organic bananas', 'OrganicChoice', 'bunch', 0.5, true, false, 10, 4.8, 25, NOW(), NOW()),
('Strawberries', 1, 5.99, 80, 'https://example.com/strawberries.jpg', 'Fresh juicy strawberries', 'BerryFresh', 'pack', 0.5, true, true, 0, 4.7, 18, NOW(), NOW()),
('Oranges', 1, 4.49, 120, 'https://example.com/oranges.jpg', 'Sweet Valencia oranges', 'CitrusGold', 'kg', 1.0, true, false, 0, 4.3, 8, NOW(), NOW()),

-- Vegetables
('Fresh Tomatoes', 2, 2.99, 180, 'https://example.com/tomatoes.jpg', 'Vine ripened tomatoes', 'VeggieFresh', 'kg', 1.0, true, false, 0, 4.4, 15, NOW(), NOW()),
('Organic Carrots', 2, 1.99, 150, 'https://example.com/carrots.jpg', 'Crunchy organic carrots', 'OrganicChoice', 'kg', 1.0, true, false, 15, 4.6, 10, NOW(), NOW()),
('Fresh Broccoli', 2, 3.49, 90, 'https://example.com/broccoli.jpg', 'Green broccoli heads', 'GreenVeg', 'piece', 0.5, true, true, 0, 4.5, 7, NOW(), NOW()),
('Baby Spinach', 2, 3.99, 100, 'https://example.com/spinach.jpg', 'Fresh baby spinach leaves', 'LeafyGreens', 'pack', 0.3, true, false, 0, 4.8, 12, NOW(), NOW()),

-- Dairy
('Whole Milk', 3, 4.99, 200, 'https://example.com/milk.jpg', 'Fresh whole milk 1 gallon', 'DairyBest', 'gallon', 3.8, true, false, 0, 4.7, 30, NOW(), NOW()),
('Greek Yogurt', 3, 5.49, 120, 'https://example.com/yogurt.jpg', 'Creamy Greek yogurt', 'GreekDelight', 'pack', 0.5, true, false, 20, 4.9, 22, NOW(), NOW()),
('Cheddar Cheese', 3, 6.99, 80, 'https://example.com/cheese.jpg', 'Sharp cheddar cheese block', 'CheeseFactory', 'pack', 0.5, true, true, 0, 4.6, 18, NOW(), NOW()),
('Butter', 3, 4.49, 150, 'https://example.com/butter.jpg', 'Salted butter sticks', 'DairyBest', 'pack', 0.5, true, false, 0, 4.5, 14, NOW(), NOW()),

-- Bakery
('Whole Wheat Bread', 4, 3.49, 100, 'https://example.com/bread.jpg', 'Fresh whole wheat bread loaf', 'BakeryFresh', 'loaf', 0.6, true, false, 0, 4.6, 20, NOW(), NOW()),
('Croissants', 4, 5.99, 60, 'https://example.com/croissants.jpg', 'Buttery French croissants', 'ParisianBakery', 'pack', 0.4, true, true, 0, 4.8, 15, NOW(), NOW()),
('Bagels', 4, 4.99, 80, 'https://example.com/bagels.jpg', 'Mixed variety bagels', 'BagelHouse', 'pack', 0.5, true, false, 10, 4.4, 12, NOW(), NOW()),

-- Beverages
('Orange Juice', 5, 5.99, 150, 'https://example.com/oj.jpg', 'Fresh squeezed orange juice', 'TropicalJuice', 'liter', 1.0, true, false, 0, 4.7, 25, NOW(), NOW()),
('Green Tea', 5, 3.99, 200, 'https://example.com/tea.jpg', 'Organic green tea bags', 'TeaTime', 'pack', 0.1, true, true, 0, 4.9, 35, NOW(), NOW()),
('Mineral Water', 5, 1.99, 300, 'https://example.com/water.jpg', 'Natural mineral water bottle', 'PureWater', 'liter', 1.0, true, false, 0, 4.5, 18, NOW(), NOW()),

-- Snacks
('Mixed Nuts', 6, 7.99, 100, 'https://example.com/nuts.jpg', 'Roasted mixed nuts', 'NuttyDelight', 'pack', 0.5, true, true, 15, 4.8, 28, NOW(), NOW()),
('Potato Chips', 6, 3.49, 150, 'https://example.com/chips.jpg', 'Classic salted potato chips', 'CrunchyChips', 'pack', 0.2, true, false, 0, 4.3, 22, NOW(), NOW()),
('Granola Bars', 6, 5.99, 120, 'https://example.com/granola.jpg', 'Healthy granola bars variety pack', 'HealthySnack', 'pack', 0.4, true, false, 20, 4.6, 16, NOW(), NOW()),

-- Meat & Seafood
('Chicken Breast', 7, 8.99, 80, 'https://example.com/chicken.jpg', 'Fresh boneless chicken breast', 'FreshMeat', 'kg', 1.0, true, false, 0, 4.7, 15, NOW(), NOW()),
('Salmon Fillet', 7, 12.99, 50, 'https://example.com/salmon.jpg', 'Atlantic salmon fillet', 'SeaFresh', 'kg', 0.5, true, true, 0, 4.9, 20, NOW(), NOW()),

-- Pantry
('Pasta', 8, 2.99, 200, 'https://example.com/pasta.jpg', 'Italian pasta penne', 'ItaliaPasta', 'pack', 0.5, true, false, 0, 4.5, 18, NOW(), NOW()),
('Rice', 8, 9.99, 150, 'https://example.com/rice.jpg', 'Basmati rice 5kg bag', 'GrainMaster', 'pack', 5.0, true, false, 10, 4.8, 30, NOW(), NOW()),
('Olive Oil', 8, 11.99, 100, 'https://example.com/oil.jpg', 'Extra virgin olive oil', 'MediterraneanGold', 'liter', 1.0, true, true, 0, 4.9, 25, NOW(), NOW());

-- Insert Sample Admin User (password: admin123)
INSERT INTO users (username, email, password, role, is_active, created_at) VALUES
('admin', 'admin@grocery.com', '$2a$10$xgZx/7eH7vQ8qPqXPqXPqOKOqXPqXPqXPqXPqXPqXPqXPqXPqXPqX', 'ADMIN', true, NOW());

-- Insert Sample Regular User (password: user123)
INSERT INTO users (username, email, password, role, is_active, created_at) VALUES
('john_doe', 'john@example.com', '$2a$10$xgZx/7eH7vQ8qPqXPqXPqOKOqXPqXPqXPqXPqXPqXPqXPqXPqXPqX', 'USER', true, NOW());

-- Note: The passwords above are placeholders. Use BCrypt to generate actual password hashes
-- You can use the registration endpoint to create users with properly hashed passwords

