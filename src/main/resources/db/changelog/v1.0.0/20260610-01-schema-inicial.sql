-- liquibase formatted sql

-- ============================================================================
-- SPRINT 0: DATABASE SCHEMA FOR ERP/POS SYSTEM (POSTGRESQL)
-- ============================================================================

-- changeset jtinoco:1 context:prod,dev
-- comment: 1. TENANT & SECURITY SUBSYSTEM
CREATE SEQUENCE tenants_seq START 1 INCREMENT 50;
CREATE TABLE tenants (
    id BIGINT DEFAULT nextval('tenants_seq') PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL,
    tax_id VARCHAR(11) NOT NULL UNIQUE, 
    fiscal_address TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE SEQUENCE users_seq START 1 INCREMENT 50;
CREATE TABLE users (
    id BIGINT DEFAULT nextval('users_seq') PRIMARY KEY,
    tenant_id BIGINT REFERENCES tenants(id),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL, 
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE', 
    version INT NOT NULL DEFAULT 0, 
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- changeset jtinoco:2 context:prod,dev
-- comment: 2. MASTER CATALOGS
CREATE SEQUENCE customers_seq START 1 INCREMENT 50;
CREATE TABLE customers (
    id BIGINT DEFAULT nextval('customers_seq') PRIMARY KEY,
    tenant_id BIGINT REFERENCES tenants(id),
    tax_id VARCHAR(11) NOT NULL, 
    company_name VARCHAR(255) NOT NULL,
    fiscal_address TEXT NOT NULL,
    payment_term VARCHAR(100) NOT NULL, 
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT chk_customer_tax_id CHECK (length(tax_id) = 11)
);

CREATE SEQUENCE customer_contacts_seq START 1 INCREMENT 50;
CREATE TABLE customer_contacts (
    id BIGINT DEFAULT nextval('customer_contacts_seq') PRIMARY KEY,
    customer_id BIGINT REFERENCES customers(id) ON DELETE CASCADE,
    full_name VARCHAR(255) NOT NULL,
    role_position VARCHAR(100),
    phone VARCHAR(50),
    email VARCHAR(255)
);

CREATE SEQUENCE brands_seq START 1 INCREMENT 50;
CREATE TABLE brands (
    id BIGINT DEFAULT nextval('brands_seq') PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE SEQUENCE product_types_seq START 1 INCREMENT 50;
CREATE TABLE product_types (
    id BIGINT DEFAULT nextval('product_types_seq') PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE SEQUENCE product_families_seq START 1 INCREMENT 50;
CREATE TABLE product_families (
    id BIGINT DEFAULT nextval('product_families_seq') PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- changeset jtinoco:3 context:prod,dev
-- comment: 3. PRODUCTS & INVENTORY SUBSYSTEM
CREATE SEQUENCE products_seq START 1 INCREMENT 50;
CREATE TABLE products (
    id BIGINT DEFAULT nextval('products_seq') PRIMARY KEY,
    tenant_id BIGINT REFERENCES tenants(id),
    sku VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    brand_id BIGINT REFERENCES brands(id),
    type_id BIGINT REFERENCES product_types(id),
    family_id BIGINT REFERENCES product_families(id),
    expected_margin NUMERIC(5,2) NOT NULL DEFAULT 30.00,
    wholesale_price NUMERIC(12,4) NOT NULL DEFAULT 0.0000,
    retail_price NUMERIC(12,4) NOT NULL DEFAULT 0.0000,
    allow_negative_stock BOOLEAN NOT NULL DEFAULT FALSE,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE SEQUENCE product_variants_seq START 1 INCREMENT 50;
CREATE TABLE product_variants (
    id BIGINT DEFAULT nextval('product_variants_seq') PRIMARY KEY,
    product_id BIGINT REFERENCES products(id) ON DELETE CASCADE,
    size_name VARCHAR(50) NOT NULL,
    current_stock INT NOT NULL DEFAULT 0,
    reserved_stock INT NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 0,
    CONSTRAINT unique_product_size UNIQUE (product_id, size_name)
);

CREATE SEQUENCE suppliers_seq START 1 INCREMENT 50;
CREATE TABLE suppliers (
    id BIGINT DEFAULT nextval('suppliers_seq') PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    tax_id VARCHAR(11) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE'
);

CREATE SEQUENCE supplier_costs_seq START 1 INCREMENT 50;
CREATE TABLE supplier_costs (
    id BIGINT DEFAULT nextval('supplier_costs_seq') PRIMARY KEY,
    product_id BIGINT REFERENCES products(id) ON DELETE CASCADE,
    supplier_id BIGINT REFERENCES suppliers(id),
    purchase_price NUMERIC(12,4) NOT NULL,
    last_updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_product_supplier_cost UNIQUE (product_id, supplier_id)
);

CREATE SEQUENCE product_documents_seq START 1 INCREMENT 50;
CREATE TABLE product_documents (
    id BIGINT DEFAULT nextval('product_documents_seq') PRIMARY KEY,
    product_id BIGINT REFERENCES products(id) ON DELETE CASCADE,
    document_type VARCHAR(50) NOT NULL,
    file_url TEXT NOT NULL,
    expiration_date DATE,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE'
);

-- changeset jtinoco:4 context:prod,dev
-- comment: 4. COMMERCIAL SUBSYSTEM (QUOTES)
CREATE SEQUENCE quotes_seq START 1 INCREMENT 50;
CREATE TABLE quotes (
    id BIGINT DEFAULT nextval('quotes_seq') PRIMARY KEY,
    tenant_id BIGINT REFERENCES tenants(id),
    customer_id BIGINT REFERENCES customers(id),
    user_id BIGINT REFERENCES users(id),
    folio_number VARCHAR(50) NOT NULL UNIQUE,
    sub_total_amount NUMERIC(12,4) NOT NULL,
    tax_percentage_snapshot NUMERIC(5,2) NOT NULL DEFAULT 18.00,
    total_amount NUMERIC(12,4) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING', 
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE SEQUENCE quote_items_seq START 1 INCREMENT 50;
CREATE TABLE quote_items (
    id BIGINT DEFAULT nextval('quote_items_seq') PRIMARY KEY,
    quote_id BIGINT REFERENCES quotes(id) ON DELETE CASCADE,
    product_id BIGINT REFERENCES products(id),
    variant_id BIGINT REFERENCES product_variants(id),
    custom_description TEXT,
    quantity_quoted INT NOT NULL,
    quantity_accepted INT NOT NULL DEFAULT 0,
    unit_price NUMERIC(12,4) NOT NULL,
    total_price NUMERIC(12,4) NOT NULL
);

-- changeset jtinoco:5 context:prod,dev
-- comment: 5. TRAZABILIDAD DOCUMENTAL INDEPENDIENTE
CREATE SEQUENCE dispatch_documents_seq START 1 INCREMENT 50;
CREATE TABLE dispatch_documents (
    id BIGINT DEFAULT nextval('dispatch_documents_seq') PRIMARY KEY,
    quote_id BIGINT REFERENCES quotes(id),
    waybill_number VARCHAR(50) NOT NULL UNIQUE, 
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE', 
    issued_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE SEQUENCE dispatch_items_seq START 1 INCREMENT 50;
CREATE TABLE dispatch_items (
    id BIGINT DEFAULT nextval('dispatch_items_seq') PRIMARY KEY,
    dispatch_document_id BIGINT REFERENCES dispatch_documents(id) ON DELETE CASCADE,
    variant_id BIGINT REFERENCES product_variants(id),
    quantity_shipped INT NOT NULL
);

CREATE SEQUENCE billing_documents_seq START 1 INCREMENT 50;
CREATE TABLE billing_documents (
    id BIGINT DEFAULT nextval('billing_documents_seq') PRIMARY KEY,
    quote_id BIGINT REFERENCES quotes(id),
    invoice_number VARCHAR(50) NOT NULL UNIQUE, 
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE', 
    payment_date TIMESTAMP WITH TIME ZONE, 
    issued_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- changeset jtinoco:6 context:prod,dev
-- comment: 6. AUDIT & LEDGER (KARDEX INMUTABLE)
CREATE SEQUENCE inventory_ledger_seq START 1 INCREMENT 50;
CREATE TABLE inventory_ledger (
    id BIGINT DEFAULT nextval('inventory_ledger_seq') PRIMARY KEY,
    variant_id BIGINT REFERENCES product_variants(id),
    movement_type VARCHAR(10) NOT NULL, 
    quantity INT NOT NULL,
    resulting_stock INT NOT NULL, 
    reference_document_type VARCHAR(50) NOT NULL, 
    reference_document_id BIGINT NOT NULL, 
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- changeset jtinoco:7 context:prod,dev
-- comment: 7. PERFORMANCE INDEXES
CREATE INDEX idx_products_sku ON products(sku);
CREATE INDEX idx_product_variants_prod ON product_variants(product_id);
CREATE INDEX idx_supplier_costs_search ON supplier_costs(product_id, purchase_price);
CREATE INDEX idx_quote_items_quote ON quote_items(quote_id);
CREATE INDEX idx_inventory_ledger_variant ON inventory_ledger(variant_id, created_at DESC);
CREATE INDEX idx_dispatch_documents_quote ON dispatch_documents(quote_id);
CREATE INDEX idx_billing_documents_quote ON billing_documents(quote_id);