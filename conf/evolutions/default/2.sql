# --- !Ups

ALTER TABLE config_settings ALTER COLUMN value TYPE text;

create table on_sale (
  id                        bigint not null,
  is_deleted                boolean,
  status                    boolean,
  sequence                  integer,
  brand_id                  bigint,
  product_id                bigint,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_on_sale primary key (id))
;

create sequence on_sale_seq;

alter table on_sale add constraint fk_on_sale_brand_16 foreign key (brand_id) references brand (id);
create index ix_on_sale_brand_16 on on_sale (brand_id);
alter table on_sale add constraint fk_on_sale_product_17 foreign key (product_id) references product (id);
create index ix_on_sale_product_17 on on_sale (product_id);
alter table on_sale add constraint fk_on_sale_userCms_18 foreign key (user_id) references user_cms (id);
create index ix_on_sale_userCms_18 on on_sale (user_id);

# --- !Downs

drop table if exists on_sale cascade;

drop sequence if exists on_sale_seq;