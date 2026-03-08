-- Restaurant Management System Database Schema
-- PostgreSQL

-- Drop tables if exist (in correct order due to foreign keys)
DROP TABLE IF EXISTS order_status_history CASCADE;
DROP TABLE IF EXISTS receipts CASCADE;
DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS meal_deal_items CASCADE;
DROP TABLE IF EXISTS meal_deals CASCADE;
DROP TABLE IF EXISTS menu_items CASCADE;
DROP TABLE IF EXISTS addresses CASCADE;
DROP TABLE IF EXISTS customers CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Users table (Manager, Waiter, Cashier)
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) CHECK (role IN ('MANAGER', 'WAITER', 'CASHIER')) NOT NULL,
    phone VARCHAR(15),
    email VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Customers table
CREATE TABLE customers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) UNIQUE NOT NULL,
    email VARCHAR(100),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Addresses table (One-to-Many with Customers)
CREATE TABLE addresses (
    id SERIAL PRIMARY KEY,
    customer_id INT REFERENCES customers(id) ON DELETE CASCADE,
    province VARCHAR(50) NOT NULL,
    city VARCHAR(50) NOT NULL,
    district VARCHAR(50),
    street_address TEXT NOT NULL,
    postal_code VARCHAR(10),
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Menu items table
CREATE TABLE menu_items (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    category VARCHAR(50) NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    preparation_time INT DEFAULT 15,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Meal deals table (for Many-to-Many relationship)
CREATE TABLE meal_deals (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    deal_price DECIMAL(10,2) NOT NULL CHECK (deal_price >= 0),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Join table for Many-to-Many relationship (MenuItem <-> MealDeal)
CREATE TABLE meal_deal_items (
    meal_deal_id INT REFERENCES meal_deals(id) ON DELETE CASCADE,
    menu_item_id INT REFERENCES menu_items(id) ON DELETE CASCADE,
    PRIMARY KEY (meal_deal_id, menu_item_id)
);

-- Orders table
CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    order_number VARCHAR(20) UNIQUE NOT NULL,
    customer_id INT REFERENCES customers(id),
    waiter_id INT REFERENCES users(id),
    table_number INT NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) DEFAULT 0,
    status VARCHAR(20) CHECK (status IN ('PENDING', 'CONFIRMED', 'PREPARING', 'READY', 'SERVED', 'PAID')) DEFAULT 'PENDING',
    payment_method VARCHAR(20) CHECK (payment_method IN ('CASH', 'CARD', 'MOBILE')),
    payment_status VARCHAR(20) CHECK (payment_status IN ('PENDING', 'PAID')) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Order items table (One-to-Many with Orders)
CREATE TABLE order_items (
    id SERIAL PRIMARY KEY,
    order_id INT REFERENCES orders(id) ON DELETE CASCADE,
    menu_item_id INT REFERENCES menu_items(id),
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10,2) NOT NULL CHECK (unit_price >= 0),
    subtotal DECIMAL(10,2) NOT NULL CHECK (subtotal >= 0),
    special_instructions TEXT
);

-- Receipts table (One-to-One with Orders)
CREATE TABLE receipts (
    id SERIAL PRIMARY KEY,
    order_id INT UNIQUE REFERENCES orders(id),
    receipt_number VARCHAR(50) UNIQUE NOT NULL,
    generated_by INT REFERENCES users(id),
    pdf_path VARCHAR(255),
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL,
    tax_amount DECIMAL(10,2),
    discount_amount DECIMAL(10,2)
);

-- Order status history table (for tracking status changes)
CREATE TABLE order_status_history (
    id SERIAL PRIMARY KEY,
    order_id INT REFERENCES orders(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL,
    changed_by INT REFERENCES users(id),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT
);

-- Create indexes for better query performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_customers_phone ON customers(phone);
CREATE INDEX idx_addresses_customer ON addresses(customer_id);
CREATE INDEX idx_addresses_province ON addresses(province);
CREATE INDEX idx_menu_items_category ON menu_items(category);
CREATE INDEX idx_menu_items_available ON menu_items(is_available);
CREATE INDEX idx_orders_customer ON orders(customer_id);
CREATE INDEX idx_orders_waiter ON orders(waiter_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_date ON orders(order_date);
CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_receipts_order ON receipts(order_id);

-- Insert sample data

-- Users
INSERT INTO users (username, password, full_name, role, phone, email) VALUES
('mugisha_manager', 'password123', 'Jean Paul Mugisha', 'MANAGER', '+250788123456', 'mugisha@restaurant.rw'),
('uwase_waiter', 'password123', 'Marie Uwase', 'WAITER', '+250788234567', 'uwase@restaurant.rw'),
('nkunda_waiter', 'password123', 'Eric Nkunda', 'WAITER', '+250788345678', 'nkunda@restaurant.rw'),
('mukamana_cashier', 'password123', 'Grace Mukamana', 'CASHIER', '+250788456789', 'mukamana@restaurant.rw');

-- Customers
INSERT INTO customers (name, phone, email, address) VALUES
('Patrick Habimana', '+250788567890', 'habimana@email.rw', 'KG 15 Ave, Kigali'),
('Claudine Uwera', '+250788678901', 'uwera@email.rw', 'KN 5 Rd, Kigali'),
('Samuel Niyonzima', '+250788789012', 'niyonzima@email.rw', 'Huye District');

-- Addresses
INSERT INTO addresses (customer_id, province, city, district, street_address, postal_code, is_default) VALUES
(1, 'Kigali', 'Kigali City', 'Gasabo', 'KG 15 Ave, Kimironko', 'KG001', TRUE),
(2, 'Kigali', 'Kigali City', 'Kicukiro', 'KN 5 Rd, Kicukiro', 'KG002', TRUE),
(3, 'Southern', 'Huye', 'Huye', 'Butare Town, Main Street', 'HY001', TRUE);

-- Menu Items (Traditional Rwandan dishes)
INSERT INTO menu_items (name, description, price, category, is_available, preparation_time) VALUES
('Isombe', 'Cassava leaves cooked with peanut sauce', 3500, 'Main Course', TRUE, 30),
('Brochettes', 'Grilled meat skewers (beef, goat, or chicken)', 5000, 'Main Course', TRUE, 25),
('Ugali', 'Cornmeal porridge served with vegetables', 2000, 'Main Course', TRUE, 20),
('Sambaza', 'Fried small fish from Lake Kivu', 4000, 'Appetizer', TRUE, 15),
('Ibirayi', 'Fried Irish potatoes', 1500, 'Side Dish', TRUE, 15),
('Ikivuguto', 'Traditional fermented milk', 1000, 'Beverage', TRUE, 5),
('Banana Juice', 'Fresh banana juice', 1500, 'Beverage', TRUE, 5),
('Matoke', 'Cooked plantains with sauce', 3000, 'Main Course', TRUE, 35);

-- Meal Deals
INSERT INTO meal_deals (name, description, deal_price, is_active) VALUES
('Family Combo', 'Brochettes, Ugali, Ibirayi, and 2 drinks', 12000, TRUE),
('Lunch Special', 'Isombe, Ibirayi, and Ikivuguto', 5000, TRUE);

-- Meal Deal Items (Many-to-Many)
INSERT INTO meal_deal_items (meal_deal_id, menu_item_id) VALUES
(1, 2), -- Family Combo: Brochettes
(1, 3), -- Family Combo: Ugali
(1, 5), -- Family Combo: Ibirayi
(1, 7), -- Family Combo: Banana Juice
(2, 1), -- Lunch Special: Isombe
(2, 5), -- Lunch Special: Ibirayi
(2, 6); -- Lunch Special: Ikivuguto

-- Sample Order
INSERT INTO orders (order_number, customer_id, waiter_id, table_number, total_amount, status, payment_status) VALUES
('ORD-001', 1, 2, 5, 9000, 'PENDING', 'PENDING');

-- Sample Order Items
INSERT INTO order_items (order_id, menu_item_id, quantity, unit_price, subtotal, special_instructions) VALUES
(1, 1, 2, 3500, 7000, 'Extra spicy'),
(1, 6, 2, 1000, 2000, NULL);

-- Query Examples

-- 1. Find all customers in Kigali province
SELECT DISTINCT c.* 
FROM customers c 
JOIN addresses a ON c.id = a.customer_id 
WHERE a.province = 'Kigali';

-- 2. Get all orders by a specific waiter
SELECT o.*, c.name as customer_name 
FROM orders o 
JOIN customers c ON o.customer_id = c.id 
WHERE o.waiter_id = 2 
ORDER BY o.order_date DESC;

-- 3. Get menu items by category with sorting
SELECT * FROM menu_items 
WHERE category = 'Main Course' AND is_available = TRUE 
ORDER BY price ASC;

-- 4. Get order details with items
SELECT o.order_number, o.total_amount, o.status,
       mi.name, oi.quantity, oi.unit_price, oi.subtotal
FROM orders o
JOIN order_items oi ON o.id = oi.order_id
JOIN menu_items mi ON oi.menu_item_id = mi.id
WHERE o.id = 1;

-- 5. Get meal deal with all items
SELECT md.name as deal_name, md.deal_price,
       mi.name as item_name, mi.price as item_price
FROM meal_deals md
JOIN meal_deal_items mdi ON md.id = mdi.meal_deal_id
JOIN menu_items mi ON mdi.menu_item_id = mi.id
WHERE md.id = 1;

-- 6. Check if username exists (existsBy implementation)
SELECT EXISTS(SELECT 1 FROM users WHERE username = 'uwase_waiter');

-- 7. Get customers with their addresses
SELECT c.name, c.phone, 
       a.province, a.city, a.district, a.street_address
FROM customers c
LEFT JOIN addresses a ON c.id = a.customer_id
ORDER BY c.name;

-- 8. Get receipt for an order (One-to-One relationship)
SELECT r.receipt_number, r.total_amount, r.tax_amount,
       o.order_number, u.full_name as cashier_name
FROM receipts r
JOIN orders o ON r.order_id = o.id
JOIN users u ON r.generated_by = u.id
WHERE o.id = 1;
