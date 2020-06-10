# --- !Ups

create table wish_list (
  id                        bigint not null,
  is_deleted                boolean,
  member_id                 bigint,
  product_id                bigint,
  brand_id                  bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_wish_list primary key (id))
;

create sequence wish_list_seq;

alter table wish_list add constraint fk_wish_list_brand_16 foreign key (brand_id) references brand (id);
create index ix_wish_list_brand_16 on wish_list (brand_id);
alter table wish_list add constraint fk_wish_list_member_18 foreign key (member_id) references member (id);
create index fk_wish_list_member_18 on wish_list (member_id);
alter table wish_list add constraint fk_wish_list_product_18 foreign key (product_id) references product (id);
create index fk_wish_list_product_18 on wish_list (product_id);

# --- !Downs

drop table if exists wish_list cascade;

drop sequence if exists wish_list_seq;