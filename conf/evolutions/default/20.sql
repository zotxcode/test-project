# --- !Ups

ALTER TABLE voucher_sign_up DROP COLUMN periode;
ALTER TABLE voucher_sign_up DROP COLUMN prefix_kupon;
ALTER TABLE voucher_sign_up DROP COLUMN client_id;
ALTER TABLE voucher_sign_up DROP COLUMN besaran_voucher;
ALTER TABLE voucher_sign_up DROP COLUMN minimum_belanja;

ALTER TABLE voucher_sign_up ADD COLUMN periode integer;
ALTER TABLE voucher_sign_up ADD COLUMN prefix_kupon VARCHAR(10);
ALTER TABLE voucher_sign_up ADD COLUMN client_id VARCHAR(50);
ALTER TABLE voucher_sign_up ADD COLUMN besaran_voucher float;
ALTER TABLE voucher_sign_up ADD COLUMN minimum_belanja float;

# --- !Downs


