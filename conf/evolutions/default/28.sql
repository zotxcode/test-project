# --- !Ups
create table sales_order_address (
  id                        bigint not null,
  is_deleted                boolean,
  sales_order_id            bigint,
  full_name                 varchar(255),
  email                     varchar(255),
  phone                     varchar(255),
  address                   TEXT,
  brand_id                  bigint,
  province_id               bigint,
  city_id                   bigint,
  postal_code               varchar(255),
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_sales_order_address primary key (id))
;

create sequence sales_order_address_seq;

alter table sales_order_address add constraint fk_sales_order_address_brand_16 foreign key (brand_id) references brand (id);
create index ix_sales_order_address_brand_16 on sales_order_address (brand_id);
alter table sales_order_address add constraint fk_sales_order_address_so foreign key (sales_order_id) references sales_order (id);
create index ix_sales_order_address_so on sales_order_address (sales_order_id);
alter table sales_order_address add constraint fk_sales_order_address_prov foreign key (province_id) references province (id);
create index ix_sales_order_address_prov on sales_order_address (sales_order_id);
alter table sales_order_address add constraint fk_sales_order_address_city foreign key (city_id) references city (id);
create index ix_sales_order_address_city on sales_order_address (city_id);


ALTER TABLE sales_order ADD COLUMN service_code VARCHAR(255) default NULL;
ALTER TABLE sales_order ADD COLUMN service_description VARCHAR(255) default NULL;
ALTER TABLE sales_order ADD COLUMN service_etd VARCHAR(255) default NULL;
ALTER TABLE sales_order_detail ADD COLUMN voucher FLOAT;

# --- !Downs
drop table if exists sales_order_address cascade;

drop sequence if exists sales_order_address_seq;

ALTER TABLE sales_order DROP COLUMN service_code;
ALTER TABLE sales_order DROP COLUMN service_description;
ALTER TABLE sales_order DROP COLUMN service_etd;
ALTER TABLE sales_order_detail DROP COLUMN voucher;
