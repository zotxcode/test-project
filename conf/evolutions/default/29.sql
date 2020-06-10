# --- !Ups
create table voucher (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  description               varchar(255),
  masking                   varchar(255),
  type                      varchar(255),
  status                    boolean,
  discount                  float,
  discount_type             integer default 0,
  count                     integer,
  max_value                 float,
  min_purchase              float,
  priority                  integer,
  stop_further_rule_porcessing integer,
  valid_from                timestamp,
  valid_to                  timestamp,
  filter_status             varchar(255),
  assigned_to               varchar(255),
  created_by                bigint,
  updated_by                bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  brand_id                  bigint,
  constraint pk_voucher primary key (id))
;

create table voucher_detail (
  id                        bigint not null,
  is_deleted                boolean,
  code                      varchar(255),
  voucher_id                bigint,
  status                    integer default 0,
  member_id                 bigint,
  used_at                   timestamp,
  odoo_id                   integer,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_voucher_detail primary key (id))
;

create table voucher_category (
  voucher_id                     bigint not null,
  category_id                    bigint not null,
  constraint pk_voucher_category primary key (voucher_id, category_id))
;

create table voucher_product (
  voucher_id                     bigint not null,
  product_id                     bigint not null,
  constraint pk_voucher_product primary key (voucher_id, product_id))
;

create table voucher_member (
  voucher_id                     bigint not null,
  member_id                      bigint not null,
  constraint pk_voucher_member primary key (voucher_id, member_id))
;

create sequence voucher_seq;

create sequence voucher_detail_seq;

alter table voucher add constraint fk_voucher_brand_16 foreign key (brand_id) references brand (id);
create index ix_voucher_brand_16 on voucher (brand_id);
alter table voucher add constraint fk_voucher_createdBy_190 foreign key (created_by) references user_cms (id);
create index ix_voucher_createdBy_190 on voucher (created_by);
alter table voucher add constraint fk_voucher_updatedBy_191 foreign key (updated_by) references user_cms (id);
create index ix_voucher_updatedBy_191 on voucher (updated_by);
alter table voucher_detail add constraint fk_voucher_detail_voucher_192 foreign key (voucher_id) references voucher (id);
create index ix_voucher_detail_voucher_192 on voucher_detail (voucher_id);
alter table voucher_detail add constraint fk_voucher_detail_member_193 foreign key (member_id) references member (id);
create index ix_voucher_detail_member_193 on voucher_detail (member_id);

alter table voucher_category add constraint fk_voucher_category_voucher_01 foreign key (voucher_id) references voucher (id);

alter table voucher_category add constraint fk_voucher_category_category_02 foreign key (category_id) references category (id);

alter table voucher_product add constraint fk_voucher_product_voucher_01 foreign key (voucher_id) references voucher (id);

alter table voucher_product add constraint fk_voucher_product_product_02 foreign key (product_id) references product (id);

alter table voucher_member add constraint fk_voucher_member_voucher_01 foreign key (voucher_id) references voucher (id);

alter table voucher_member add constraint fk_voucher_member_member_02 foreign key (member_id) references member (id);

# --- !Downs
drop table if exists voucher cascade;
drop table if exists voucher_detail cascade;
drop table if exists voucher_category cascade;
drop table if exists voucher_product cascade;
drop table if exists voucher_member cascade;

drop sequence if exists voucher_seq;

drop sequence if exists voucher_detail_seq;