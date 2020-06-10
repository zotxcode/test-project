# --- !Ups
ALTER TABLE shipping_address ADD COLUMN province_id bigint;
ALTER TABLE shipping_address ADD COLUMN city_id bigint;
ALTER TABLE shipping_address ADD COLUMN postal_code varchar(255);

# --- !Downs
ALTER TABLE shipping_address DROP COLUMN province_id;
ALTER TABLE shipping_address DROP COLUMN city_id;
ALTER TABLE shipping_address DROP COLUMN postal_code;
