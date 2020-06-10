# --- !Ups
ALTER TABLE promotion ADD COLUMN image_url varchar(255);

# --- !Downs
ALTER TABLE promotion DROP COLUMN image_url;
