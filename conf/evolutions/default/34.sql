# --- !Ups

ALTER TABLE member 
RENAME COLUMN gramedia_signup_respon TO enwie_signup_respon;


# --- !Downs

ALTER TABLE member 
RENAME COLUMN enwie_signup_respon TO gramedia_signup_respon;