# --- !Ups
ALTER TABLE sales_order ADD COLUMN payment_service integer DEFAULT 0;

# --- !Downs
ALTER TABLE sales_order DROP COLUMN payment_service;