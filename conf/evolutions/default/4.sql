# --- !Ups

create table fashion_size (
  id                        bigint not null,
  is_deleted                boolean,
  international            	varchar(5),
  eu                     	int,
  collar                   	float,
  user_id                 	bigint,
  brand_id                  bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  sequence                	int,
  constraint pk_fashion_size primary key (id))
;

create sequence fashion_size_seq;

alter table fashion_size add constraint fk_fashion_size_brand_16 foreign key (brand_id) references brand (id);
create index ix_fashion_size_brand_16 on fashion_size (brand_id);
alter table fashion_size add constraint fk_fashion_size_userCms_18 foreign key (user_id) references user_cms (id);
create index ix_fashion_size_userCms_18 on fashion_size (user_id);

# --- !Downs

drop table if exists fashion_size cascade;

drop sequence if exists fashion_size_seq;