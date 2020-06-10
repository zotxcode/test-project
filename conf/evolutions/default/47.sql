# --- !Ups
ALTER TABLE sales_order ADD COLUMN tracking_number VARCHAR(255);


# --- !Downs
ALTER TABLE sales_order DROP COLUMN tracking_number;
