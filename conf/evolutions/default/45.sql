# --- !Ups
create table product_stock (
  id                        bigint not null,
  is_deleted                boolean,
  product_id                BIGINT,
  distributor_id            BIGINT,
  reseller_id               bigint,
  stock                     bigint,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_product_stock primary key (id))
;

create table product_mutation_stock (
  id                        bigint not null,
  is_deleted                boolean,
  product_id                BIGINT,
  distributor_id            BIGINT,
  reseller_id               bigint,
  date                      timestamp,
  type                      INTEGER,
  qty                       INTEGER,
  description               VARCHAR(255),
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_product_mutation_stock primary key (id))
;

create sequence product_stock_seq;
create sequence product_mutation_stock_seq;


# --- !Downs
drop table if exists product_stock cascade;
drop table if exists product_mutation_stock cascade;