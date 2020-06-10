# --- !Ups

create table contact_us (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  email                     varchar(255),
  content                   TEXT,
  brand_id                  bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_contact_us primary key (id))
;

create sequence contact_us_seq;

alter table contact_us add constraint fk_contact_us_brand_16 foreign key (brand_id) references brand (id);
create index ix_contact_us_brand_16 on contact_us (brand_id);

# --- !Downs

drop table if exists contact_us cascade;

drop sequence if exists contact_us_seq;