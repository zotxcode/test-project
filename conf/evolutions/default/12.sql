# --- !Ups

create table product_variant_group (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  brand_id                  bigint,
  lowest_price_product   	bigint,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_product_variant_group primary key (id))
;

ALTER TABLE product ADD COLUMN product_variant_group_id bigint;

create sequence product_variant_group_seq;
alter table product add constraint fk_product_productVariantGroup_75 foreign key (product_variant_group_id) references product_variant_group (id);
create index ix_product_productVariantGroup_75 on product (product_variant_group_id);

alter table product_variant_group add constraint fk_product_variant_group_lowestPriceP_80 foreign key (lowest_price_product) references product (id);
create index ix_product_variant_group_lowestPriceP_80 on product_variant_group (lowest_price_product);
alter table product_variant_group add constraint fk_product_variant_group_userCms_81 foreign key (user_id) references user_cms (id);
create index ix_product_variant_group_userCms_81 on product_variant_group (user_id);
alter table product_variant_group add constraint fk_product_variant_group_brand_16 foreign key (brand_id) references brand (id);
create index ix_product_variant_group_brand_16 on bank (brand_id);

create table product_variant_group_base_attri (
  product_variant_group_id    				bigint not null,
  base_attribute_id                       	bigint not null,
  constraint pk_product_variant_group_base_attri primary key (product_variant_group_id, base_attribute_id))
;


alter table product_variant_group_base_attri add constraint fk_product_vgattribute_pvgroup_01 foreign key (product_variant_group_id) references product_variant_group (id);

alter table product_variant_group_base_attri add constraint fk_product_vgattribute_base_attribute_02 foreign key (base_attribute_id) references base_attribute (id);


# --- !Downs

drop table if exists product_variant_group_base_attri cascade;
drop table if exists product_variant_group cascade;
ALTER TABLE product DROP product_variant_group_id;
drop sequence if exists product_variant_group_seq;