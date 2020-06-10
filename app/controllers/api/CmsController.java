package controllers.api;

import com.enwie.service.InstagramService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.enwie.api.BaseResponse;
import com.enwie.mapper.response.*;
import com.enwie.util.Constant;
import com.enwie.util.MailConfig;
import com.wordnik.swagger.annotations.Api;
import controllers.BaseController;
import models.*;
import models.mapper.MapPromotion;
import models.mapper.MapVoucher;
import play.mvc.Result;
import play.libs.Json;

import java.util.*;

/**
 * @author hendriksaragih
 */
@Api(value = "/v1/cms", description = "CMS")
public class CmsController extends BaseController {
    private static BaseResponse response = new BaseResponse();

    public static Result categories() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<Category> query;
            query = Category.find.where().eq("is_deleted", false)
                    .eq("is_active", true)
                    .eq("parent_id", null)
                    .eq("brand_id", getBrandId())
                    .order("sequence asc").findList();
            for (Category c : query) {
                c.childCategory = Category.recGetAllChildCategory(c.id, getBrandId());
            }
            response.setBaseResponse(query.size(), offset, query.size(), success, new ObjectMapper().convertValue(query, MapCategory[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result faq(String search) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {

            Map<String, List> results = new HashMap<>();
            List<Map<String, Object>> all = new LinkedList<>();
            List<InformationCategoryGroup> groups = InformationCategoryGroup.getHomePage("faq", getBrandId());
            for (InformationCategoryGroup group : groups) {
                List<Faq> faqs = Faq.getHomePage(group.id, search, 0, getBrandId());
                if (faqs.size() > 0){
                    Map<String, Object> dt = new HashMap<>();
                    dt.put("group_id", group.id);
                    dt.put("group_name", group.name);
                    List<Map<String, Object>> details = new LinkedList<>();
                    for (Faq faq : faqs){
                        Map<String, Object> f = new HashMap<>();
                        f.put("faq_name", faq.name);
                        f.put("faq_id", faq.id);
                        f.put("faq_slug", faq.slug);
                        details.add(f);
                    }

                    dt.put("detail", details);
                    all.add(dt);
                }
            }

            List<Map<String, Object>> details = new LinkedList<>();
            Faq.getPopular(0, getBrandId()).forEach(faq->{
                Map<String, Object> f = new HashMap<>();
                f.put("faq_id", faq.id);
                f.put("faq_name", faq.name);
                f.put("faq_slug", faq.slug);
                details.add(f);
            });

            results.put("faq_favourite", details);
            results.put("faq_list", all);

            response.setBaseResponse(results.size(), offset, results.size(), success, results);
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result footer() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            Map<String, List<Footer>> result = new HashMap<>();
            result.put("left", Footer.getFooterByPosition("Left", getBrandId()));
            result.put("middle", Footer.getFooterByPosition("Middle", getBrandId()));
            result.put("right", Footer.getFooterByPosition("Right", getBrandId()));

            response.setBaseResponse(result.size(), offset, result.size(), success, new ObjectMapper().convertValue(result, MapFooterAll.class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result staticPage(String slug) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            StaticPage model = StaticPage.find.where()
                    .eq("slug", slug)
                    .eq("isDeleted", false)
                    .eq("brand_id", getBrandId())
                    .setMaxRows(1).findUnique();
            if (model != null) {
                response.setBaseResponse(1, offset, 1, success,
                        new ObjectMapper().convertValue(model, MapStaticPages.class));
                return ok(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return notFound(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result faqBySlug(String slug) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            Faq model = Faq.find.where()
                    .eq("slug", slug)
                    .eq("isDeleted", false)
                    .eq("brand_id", getBrandId())
                    .setMaxRows(1).findUnique();
            if (model != null) {
                model.viewCount = model.viewCount + 1;
                model.update();
                response.setBaseResponse(1, offset, 1, success,
                        new ObjectMapper().convertValue(model, MapFaq.class));
                return ok(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return notFound(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result categoryById(Long id) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<Category> query;
            query = Category.find
                    .fetch("parentCategory")
                    .where()
                    .eq("t0.is_active", true)
                    .eq("t0.is_deleted", false)
                    .eq("t1.is_deleted", false)
                    .eq("t1.parent_id", id)
                    .eq("t0.brand_id", getBrandId())
                    .order("t0.sequence asc").findList();

            response.setBaseResponse(query.size(), offset, query.size(), success, new ObjectMapper().convertValue(query, MapCategory[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result banner(int limit) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<Banner> banners = Banner.getAllBanner(getBrandId(), limit);

            response.setBaseResponse(banners.size(), offset, limit, success, new ObjectMapper().convertValue(banners, MapBanner[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result newArrivalBanner(int limit) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<NewArrivalBanner> banners = NewArrivalBanner.getAllBanner(getBrandId(), limit);

            response.setBaseResponse(banners.size(), offset, limit, success, new ObjectMapper().convertValue(banners, MapBanner[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result onSaleBanner(int limit) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<OnSaleBanner> banners = OnSaleBanner.getAllBanner(getBrandId(), limit);

            response.setBaseResponse(banners.size(), offset, limit, success, new ObjectMapper().convertValue(banners, MapBanner[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result bestSellerBanner(int limit) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<BestSellerBanner> banners = BestSellerBanner.getAllBanner(getBrandId(), limit);

            response.setBaseResponse(banners.size(), offset, limit, success, new ObjectMapper().convertValue(banners, MapBanner[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result instagramBanner(int limit) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<InstagramBanner> banners = InstagramBanner.getAllBanner(getBrandId(), limit);

            response.setBaseResponse(banners.size(), offset, limit, success, new ObjectMapper().convertValue(banners, MapBanner[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result categoryBanner(int limit) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<CategoryBanner> banners = CategoryBanner.getAllBanner(getBrandId(), limit);

            response.setBaseResponse(banners.size(), offset, limit, success, new ObjectMapper().convertValue(banners, MapBanner[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result configSetting(String module, String key) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            ConfigSettings data = ConfigSettings.findByData(getBrandId(), module, key);
            response.setBaseResponse(1, offset, 1, success, data.value);
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result newArrival(int limit) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<Product> data = NewArrival.getAllData(getBrandId(), limit);
            response.setBaseResponse(data.size(), offset, limit, success, new ObjectMapper().convertValue(data, MapProductList[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result bestSeller(int limit) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<Product> data = BestSeller.getAllData(getBrandId(), limit);
            response.setBaseResponse(data.size(), offset, limit, success, new ObjectMapper().convertValue(data, MapProductList[].class));
//            List<Product> data = BestSeller.getListAllData(getBrandId(), limit);
//            response.setBaseResponse(data.size(), offset, limit, success, new ObjectMapper().convertValue(data, MapProductList[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result onSale(int limit) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<Product> data = OnSale.getAllData(getBrandId(), limit);
            response.setBaseResponse(data.size(), offset, limit, success, new ObjectMapper().convertValue(data, MapProductList[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result socmed(int limit) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<Socmed> data = Socmed.getAllSocmed(getBrandId(), limit);
            response.setBaseResponse(data.size(), offset, limit, success, new ObjectMapper().convertValue(data, MapSocmed[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result colors() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<Attribute> data = Attribute.getColor(getBrandId());
            response.setBaseResponse(data.size(), offset, limit, success, new ObjectMapper().convertValue(data, MapColor[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result size() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<Size> data = Size.getSize(getBrandId());
            response.setBaseResponse(data.size(), offset, limit, success, new ObjectMapper().convertValue(data, MapSize[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result postContactUs() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            JsonNode json = request().body().asJson();
            String email = json.findPath("email").asText();
            String name = json.findPath("name").asText();
            String content = json.findPath("content").asText();
            String validation = ContactUs.validate(email, name, content);
            if (validation == null) {
                ContactUs model = new ContactUs();
                model.brand = getBrand();
                model.content = content;
                model.email = email;
                model.name = name;
                model.save();
                String emailAdmin = Constant.getInstance().getEmailAdmin();

                 Thread thread = new Thread(() -> {
                    try {
                        String cont = "Name : "+name+"<br/>Email : "+email+"<br/><br/>"+content;
                        MailConfig.sendmail(emailAdmin,
                                "[Contact Us] From "+name, cont);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                thread.start();

                response.setBaseResponse(1, offset, 1, created, null);
                return ok(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, validation, null);
            return badRequest(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result vouchers() {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            List<MapVoucher> data = VoucherDetail.getVoucherMember(actor.id, getBrandId());
            response.setBaseResponse(data.size(), offset, data.size(), success, new ObjectMapper().convertValue(data, MapVoucher[].class));
            return ok(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result promotions() {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            List<MapPromotion> data = PromotionProduct.getPromotionProduct(getBrandId());
            response.setBaseResponse(data.size(), offset, data.size(), success, new ObjectMapper().convertValue(data, MapPromotion[].class));
            return ok(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result bannerPromotions() {
        Integer limit = 3;
        Date now = new Date();
        List<Promotion> data = Promotion.find.where()
            .le("valid_from", now)
            .ge("valid_to", now)
            .eq("status", true)
            .eq("is_deleted", false)
            .setMaxRows(limit)
            .order("status DESC, created_at DESC").findList();

        response.setBaseResponse(data.size(), offset, data.size(), success, new ObjectMapper().convertValue(data, MapBannerPromo[].class));
        return ok(Json.toJson(response));
        
    }

    public static Result allergies() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<Allergy> data = Allergy.getAllData();
            response.setBaseResponse(data.size(), offset, limit, success, new ObjectMapper().convertValue(data, MapAllergy[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result presentations() {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            List<Presentation> presentations = Presentation.find.where()
                .eq("status", true)
                .eq("is_deleted", false)
                .orderBy("id ASC")
                .findList();
            response.setBaseResponse(presentations.size(), offset, presentations.size(), success, new ObjectMapper().convertValue(presentations, MapPresentation[].class));
            return ok(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result fetchInstagramBanner() {
        JsonNode response = new InstagramService().fetchData();
        new InstagramService().syncBanner();
        return ok(response);
    }
}
