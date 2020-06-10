# --- !Ups
ALTER TABLE member ADD COLUMN province_id bigint;
ALTER TABLE member ADD COLUMN city_id bigint;
ALTER TABLE member ADD COLUMN address varchar(255);
ALTER TABLE member ADD COLUMN refferal_id bigint;

# --- !Downs
ALTER TABLE member DROP COLUMN province_id;
ALTER TABLE member DROP COLUMN city_id;
ALTER TABLE member DROP COLUMN address;
ALTER TABLE member DROP COLUMN refferal_id;


