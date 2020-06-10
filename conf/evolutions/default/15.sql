# --- !Ups

ALTER TABLE banner ADD COLUMN external_url TEXT;

# --- !Downs

ALTER TABLE banner DROP COLUMN external_url;
