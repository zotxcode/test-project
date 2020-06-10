# --- !Ups

create table bank (
  id                        bigint not null,
  is_deleted                boolean,
  bank_name                 varchar(255),
  account_name              varchar(255),
  account_number            varchar(255),
  description               varchar(255),
  image_name                TEXT,
  brand_id                  bigint,
  image_keyword             varchar(255),
  image_title               varchar(255),
  image_description         TEXT,
  image_url                 varchar(255),
  status                    boolean,
  odoo_id                   integer,
  partner_bank_id           integer,
  account_journal_id        integer,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_bank primary key (id))
;

create sequence bank_seq;

alter table bank add constraint fk_bank_brand_16 foreign key (brand_id) references brand (id);
create index ix_bank_brand_16 on bank (brand_id);
alter table bank add constraint fk_bank_userCms_18 foreign key (user_id) references user_cms (id);
create index ix_bank_userCms_18 on bank (user_id);

# --- !Downs

drop table if exists bank cascade;

drop sequence if exists bank_seq;