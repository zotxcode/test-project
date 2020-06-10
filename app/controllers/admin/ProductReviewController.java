package controllers.admin;

import com.avaje.ebean.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.enwie.api.BaseResponse;
import com.enwie.util.CommonFunction;
import controllers.BaseController;
import models.Product;
import models.ProductDetail;
import models.ProductReview;
import models.RoleFeature;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.admin.productreview.detail;
import views.html.admin.productreview.list;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * @author hendriksaragih
 */
public class ProductReviewController  extends BaseController {
    private final static Logger.ALogger logger = Logger.of(ProductReviewController.class);
    private static final String TITLE = "Product Reviews";
    private static final String featureKey = "productreview";
    private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");

    private static Html htmlList(){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return list.render("Product Reviews", "List", feature);
    }

    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(htmlList());
    }

    @Security.Authenticated(Secured.class)
    public static Result lists() {
        RoleFeature feature = getUserAccessByFeature(featureKey);
        Map<String, String[]> params = request().queryString();

        Integer iTotalRecords = ProductReview.findRowCount(getBrandId());
        String name = params.get("search[value]")[0];
        String filter = params.get("filter")[0];


        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "createdAt";
        String order = params.get("order[0][dir]")[0];

        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
            case 1 :  sortBy = "id"; break;
            case 2 :  sortBy = "product.name"; break;
            case 3 :  sortBy = "member.fullName"; break;
            case 4 :  sortBy = "createdAt"; break;
            case 5 :  sortBy = "approvedStatus"; break;
        }

        ExpressionList<ProductReview> qry = ProductReview.find.where()
                .or(Expr.ilike("member.fullName", "%" + name + "%"), Expr.ilike("product.name", "%" + name + "%"))
                .eq("t0.brand_id", getBrandId())
                .eq("t0.is_deleted", false);
        if(!filter.equals("")){
            qry = qry.eq("approvedStatus", filter);
        }

        Page<ProductReview> datas = qry.isNotNull("member")
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
        for (ProductReview dt : datas.getList()) {
            ObjectNode row = Json.newObject();
            String action = "";
            if(feature.isView()){
                action += "<a class=\"btn btn-default btn-sm action\" href=\""+ routes.ProductReviewController.detail(dt.id)+"\"><i class=\"fa fa-search\"></i></a>&nbsp;" ;
            }
            if(feature.isEdit()){
                if (dt.approvedStatus.equals("P")){
                    action += "<a class=\"btn btn-primary btn-sm action\" title=\"Approve\" href=\"javascript:approveData(" + dt.id + ");\"><i class=\"fa fa-check\"></i></a>&nbsp;<a class=\"btn btn-warning btn-sm action\" title=\"Reject\" href=\"javascript:rejectData(" + dt.id + ");\"><i class=\"fa fa-close \"></i></a>&nbsp;";
                }
            }
            if(feature.isDelete()){
                action += "<a class=\"btn btn-danger btn-sm action\" title=\"Delete\" href=\"javascript:deleteData("+dt.id+");\"><i class=\"fa fa-trash\"></i></a>";
            }

            row.put("0", dt.id.toString());
            row.put("1", num);
            row.put("2", dt.product.name);
            row.put("3", (dt.member != null)? dt.member.fullName:"");
            row.put("4", CommonFunction.getDateTime2(dt.createdAt));
            row.put("5", dt.getStatus());
            row.put("6", action);
            an.add(row);
            num++;
        }

        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public static Result delete(String id) {
        Ebean.beginTransaction();
        int status = 0;
        try {
            String[] tmp = id.split(",");
            for (int i=0; i < tmp.length; i++)
            {
                ProductReview data = ProductReview.findById(Long.parseLong(tmp[i]), getBrandId());
                data.isDeleted = true;
                data.update();
                status = 1;
            }

            Ebean.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            Ebean.rollbackTransaction();
            status = 0;
        } finally {
            Ebean.endTransaction();
        }

        BaseResponse response = new BaseResponse();
        String message = "";
        if(status == 1)
            message = "Data success deleted";
        else message = "Data failed deleted";

        response.setBaseResponse(status, offset, 1, message, null);
        return ok(Json.toJson(response));
    }

    @Security.Authenticated(Secured.class)
    public static Result updateStatus(String id, String newStatus) {

        Ebean.beginTransaction();
        int status = 0;
        try {
            String[] tmp = id.split(",");
            for (int i=0; i < tmp.length; i++)
            {

                ProductReview data = ProductReview.findById(Long.parseLong(tmp[i]), getBrandId());
                data.updateStatus(newStatus, getUserCms());
                Product.updateAverageRating(data.product.id, getBrandId());
                Ebean.commitTransaction();

                status = 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Ebean.rollbackTransaction();
            status = 0;
        } finally {
            Ebean.endTransaction();
        }

        BaseResponse response = new BaseResponse();
        String message = "";
        if(status == 1)
            message = "Data success updated";
        else message = "Data failed updated";

        response.setBaseResponse(status, offset, 1, message, null);
        return ok(Json.toJson(response));
    }

    private static Html htmlDetail(Long id, Product product, ProductDetail productDetail, List<ProductReview> reviews){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return detail.render("Product Detail", id, product, productDetail, reviews, routes.ProductReviewController.saveReview(), feature);
    }
    @Security.Authenticated(Secured.class)
    public static Result detail(Long id) {
        ProductReview review = ProductReview.findById(id, getBrandId());
        List<ProductReview> reviews = ProductReview.find.where()
                .eq("is_deleted", false)
                .eq("product", review.product)
                .eq("brand_id", getBrandId())
                .orderBy("createdAt").findList();

        Product product = review.product;
        ProductDetail proddetail = product.getProductDetail(getBrandId());
        return ok(htmlDetail(id, product, proddetail, reviews));
    }

    @Security.Authenticated(Secured.class)
    public static Result saveReview() {
        DynamicForm dForm = Form.form().bindFromRequest();
        Long id = Long.parseLong(dForm.get("id"));
        Long productId = Long.parseLong(dForm.get("product_id"));
        Transaction txn = Ebean.beginTransaction();
        try {
            String review = dForm.get("review");
            int rating = Integer.parseInt(dForm.get("rating"));
            ProductReview productReview = new ProductReview();
            productReview.approvedStatus = "A";
            productReview.reviewer = dForm.get("reviewer") != "" ? dForm.get("reviewer") : null;
            productReview.rating = rating;
            productReview.brand = getBrand();
            productReview.comment = review;
            productReview.product = Product.find.byId(productId);
            productReview.user = getUserCms();
            productReview.save();
            txn.commit();

            Product.updateAverageRating(productReview.product.id, getBrandId());
            flash("success", "Product review instance created");
        }catch (Exception e){
            flash("error", "Product review instance create failed");
        }finally {
            txn.end();
        }

        return redirect(routes.ProductReviewController.detail(id));

    }
}