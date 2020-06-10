# --- !Ups
create table payment_expiration (
  id                        bigint not null,
  is_deleted                boolean,
  type                      varchar(255),
  total                     integer,
  brand_id                  bigint,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_payment_expiration primary key (id))
;

create sequence payment_expiration_seq;

alter table payment_expiration add constraint fk_payment_expiration_brand_16 foreign key (brand_id) references brand (id);
create index ix_payment_expiration_brand_16 on payment_expiration (brand_id);
alter table payment_expiration add constraint fk_payment_expiration_userCms_18 foreign key (user_id) references user_cms (id);
create index ix_payment_expiration_userCms_18 on payment_expiration (user_id);

# --- !Downs

drop table if exists payment_expiration cascade;

drop sequence if exists payment_expiration_seq;
