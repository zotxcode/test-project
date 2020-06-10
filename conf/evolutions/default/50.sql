# --- !Ups
create table product_profit (
  id                        bigint not null,
  product_id                bigint,
  is_deleted                boolean,
  type                      INTEGER,
  percentage                float,
  value                     float,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_product_profit primary key (id))
;

create table member_mutation_balance (
  id                        bigint not null,
  member_id                 bigint,
  is_deleted                boolean,
  date                      timestamp,
  type                      INTEGER,
  value                     float,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  description               varchar(255),
  constraint pk_member_mutation_balance primary key (id))
;

create table distributor_mutation_balance (
  id                        bigint not null,
  user_cms_id               bigint,
  is_deleted                boolean,
  date                      timestamp,
  type                      INTEGER,
  value                     float,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  description               varchar(255),
  constraint pk_distributor_mutation_balance primary key (id))
;

ALTER TABLE user_cms ADD COLUMN balance float DEFAULT 0;
ALTER TABLE member ADD COLUMN balance float DEFAULT 0;

create sequence product_profit_seq;
create sequence member_mutation_balance_seq;
create sequence distributor_mutation_balance_seq;

# --- !Downs
drop table if exists product_profit cascade;
drop table if exists member_mutation_balance cascade;
drop table if exists distributor_mutation_balance cascade;
drop sequence if exists product_profit_seq;
drop sequence if exists member_mutation_balance_seq;
drop sequence if exists distributor_mutation_balance_seq;

ALTER TABLE user_cms DROP COLUMN balance;
ALTER TABLE member DROP COLUMN balance;
