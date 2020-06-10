# --- !Ups
create table shipping_address (
  id                        bigint not null,
  is_deleted                boolean,
  is_default                boolean,
  recipient_name            varchar(255),
  phone                     varchar(255),
  address                   varchar(255),
  created_by                bigint,
  updated_by                bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  member_id                 bigint,
  constraint pk_shipping_address primary key (id))
;
ALTER TABLE member ADD COLUMN shipping_address_id bigint;

alter table shipping_address add constraint fk_shipping_address_member_198 foreign key (member_id) references member (id);
create index ix_shipping_address_member_198 on shipping_address (member_id);

create sequence shipping_address_seq;

# --- !Downs
drop table if exists shipping_address cascade;
ALTER TABLE member DROP COLUMN shipping_address_id;

drop sequence if exists shipping_address_seq;
