# --- !Ups
create table voucher_sign_up (
  id                        bigint not null,
  is_deleted                boolean,
  brand_id                  bigint,
  user_id                   bigint,
  user_param                varchar(255),
  periode                   timestamp not null,
  prefix_kupon				integer,
  client_id					integer,
  besaran_voucher			integer,
  minimum_belanja			integer,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_voucher_sign_up primary key (id))
;

create sequence voucher_sign_up_seq;

# --- !Downs

drop table if exists voucher_sign_up cascade;

drop sequence if exists voucher_sign_up_seq;