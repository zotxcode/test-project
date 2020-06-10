# --- !Ups

ALTER TABLE "member" DROP CONSTRAINT "uq_member_email";
ALTER TABLE "member" DROP CONSTRAINT "uq_member_phone";
ALTER TABLE "member" DROP CONSTRAINT "uq_member_facebook_user_id";
ALTER TABLE "member" DROP CONSTRAINT "uq_member_google_user_id";

ALTER TABLE "article_category" DROP CONSTRAINT "uq_article_category_name";
ALTER TABLE "attribute" DROP CONSTRAINT "uq_attribute_1";
ALTER TABLE "base_attribute" DROP CONSTRAINT "uq_base_attribute_name";
ALTER TABLE "config_settings" DROP CONSTRAINT "uq_config_settings_key";
ALTER TABLE "product" DROP CONSTRAINT "uq_product_sku";
ALTER TABLE "tag" DROP CONSTRAINT "uq_tag_name";
ALTER TABLE "user_cms" DROP CONSTRAINT "uq_user_cms_email";

# --- !Downs

