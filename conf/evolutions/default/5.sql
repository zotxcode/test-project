# --- !Ups

ALTER TABLE member ADD COLUMN brand_id bigint;

alter table member add constraint fk_member_brand_16 foreign key (brand_id) references brand (id);
create index ix_member_brand_16 on member (brand_id);

# --- !Downs

