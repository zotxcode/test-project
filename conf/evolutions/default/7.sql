# --- !Ups

ALTER TABLE member_log ADD COLUMN brand_id bigint;

alter table member_log add constraint fk_member_log_brand_16 foreign key (brand_id) references brand (id);
create index ix_member_log_brand_16 on member_log (brand_id);

# --- !Downs

