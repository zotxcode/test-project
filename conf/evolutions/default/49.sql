# --- !Ups
ALTER TABLE instagram_banner ALTER COLUMN description TYPE TEXT;

# --- !Downs
ALTER TABLE instagram_banner ALTER COLUMN description TYPE VARCHAR(255);
