# --- !Ups

create table article (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  title                     varchar(255),
  slug                      varchar(255),
  description               varchar(255),
  keyword                   varchar(255),
  image_header_url          varchar(255),
  image_thumbnail_url       varchar(255),
  image_name                varchar(255),
  image_title               varchar(255),
  image_alternate           varchar(255),
  image_description         varchar(255),
  view_count                integer,
  content                   TEXT,
  brand_id                  bigint,
  short_description         TEXT,
  status                    varchar(255),
  article_category_id       bigint,
  article_category_name     varchar(255),
  user_id                   bigint,
  change_by                 bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_article primary key (id))
;

create table article_category (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  sequence                  integer,
  brand_id                  bigint,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_article_category_name unique (name),
  constraint pk_article_category primary key (id))
;

create table article_comment (
  id                        bigint not null,
  is_deleted                boolean,
  comment_parent_id         bigint,
  brand_id                  bigint,
  article_id                bigint,
  commenter_id              bigint,
  is_admin                  boolean,
  comment                   TEXT,
  is_removed                boolean,
  status                    integer,
  approve_by                bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_article_comment primary key (id))
;

create table attribute (
  id                        bigint not null,
  is_deleted                boolean,
  value                     varchar(255),
  image_url                 varchar(255),
  is_default                boolean,
  additional                varchar(255),
  base_attribute_id         bigint,
  brand_id                  bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_attribute_1 unique (value,base_attribute_id),
  constraint pk_attribute primary key (id))
;

create table banner (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  caption1                  varchar(255),
  caption2                  varchar(255),
  title                     varchar(255),
  slug                      varchar(255),
  description               varchar(255),
  keyword                   varchar(255),
  status                    boolean,
  sequence                  integer,
  sequence_mobile           integer,
  type_id                   integer,
  position_id               integer,
  banner_image_name         TEXT,
  banner_image_keyword      varchar(255),
  banner_image_title        varchar(255),
  banner_image_description  TEXT,
  image_url                 varchar(255),
  banner_size               varchar(255),
  image_url_responsive      varchar(255),
  banner_responsive_size    varchar(255),
  image_url_mobile          varchar(255),
  banner_mobile_size        varchar(255),
  brand_id                  bigint,
  active_from               timestamp,
  active_to                 timestamp,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_banner primary key (id))
;

create table base_attribute (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  type                      varchar(255),
  brand_id                  bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_base_attribute_name unique (name),
  constraint pk_base_attribute primary key (id))
;

create table best_seller (
  id                        bigint not null,
  is_deleted                boolean,
  status                    boolean,
  sequence                  integer,
  brand_id                  bigint,
  product_id                bigint,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_best_seller primary key (id))
;

create table best_seller_banner (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  caption1                  varchar(255),
  caption2                  varchar(255),
  title                     varchar(255),
  slug                      varchar(255),
  description               varchar(255),
  keyword                   varchar(255),
  status                    boolean,
  sequence                  integer,
  banner_image_name         TEXT,
  banner_image_keyword      varchar(255),
  banner_image_title        varchar(255),
  banner_image_description  TEXT,
  image_url                 varchar(255),
  banner_size               varchar(255),
  image_url_responsive      varchar(255),
  banner_responsive_size    varchar(255),
  brand_id                  bigint,
  active_from               timestamp,
  active_to                 timestamp,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_best_seller_banner primary key (id))
;

create table brand (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  domain                    varchar(255),
  address                   varchar(255),
  email                     varchar(255),
  slug                      varchar(255),
  status                    boolean,
  image_name                TEXT,
  image_keyword             varchar(255),
  image_title               varchar(255),
  image_description         TEXT,
  image_url                 varchar(255),
  qty_prodyct               integer,
  stock_product             integer,
  user_id                   bigint,
  view_count                integer,
  image_size                varchar(255),
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_brand_name unique (name),
  constraint pk_brand primary key (id))
;

create table category (
  id                        bigint not null,
  is_deleted                boolean,
  code                      varchar(255),
  root_category_code        varchar(255),
  is_active                 boolean,
  name                      varchar(255),
  title                     varchar(255),
  description               varchar(255),
  keyword                   varchar(255),
  alias                     varchar(255),
  level                     integer,
  sequence                  integer,
  slug                      varchar(255),
  share_profit              float,
  image_name                TEXT,
  image_keyword             varchar(255),
  image_title               varchar(255),
  image_description         TEXT,
  image_url                 varchar(255),
  image_url_responsive      varchar(255),
  brand_id                  bigint,
  user_id                   bigint,
  view_count                integer,
  parent_id                 bigint,
  image_size                varchar(255),
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_category primary key (id))
;

create table category_banner (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  caption1                  varchar(255),
  caption2                  varchar(255),
  title                     varchar(255),
  slug                      varchar(255),
  description               varchar(255),
  keyword                   varchar(255),
  status                    boolean,
  sequence                  integer,
  banner_image_name         TEXT,
  banner_image_keyword      varchar(255),
  banner_image_title        varchar(255),
  banner_image_description  TEXT,
  image_url                 varchar(255),
  banner_size               varchar(255),
  image_url_responsive      varchar(255),
  banner_responsive_size    varchar(255),
  brand_id                  bigint,
  active_from               timestamp,
  active_to                 timestamp,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_category_banner primary key (id))
;

create table change_log (
  id                        varchar(255),
  action_date               timestamp,
  type                      varchar(255),
  user_id                   bigint,
  table_name                varchar(255),
  item_id                   bigint,
  action                    varchar(255),
  before                    TEXT,
  after                     TEXT)
;

create table config_settings (
  id                        bigint not null,
  is_deleted                boolean,
  module                    varchar(255),
  key                       varchar(255),
  name                      varchar(255),
  value                     varchar(255),
  brand_id                  bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_config_settings_key unique (key),
  constraint pk_config_settings primary key (id))
;

create table faq (
  id                        bigint not null,
  is_deleted                boolean,
  title                     varchar(255),
  name                      varchar(255),
  slug                      varchar(255),
  description               varchar(255),
  keyword                   varchar(255),
  content                   TEXT,
  brand_id                  bigint,
  sequence                  integer,
  faq_group_id              bigint,
  type                      integer,
  user_id                   bigint,
  view_count                integer,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_faq primary key (id))
;

create table feature (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  key                       varchar(255),
  section                   varchar(255),
  description               varchar(255),
  is_active                 boolean,
  sequence                  integer,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_feature_key unique (key),
  constraint pk_feature primary key (id))
;

create table footer (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  position                  varchar(255),
  title                     varchar(255),
  slug                      varchar(255),
  description               varchar(255),
  keyword                   varchar(255),
  brand_id                  bigint,
  page_url                  varchar(255),
  static_page_id            bigint,
  sequence                  integer,
  new_tab                   boolean,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_footer primary key (id))
;

create table images (
  id                        varchar(255) not null,
  images                    TEXT,
  constraint pk_images primary key (id))
;

create table information_category_group (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  module_type               varchar(255),
  slug                      varchar(255),
  sequence                  integer,
  user_id                   bigint,
  brand_id                  bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_information_category_group primary key (id))
;

create table instagram_banner (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  caption1                  varchar(255),
  caption2                  varchar(255),
  title                     varchar(255),
  slug                      varchar(255),
  description               varchar(255),
  keyword                   varchar(255),
  status                    boolean,
  sequence                  integer,
  banner_image_name         TEXT,
  banner_image_keyword      varchar(255),
  banner_image_title        varchar(255),
  banner_image_description  TEXT,
  image_url                 varchar(255),
  banner_size               varchar(255),
  image_url_responsive      varchar(255),
  banner_responsive_size    varchar(255),
  brand_id                  bigint,
  active_from               timestamp,
  active_to                 timestamp,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_instagram_banner primary key (id))
;

create table member (
  id                        bigint not null,
  is_deleted                boolean,
  password                  varchar(255),
  first_name                varchar(255),
  last_name                 varchar(255),
  full_name                 varchar(255),
  email                     varchar(255),
  email_notifikasi          varchar(255),
  thumbnail_image_url       varchar(255),
  medium_image_url          varchar(255),
  large_image_url           varchar(255),
  phone                     varchar(255),
  gender                    varchar(1),
  birth_date                timestamp,
  billing_address_id        varchar(255),
  facebook_user_id          varchar(255),
  google_user_id            varchar(255),
  activation_code           varchar(255),
  is_active                 boolean,
  news_letter               boolean,
  reset_token               varchar(255),
  reset_time                bigint,
  code_expire               timestamp,
  last_login                timestamp,
  last_purchase             timestamp,
  odoo_id                   integer,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_member_email unique (email),
  constraint uq_member_phone unique (phone),
  constraint uq_member_facebook_user_id unique (facebook_user_id),
  constraint uq_member_google_user_id unique (google_user_id),
  constraint pk_member primary key (id))
;

create table member_log (
  id                        bigint not null,
  is_deleted                boolean,
  member_type               varchar(255),
  is_active                 boolean,
  token                     varchar(255),
  expired_date              timestamp,
  device_model              varchar(255),
  device_type               varchar(255),
  device_id                 varchar(255),
  api_key                   varchar(255),
  member_id                 bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_member_log primary key (id))
;

create table new_arrival (
  id                        bigint not null,
  is_deleted                boolean,
  status                    boolean,
  sequence                  integer,
  brand_id                  bigint,
  product_id                bigint,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_new_arrival primary key (id))
;

create table new_arrival_banner (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  caption1                  varchar(255),
  caption2                  varchar(255),
  title                     varchar(255),
  slug                      varchar(255),
  description               varchar(255),
  keyword                   varchar(255),
  status                    boolean,
  sequence                  integer,
  banner_image_name         TEXT,
  banner_image_keyword      varchar(255),
  banner_image_title        varchar(255),
  banner_image_description  TEXT,
  image_url                 varchar(255),
  banner_size               varchar(255),
  image_url_responsive      varchar(255),
  banner_responsive_size    varchar(255),
  brand_id                  bigint,
  active_from               timestamp,
  active_to                 timestamp,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_new_arrival_banner primary key (id))
;

create table on_sale_banner (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  caption1                  varchar(255),
  caption2                  varchar(255),
  title                     varchar(255),
  slug                      varchar(255),
  description               varchar(255),
  keyword                   varchar(255),
  status                    boolean,
  sequence                  integer,
  banner_image_name         TEXT,
  banner_image_keyword      varchar(255),
  banner_image_title        varchar(255),
  banner_image_description  TEXT,
  image_url                 varchar(255),
  banner_size               varchar(255),
  image_url_responsive      varchar(255),
  banner_responsive_size    varchar(255),
  brand_id                  bigint,
  active_from               timestamp,
  active_to                 timestamp,
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_on_sale_banner primary key (id))
;

create table photo (
  id                        bigint not null,
  is_deleted                boolean,
  file_name                 varchar(255),
  file_name_before          varchar(255),
  full_url                  varchar(255),
  medium_url                varchar(255),
  thumb_url                 varchar(255),
  blur_url                  varchar(255),
  user_id                   bigint,
  user_type                 varchar(255),
  module                    varchar(255),
  module_id                 bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_photo primary key (id))
;

create table product (
  id                        bigint not null,
  is_deleted                boolean,
  sku                       varchar(255),
  sku_seller                varchar(255),
  name                      varchar(255),
  is_new                    boolean,
  status                    boolean,
  title                     varchar(255),
  slug                      varchar(255),
  description               varchar(255),
  keyword                   varchar(255),
  buy_price                 float,
  price                     float,
  retur                     integer,
  view_count                integer,
  stock                     boolean,
  item_count                bigint,
  strike_through_display    float,
  price_display             float,
  discount                  float,
  discount_type             integer default 0,
  thumbnail_url             varchar(255),
  image_url                 varchar(255),
  odoo_id                   integer,
  position                  integer,
  is_show                   boolean default true,
  brand_id                  bigint,
  category_id               bigint,
  parent_category_id        bigint,
  grand_parent_category_id  bigint,
  average_rating            float,
  count_rating              integer,
  num_of_order              integer,
  discount_active_from      timestamp,
  discount_active_to        timestamp,
  user_id                   bigint,
  first_po_status           integer,
  approved_status           varchar(255),
  approved_note             varchar(2000),
  approved_information      varchar(1000),
  approved_by_id            bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_product_sku unique (sku),
  constraint pk_product primary key (id))
;

create table role (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  key                       varchar(255),
  description               varchar(255),
  is_active                 boolean,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_role_key unique (key),
  constraint pk_role primary key (id))
;

create table role_feature (
  feature_id                bigint,
  role_id                   bigint,
  access                    integer)
;

create table page (
  id                        bigint not null,
  is_deleted                boolean,
  content                   TEXT,
  title                     varchar(255),
  description               varchar(255),
  keyword                   varchar(255),
  slug                      varchar(255),
  name                      varchar(255),
  user_id                   bigint,
  brand_id                  bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_page primary key (id))
;

create table tag (
  id                        bigint not null,
  is_deleted                boolean,
  name                      varchar(255),
  brand_id                  bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_tag_name unique (name),
  constraint pk_tag primary key (id))
;

create table user_cms (
  id                        bigint not null,
  is_deleted                boolean,
  password                  varchar(255),
  first_name                varchar(255),
  last_name                 varchar(255),
  email                     varchar(255),
  full_name                 varchar(255),
  phone                     varchar(255),
  gender                    varchar(1),
  birth_date                timestamp,
  activation_code           varchar(255),
  is_active                 boolean,
  role_id                   bigint,
  brand_id                  bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_user_cms_email unique (email),
  constraint pk_user_cms primary key (id))
;

create table user_log (
  id                        bigint not null,
  is_deleted                boolean,
  user_type                 varchar(255),
  is_active                 boolean,
  token                     varchar(255),
  expired_date              timestamp,
  device_model              varchar(255),
  device_type               varchar(255),
  api_key                   varchar(255),
  user_id                   bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_user_log primary key (id))
;


create table banner_product (
  banner_id                      bigint not null,
  product_id                     bigint not null,
  constraint pk_banner_product primary key (banner_id, product_id))
;

create table banner_category (
  banner_id                      bigint not null,
  category_id                    bigint not null,
  constraint pk_banner_category primary key (banner_id, category_id))
;

create table best_seller_banner_category (
  best_seller_banner_id          bigint not null,
  category_id                    bigint not null,
  constraint pk_best_seller_banner_category primary key (best_seller_banner_id, category_id))
;

create table best_seller_banner_product (
  best_seller_banner_id          bigint not null,
  product_id                     bigint not null,
  constraint pk_best_seller_banner_product primary key (best_seller_banner_id, product_id))
;

create table category_base_attribute (
  category_id                    bigint not null,
  base_attribute_id              bigint not null,
  constraint pk_category_base_attribute primary key (category_id, base_attribute_id))
;

create table category_banner_category (
  category_banner_id             bigint not null,
  category_id                    bigint not null,
  constraint pk_category_banner_category primary key (category_banner_id, category_id))
;

create table category_banner_product (
  category_banner_id             bigint not null,
  product_id                     bigint not null,
  constraint pk_category_banner_product primary key (category_banner_id, product_id))
;

create table instagram_banner_category (
  instagram_banner_id            bigint not null,
  category_id                    bigint not null,
  constraint pk_instagram_banner_category primary key (instagram_banner_id, category_id))
;

create table instagram_banner_product (
  instagram_banner_id            bigint not null,
  product_id                     bigint not null,
  constraint pk_instagram_banner_product primary key (instagram_banner_id, product_id))
;

create table new_arrival_banner_category (
  new_arrival_banner_id          bigint not null,
  category_id                    bigint not null,
  constraint pk_new_arrival_banner_category primary key (new_arrival_banner_id, category_id))
;

create table new_arrival_banner_product (
  new_arrival_banner_id          bigint not null,
  product_id                     bigint not null,
  constraint pk_new_arrival_banner_product primary key (new_arrival_banner_id, product_id))
;

create table on_sale_banner_category (
  on_sale_banner_id              bigint not null,
  category_id                    bigint not null,
  constraint pk_on_sale_banner_category primary key (on_sale_banner_id, category_id))
;

create table on_sale_banner_product (
  on_sale_banner_id              bigint not null,
  product_id                     bigint not null,
  constraint pk_on_sale_banner_product primary key (on_sale_banner_id, product_id))
;

create table product_base_attribute (
  product_id                     bigint not null,
  base_attribute_id              bigint not null,
  constraint pk_product_base_attribute primary key (product_id, base_attribute_id))
;

create table product_attribute (
  product_id                     bigint not null,
  attribute_id                   bigint not null,
  constraint pk_product_attribute primary key (product_id, attribute_id))
;

create table tag_article (
  tag_id                         bigint not null,
  article_id                     bigint not null,
  constraint pk_tag_article primary key (tag_id, article_id))
;

create table tag_product (
  tag_id                         bigint not null,
  product_id                     bigint not null,
  constraint pk_tag_product primary key (tag_id, product_id))
;
create sequence article_seq;

create sequence article_category_seq;

create sequence article_comment_seq;

create sequence attribute_seq;

create sequence banner_seq;

create sequence base_attribute_seq;

create sequence best_seller_seq;

create sequence best_seller_banner_seq;

create sequence brand_seq;

create sequence category_seq;

create sequence category_banner_seq;

create sequence config_settings_seq;

create sequence faq_seq;

create sequence feature_seq;

create sequence footer_seq;

create sequence images_seq;

create sequence information_category_group_seq;

create sequence instagram_banner_seq;

create sequence member_seq;

create sequence member_log_seq;

create sequence new_arrival_seq;

create sequence new_arrival_banner_seq;

create sequence on_sale_banner_seq;

create sequence photo_seq;

create sequence product_seq;

create sequence role_seq;

create sequence page_seq;

create sequence tag_seq;

create sequence user_cms_seq;

create sequence user_log_seq;

alter table article add constraint fk_article_brand_1 foreign key (brand_id) references brand (id);
create index ix_article_brand_1 on article (brand_id);
alter table article add constraint fk_article_articleCategory_2 foreign key (article_category_id) references article_category (id);
create index ix_article_articleCategory_2 on article (article_category_id);
alter table article add constraint fk_article_userCms_3 foreign key (user_id) references user_cms (id);
create index ix_article_userCms_3 on article (user_id);
alter table article add constraint fk_article_changeBy_4 foreign key (change_by) references user_cms (id);
create index ix_article_changeBy_4 on article (change_by);
alter table article_category add constraint fk_article_category_brand_5 foreign key (brand_id) references brand (id);
create index ix_article_category_brand_5 on article_category (brand_id);
alter table article_category add constraint fk_article_category_userCms_6 foreign key (user_id) references user_cms (id);
create index ix_article_category_userCms_6 on article_category (user_id);
alter table article_comment add constraint fk_article_comment_replyFrom_7 foreign key (comment_parent_id) references article_comment (id);
create index ix_article_comment_replyFrom_7 on article_comment (comment_parent_id);
alter table article_comment add constraint fk_article_comment_brand_8 foreign key (brand_id) references brand (id);
create index ix_article_comment_brand_8 on article_comment (brand_id);
alter table article_comment add constraint fk_article_comment_article_9 foreign key (article_id) references article (id);
create index ix_article_comment_article_9 on article_comment (article_id);
alter table article_comment add constraint fk_article_comment_userCms_10 foreign key (approve_by) references user_cms (id);
create index ix_article_comment_userCms_10 on article_comment (approve_by);
alter table attribute add constraint fk_attribute_baseAttribute_11 foreign key (base_attribute_id) references base_attribute (id);
create index ix_attribute_baseAttribute_11 on attribute (base_attribute_id);
alter table attribute add constraint fk_attribute_brand_12 foreign key (brand_id) references brand (id);
create index ix_attribute_brand_12 on attribute (brand_id);
alter table banner add constraint fk_banner_brand_13 foreign key (brand_id) references brand (id);
create index ix_banner_brand_13 on banner (brand_id);
alter table banner add constraint fk_banner_userCms_14 foreign key (user_id) references user_cms (id);
create index ix_banner_userCms_14 on banner (user_id);
alter table base_attribute add constraint fk_base_attribute_brand_15 foreign key (brand_id) references brand (id);
create index ix_base_attribute_brand_15 on base_attribute (brand_id);
alter table best_seller add constraint fk_best_seller_brand_16 foreign key (brand_id) references brand (id);
create index ix_best_seller_brand_16 on best_seller (brand_id);
alter table best_seller add constraint fk_best_seller_product_17 foreign key (product_id) references product (id);
create index ix_best_seller_product_17 on best_seller (product_id);
alter table best_seller add constraint fk_best_seller_userCms_18 foreign key (user_id) references user_cms (id);
create index ix_best_seller_userCms_18 on best_seller (user_id);
alter table best_seller_banner add constraint fk_best_seller_banner_brand_19 foreign key (brand_id) references brand (id);
create index ix_best_seller_banner_brand_19 on best_seller_banner (brand_id);
alter table best_seller_banner add constraint fk_best_seller_banner_userCms_20 foreign key (user_id) references user_cms (id);
create index ix_best_seller_banner_userCms_20 on best_seller_banner (user_id);
alter table brand add constraint fk_brand_userCms_21 foreign key (user_id) references user_cms (id);
create index ix_brand_userCms_21 on brand (user_id);
alter table category add constraint fk_category_brand_22 foreign key (brand_id) references brand (id);
create index ix_category_brand_22 on category (brand_id);
alter table category add constraint fk_category_userCms_23 foreign key (user_id) references user_cms (id);
create index ix_category_userCms_23 on category (user_id);
alter table category add constraint fk_category_parentCategory_24 foreign key (parent_id) references category (id);
create index ix_category_parentCategory_24 on category (parent_id);
alter table category_banner add constraint fk_category_banner_brand_25 foreign key (brand_id) references brand (id);
create index ix_category_banner_brand_25 on category_banner (brand_id);
alter table category_banner add constraint fk_category_banner_userCms_26 foreign key (user_id) references user_cms (id);
create index ix_category_banner_userCms_26 on category_banner (user_id);
alter table config_settings add constraint fk_config_settings_brand_27 foreign key (brand_id) references brand (id);
create index ix_config_settings_brand_27 on config_settings (brand_id);
alter table faq add constraint fk_faq_brand_28 foreign key (brand_id) references brand (id);
create index ix_faq_brand_28 on faq (brand_id);
alter table faq add constraint fk_faq_faqGroup_29 foreign key (faq_group_id) references information_category_group (id);
create index ix_faq_faqGroup_29 on faq (faq_group_id);
alter table faq add constraint fk_faq_userCms_30 foreign key (user_id) references user_cms (id);
create index ix_faq_userCms_30 on faq (user_id);
alter table footer add constraint fk_footer_brand_31 foreign key (brand_id) references brand (id);
create index ix_footer_brand_31 on footer (brand_id);
alter table footer add constraint fk_footer_staticPage_32 foreign key (static_page_id) references page (id);
create index ix_footer_staticPage_32 on footer (static_page_id);
alter table footer add constraint fk_footer_userCms_33 foreign key (user_id) references user_cms (id);
create index ix_footer_userCms_33 on footer (user_id);
alter table information_category_group add constraint fk_information_category_group_34 foreign key (user_id) references user_cms (id);
create index ix_information_category_group_34 on information_category_group (user_id);
alter table information_category_group add constraint fk_information_category_group_35 foreign key (brand_id) references brand (id);
create index ix_information_category_group_35 on information_category_group (brand_id);
alter table instagram_banner add constraint fk_instagram_banner_brand_36 foreign key (brand_id) references brand (id);
create index ix_instagram_banner_brand_36 on instagram_banner (brand_id);
alter table instagram_banner add constraint fk_instagram_banner_userCms_37 foreign key (user_id) references user_cms (id);
create index ix_instagram_banner_userCms_37 on instagram_banner (user_id);
alter table member_log add constraint fk_member_log_member_38 foreign key (member_id) references member (id);
create index ix_member_log_member_38 on member_log (member_id);
alter table new_arrival add constraint fk_new_arrival_brand_39 foreign key (brand_id) references brand (id);
create index ix_new_arrival_brand_39 on new_arrival (brand_id);
alter table new_arrival add constraint fk_new_arrival_product_40 foreign key (product_id) references product (id);
create index ix_new_arrival_product_40 on new_arrival (product_id);
alter table new_arrival add constraint fk_new_arrival_userCms_41 foreign key (user_id) references user_cms (id);
create index ix_new_arrival_userCms_41 on new_arrival (user_id);
alter table new_arrival_banner add constraint fk_new_arrival_banner_brand_42 foreign key (brand_id) references brand (id);
create index ix_new_arrival_banner_brand_42 on new_arrival_banner (brand_id);
alter table new_arrival_banner add constraint fk_new_arrival_banner_userCms_43 foreign key (user_id) references user_cms (id);
create index ix_new_arrival_banner_userCms_43 on new_arrival_banner (user_id);
alter table on_sale_banner add constraint fk_on_sale_banner_brand_44 foreign key (brand_id) references brand (id);
create index ix_on_sale_banner_brand_44 on on_sale_banner (brand_id);
alter table on_sale_banner add constraint fk_on_sale_banner_userCms_45 foreign key (user_id) references user_cms (id);
create index ix_on_sale_banner_userCms_45 on on_sale_banner (user_id);
alter table product add constraint fk_product_brand_46 foreign key (brand_id) references brand (id);
create index ix_product_brand_46 on product (brand_id);
alter table product add constraint fk_product_category_47 foreign key (category_id) references category (id);
create index ix_product_category_47 on product (category_id);
alter table product add constraint fk_product_parentCategory_48 foreign key (parent_category_id) references category (id);
create index ix_product_parentCategory_48 on product (parent_category_id);
alter table product add constraint fk_product_grandParentCategor_49 foreign key (grand_parent_category_id) references category (id);
create index ix_product_grandParentCategor_49 on product (grand_parent_category_id);
alter table product add constraint fk_product_userCms_50 foreign key (user_id) references user_cms (id);
create index ix_product_userCms_50 on product (user_id);
alter table product add constraint fk_product_approvedBy_51 foreign key (approved_by_id) references user_cms (id);
create index ix_product_approvedBy_51 on product (approved_by_id);
alter table role_feature add constraint fk_role_feature_feature_52 foreign key (feature_id) references feature (id);
create index ix_role_feature_feature_52 on role_feature (feature_id);
alter table role_feature add constraint fk_role_feature_role_53 foreign key (role_id) references role (id);
create index ix_role_feature_role_53 on role_feature (role_id);
alter table page add constraint fk_page_userCms_54 foreign key (user_id) references user_cms (id);
create index ix_page_userCms_54 on page (user_id);
alter table page add constraint fk_page_brand_55 foreign key (brand_id) references brand (id);
create index ix_page_brand_55 on page (brand_id);
alter table tag add constraint fk_tag_brand_56 foreign key (brand_id) references brand (id);
create index ix_tag_brand_56 on tag (brand_id);
alter table user_cms add constraint fk_user_cms_role_57 foreign key (role_id) references role (id);
create index ix_user_cms_role_57 on user_cms (role_id);
alter table user_cms add constraint fk_user_cms_brand_58 foreign key (brand_id) references brand (id);
create index ix_user_cms_brand_58 on user_cms (brand_id);
alter table user_log add constraint fk_user_log_user_59 foreign key (user_id) references user_cms (id);
create index ix_user_log_user_59 on user_log (user_id);



alter table banner_product add constraint fk_banner_product_banner_01 foreign key (banner_id) references banner (id);

alter table banner_product add constraint fk_banner_product_product_02 foreign key (product_id) references product (id);

alter table banner_category add constraint fk_banner_category_banner_01 foreign key (banner_id) references banner (id);

alter table banner_category add constraint fk_banner_category_category_02 foreign key (category_id) references category (id);

alter table best_seller_banner_category add constraint fk_best_seller_banner_categor_01 foreign key (best_seller_banner_id) references best_seller_banner (id);

alter table best_seller_banner_category add constraint fk_best_seller_banner_categor_02 foreign key (category_id) references category (id);

alter table best_seller_banner_product add constraint fk_best_seller_banner_product_01 foreign key (best_seller_banner_id) references best_seller_banner (id);

alter table best_seller_banner_product add constraint fk_best_seller_banner_product_02 foreign key (product_id) references product (id);

alter table category_base_attribute add constraint fk_category_base_attribute_ca_01 foreign key (category_id) references category (id);

alter table category_base_attribute add constraint fk_category_base_attribute_ba_02 foreign key (base_attribute_id) references base_attribute (id);

alter table category_banner_category add constraint fk_category_banner_category_c_01 foreign key (category_banner_id) references category_banner (id);

alter table category_banner_category add constraint fk_category_banner_category_c_02 foreign key (category_id) references category (id);

alter table category_banner_product add constraint fk_category_banner_product_ca_01 foreign key (category_banner_id) references category_banner (id);

alter table category_banner_product add constraint fk_category_banner_product_pr_02 foreign key (product_id) references product (id);

alter table instagram_banner_category add constraint fk_instagram_banner_category__01 foreign key (instagram_banner_id) references instagram_banner (id);

alter table instagram_banner_category add constraint fk_instagram_banner_category__02 foreign key (category_id) references category (id);

alter table instagram_banner_product add constraint fk_instagram_banner_product_i_01 foreign key (instagram_banner_id) references instagram_banner (id);

alter table instagram_banner_product add constraint fk_instagram_banner_product_p_02 foreign key (product_id) references product (id);

alter table new_arrival_banner_category add constraint fk_new_arrival_banner_categor_01 foreign key (new_arrival_banner_id) references new_arrival_banner (id);

alter table new_arrival_banner_category add constraint fk_new_arrival_banner_categor_02 foreign key (category_id) references category (id);

alter table new_arrival_banner_product add constraint fk_new_arrival_banner_product_01 foreign key (new_arrival_banner_id) references new_arrival_banner (id);

alter table new_arrival_banner_product add constraint fk_new_arrival_banner_product_02 foreign key (product_id) references product (id);

alter table on_sale_banner_category add constraint fk_on_sale_banner_category_on_01 foreign key (on_sale_banner_id) references on_sale_banner (id);

alter table on_sale_banner_category add constraint fk_on_sale_banner_category_ca_02 foreign key (category_id) references category (id);

alter table on_sale_banner_product add constraint fk_on_sale_banner_product_on__01 foreign key (on_sale_banner_id) references on_sale_banner (id);

alter table on_sale_banner_product add constraint fk_on_sale_banner_product_pro_02 foreign key (product_id) references product (id);

alter table product_base_attribute add constraint fk_product_base_attribute_pro_01 foreign key (product_id) references product (id);

alter table product_base_attribute add constraint fk_product_base_attribute_bas_02 foreign key (base_attribute_id) references base_attribute (id);

alter table product_attribute add constraint fk_product_attribute_product_01 foreign key (product_id) references product (id);

alter table product_attribute add constraint fk_product_attribute_attribut_02 foreign key (attribute_id) references attribute (id);

alter table tag_article add constraint fk_tag_article_tag_01 foreign key (tag_id) references tag (id);

alter table tag_article add constraint fk_tag_article_article_02 foreign key (article_id) references article (id);

alter table tag_product add constraint fk_tag_product_tag_01 foreign key (tag_id) references tag (id);

alter table tag_product add constraint fk_tag_product_product_02 foreign key (product_id) references product (id);

# --- !Downs

drop table if exists article cascade;

drop table if exists tag_article cascade;

drop table if exists article_category cascade;

drop table if exists article_comment cascade;

drop table if exists attribute cascade;

drop table if exists banner cascade;

drop table if exists banner_product cascade;

drop table if exists banner_category cascade;

drop table if exists base_attribute cascade;

drop table if exists best_seller cascade;

drop table if exists best_seller_banner cascade;

drop table if exists best_seller_banner_category cascade;

drop table if exists best_seller_banner_product cascade;

drop table if exists brand cascade;

drop table if exists category cascade;

drop table if exists category_base_attribute cascade;

drop table if exists category_banner cascade;

drop table if exists category_banner_category cascade;

drop table if exists category_banner_product cascade;

drop table if exists change_log cascade;

drop table if exists config_settings cascade;

drop table if exists faq cascade;

drop table if exists feature cascade;

drop table if exists footer cascade;

drop table if exists images cascade;

drop table if exists information_category_group cascade;

drop table if exists instagram_banner cascade;

drop table if exists instagram_banner_category cascade;

drop table if exists instagram_banner_product cascade;

drop table if exists member cascade;

drop table if exists member_log cascade;

drop table if exists new_arrival cascade;

drop table if exists new_arrival_banner cascade;

drop table if exists new_arrival_banner_category cascade;

drop table if exists new_arrival_banner_product cascade;

drop table if exists on_sale_banner cascade;

drop table if exists on_sale_banner_category cascade;

drop table if exists on_sale_banner_product cascade;

drop table if exists photo cascade;

drop table if exists product cascade;

drop table if exists product_base_attribute cascade;

drop table if exists product_attribute cascade;

drop table if exists tag_product cascade;

drop table if exists role cascade;

drop table if exists role_feature cascade;

drop table if exists page cascade;

drop table if exists tag cascade;

drop table if exists user_cms cascade;

drop table if exists user_log cascade;

drop sequence if exists article_seq;

drop sequence if exists article_category_seq;

drop sequence if exists article_comment_seq;

drop sequence if exists attribute_seq;

drop sequence if exists banner_seq;

drop sequence if exists base_attribute_seq;

drop sequence if exists best_seller_seq;

drop sequence if exists best_seller_banner_seq;

drop sequence if exists brand_seq;

drop sequence if exists category_seq;

drop sequence if exists category_banner_seq;

drop sequence if exists config_settings_seq;

drop sequence if exists faq_seq;

drop sequence if exists feature_seq;

drop sequence if exists footer_seq;

drop sequence if exists images_seq;

drop sequence if exists information_category_group_seq;

drop sequence if exists instagram_banner_seq;

drop sequence if exists member_seq;

drop sequence if exists member_log_seq;

drop sequence if exists new_arrival_seq;

drop sequence if exists new_arrival_banner_seq;

drop sequence if exists on_sale_banner_seq;

drop sequence if exists photo_seq;

drop sequence if exists product_seq;

drop sequence if exists role_seq;

drop sequence if exists page_seq;

drop sequence if exists tag_seq;

drop sequence if exists user_cms_seq;

drop sequence if exists user_log_seq;

