# --- !Ups

create table currency (
  code                      varchar(255) not null,
  code_display              varchar(255),
  name                      varchar(255),
  sequence                  integer,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_currency primary key (code))
;

create table product_detail (
  id                        bigint not null,
  is_deleted                boolean,
  product_name              varchar(255),
  short_descriptions        varchar(255),
  description               text,
  weight                    float,
  dimension1                float,
  dimension2                float,
  dimension3                float,
  warranty_type             integer default 0,
  warranty_period           integer default 0,
  sold_fulfilled_by         varchar(255),
  what_in_the_box           varchar(255),
  brand_id                  bigint,
  total_stock               bigint,
  free_stock                bigint,
  reserved_stock            bigint,
  stock                     boolean,
  limited_stock             boolean,
  stock_counter             boolean,
  published                 boolean,
  full_image_urls           TEXT,
  medium_image_urls         TEXT,
  thumbnail_image_urls      TEXT,
  blur_image_urls           TEXT,
  threesixty_image_urls     TEXT,
  product_id                bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_product_detail primary key (id))
;

create table product_detail_tmp (
  id_tmp                    varchar(255) not null,
  id                        bigint,
  brand_id                  bigint,
  product_name              varchar(255),
  short_descriptions        varchar(255),
  description               text,
  weight                    float,
  dimension1                float,
  dimension2                float,
  dimension3                float,
  warranty_type             integer default 0,
  warranty_period           integer default 0,
  sold_fulfilled_by         varchar(255),
  what_in_the_box           varchar(255),
  total_stock               bigint,
  free_stock                bigint,
  reserved_stock            bigint,
  stock                     boolean,
  limited_stock             boolean,
  stock_counter             boolean,
  published                 boolean,
  full_image_urls           TEXT,
  medium_image_urls         TEXT,
  thumbnail_image_urls      TEXT,
  blur_image_urls           TEXT,
  threesixty_image_urls     TEXT,
  product_id                varchar(255),
  constraint pk_product_detail_tmp primary key (id_tmp))
;

create table product_group (
  id                        bigint not null,
  is_deleted                boolean,
  brand_id                  bigint,
  name                      varchar(255),
  lowest_price_product      bigint,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_product_group primary key (id))
;

create table product_review (
  id                        bigint not null,
  is_deleted                boolean,
  brand_id                  bigint,
  title                     varchar(255),
  comment                   varchar(255),
  rating                    integer,
  is_active                 boolean,
  approved_status           varchar(255),
  approved_by_id            bigint,
  image_url                 varchar(255),
  member_id                 bigint,
  user_id                   bigint,
  product_id                bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_product_review primary key (id))
;


create table product_fashion_size (
  product_id                     bigint not null,
  fashion_size_id                   bigint not null,
  constraint pk_product_fashion_size primary key (product_id, fashion_size_id))
;


create sequence currency_seq;

create sequence product_detail_seq;

create sequence product_group_seq;

create sequence product_review_seq;

ALTER TABLE product ADD COLUMN product_group_id bigint;
ALTER TABLE product ADD COLUMN currency_cd varchar(255);
ALTER TABLE product_detail ADD COLUMN size_guide TEXT;
alter table product add constraint fk_product_productGroup_75 foreign key (product_group_id) references product_group (id);
create index ix_product_productGroup_75 on product (product_group_id);
alter table product add constraint fk_product_currency_68 foreign key (currency_cd) references currency (code);
create index ix_product_currency_68 on product (currency_cd);

alter table product_detail add constraint fk_product_detail_mainProduct_78 foreign key (product_id) references product (id);
create index ix_product_detail_mainProduct_78 on product_detail (product_id);
alter table product_group add constraint fk_product_group_lowestPriceP_80 foreign key (lowest_price_product) references product (id);
create index ix_product_group_lowestPriceP_80 on product_group (lowest_price_product);
alter table product_group add constraint fk_product_group_userCms_81 foreign key (user_id) references user_cms (id);
create index ix_product_group_userCms_81 on product_group (user_id);
alter table product_review add constraint fk_product_review_approvedBy_82 foreign key (approved_by_id) references user_cms (id);
create index ix_product_review_approvedBy_82 on product_review (approved_by_id);
alter table product_review add constraint fk_product_review_member_83 foreign key (member_id) references member (id);
create index ix_product_review_member_83 on product_review (member_id);
alter table product_review add constraint fk_product_review_user_85 foreign key (user_id) references user_cms (id);
create index ix_product_review_user_85 on product_review (user_id);
alter table product_review add constraint fk_product_review_product_86 foreign key (product_id) references product (id);
create index ix_product_review_product_86 on product_review (product_id);
alter table product_detail add constraint fk_product_detail_brand_16 foreign key (brand_id) references brand (id);
create index ix_product_detail_brand_16 on product_detail (brand_id);
alter table product_group add constraint fk_product_group_brand_16 foreign key (brand_id) references brand (id);
create index ix_product_group_brand_16 on product_group (brand_id);
alter table product_review add constraint fk_product_review_brand_16 foreign key (brand_id) references brand (id);
create index ix_product_review_brand_16 on product_review (brand_id);

# --- !Downs

drop table if exists currency cascade;

drop table if exists product_detail cascade;

drop table if exists product_group cascade;

drop table if exists product_review cascade;

drop table if exists product_fashion_size cascade;

drop sequence if exists product_detail_seq;

drop sequence if exists product_group_seq;

drop sequence if exists product_review_seq;

drop sequence if exists currency_seq;

