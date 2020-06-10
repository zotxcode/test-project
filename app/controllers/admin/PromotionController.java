package controllers.admin;

import com.avaje.ebean.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.enwie.api.BaseResponse;
import controllers.BaseController;
import com.enwie.util.CommonFunction;
import models.*;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.admin.promotion._form;
import views.html.admin.promotion._form_edit;
import views.html.admin.promotion.list;
import java.text.SimpleDateFormat;
import java.io.File;
import java.util.*;

/**
 * Created by hilmanzaky.
 */
public class PromotionController extends BaseController {
    private static final String TITLE = "Promotion";
    private static final String featureKey = "promotion";

    private static Html htmlList(){
        return list.render(TITLE, "List");
    }

    private static Html htmlAdd(Form<Promotion> data){
        return _form.render(TITLE, "Generate", data, routes.PromotionController.save());
    }

    private static Html htmlEdit(Form<Promotion> data, Promotion data2){
        return _form_edit.render(TITLE, "Edit", data, data2, routes.PromotionController.update(), getListProducts(data2.products));
    }

    private static Map<Integer, String> getListProducts(List<Product> products){
        Map<Integer, String> listsProduct = new LinkedHashMap<>();
        for(Product product : products){
            listsProduct.put(product.id.intValue(), product.name);
        }
        return listsProduct;
    }

    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(htmlList());
    }

    @Security.Authenticated(Secured.class)
    public static Result add() {
        Form<Promotion> formData = Form.form(Promotion.class).fill(new Promotion());
        return ok(htmlAdd(formData));
    }

    @Security.Authenticated(Secured.class)
    public static Result edit(Long id) {
        Promotion promotion = Promotion.findById(id, getBrandId());
        Form<Promotion> formData = Form.form(Promotion.class).fill(promotion);
        return ok(htmlEdit(formData, promotion));
    }

    @Security.Authenticated(Secured.class)
    public static Result lists() {
        Map<String, String[]> params = request().queryString();


        Integer iTotalRecords = Promotion.findRowCount();
        String filter = params.get("search[value]")[0];


        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "validTo";
        String order = params.get("order[0][dir]")[0];

        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
            case 1 :  sortBy = "id"; break;
            case 2 :  sortBy = "name"; break;
        }

        // Collect the data
        Page<Promotion> datas = Promotion.page(page, pageSize, sortBy, order, filter, getBrandId());
        Integer iTotalDisplayRecords = datas.getTotalRowCount();


        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (Promotion dt : datas.getList()) {
//            int totalUsed = PromotionProduct.find.where().eq("promotion", dt).eq("status", 1).findRowCount();

            ObjectNode row = Json.newObject();
            String status = "";
//            String action = "" +
//                    "<a class=\"btn btn-default btn-sm action\" href=\""+ routes.PromotionController.detail(dt.id)+"\"><i class=\"fa fa-search\"></i></a>&nbsp;" +
//                    "<a class=\"btn btn-primary btn-sm action\" href=\""+ routes.PromotionController.edit(dt.id)+"\"><i class=\"fa fa-pencil\"></i></a>" ;
            String action = "" +
                    "<a class=\"btn btn-primary btn-sm action\" href=\""+ routes.PromotionController.edit(dt.id)+"\"><i class=\"fa fa-pencil\"></i></a>" ;

            if((dt.validFrom.compareTo(new Date()) < 0 || dt.validFrom.compareTo(new Date()) == 0) &&
                    (dt.validTo.compareTo(new Date()) > 0 || dt.validTo.compareTo(new Date()) == 0)){
                status = "<input value=\""+dt.id+"\" class=\"cb-status\" data-toggle=\"toggle\" data-on=\"Active\" data-off=\"Inactive\" data-size=\"small\" data-style=\"ios\" data-offstyle=\"danger\" type=\"checkbox\" "+((dt.status)?"checked":"")+">";
            }else{
                if(dt.validTo.compareTo(new Date()) < 0){
                    status = "Expired";
                }else if(dt.validFrom.compareTo(new Date()) > 0){
                    status = "Inactive";
                }
            }



            row.put("0", dt.id.toString());
            row.put("1", dt.name);
            row.put("2", dt.description);
            row.put("3", dt.getValidFrom()+" - "+dt.getValidTo());
            row.put("4", status);
            row.put("5", action);
            an.add(row);
            num++;
        }

        return ok(result);
    }


    @Security.Authenticated(Secured.class)
    public static Result listsPromotionProduct(Long id) {
        Map<String, String[]> params = request().queryString();

        Integer iTotalRecords = PromotionProduct.findRowCountByPromotionId(id);
        String filter = params.get("search[value]")[0];


        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "validTo";
        String order = params.get("order[0][dir]")[0];

        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
            case 1 :  sortBy = "id"; break;
            case 2 :  sortBy = "name"; break;
        }

        // Collect the data
        Page<PromotionProduct> datas = PromotionProduct.page(id, page, pageSize, sortBy, order, filter);
        Integer iTotalDisplayRecords = datas.getTotalRowCount();


        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (PromotionProduct dt : datas.getList()) {
//            int totalUsed = PromotionProduct.find.where().eq("promotion", dt).eq("status", 1).findRowCount();

            ObjectNode row = Json.newObject();
            String action = "" +
                    "<a class=\"btn btn-danger btn-sm action\" href=\"#\" onclick=\"deleteItem("+dt.id+")\"><i class=\"fa fa-trash\"></i></a>" ;

            row.put("0", dt.id.toString());
            row.put("1", dt.product.name);
            row.put("2", dt.discount);
            row.put("3", action);
            an.add(row);
            num++;
        }

        return ok(result);
    }




    @Security.Authenticated(Secured.class)
    public static Result listsProduct(Long id) {
        Promotion promotion = Promotion.find.byId(id);

        Map<String, String[]> params = request().queryString();

        String filter = params.get("search[value]")[0];

        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "name";
        String order = params.get("order[0][dir]")[0];

        ExpressionList<Product> qry = Product.find
                .where()
                .ilike("name", "%" + filter + "%")
                .eq("is_deleted", false);

        Page<Product> datas = qry.orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);

        Integer iTotalRecords = qry.findRowCount();
        Integer iTotalDisplayRecords = datas.getTotalRowCount();


        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (Product dt : datas.getList()) {
            ObjectNode row = Json.newObject();
            row.put("0", num);
            row.put("1", dt.name);
            an.add(row);
            num++;
        }

        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public static Result save() {
        Form<Promotion> form = Form.form(Promotion.class).bindFromRequest();
        DynamicForm dForm = Form.form().bindFromRequest();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlAdd(form));
        }else{
            Http.MultipartFormData body = request().body().asMultipartFormData();
            Transaction txn = Ebean.beginTransaction();
            Promotion data = form.get();
            try {
                Http.MultipartFormData.FilePart picture = body.getFile("imageUrl");
                data.brand = getBrand();

                Promotion uniqueCheck = Promotion.find.where().eq("name", data.name).findUnique();
                if (uniqueCheck != null) {
                    flash("error", "Promotion with similar name already exist");
                    return badRequest(htmlAdd(form));
                }

                List<Product> products = new ArrayList<>();
                if (data.product_list != null) {
                    for (String product : data.product_list) {
                        products.add(Product.find.byId(Long.parseLong(product)));
                    }
                }
                data.products = products;

                data.validFrom = simpleDateFormat.parse(dForm.get("fromDate") +" "+ dForm.get("fromTime"));
                data.validTo = simpleDateFormat.parse(dForm.get("toDate") +" "+ dForm.get("toTime"));

                File newFile = Photo.uploadImage(picture, "prm", CommonFunction.slugGenerate(data.name), Photo.promoImageSize, "jpg");

                UserCms cms = getUserCms();
                data.createdBy = cms;
                data.status = true;
                data.imageUrl = newFile != null ? Photo.createUrl("prm", newFile.getName()) : "";
                data.save();

                String roleKey = cms.role.key;
                if (newFile != null){
                    Photo.saveRecord("prm", newFile.getName(), "", "", "", picture.getFilename(), cms.id, roleKey,
                            "Promo", data.id);
                }

                txn.commit();

            }catch (Exception e){
                Logger.debug("error = "+e.getMessage());
                txn.rollback();
            }

            flash("success", TITLE + " instance created");
            if (data.save.equals("1")){
                return redirect(routes.PromotionController.edit(data.id));
            }else{
                return redirect(routes.PromotionController.add());
            }
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result addProductDiscount() {
        int status = 0;
        JsonNode json = request().body().asJson();
        JsonNode productIds = json.findPath("ids");
        Long discount = json.get("discount").asLong();
        Long promotionId = json.get("promotion_id").asLong();

        Promotion promotion = Promotion.findById(promotionId, getBrandId());

        Transaction txn = Ebean.beginTransaction();

        try {
            if (productIds.isArray()) {
                for (final JsonNode productId : productIds) {
                    Long prodId = Long.parseLong(productId.textValue());
                    Product product = Product.findById(prodId, getBrandId());
                    if(PromotionProduct.isExist(promotion.id, prodId)) {
                        txn.rollback();
                        BaseResponse response = new BaseResponse();
                        String message = "Data failed to be added. " + product.name + " already exist.";
                        response.setBaseResponse(0, offset, 1, message, null);
                        return badRequest(Json.toJson(response));
                    }
                    promotion.addPromotionProduct(product, discount);
                }
                status = 1;
            }

            txn.commit();
        } catch (Exception e){
            status = 0;
            Logger.debug("error = "+e.getMessage());
            txn.rollback();
        }

        BaseResponse response = new BaseResponse();
        String message = status == 1 ? "Data added successfully" : "Data failed to be added";

        response.setBaseResponse(status, offset, 1, message, null);
        return ok(Json.toJson(response));
    }

    @Security.Authenticated(Secured.class)
    public static Result update() {
        Form<Promotion> form = Form.form(Promotion.class).bindFromRequest();
        DynamicForm dForm = Form.form().bindFromRequest();

        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            Promotion data = form.get();
            Promotion promotion = Promotion.find.byId(data.id);
            return badRequest(htmlEdit(form, promotion));
        }else{
            Transaction txn = Ebean.beginTransaction();
            Promotion data = form.get();
            Promotion model = Promotion.find.byId(data.id);
            try {
                model.update();

//                OdooService.getInstance().createPromotion(data);
                txn.commit();

            }catch (Exception e){
                Logger.debug("error = "+e.getMessage());
                txn.rollback();
            }

            flash("success", TITLE + " instance updated");
            return redirect(routes.PromotionController.index());
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result delete(String id) {
        Ebean.beginTransaction();
        int status = 0;
        try {
            for (String aTmp : id.split(",")) {
                Promotion data = Promotion.find.ref(Long.parseLong(aTmp));
                data.isDeleted = true;
                data.update();
                status = 1;

                List<PromotionProduct> details = PromotionProduct.find.where().eq("promotion", data).findList();
                for(PromotionProduct detail : details){
                    detail.isDeleted = true;
                    detail.update();
                }
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
        String message = status == 1 ? "Data success deleted" : "Data failed deleted";

        response.setBaseResponse(status, offset, 1, message, null);
        return ok(Json.toJson(response));
    }

    @Security.Authenticated(Secured.class)
    public static Result updateStatus(String id, String newStatus) {

        Ebean.beginTransaction();
        int status = 0;
        int countUpdated = 0;
        String[] tmp = id.split(",");
        String errorMessage = "";
        try {
            for (int i=0; i < tmp.length; i++)
            {
                Promotion data = Promotion.find.ref(Long.parseLong(tmp[i]));
//                if(newStatus.equals("active")){
//                    data.updateStatus(newStatus);
//                    if (!checkMaskingNameById(data.id)) {
//                        data.updateStatus(newStatus);
//                        countUpdated ++;
//                    }else{
//                        errorMessage = "Promotion with similar masking already exist";
//                    }
//                }else{
                    data.updateStatus(newStatus);
                    countUpdated ++;
//                }

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
        if(status == 1){
            if(countUpdated == tmp.length)
                message = "Data success updated";
            else if(countUpdated == 0) {
                message = errorMessage;
                status = 0;
            }else message = "Some data can't be updated";
        }
        else message = "Data failed updated";

        response.setBaseResponse(status, offset, 1, message, null);
        return ok(Json.toJson(response));
    }
}
