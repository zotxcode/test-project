import models.*;

/**
 * @author hendriksaragih
 */
public class Seed {
    public static void seedTestingRoleFeatureUser() {
        if (Feature.find.findRowCount() == 0 && Role.find.findRowCount() == 0 && UserCms.find.findRowCount() == 0) {
            Feature brandF = new Feature("Brand", "User Management", "Can view, create, and edit brand data.", true);
            brandF.save();
            Feature roleF = new Feature("Role", "User Management", "Can view, create and edit user's role data.", true);
            roleF.save();
            Feature userF = new Feature("User", "User Management", "Can view, create and edit cms user data.", true);
            userF.save();
            Feature dashboardF = new Feature("Admin dashboard", "",
                    "Can view graphic about new users and visitors with selected filters.", true);
            dashboardF.save();

            Feature bannerF = new Feature("Banner", "Information", "Can view, create, and edit banner data.", true);
            bannerF.save();
            Feature naBannerF = new Feature("New Arrival Banner", "Information", "Can view, create, and edit banner data.", true);
            naBannerF.save();
            Feature catBannerF = new Feature("Category Banner", "Information", "Can view, create, and edit banner data.", true);
            catBannerF.save();
            Feature osBannerF = new Feature("On Sale Banner", "Information", "Can view, create, and edit banner data.", true);
            osBannerF.save();
            Feature bsBannerF = new Feature("Best Seller Banner", "Information", "Can view, create, and edit banner data.", true);
            bsBannerF.save();
            Feature igBannerF = new Feature("Instagram Banner", "Information", "Can view, create, and edit banner data.", true);
            igBannerF.save();
            Feature newArrivalF = new Feature("New Arrival", "Information", "Can view, create, and edit banner data.", true);
            newArrivalF.save();
            Feature bestSellerF = new Feature("Best Seller", "Information", "Can view, create, and edit banner data.", true);
            bestSellerF.save();

            Feature faqF = new Feature("FAQ", "Information", "Can view, create, and edit FAQ data.", true);
            faqF.save();
            Feature pageF = new Feature("StaticPage", "Information", "Can view, create, and edit static page data.",
                    true);
            pageF.save();
            Feature articleF = new Feature("Article", "Information", "Can view, create, and edit article data.", true);
            articleF.save();
            Feature articleCF = new Feature("Article Comment", "Information", "Can view, create, and edit article data.", true);
            articleCF.save();
            Feature locationCF = new Feature("Location Setting", "Information", "Can view, create, and edit data.", true);
            locationCF.save();
            Feature osCF = new Feature("Our Story", "Information", "Can view, create, and edit data.", true);
            osCF.save();
            Feature categoryCF = new Feature("Category", "Master Products", "Can view, create, and edit data.", true);
            categoryCF.save();
            Feature allergyCF = new Feature("Allergy", "Master Allergies", "Can view, create, and edit data.", true);
            allergyCF.save();

            // role
            Role superAdmin = new Role("Super Admin", "Super Administrator", true);
            superAdmin.save();
            Role adminR = new Role("Admin", "Administrator", true);
            adminR.save();
            Role staffR = new Role("Staff", "Staff", true);
            staffR.save();

            RoleFeature.setRoles(superAdmin, roleF, userF, dashboardF, brandF);
            RoleFeature.setRoles(adminR, bannerF, faqF, articleF, pageF, articleCF, locationCF, osCF, categoryCF, allergyCF,
                    naBannerF, osBannerF, bsBannerF, igBannerF, newArrivalF, bestSellerF, catBannerF);


            Brand milorB = new Brand("Enwie", "www.enwie.com", "Jl enwie", "email@enwie.com");


            // user
            try {
                UserCms admin = new UserCms("password", "Super", "Admin", "superadmin@enwie.com", "", "M",
                        "1945-8-17");
                admin.role = superAdmin;
                admin.save();

                UserCms millor = new UserCms("password", "Enwie", "Admin", "admin@enwie.com", "", "M",
                        "1945-8-17");
                millor.role = adminR;
                millor.brand = milorB;
                millor.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void seedSettings() {
        if (ConfigSettings.find.findRowCount() == 0) {
            ConfigSettings.seed("information", "location_setting", 1L);
            ConfigSettings.seed("information", "location_setting", 2L);

            ConfigSettings.seed("information", "our_story", 1L);
            ConfigSettings.seed("information", "our_story", 2L);
        }
    }

    public static void seedTestingProduct() {
        if (Category.find.findRowCount() == 0) {
            // Level 1
            Category cat1 = Category.seed("Back Pack", 1L, 0L, 1L);
            Category cat2 = Category.seed("Accessories", 1L, 0L, 1L);
            Category cat3 = Category.seed("BACKPACK", 1L, 0L, 2L);
            Category cat4 = Category.seed("TOTEBAG", 1L, 0L, 2L);
            Category cat5 = Category.seed("POUCH", 1L, 0L, 2L);

            // Level 2
            Category cat11 = Category.seed("Backcpack", 1L, cat1.id, 1L);
            Category cat12 = Category.seed("Sling Bag", 1L, cat1.id, 1L);
            Category cat21 = Category.seed("Accessories", 1L, cat2.id, 1L);
            Category cat31 = Category.seed("Wielki Backpack", 1L, cat3.id, 2L);
            Category cat32 = Category.seed("One Shoulder Backpack", 1L, cat3.id, 2L);
            Category cat41 = Category.seed("Totebag", 1L, cat4.id, 2L);
            Category cat51 = Category.seed("Pouch", 1L, cat5.id, 2L);

            // Level 3
            Category cat111 = Category.seed("Backcpack", 1L, cat11.id, 1L);
            Category cat121 = Category.seed("Sling Bag", 1L, cat12.id, 1L);
            Category cat211 = Category.seed("Accessories", 1L, cat21.id, 1L);
            Category cat311 = Category.seed("Wielki Backpack", 1L, cat31.id, 2L);
            Category cat321 = Category.seed("One Shoulder Backpack", 1L, cat32.id, 2L);
            Category cat411 = Category.seed("Totebag", 1L, cat41.id, 2L);
            Category cat511 = Category.seed("Pouch", 1L, cat51.id, 2L);

            Product.seed("sku111", "Product Backpack", 1L, cat111.id, 1000D);
            Product.seed("cat121", "Product Sling Bag", 1L, cat121.id, 1000D);
            Product.seed("cat211", "Product Accessories", 1L, cat211.id, 1000D);
            Product.seed("cat311", "Product Wielki Backpack", 2L, cat311.id, 1000D);
            Product.seed("cat321", "Product One Shoulder Backpack", 2L, cat321.id, 1000D);
            Product.seed("cat411", "Product Totebag", 2L, cat411.id, 1000D);
            Product.seed("cat511", "Product Pouch", 2L, cat511.id, 1000D);
        }
    }
}
