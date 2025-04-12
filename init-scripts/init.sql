-- Create customers table
CREATE TABLE IF NOT EXISTS customers (
    customer_id VARCHAR(50) PRIMARY KEY,
    customer_type VARCHAR(20) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    email VARCHAR(255),
    phone_number VARCHAR(20),
    tenure_months INTEGER,
    risk_category VARCHAR(20),
    kyc_status VARCHAR(20),
    country_of_residence VARCHAR(50),
    last_login_time TIMESTAMP,
    account_creation_date TIMESTAMP,
    has_previous_fraud BOOLEAN DEFAULT FALSE,
    activity_level INTEGER
    );

-- Create merchants table
CREATE TABLE IF NOT EXISTS merchants (
    merchant_id VARCHAR(50) PRIMARY KEY,
    merchant_name VARCHAR(255) NOT NULL,
    merchant_category VARCHAR(100),
    merchant_country VARCHAR(50),
    risk_level VARCHAR(20),
    is_high_risk BOOLEAN DEFAULT FALSE,
    tenure_days INTEGER,
    fraud_rate_percentage FLOAT
    );

-- Create transactions table
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id VARCHAR(50) PRIMARY KEY,
    account_id VARCHAR(50) NOT NULL,
    customer_id VARCHAR(50) NOT NULL,
    merchant_id VARCHAR(50) NOT NULL,
    merchant_name VARCHAR(255),
    merchant_category VARCHAR(100),
    amount DECIMAL(19, 4) NOT NULL,
    currency VARCHAR(3),
    card_type VARCHAR(50),
    payment_method VARCHAR(50),
    transaction_type VARCHAR(50),
    transaction_status VARCHAR(20),
    transaction_date TIMESTAMP NOT NULL,
    device_id VARCHAR(100),
    ip_address VARCHAR(45),
    location VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES customers (customer_id),
    CONSTRAINT fk_merchant FOREIGN KEY (merchant_id) REFERENCES merchants (merchant_id)
    );

-- Create indexes for performance
CREATE INDEX idx_transactions_customer_id ON transactions (customer_id);
CREATE INDEX idx_transactions_merchant_id ON transactions (merchant_id);
CREATE INDEX idx_transactions_date ON transactions (transaction_date);
CREATE INDEX idx_transactions_customer_date ON transactions (customer_id, transaction_date);

-- Sample data for testing

-- Insert sample customers
INSERT INTO customers (customer_id, customer_type, first_name, last_name, email, phone_number, tenure_months, risk_category, kyc_status, country_of_residence, last_login_time, account_creation_date, has_previous_fraud, activity_level)
VALUES
    ('C1001', 'INDIVIDUAL', 'John', 'Doe', 'john.doe@example.com', '+1234567890', 36, 'LOW', 'VERIFIED', 'US', NOW() - INTERVAL '2 HOURS', NOW() - INTERVAL '3 YEARS', FALSE, 8),
    ('C1002', 'INDIVIDUAL', 'Jane', 'Smith', 'jane.smith@example.com', '+1987654321', 24, 'MEDIUM', 'VERIFIED', 'CA', NOW() - INTERVAL '1 DAY', NOW() - INTERVAL '2 YEARS', FALSE, 6),
    ('C1003', 'BUSINESS', 'Acme', 'Corp', 'contact@acmecorp.com', '+1122334455', 18, 'LOW', 'VERIFIED', 'UK', NOW() - INTERVAL '5 DAYS', NOW() - INTERVAL '1.5 YEARS', FALSE, 9),
    ('C1004', 'INDIVIDUAL', 'Robert', 'Johnson', 'robert.j@example.com', '+1555666777', 6, 'HIGH', 'PENDING', 'DE', NOW() - INTERVAL '3 DAYS', NOW() - INTERVAL '6 MONTHS', TRUE, 3),
    ('C1005', 'INDIVIDUAL', 'Alice', 'Wong', 'alice.wong@example.com', '+1333444555', 12, 'MEDIUM', 'VERIFIED', 'SG', NOW() - INTERVAL '12 HOURS', NOW() - INTERVAL '1 YEAR', FALSE, 7);

-- Insert sample merchants
INSERT INTO merchants (merchant_id, merchant_name, merchant_category, merchant_country, risk_level, is_high_risk, tenure_days, fraud_rate_percentage)
VALUES
    ('M2001', 'Amazon', 'RETAIL', 'US', 'LOW', FALSE, 1825, 0.05),
    ('M2002', 'Netflix', 'ENTERTAINMENT', 'US', 'LOW', FALSE, 1460, 0.02),
    ('M2003', 'Local Restaurant', 'FOOD', 'CA', 'MEDIUM', FALSE, 365, 0.1),
    ('M2004', 'Online Casino', 'GAMBLING', 'MT', 'HIGH', TRUE, 730, 1.2),
    ('M2005', 'Digital Downloads', 'DIGITAL_GOODS', 'LU', 'HIGH', TRUE, 45, 2.5),
    ('M2006', 'Travel Agency', 'TRAVEL', 'UK', 'MEDIUM', FALSE, 912, 0.3),
    ('M2007', 'Electronics Store', 'ELECTRONICS', 'US', 'LOW', FALSE, 1095, 0.08);

-- Insert sample transactions
INSERT INTO transactions (transaction_id, account_id, customer_id, merchant_id, merchant_name, merchant_category, amount, currency, card_type, payment_method, transaction_type, transaction_status, transaction_date, device_id, ip_address, location)
VALUES
    ('T3001', 'A5001', 'C1001', 'M2001', 'Amazon', 'RETAIL', 99.99, 'USD', 'VISA', 'CARD', 'PURCHASE', 'COMPLETED', NOW() - INTERVAL '2 DAYS', 'D1001', '192.168.1.1', 'US:NEW_YORK:40.7128:-74.0060'),
    ('T3002', 'A5001', 'C1001', 'M2002', 'Netflix', 'ENTERTAINMENT', 14.99, 'USD', 'MASTERCARD', 'RECURRING', 'SUBSCRIPTION', 'COMPLETED', NOW() - INTERVAL '15 DAYS', 'D1001', '192.168.1.1', 'US:NEW_YORK:40.7128:-74.0060'),
    ('T3003', 'A5002', 'C1002', 'M2003', 'Local Restaurant', 'FOOD', 45.50, 'CAD', 'AMEX', 'CARD', 'PURCHASE', 'COMPLETED', NOW() - INTERVAL '3 DAYS', 'D1002', '203.0.113.1', 'CA:TORONTO:43.6532:-79.3832'),
    ('T3004', 'A5003', 'C1003', 'M2006', 'Travel Agency', 'TRAVEL', 1250.00, 'GBP', 'VISA', 'CARD', 'PURCHASE', 'COMPLETED', NOW() - INTERVAL '7 DAYS', 'D1003', '198.51.100.1', 'UK:LONDON:51.5074:-0.1278'),
    ('T3005', 'A5004', 'C1004', 'M2004', 'Online Casino', 'GAMBLING', 500.00, 'EUR', 'MASTERCARD', 'CARD', 'PURCHASE', 'COMPLETED', NOW() - INTERVAL '1 DAY', 'D1004', '198.51.100.123', 'DE:BERLIN:52.5200:13.4050'),
    ('T3006', 'A5005', 'C1005', 'M2007', 'Electronics Store', 'ELECTRONICS', 899.99, 'SGD', 'VISA', 'CARD', 'PURCHASE', 'COMPLETED', NOW() - INTERVAL '5 DAYS', 'D1005', '203.0.113.42', 'SG:SINGAPORE:1.3521:103.8198'),
    ('T3007', 'A5001', 'C1001', 'M2001', 'Amazon', 'RETAIL', 25.50, 'USD', 'VISA', 'CARD', 'PURCHASE', 'COMPLETED', NOW() - INTERVAL '30 DAYS', 'D1001', '192.168.1.1', 'US:NEW_YORK:40.7128:-74.0060'),
    ('T3008', 'A5001', 'C1001', 'M2002', 'Netflix', 'ENTERTAINMENT', 14.99, 'USD', 'MASTERCARD', 'RECURRING', 'SUBSCRIPTION', 'COMPLETED', NOW() - INTERVAL '45 DAYS', 'D1001', '192.168.1.1', 'US:NEW_YORK:40.7128:-74.0060'),
    ('T3009', 'A5004', 'C1004', 'M2005', 'Digital Downloads', 'DIGITAL_GOODS', 29.99, 'EUR', 'MASTERCARD', 'ONLINE', 'PURCHASE', 'COMPLETED', NOW() - INTERVAL '2 DAYS', 'D1006', '203.0.113.45', 'DE:MUNICH:48.1351:11.5820'),
    ('T3010', 'A5003', 'C1003', 'M2007', 'Electronics Store', 'ELECTRONICS', 1299.99, 'GBP', 'AMEX', 'CARD', 'PURCHASE', 'COMPLETED', NOW() - INTERVAL '10 DAYS', 'D1003', '198.51.100.1', 'UK:LONDON:51.5074:-0.1278');

-- Create user with proper permissions if needed
-- CREATE USER transaction_service WITH PASSWORD 'your-password';
-- GRANT SELECT, INSERT, UPDATE ON customers, merchants, transactions TO transaction_service;