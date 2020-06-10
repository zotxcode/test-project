# --- !Ups
create table allergy (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  created_by                bigint,
  updated_by                bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_allergy primary key (id))
;

create table allergy_member (
  allergy_id                bigint not null,
  member_id                 bigint not null,
  constraint pk_allergy_member primary key (member_id, allergy_id))
;

create sequence allergy_seq;

alter table allergy add constraint fk_allergy_createdBy_194 foreign key (created_by) references user_cms (id);
create index ix_allergy_createdBy_194 on allergy (created_by);
alter table allergy add constraint fk_allergy_updatedBy_195 foreign key (updated_by) references user_cms (id);
create index ix_allergy_updatedBy_195 on allergy (updated_by);

alter table allergy_member add constraint fk_allergy_member_member_196 foreign key (member_id) references member (id);
alter table allergy_member add constraint fk_allergy_member_allergy_197 foreign key (allergy_id) references allergy (id);

# --- !Downs
drop table if exists allergy cascade;
drop table if exists allergy_member cascade;

drop sequence if exists allergy_seq;
