# --- !Ups

create table newsletters (
  id                        bigint not null,
  is_deleted                boolean,
  status                    boolean,
  brand_id                  bigint,
  title                     varchar(255),
  contents					text,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_newsletters primary key (id))
;

create sequence newsletters_seq;


# --- !Downs

drop table if exists newsletters cascade;

drop sequence if exists newsletters_seq;