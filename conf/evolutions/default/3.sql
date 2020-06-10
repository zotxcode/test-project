# --- !Ups

create table blacklist_email (
  id                        bigint not null,
  is_deleted                boolean,
  brand_id                  bigint,
  name                      varchar(255),
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_blacklist_email primary key (id))
;

create sequence blacklist_email_seq;

alter table blacklist_email add constraint fk_blacklist_email_brand_16 foreign key (brand_id) references brand (id);
create index ix_blacklist_email_brand_16 on blacklist_email (brand_id);
alter table blacklist_email add constraint fk_blacklist_email_userCms_18 foreign key (user_id) references user_cms (id);
create index ix_blacklist_email_userCms_18 on blacklist_email (user_id);

# --- !Downs

drop table if exists blacklist_email cascade;

drop sequence if exists blacklist_email_seq;