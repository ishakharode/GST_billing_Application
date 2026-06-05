-- ============================================================
-- GST Billing Application - MySQL Schema
-- ============================================================

CREATE DATABASE IF NOT EXISTS gst_billing_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE gst_billing_db;

-- ============================================================
-- Table: businesses
-- ============================================================
CREATE TABLE IF NOT EXISTS businesses (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    business_name VARCHAR(255)  NOT NULL,
    gst_number    VARCHAR(15)   NOT NULL UNIQUE,
    address       TEXT,
    state         VARCHAR(100),
    phone         VARCHAR(20),
    created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_gst_length CHECK (CHAR_LENGTH(gst_number) = 15)
) ENGINE=InnoDB;

-- ============================================================
-- Table: products
-- ============================================================
CREATE TABLE IF NOT EXISTS products (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_name   VARCHAR(255) NOT NULL,
    price          DOUBLE       NOT NULL,
    gst_percentage DOUBLE       NOT NULL,
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_price_positive  CHECK (price > 0),
    CONSTRAINT chk_gst_allowed     CHECK (gst_percentage IN (0, 5, 12, 18, 28))
) ENGINE=InnoDB;

-- ============================================================
-- Table: invoices
-- ============================================================
CREATE TABLE IF NOT EXISTS invoices (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(50)  NOT NULL UNIQUE,
    invoice_date   DATE         NOT NULL,
    customer_name  VARCHAR(255),
    customer_state VARCHAR(100),
    gst_type       VARCHAR(20)  NOT NULL COMMENT 'CGST_SGST | IGST',
    total_amount   DOUBLE       NOT NULL DEFAULT 0,
    total_gst      DOUBLE       NOT NULL DEFAULT 0,
    grand_total    DOUBLE       NOT NULL DEFAULT 0,
    business_id    BIGINT       NOT NULL,
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_invoice_business FOREIGN KEY (business_id) REFERENCES businesses(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- Table: invoice_items
-- ============================================================
CREATE TABLE IF NOT EXISTS invoice_items (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_id   BIGINT  NOT NULL,
    product_id   BIGINT  NOT NULL,
    quantity     INT     NOT NULL,
    price        DOUBLE  NOT NULL,
    total_amount DOUBLE  NOT NULL DEFAULT 0,
    gst_amount   DOUBLE  NOT NULL DEFAULT 0,
    cgst_amount  DOUBLE           DEFAULT 0,
    sgst_amount  DOUBLE           DEFAULT 0,
    igst_amount  DOUBLE           DEFAULT 0,
    CONSTRAINT fk_item_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_item_product FOREIGN KEY (product_id) REFERENCES products(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- Sample Data (optional - for testing)
-- ============================================================

-- Insert sample business
INSERT INTO businesses (business_name, gst_number, address, state, phone) VALUES
('TechMart Pvt Ltd',    '27AABCT1332L1ZV', 'Andheri, Mumbai',      'Maharashtra', '9876543210'),
('Global Traders',      '24AAACG5555B1ZR', 'Satellite, Ahmedabad', 'Gujarat',     '9988776655');

-- Insert sample products
INSERT INTO products (product_name, price, gst_percentage) VALUES
('Laptop Core i5',  45000.00, 18),
('Wireless Mouse',   1200.00, 18),
('USB-C Hub',        2500.00, 12),
('HDMI Cable',        500.00,  5),
('Office Chair',    12000.00, 28),
('Notebook Pack',     350.00,  0);
