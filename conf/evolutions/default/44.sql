# --- !Ups
create table purchase_order (
  id                        bigint not null,
  is_deleted                boolean,
  code                      varchar(255),
  total                     float,
  received_at               timestamp,
  status                    integer,
  information               varchar(255),
  distributor_id               bigint,
  reseller_id                 bigint,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_purchase_order_code unique (code),
  constraint pk_purchase_order primary key (id))
;

create table purchase_order_detail (
  id                        bigint not null,
  is_deleted                boolean,
  po_id                     bigint,
  product_id                bigint,
  qty                       integer,
  price                     float,
  sub_total                 float,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_purchase_order_detail primary key (id))
;

create sequence purchase_order_seq;

create sequence purchase_order_detail_seq;


# --- !Downs
drop table if exists purchase_order cascade;

drop table if exists purchase_order_detail cascade;