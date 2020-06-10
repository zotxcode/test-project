# --- !Ups
create table product_also_view (
  product_also_view_id    	bigint not null,
  product_id                bigint not null,
  constraint pk_product_also_view primary key (product_also_view_id , product_id))
;

alter table product_also_view add constraint fk_product_also_view foreign key (product_also_view_id) references product (id);

alter table product_also_view add constraint fk_product_av_id foreign key (product_id) references product (id);


# --- !Downs

drop table if exists product_also_view cascade;