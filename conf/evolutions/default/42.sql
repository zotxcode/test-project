# --- !Ups
ALTER TABLE sales_order ADD COLUMN payment_description varchar(255);
ALTER TABLE sales_order ADD COLUMN va_number varchar(255);

# --- !Downs
ALTER TABLE sales_order DROP COLUMN payment_description;
ALTER TABLE sales_order DROP COLUMN va_number;
