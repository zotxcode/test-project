# --- !Ups

ALTER TABLE voucher ADD COLUMN can_be_combined integer;

# --- !Downs

ALTER TABLE voucher DROP COLUMN can_be_combined;