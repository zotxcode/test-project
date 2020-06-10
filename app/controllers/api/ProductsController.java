package controllers.api;

import com.avaje.ebean.Query;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.enwie.api.BaseResponse;
import com.enwie.mapper.request.MapSearchProduct;
import com.enwie.mapper.response.*;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import controllers.BaseController;
import models.*;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hendriksaragih
 */
@Api(value = "/v1/products", description = "Products")
public class ProductsController extends BaseController {
    private static BaseResponse response = new BaseResponse();

    public static Result index() {
        return ok();
    }

    @ApiOperation(value = "Get all product detail.", notes = "Returns list of product detail.\n" + swaggerInfo
            + "", response = ProductDetail.class, responseContainer = "List", httpMethod = "GET")
    public static Result search() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            Member currentMember = checkMemberAccessAuthorization();
            Long memberId = currentMember == null ? 0L : currentMember.id;
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            try {
                MapSearchProduct map = mapper.readValue(json.toString(), MapSearchProduct.class);
                Query<Product> query = Product.getQueryProductList(getBrandId(), memberId);
                BaseResponse<Product> responseIndex;
                try {
                    responseIndex = Product.getData(query, map, getBrandId());
                    return ok(Json.toJson(responseIndex));
                } catch (IOException e) {
                    Logger.error("allDetail", e);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Get all top sales Product", notes = "Returns list of product.\n" + swaggerInfo
            + "", response = ProductDetail.class, responseContainer = "List", httpMethod = "GET")
    public static Result topSales(int limit) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<Product> data = Product.find.where().eq("is_deleted", false)
                    .eq("status", true)
                    .eq("brand_id", getBrandId())
                    .gt("item_count", 0)
                    .orderBy("num_of_order DESC")
                    .setMaxRows(limit)
                    .findList();
            response.setBaseResponse(data.size(), offset, limit, success,
                    new ObjectMapper().convertValue(data, MapProductList[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Get fast search product.", notes = "Returns list of product detail.\n" + swaggerInfo
            + "", response = ProductDetail.class, responseContainer = "List", httpMethod = "GET")
    public static Result fastSearch(String query, int offset, int limit) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            response.setBaseResponse(1, offset, 1, success, Product.fastSearch(query, getBrandId()));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
//        int authority = checkAccessAuthorization("all");
//        if (authority == 200 || authority == 203) {
//            IndexQuery<indexing.Product> indexQuery = indexing.Product.find.query();
//            indexQuery.setBuilder(QueryBuilders.queryString(query))
//                    .from(offset)
//                    .size(limit);
//            IndexResults<indexing.Product> results = indexing.Product.find.search(indexQuery);
//
//            response.setBaseResponse((int)results.getTotalCount(), offset, limit, success, new ObjectMapper().convertValue(results.getResults(), MapProductFastSearch[].class));
//            return ok(Json.toJson(response));
//
//        } else if (authority == 403) {
//            response.setBaseResponse(0, 0, 0, forbidden, null);
//            return forbidden(Json.toJson(response));
//        }
//        response.setBaseResponse(0, 0, 0, unauthorized, null);
//        return unauthorized(Json.toJson(response));
    }

    public static Result detail(Long id) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200) {
            Product model = Product.find.where().eq("id", id)
                    .eq("is_deleted", false)
                    .eq("brand_id", getBrandId())
                    .eq("status", true)
                    .setMaxRows(1).findUnique();
            if (model != null) {
                Member currentMember = checkMemberAccessAuthorization();
                Long memberId = currentMember == null ? 0L : currentMember.id;
                String message;
                try {
                    ObjectNode result = Json.newObject();
                    model.setRating();
                    model.setVariant();
                    model.setIsLike(memberId);
//                    model.setColors();
                    model.getProductDetails().get(0).setAttribute();
                    result.put("filter", Json.toJson(new ArrayList<>()));
                    result.put("product_data", Json.toJson(new ObjectMapper().convertValue(model, MapProduct.class)));
                    result.put("product_variants", Json.toJson(new ObjectMapper().convertValue(model.productVariants, MapVariantGroup[].class)));
                    result.put("product_sizes", Json.toJson(new ObjectMapper().convertValue(model.sizes, MapSize[].class)));
                    result.put("product_colors", Json.toJson(new ObjectMapper().convertValue(model.getColors(), MapProductColor[].class)));
                    result.put("product_also_views", Json.toJson(new ObjectMapper().convertValue(model.productAlsoViews, MapProductList[].class)));

                    Product.incrementViewCount(id, model.viewCount + 1);

                    response.setBaseResponse(1, offset, 1, success, result);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    message = e.getMessage();
                    Logger.error("show", e);
                }
                response.setBaseResponse(0, 0, 0, error, message);
                return internalServerError(Json.toJson(response));
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

    public static Result reviews() {
        Member currentMember = checkMemberAccessAuthorization();
        if (currentMember != null) {
            JsonNode json = request().body().asJson();

            Product product = Product.findById(json.findPath("product_id").asLong(), getBrandId());
            if (product != null){
                ProductReview exists = ProductReview.find.where()
                        .eq("product", product)
                        .eq("member", currentMember)
                        .setMaxRows(1).findUnique();
                if (exists == null){
                    ProductReview productReview = new ProductReview();
                    productReview.title = json.findPath("title").asText();
                    productReview.comment = json.findPath("comment").asText();
                    productReview.rating = json.findPath("rating").asInt();
                    productReview.product = product;
                    productReview.brand = getBrand();
                    productReview.member = currentMember;
                    productReview.approvedStatus = "P";
                    productReview.isActive = true;
                    productReview.save();
                    response.setBaseResponse(1, offset, 1, success, null);
                    return ok(Json.toJson(response));
                }
                response.setBaseResponse(0, 0, 0, "Already Reviewed", null);
                return notFound(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return notFound(Json.toJson(response));

        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result saveWhislist() {
        Member currentMember = checkMemberAccessAuthorization();
        if (currentMember != null) {
            JsonNode json = request().body().asJson();
            if (json.has("product_id")) {
                Long prod = json.get("product_id").asLong();
                int numCount = WishList.find.where()
                        .eq("product_id", prod)
                        .eq("is_deleted", false)
                        .eq("brand_id", getBrandId())
                        .eq("member", currentMember).findRowCount();
                if (numCount == 0){
                    WishList model = new WishList();
                    model.member = currentMember;
                    model.brand = getBrand();
                    model.product = Product.findById(prod, getBrandId());
                    model.save();

                    response.setBaseResponse(1, offset, 1, created, null);
                    return ok(Json.toJson(response));
                }
                response.setBaseResponse(0, 0, 0, "Duplicate product", null);
                return badRequest(Json.toJson(response));

            }
            response.setBaseResponse(0, 0, 0, inputParameter, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));

    }

    public static Result getWhislist(){
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            List<WishList> data = WishList.find.where()
                    .eq("member", actor)
                    .eq("is_deleted", false)
                    .eq("brand_id", getBrandId())
                    .orderBy("created_at DESC")
                    .findList();

            List<Product> results = new ArrayList<>();
            data.forEach(r-> results.add(r.product));

            response.setBaseResponse(results.size(), offset, results.size(), success, new ObjectMapper().convertValue(results, MapProductList[].class));
            return ok(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result deleteWishlist(Long id) {
        Member currentMember = checkMemberAccessAuthorization();
        if (currentMember != null) {
            WishList model = WishList.find.where()
                    .eq("member", currentMember)
                    .eq("is_deleted", false)
                    .eq("brand_id", getBrandId())
                    .eq("product_id", id)
                    .setMaxRows(1).findUnique();
            if (model != null) {
                model.isDeleted = true; // SOFT DELETE
                model.save();

                response.setBaseResponse(1, offset, 1, deleted, null);
                return ok(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return notFound(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result attributes() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<BaseAttribute> data = BaseAttribute.getAllData(getBrandId());
            response.setBaseResponse(data.size(), offset, data.size(), success, new ObjectMapper().convertValue(data, MapAttributeAll[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
}
