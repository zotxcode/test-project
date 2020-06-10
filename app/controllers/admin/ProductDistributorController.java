package controllers.admin;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.enwie.util.CommonFunction;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.BaseController;
import models.*;
import play.api.mvc.Call;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.admin.productdistributor.detail;
import views.html.admin.productdistributor.list;

import java.util.List;
import java.util.Map;

/**
 * @author hendriksaragih
 */
public class ProductDistributorController extends BaseController {
    private static final String featureKey = "productdistributor";

    private static Html htmlList(){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return list.render("Product List", "List", feature);
    }
    private static Html htmlDetail(Product product, ProductDetail productDetail, List<ProductReview> reviews, Call back){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return detail.render("Product Detail", product, productDetail, reviews, back, feature);
    }

    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(htmlList());
    }

    @Security.Authenticated(Secured.class)
    public static Result lists() {
        RoleFeature feature = getUserAccessByFeature(featureKey);
        Map<String, String[]> params = request().queryString();

        Integer iTotalRecords = Product.findRowCount(getBrand());
        String name = params.get("search[value]")[0];


        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "name";
        String order = params.get("order[0][dir]")[0];

        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
            case 1 :  sortBy = "id"; break;
            case 2 :  sortBy = "name"; break;
        }

        ExpressionList<ProductStock> qry = ProductStock.find
                .where()
                .eq("distributor", getUserCms())
                .isNull("reseller")
                .ilike("product.name", "%" + name + "%")
                .eq("product.isDeleted", false);


        Page<ProductStock> datas = qry
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .setFetchAhead(false)
                .getPage(page);
        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (ProductStock dt : datas.getList()) {
            ObjectNode row = Json.newObject();
            String action = "";
            if(feature.isView()){
                action += "<a class=\"btn btn-default btn-sm action\" title=\"View\" href=\""+ routes.ProductDistributorController.detail(dt.id)+"\"><i class=\"fa fa-search\"></i></a>&nbsp;";
            }

            row.put("0", num);
            row.put("1", dt.product.name);
            row.put("2", dt.product.category.name);
            row.put("3", CommonFunction.numberFormat(dt.product.price));
            row.put("4", dt.stock);
            row.put("5", dt.product.likeCount);
            row.put("6", action);
            an.add(row);
            num++;
        }

        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public static Result detail(Long id) {
        ProductStock productStock = ProductStock.find.byId(id);
        Product product = productStock.product;
        product.itemCount = productStock.stock;
        ProductDetail proddetail = product.getProductDetail(getBrandId());
        List<ProductReview> reviews = ProductReview.find.where()
                .eq("is_deleted", false)
                .eq("brand_id", getBrandId())
                .eq("product", product)
                .orderBy("createdAt").findList();
        Call back = routes.ProductDistributorController.index();

        return ok(htmlDetail(product, proddetail, reviews, back));
    }

}