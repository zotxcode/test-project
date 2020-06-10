# --- !Ups
create table province (
  id                        bigint not null,
  is_deleted                boolean,
  code                      varchar(255),
  name                      varchar(255),
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_province primary key (id))
;

create table city (
  id                        bigint not null,
  is_deleted                boolean,
  province_id                bigint,
  code                      varchar(255),
  name                      varchar(255),
  type                      varchar(255),
  postal_code               varchar(255),
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_city primary key (id))
;

create table courier (
  id                        bigint not null,
  is_deleted                boolean,
  code                      varchar(50),
  name                      varchar(255),
  image_name                TEXT,
  image_keyword             varchar(255),
  image_title               varchar(255),
  image_description         TEXT,
  image_url                 varchar(255),
  user_id                   bigint,
  brand_id                  bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_courier primary key (id))
;

create table sales_order (
  id                        bigint not null,
  is_deleted                boolean,
  brand_id                  bigint,
  order_number              varchar(255),
  discount                  float,
  subtotal                  float,
  voucher                  float,
  shipping                  float,
  total_price               float,
  member_id                 bigint,
  courier_id                bigint,
  bank_id                   bigint,
  status                    varchar(255),
  expired_date              timestamp,
  struct                    varchar(255),
  shipment_type             varchar(255),
  payment_type              varchar(255),
  email_notif               varchar(255),
  approved_date             timestamp,
  approved_by_id            bigint,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  order_date                timestamp not null,
  constraint uq_sales_order_order_number unique (order_number),
  constraint pk_sales_order primary key (id))
;

create table cart (
  id                        bigint not null,
  is_deleted                boolean,
  brand_id                  bigint,
  cart_number              varchar(255),
  member_id                 bigint,
  product_id                bigint,
  qty                       INTEGER ,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  order_date                timestamp not null,
  constraint uq_cart_cart_number unique (cart_number),
  constraint pk_cart_order primary key (id))
;

create table sales_order_detail (
  id                        bigint not null,
  is_deleted                boolean,
  product_id                bigint,
  brand_id                  bigint,
  status                    varchar(255),
  sales_order_id            bigint,
  product_name              varchar(255),
  price                     float,
  voucher_price             float,
  quantity                  integer,
  discount_persen           float,
  discount_amount           float,
  sub_total                 float,
  total_price               float,
  tax                       float,
  tax_price                 float,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_sales_order_detail primary key (id))
;

create table sales_order_payment (
  id                        bigint not null,
  is_deleted                boolean,
  sales_order_id            bigint,
  brand_id                  bigint,
  confirm_at                timestamp,
  confirm_by_id             bigint,
  total_transfer            float,
  invoice_no                varchar(255),
  debit_account_name        varchar(255),
  debit_account_number      varchar(255),
  image_url                 varchar(255),
  comments                  varchar(255),
  status                    varchar(255),
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_sales_order_payment_invoice_n unique (invoice_no),
  constraint pk_sales_order_payment primary key (id))
;

create table sales_order_return (
  id                        bigint not null,
  is_deleted                boolean,
  sales_order_id            bigint,
  brand_id                  bigint,
  return_number             varchar(30),
  member_id                 bigint,
  date                      timestamp,
  document_no               varchar(255),
  type                      varchar(1),
  status                    varchar(1),
  request_at                timestamp,
  description               varchar(255),
  schedule_at               timestamp,
  send_at                   timestamp,
  approved_note             varchar(255),
  approved_by_id            bigint,
  user_id                   bigint,
  odoo_id                   integer,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_sales_order_return_return_num unique (return_number),
  constraint pk_sales_order_return primary key (id))
;

create table sales_order_return_detail (
  id                        bigint not null,
  is_deleted                boolean,
  brand_id                  bigint,
  sales_order_return_id     bigint,
  product_id                bigint,
  quantity                  integer,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_sales_order_return_detail primary key (id))
;

create table sales_order_status (
  id                        bigint not null,
  is_deleted                boolean,
  sales_order_id            bigint,
  date                      timestamp,
  description               varchar(255),
  type                      integer,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_sales_order_seller_status primary key (id))
;

alter table city add constraint fk_city_province_id foreign key (province_id) references province (id);
create index ix_city_province_id on city (province_id);

alter table courier add constraint fk_courier_userCms_43 foreign key (user_id) references user_cms (id);
create index ix_courier_userCms_43 on courier (user_id);

alter table courier add constraint fk_courier_brand_16 foreign key (brand_id) references brand (id);
create index ix_courier_brand_16 on courier (brand_id);

create sequence courier_seq;
create sequence province_seq;
create sequence city_seq;

create sequence sales_order_seq;
create sequence sales_order_detail_seq;
create sequence sales_order_payment_seq;
create sequence sales_order_return_seq;
create sequence sales_order_return_detail_seq;
create sequence sales_order_status_seq;
create sequence cart_seq;

alter table cart add constraint fk_cart_member_113 foreign key (member_id) references member (id);
create index ix_cart_member_113 on cart (member_id);
alter table cart add constraint fk_cart_brand_16 foreign key (brand_id) references brand (id);
create index ix_cart_brand_16 on cart (brand_id);
alter table cart add constraint fk_cart_produc_120 foreign key (product_id) references product (id);
create index ix_cart_produc_120 on cart (product_id);

alter table sales_order add constraint fk_sales_order_brand_16 foreign key (brand_id) references brand (id);
create index ix_sales_order_brand_16 on sales_order (brand_id);

alter table sales_order_detail add constraint fk_sales_order_detail_brand_16 foreign key (brand_id) references brand (id);
create index ix_sales_order_detail_brand_16 on sales_order_detail (brand_id);

alter table sales_order_payment add constraint fk_sales_order_payment_brand_16 foreign key (brand_id) references brand (id);
create index ix_sales_order_payment_brand_16 on sales_order_payment (brand_id);

alter table sales_order_return add constraint fk_sales_order_return_brand_16 foreign key (brand_id) references brand (id);
create index ix_sales_order_return_brand_16 on sales_order_return (brand_id);

alter table sales_order_return_detail add constraint fk_sales_order_return_detail_brand_16 foreign key (brand_id) references brand (id);
create index ix_sales_order_return_detail_brand_16 on sales_order_return_detail (brand_id);

alter table sales_order add constraint fk_sales_order_member_113 foreign key (member_id) references member (id);
create index ix_sales_order_member_113 on sales_order (member_id);
alter table sales_order add constraint fk_sales_order_courier_115 foreign key (courier_id) references courier (id);
create index ix_sales_order_courier_115 on sales_order (courier_id);
alter table sales_order add constraint fk_sales_order_bank_117 foreign key (bank_id) references bank (id);
create index ix_sales_order_bank_117 on sales_order (bank_id);
alter table sales_order add constraint fk_sales_order_approvedBy_118 foreign key (approved_by_id) references user_cms (id);
create index ix_sales_order_approvedBy_118 on sales_order (approved_by_id);
alter table sales_order add constraint fk_sales_order_userCms_119 foreign key (user_id) references user_cms (id);
create index ix_sales_order_userCms_119 on sales_order (user_id);
alter table sales_order_detail add constraint fk_sales_order_detail_produc_120 foreign key (product_id) references product (id);
create index ix_sales_order_detail_produc_120 on sales_order_detail (product_id);
alter table sales_order_detail add constraint fk_sales_order_detail_salesO_123 foreign key (sales_order_id) references sales_order (id);
create index ix_sales_order_detail_salesO_123 on sales_order_detail (sales_order_id);
alter table sales_order_payment add constraint fk_sales_order_payment_sales_125 foreign key (sales_order_id) references sales_order (id);
create index ix_sales_order_payment_sales_125 on sales_order_payment (sales_order_id);
alter table sales_order_payment add constraint fk_sales_order_payment_confi_126 foreign key (confirm_by_id) references user_cms (id);
create index ix_sales_order_payment_confi_126 on sales_order_payment (confirm_by_id);
alter table sales_order_return add constraint fk_sales_order_return_salesO_127 foreign key (sales_order_id) references sales_order (id);
create index ix_sales_order_return_salesO_127 on sales_order_return (sales_order_id);
alter table sales_order_return add constraint fk_sales_order_return_member_128 foreign key (member_id) references member (id);
create index ix_sales_order_return_member_128 on sales_order_return (member_id);
alter table sales_order_return add constraint fk_sales_order_return_approv_129 foreign key (approved_by_id) references user_cms (id);
create index ix_sales_order_return_approv_129 on sales_order_return (approved_by_id);
alter table sales_order_return add constraint fk_sales_order_return_userCm_130 foreign key (user_id) references user_cms (id);
create index ix_sales_order_return_userCm_130 on sales_order_return (user_id);
alter table sales_order_return_detail add constraint fk_sales_order_return_detail_131 foreign key (sales_order_return_id) references sales_order_return (id);
create index ix_sales_order_return_detail_131 on sales_order_return_detail (sales_order_return_id);
alter table sales_order_return_detail add constraint fk_sales_order_return_detail_132 foreign key (product_id) references product (id);
create index ix_sales_order_return_detail_132 on sales_order_return_detail (product_id);
alter table sales_order_status add constraint fk_sales_order_status_140 foreign key (sales_order_id) references sales_order (id);
create index ix_sales_order_seller_140 on sales_order_status (sales_order_id);

# --- !Downs

drop table if exists province cascade;
drop table if exists city cascade;
drop table if exists courier cascade;

drop table if exists sales_order cascade;
drop table if exists sales_order_detail cascade;
drop table if exists sales_order_payment cascade;
drop table if exists sales_order_return cascade;
drop table if exists sales_order_return_detail cascade;
drop table if exists sales_order_status cascade;
drop table if exists cart cascade;

drop sequence if exists province_seq;
drop sequence if exists city_seq;
drop sequence if exists courier_seq;

drop sequence if exists sales_order_seq;
drop sequence if exists sales_order_detail_seq;
drop sequence if exists sales_order_payment_seq;
drop sequence if exists sales_order_return_seq;
drop sequence if exists sales_order_return_detail_seq;
drop sequence if exists sales_order_status_seq;
drop sequence if exists cart_seq;