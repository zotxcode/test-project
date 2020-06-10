
-- 2018-08-05 10:23
INSERT INTO feature ("id", "is_deleted", "name", "key", "section", "description", "is_active", "created_at", "updated_at", "sequence") VALUES ('20', 'f', 'On Sale', 'on_sale', 'Information', 'Can view, create, and edit.', 't', '2017-06-09 05:44:40.246', '2017-06-09 05:44:40.246', '1');
INSERT INTO role_feature VALUES ('20', '2', '210');

-- 2018-08-14 10:23
INSERT INTO feature ("id", "is_deleted", "name", "key", "section", "description", "is_active", "created_at", "updated_at", "sequence") VALUES ('21', 'f', 'Blacklist Setting', 'blacklist', 'Customers', 'Can view, create, and edit.', 't', '2017-06-09 05:44:40.246', '2017-06-09 05:44:40.246', '1');
INSERT INTO role_feature VALUES ('21', '2', '210');

-- 2018-08-14 21:58
INSERT INTO feature ("id", "is_deleted", "name", "key", "section", "description", "is_active", "created_at", "updated_at", "sequence") VALUES ('22', 'f', 'Master Attribute', 'attribute', 'Master Products', 'Can view, create, and edit attribute data.', 't', '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294', '1');
INSERT INTO role_feature VALUES ('22', '2', '210');

INSERT INTO feature ("id", "is_deleted", "name", "key", "section", "description", "is_active", "created_at", "updated_at", "sequence") VALUES ('23', 'f', 'Master Size', 'size', 'Master Products', 'Can view, create, and edit size data.', 't', '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294', '19');
INSERT INTO role_feature VALUES ('23', '2', '210');

INSERT INTO feature ("id", "is_deleted", "name", "key", "section", "description", "is_active", "created_at", "updated_at", "sequence") VALUES ('24', 'f', 'Customer List', 'customer', 'Customers', 'Can view, create, and edit customer data.', 't', '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294', '14');
INSERT INTO role_feature VALUES ('24', '2', '210');

INSERT INTO feature ("id", "is_deleted", "name", "key", "section", "description", "is_active", "created_at", "updated_at", "sequence") VALUES ('25', 'f', 'Footer', 'footer', 'Information', 'Can view, create, and edit footer data.', 't', '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294', '11');
INSERT INTO role_feature VALUES ('25', '2', '210');

-- 2018-08-19 20:30
INSERT INTO feature ("id", "is_deleted", "name", "key", "section", "description", "is_active", "created_at", "updated_at", "sequence") VALUES ('26', 'f', 'Contact Us', 'contact_us', 'Customers', 'Can view, create, and edit data.', 't', '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294', '11');
INSERT INTO role_feature VALUES ('26', '2', '210');

INSERT INTO feature ("id", "is_deleted", "name", "key", "section", "description", "is_active", "created_at", "updated_at", "sequence") VALUES ('27', 'f', 'Socmed', 'socmed', 'Information', 'Can view, create, and edit data.', 't', '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294', '11');
INSERT INTO role_feature VALUES ('27', '2', '210');

INSERT INTO feature ("id", "is_deleted", "name", "key", "section", "description", "is_active", "created_at", "updated_at", "sequence") VALUES ('28', 'f', 'Hashtag', 'hashtag', 'Preference', 'Can view, and edit data.', 't', '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294', '11');
INSERT INTO role_feature VALUES ('29', '2', '210');

INSERT INTO config_settings ("id", "is_deleted", "module", "key", "name", "value", "brand_id","created_at", "updated_at") VALUES ('5', 'f', 'preference', 'hashtag', 'hashtag', 'hashtagEversac', 1,'2017-07-26 07:09:00', '2017-07-26 07:09:03');
INSERT INTO config_settings ("id", "is_deleted", "module", "key", "name", "value", "brand_id","created_at", "updated_at") VALUES ('6', 'f', 'preference', 'hashtag', 'hashtag', 'hashtagMilors', 2,'2017-07-26 07:09:00', '2017-07-26 07:09:03');

-- 2018-08-28 10:36
INSERT INTO feature ("id", "is_deleted", "name", "key", "section", "description", "is_active", "created_at", "updated_at", "sequence") VALUES ('29', 'f', 'Bank Account', 'bank', 'Shop', 'Can view, create, and edit bank data.', 't', '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294', '29');
INSERT INTO role_feature VALUES ('29', '2', '210');

-- 2018-08-30 09:47
INSERT INTO feature ("id", "is_deleted", "name", "key", "section", "description", "is_active", "created_at", "updated_at", "sequence") VALUES ('30', 'f', 'New Product', 'newproduct', 'Products', 'Can create product.', 't', '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294', '19');
INSERT INTO role_feature VALUES ('30', '2', '210');
INSERT INTO feature ("id", "is_deleted", "name", "key", "section", "description", "is_active", "created_at", "updated_at", "sequence") VALUES ('31', 'f', 'Grouping Product', 'grouping', 'Products', 'Can view, create, and edit grouping product data.', 't', '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294', '21');
INSERT INTO role_feature VALUES ('31', '2', '210');
INSERT INTO feature ("id", "is_deleted", "name", "key", "section", "description", "is_active", "created_at", "updated_at", "sequence") VALUES ('32', 'f', 'Grouping Variant Product', 'variant', 'Products', 'Can view, create, and edit grouping variant product data.', 't', '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294', '22');
INSERT INTO role_feature VALUES ('32', '2', '210');
INSERT INTO feature ("id", "is_deleted", "name", "key", "section", "description", "is_active", "created_at", "updated_at", "sequence") VALUES ('33', 'f', 'Product List', 'product', 'Products', 'Can view, create, and edit product data.', 't', '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294', '23');
INSERT INTO role_feature VALUES ('33', '2', '210');
INSERT INTO feature ("id", "is_deleted", "name", "key", "section", "description", "is_active", "created_at", "updated_at", "sequence") VALUES ('34', 'f', 'Product Reviews', 'productreview', 'Products', 'Can view, create, and edit product review data.', 't', '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294', '25');
INSERT INTO role_feature VALUES ('34', '2', '210');

-- 2018-09-16 09:47
INSERT INTO config_settings("id", "is_deleted", "module", "key", "name", "value", "brand_id", "created_at", "updated_at") VALUES (7, 'f', 'preference', 'seo', 'seo', 'seoEversac', 1, '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294');
INSERT INTO config_settings("id", "is_deleted", "module", "key", "name", "value", "brand_id", "created_at", "updated_at") VALUES (8, 'f', 'preference', 'seo', 'seo', 'seoMilors', 2, '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294');

INSERT INTO feature ("id", "is_deleted", "name", "key", "section", "description", "is_active", "created_at", "updated_at", "sequence") VALUES ('35', 'f', 'SEO', 'seo', 'Preference', 'Can view, and edit data.', 't', '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294', '11');
INSERT INTO role_feature VALUES ('35', '2', '210');

INSERT INTO config_settings("id", "is_deleted", "module", "key", "name", "value", "brand_id", "created_at", "updated_at") VALUES (9, 'f', 'preference', 'cartbutton', 'cartbutton', 't', 1, '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294');
INSERT INTO config_settings("id", "is_deleted", "module", "key", "name", "value", "brand_id", "created_at", "updated_at") VALUES (10, 'f', 'preference', 'cartbutton', 'cartbutton', 'f', 2, '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294');

INSERT INTO feature ("id", "is_deleted", "name", "key", "section", "description", "is_active", "created_at", "updated_at", "sequence") VALUES ('36', 'f', 'Cart Button', 'cartbutton', 'Preference', 'Can view, and edit data.', 't', '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294', '11');
INSERT INTO role_feature VALUES ('36', '2', '210');

INSERT INTO config_settings("id", "is_deleted", "module", "key", "name", "value", "brand_id", "created_at", "updated_at") VALUES (11, 'f', 'preference', 'theme', 'theme', '#000000', 1, '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294');
INSERT INTO config_settings("id", "is_deleted", "module", "key", "name", "value", "brand_id", "created_at", "updated_at") VALUES (12, 'f', 'preference', 'theme', 'theme', '#000000', 2, '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294');

INSERT INTO feature ("id", "is_deleted", "name", "key", "section", "description", "is_active", "created_at", "updated_at", "sequence") VALUES ('37', 'f', 'Themes', 'theme', 'Preference', 'Can view, and edit data.', 't', '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294', '11');
INSERT INTO role_feature VALUES ('37', '2', '210');

INSERT INTO payment_expiration("id", "is_deleted", "type", "total", "brand_id", "created_at", "updated_at") VALUES (1, 'f', 'hour', '24', 1, '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294');
INSERT INTO payment_expiration("id", "is_deleted", "type", "total", "brand_id", "created_at", "updated_at") VALUES (2, 'f', 'hour', '24', 2, '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294');

INSERT INTO feature ("id", "is_deleted", "name", "key", "section", "description", "is_active", "created_at", "updated_at", "sequence") VALUES ('38', 'f', 'Setting Expire Payment', 'expirepaymentsetting', 'Preference', 'Can view, and edit data.', 't', '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294', '11');
INSERT INTO role_feature VALUES ('38', '2', '210');

-- 2018-09-20 20:37
INSERT INTO feature ("id", "is_deleted", "name", "key", "section", "description", "is_active", "created_at", "updated_at", "sequence") VALUES ('39', 'f', 'Setting Voucher Signup', 'vouchersignupvaluesetting', 'Preference', 'Can view, and edit data.', 't', '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294', '11');
INSERT INTO role_feature VALUES ('39', '2', '210');


INSERT INTO voucher_sign_up("id", "is_deleted", "brand_id", "user_id", "user_param", "periode", "prefix_kupon", "client_id", "besaran_voucher", "minimum_belanja", "created_at", "updated_at") VALUES (1, 'f', 1, 1, 'email,name', 10, 'EVS', 'eversac', 100000, 10000, '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294');
INSERT INTO voucher_sign_up("id", "is_deleted", "brand_id", "user_id", "user_param", "periode", "prefix_kupon", "client_id", "besaran_voucher", "minimum_belanja", "created_at", "updated_at") VALUES (2, 'f', 2, 1, 'email,name', 10, 'MLR', 'milors', 100000, 10000, '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294');


-- 2018-10-01 20:37
INSERT INTO feature ("id", "is_deleted", "name", "key", "section", "description", "is_active", "created_at", "updated_at", "sequence") VALUES ('40', 'f', 'Master Courier', 'courier', 'Shop', 'Can view, create, and edit master courier data.', 't', '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294', '32');
INSERT INTO role_feature VALUES ('40', '2', '210');

-- 2018-10-08 21:55
INSERT INTO feature ("id", "is_deleted", "name", "key", "section", "description", "is_active", "created_at", "updated_at", "sequence") VALUES ('41', 'f', 'Voucher', 'voucher', 'Voucher', 'Can view, create, and edit voucher data.', 't', '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294', '33');
INSERT INTO role_feature VALUES ('41', '2', '210');

INSERT INTO feature ("id", "is_deleted", "name", "key", "section", "description", "is_active", "created_at", "updated_at", "sequence") VALUES ('42', 'f', 'Promotion', 'promotion', 'Promotion', 'Can view, create, and edit promotion data.', 't', '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294', '33');
INSERT INTO role_feature VALUES ('42', '2', '210');

-- 2018-10-09 04:17
INSERT INTO feature ("id", "is_deleted", "name", "key", "section", "description", "is_active", "created_at", "updated_at", "sequence") VALUES ('43', 'f', 'Orders', 'order', 'Shop', 'Can view, create, and edit Shop data.', 't', '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294', '34');
INSERT INTO role_feature VALUES ('43', '2', '210');

INSERT INTO feature ("id", "is_deleted", "name", "key", "section", "description", "is_active", "created_at", "updated_at", "sequence") VALUES ('44', 'f', 'Payment Confirmation', 'paymentconfirmation', 'Shop', 'Can view, create, and edit Payment Confirmation data.', 't', '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294', '34');
INSERT INTO role_feature VALUES ('44', '2', '210');
-- 2018-10-17 04:17
INSERT INTO feature ("id", "is_deleted", "name", "key", "section", "description", "is_active", "created_at", "updated_at", "sequence") VALUES ('45', 'f', 'Newsletter', 'newsletter', 'Newsletter', 'Can view, create, and edit newsletter data.', 't', '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294', '33');
INSERT INTO role_feature VALUES ('45', '2', '210');
-- 2019-10-28 04:17
INSERT INTO feature ("id", "is_deleted", "name", "key", "section", "description", "is_active", "created_at", "updated_at", "sequence") VALUES ('46', 'f', 'Allergy', 'allergy', 'Master Allergies', 'Can view, create, and edit newsletter data.', 't', '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294', '35');
INSERT INTO role_feature VALUES ('46', '2', '210');


-- 2019-11-03 19:07
INSERT INTO feature ("id", "is_deleted", "name", "key", "section", "description", "is_active", "created_at", "updated_at", "sequence") VALUES ('47', 'f', 'Purchase Order', 'purchase', 'Distributor', 'Can view, create, and edit po data.', 't', '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294', '36');
INSERT INTO feature ("id", "is_deleted", "name", "key", "section", "description", "is_active", "created_at", "updated_at", "sequence") VALUES ('48', 'f', 'Order Distributor', 'orderdistributor', 'Distributor', 'Can view, create, and edit po data.', 't', '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294', '37');
INSERT INTO feature ("id", "is_deleted", "name", "key", "section", "description", "is_active", "created_at", "updated_at", "sequence") VALUES ('49', 'f', 'Product Distributor', 'productdistributor', 'Products', 'Can view, create, and edit product data.', 't', '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294', '37');
INSERT INTO feature ("id", "is_deleted", "name", "key", "section", "description", "is_active", "created_at", "updated_at", "sequence") VALUES ('50', 'f', 'Order Reseller', 'orderreseller', 'Distributor', 'Can view, create, and edit po data.', 't', '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294', '37');

-- 2019-11-10 19:07
INSERT INTO feature ("id", "is_deleted", "name", "key", "section", "description", "is_active", "created_at", "updated_at", "sequence") VALUES ('51', 'f', 'Presentation', 'presentation', 'Presentation', 'Can view, create, and edit data.', 't', '2017-06-09 05:44:40.294', '2017-06-09 05:44:40.294', '37');

-- 2019-12-27 05:43
CREATE OR REPLACE FUNCTION update_merchant_balance() RETURNS TRIGGER AS $emp_audit$
    DECLARE
        delta_balance_old       numeric(15,2);
        delta_balance_new       numeric(15,2);
    BEGIN
        IF (TG_OP = 'DELETE') THEN
            delta_balance_old := OLD.type * OLD.value;
            UPDATE member SET balance = (balance - delta_balance_old) where id = OLD.member_id;
        ELSIF (TG_OP = 'UPDATE') THEN
            delta_balance_old := OLD.type * OLD.value;
            delta_balance_new := NEW.type * NEW.value;
            UPDATE member SET balance = ((balance - delta_balance_old) + delta_balance_new) where id = NEW.member_id;
        ELSIF (TG_OP = 'INSERT') THEN
            delta_balance_new := NEW.type * NEW.value;
            UPDATE member SET balance = (balance + delta_balance_new) where id = NEW.member_id;
        END IF;
        RETURN NULL;
    END;
$emp_audit$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION update_distributor_balance() RETURNS TRIGGER AS $emp_audit$
    DECLARE
        delta_balance_old       numeric(15,2);
        delta_balance_new       numeric(15,2);
    BEGIN
        IF (TG_OP = 'DELETE') THEN
            delta_balance_old := OLD.type * OLD.value;
            UPDATE user_cms SET balance = (balance - delta_balance_old) where id = OLD.user_cms_id;
        ELSIF (TG_OP = 'UPDATE') THEN
            delta_balance_old := OLD.type * OLD.value;
            delta_balance_new := NEW.type * NEW.value;
            UPDATE user_cms SET balance = ((balance - delta_balance_old) + delta_balance_new) where id = NEW.user_cms_id;
        ELSIF (TG_OP = 'INSERT') THEN
            delta_balance_new := NEW.type * NEW.value;
            UPDATE user_cms SET balance = (balance + delta_balance_new) where id = NEW.user_cms_id;
        END IF;
        RETURN NULL;
    END;
$emp_audit$ LANGUAGE plpgsql;

CREATE TRIGGER check_merchant_balance
AFTER INSERT OR UPDATE OR DELETE ON member_mutation_balance
    FOR EACH ROW EXECUTE PROCEDURE update_merchant_balance();

CREATE TRIGGER check_distributor_balance
AFTER INSERT OR UPDATE OR DELETE ON distributor_mutation_balance
    FOR EACH ROW EXECUTE PROCEDURE update_distributor_balance();

