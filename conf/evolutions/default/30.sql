# --- !Ups
create table promotion (
  id                        bigint not null,
  is_deleted                boolean,
  status                    boolean,
  name                      varchar(255),
  description               varchar(255),
  valid_from                timestamp,
  valid_to                  timestamp,
  created_by                bigint,
  updated_by                bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  brand_id                  bigint,
  constraint pk_promotion primary key (id))
;

create table promotion_product (
  id                        bigint not null,
  is_deleted                boolean,
  promotion_id              bigint,
  product_id                bigint,
  discount                  float,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  brand_id                  bigint,
  constraint pk_promotion_product primary key (id))
;

create sequence promotion_seq;

create sequence promotion_product_seq;

alter table promotion add constraint fk_promotion_brand_16 foreign key (brand_id) references brand (id);
create index ix_promotion_brand_16 on promotion (brand_id);
alter table promotion add constraint fk_promotion_createdBy_190 foreign key (created_by) references user_cms (id);
create index ix_promotion_createdBy_190 on promotion (created_by);
alter table promotion add constraint fk_promotion_updatedBy_191 foreign key (updated_by) references user_cms (id);
create index ix_promotion_updatedBy_191 on promotion (updated_by);
alter table promotion_product add constraint fk_promotion_product_promotion_192 foreign key (promotion_id) references promotion (id);
create index ix_promotion_product_promotion_192 on promotion_product (promotion_id);
alter table promotion_product add constraint fk_promotion_product_product_193 foreign key (product_id) references product (id);
create index ix_promotion_product_product_193 on promotion_product (product_id);

# --- !Downs
drop table if exists promotion cascade;
drop table if exists promotion_product cascade;

drop sequence if exists promotion_seq;
drop sequence if exists promotion_product_seq;