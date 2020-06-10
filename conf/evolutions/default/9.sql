# --- !Ups

create table socmed (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  url                     varchar(255),
  brand_id                  bigint,
  status                    boolean,
  image_name         TEXT,
  image_keyword      varchar(255),
  image_title        varchar(255),
  image_description  TEXT,
  image_url                 varchar(255),
  image_size               varchar(255),
  image_url_responsive      varchar(255),
  image_responsive_size    varchar(255),
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_socmed primary key (id))
;

create sequence socmed_seq;

alter table socmed add constraint fk_socmed_brand_16 foreign key (brand_id) references brand (id);
create index ix_socmed_brand_16 on socmed (brand_id);
alter table socmed add constraint fk_socmed_userCms_18 foreign key (user_id) references user_cms (id);
create index ix_socmed_userCms_18 on socmed (user_id);

# --- !Downs

drop table if exists socmed cascade;

drop sequence if exists socmed_seq;