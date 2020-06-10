# --- !Ups
ALTER TABLE sales_order ADD COLUMN reseller_id BIGINT;
ALTER TABLE sales_order ADD COLUMN token varchar(255);
ALTER TABLE sales_order ADD COLUMN redirect_url varchar(255);

# --- !Downs
ALTER TABLE sales_order DROP COLUMN reseller_id;
ALTER TABLE sales_order DROP COLUMN redirect_url;
ALTER TABLE sales_order DROP COLUMN token;
