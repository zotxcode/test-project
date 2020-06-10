# --- !Ups
create table presentation (
  id                        bigint not null,
  is_deleted                boolean,
  status                    boolean,
  title                     varchar(255),
  short_desc                varchar(255),
  description               varchar(255),
  author                    varchar(255),
  image_urls                text,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_presentation primary key (id))
;

create sequence presentation_seq;

# --- !Downs
drop table if exists presentation cascade;

drop sequence if exists presentation_seq;
