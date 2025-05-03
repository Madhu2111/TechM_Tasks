-- Create default admin user if not exists
INSERT IGNORE INTO entity_users (username, email, password)
VALUES ('admin', 'admin@ecommerce.com', '$2a$10$EqWWNXxgZqOmqpFUXW6adOYXbhwAElQYVIgUz7oJkWWjZAQ9mJE0a');

-- Insert roles if they don't exist
INSERT IGNORE INTO roles (name) VALUES ('ROLE_USER');
INSERT IGNORE INTO roles (name) VALUES ('ROLE_ADMIN');

-- Assign admin role to admin user
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM entity_users u, roles r
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN';

-- Update existing products with valid created_at and updated_at values if they are null
UPDATE products SET created_at = CURRENT_TIMESTAMP WHERE created_at IS NULL;
UPDATE products SET updated_at = CURRENT_TIMESTAMP WHERE updated_at IS NULL;

-- Update existing orders with valid created_at values if they are null
UPDATE entity_orders SET created_at = CURRENT_TIMESTAMP WHERE created_at IS NULL;

-- Password is 'admin123' encrypted with BCrypt