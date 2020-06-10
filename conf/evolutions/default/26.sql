# --- !Ups

ALTER TABLE product_review ADD COLUMN reviewer VARCHAR(255) default NULL;

# --- !Downs

ALTER TABLE product_review DROP COLUMN reviewer;