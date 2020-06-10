# --- !Ups
ALTER TABLE member ADD COLUMN is_reseller boolean default false;
ALTER TABLE member ADD COLUMN become_reseller_at timestamp;
ALTER TABLE member ADD COLUMN total_refferal bigint default 0;

# --- !Downs
ALTER TABLE member DROP COLUMN is_reseller;
ALTER TABLE member DROP COLUMN become_reseller_at;
ALTER TABLE member DROP COLUMN total_refferal;

