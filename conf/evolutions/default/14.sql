# --- !Ups

ALTER TABLE product_detail ADD COLUMN feature TEXT;
ALTER TABLE product_detail ADD COLUMN specifications TEXT;
ALTER TABLE product_detail ADD COLUMN recomended_care TEXT;
ALTER TABLE product_detail ADD COLUMN waranty TEXT;

# --- !Downs

ALTER TABLE product_detail DROP COLUMN feature;
ALTER TABLE product_detail DROP COLUMN specifications;
ALTER TABLE product_detail DROP COLUMN recomended_care;
ALTER TABLE product_detail DROP COLUMN waranty;