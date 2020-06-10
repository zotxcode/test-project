# --- !Ups

ALTER TABLE product_detail ADD COLUMN thumbnail_youtube varchar(255);
ALTER TABLE product_detail ADD COLUMN url_youtube varchar(255);

# --- !Downs


