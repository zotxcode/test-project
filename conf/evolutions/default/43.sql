# --- !Ups
ALTER TABLE user_cms ADD COLUMN is_distributor BOOLEAN DEFAULT FALSE;
ALTER TABLE user_cms ADD COLUMN province_id BIGINT;
ALTER TABLE user_cms ADD COLUMN city_id BIGINT;

# --- !Downs
ALTER TABLE user_cms DROP COLUMN is_distributor;
ALTER TABLE user_cms DROP COLUMN province_id;
ALTER TABLE user_cms DROP COLUMN city_id;
