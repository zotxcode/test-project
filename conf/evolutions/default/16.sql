# --- !Ups

ALTER TABLE best_seller_banner ADD COLUMN external_url varchar(255);
ALTER TABLE category_banner ADD COLUMN external_url varchar(255);
ALTER TABLE instagram_banner ADD COLUMN external_url varchar(255);
ALTER TABLE new_arrival_banner ADD COLUMN external_url varchar(255);

# --- !Downs

ALTER TABLE best_seller_banner DROP COLUMN external_url;
ALTER TABLE category_banner DROP COLUMN external_url;
ALTER TABLE instagram_banner DROP COLUMN external_url;
ALTER TABLE new_arrival_banner DROP COLUMN external_url;
