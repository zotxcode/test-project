# --- !Ups
ALTER TABLE sales_order ADD COLUMN is_manual BOOLEAN DEFAULT FALSE;
ALTER TABLE sales_order ADD COLUMN status_reseller VARCHAR(255);


# --- !Downs
ALTER TABLE sales_order DROP COLUMN is_manual;
ALTER TABLE sales_order DROP COLUMN status_reseller;