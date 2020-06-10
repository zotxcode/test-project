# --- !Ups

ALTER TABLE product ADD COLUMN article_code VARCHAR(255);
ALTER TABLE product_detail ADD COLUMN color_image_urls TEXT;

# --- !Downs


