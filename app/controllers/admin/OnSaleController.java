package controllers.admin;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.Query;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.enwie.api.BaseResponse;
import com.wordnik.swagger.annotations.ApiOperation;
import controllers.BaseController;
import models.OnSale;
import models.Product;
import models.RoleFeature;
import models.UserCms;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.admin.onSale._form;
import views.html.admin.onSale.list;
import views.html.admin.onSale.sequence;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hendriksaragih
 */
public class OnSaleController extends BaseController {
    private final static Logger.ALogger logger = Logger.of(OnSaleController.class);
    private static final String TITLE = "On Sale Product";
    private static final String featureKey = "on_sale";

    private static Html htmlList(){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return list.render(TITLE, "List", feature);
    }

    private static Html htmlAdd(Form<OnSale> data){
        return _form.render(TITLE, "Add", data, routes.OnSaleController.save(), new LinkedHashMap<>(), new LinkedHashMap<>(), new LinkedHashMap<>());
    }


    @Security.Authenticated(Secured.class)
    public static Result sequence() {
        return ok(sequence.render(TITLE, "Sequence", getListBannerByType()));
    }

    private static List<OnSale> getListBannerByType() {
        Query<OnSale> qry = OnSale.find.where()
                .eq("is_deleted", false)
                .eq("brand", getBrand())
                .eq("status", true)
                .orderBy("sequence ASC");

        return qry.findList();
    }

    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(htmlList());
    }

    @Security.Authenticated(Secured.class)
    public static Result add() {
        OnSale dt = new OnSale();
        Form<OnSale> formData = Form.form(OnSale.class).fill(dt);
        return ok(htmlAdd(formData));
    }

    @Security.Authenticated(Secured.class)
    public static Result lists() {
        RoleFeature feature = getUserAccessByFeature(featureKey);

        Map<String, String[]> params = request().queryString();

        Integer iTotalRecords = OnSale.findRowCount(getBrand());
        String name = params.get("search[value]")[0];
        Integer filter = Integer.valueOf(params.get("filter")[0]);


        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "name";
        String order = params.get("order[0][dir]")[0];

        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
            case 0 :  sortBy = "id"; break;
            case 1 :  sortBy = "id"; break;
            case 2 :  sortBy = "product.name"; break;
            case 3 :  sortBy = "t0.status"; break;
        }

        Page<OnSale> datas = OnSale.page(page, pageSize, sortBy, order, name, filter, getBrand());
        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (OnSale dt : datas.getList()) {
            ObjectNode row = Json.newObject();
            String action = "" ;
            if(feature.isDelete()){
                action += "<a class=\"btn btn-danger btn-sm action btn_delete\" href=\"#\" onclick=\"deleteItem("+dt.id+")\"><i class=\"fa fa-trash\"></i></a>";
            }

            String status = "" ;
            if(feature.isEdit()){
                status = "<input value=\""+dt.id+"\" class=\"cb-status\" data-toggle=\"toggle\" data-on=\"Active\" data-off=\"Inactive\" data-size=\"small\" data-style=\"ios\" data-offstyle=\"danger\" type=\"checkbox\" "+((dt.status)?"checked":"")+">";
            }else{
                status = dt.getStatus();
            }

            row.put("0", dt.id.toString());
            row.put("1", dt.id);
            row.put("2", dt.product.name);
            row.put("3", status);
            row.put("4", action);
            an.add(row);
            num++;
        }

        return ok(result);
    }


    @Security.Authenticated(Secured.class)
    public static Result save() {
        Form<OnSale> form = Form.form(OnSale.class).bindFromRequest();
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlAdd(form));
        }else{
            OnSale data = form.get();
            Transaction txn = Ebean.beginTransaction();
            try {
                UserCms cms = getUserCms();

                if(data.product_list != null) {
                    for (String product : data.product_list) {
                        Product prod = Product.find.byId(Long.parseLong(product));
                        OnSale n = OnSale.findByProduct(prod, getBrand());
                        if (n == null){
                            OnSale na = new OnSale();
                            na.product = prod;
                            na.brand = getBrand();
                            na.sequence = OnSale.getNextSequence(getBrandId());
                            na.userCms = cms;
                            na.status = data.status;
                            na.save();
                        }else {
                            n.status = data.status;
                            n.update();
                        }
                    }
                }

                txn.commit();

            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
                flash("error", "Please correct errors bellow.");
                return badRequest(htmlAdd(form));
            } finally {
                txn.end();
            }

            flash("success", TITLE + " instance created");
            if (data.save.equals("1")){
                return redirect(routes.OnSaleController.index());
            }else{
                return redirect(routes.OnSaleController.add());
            }

        }
    }

    @Security.Authenticated(Secured.class)
    public static Result updateStatus(String id, String newStatus) {

        Ebean.beginTransaction();
        int status = 0;
        try {
            String[] tmp = id.split(",");
            for (String aTmp : tmp) {
                OnSale data = OnSale.findById(Long.parseLong(aTmp), getBrandId());
                data.updateStatus(newStatus);
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
            message = "Data success updated";
        else message = "Data failed updated";

        response.setBaseResponse(status, offset, 1, message, null);
        return ok(Json.toJson(response));
    }

    @Security.Authenticated(Secured.class)
    public static Result delete(String id) {
        Ebean.beginTransaction();
        int status = 0;
        try {
            String[] tmp = id.split(",");
            for (String aTmp : tmp) {
                OnSale data = OnSale.findById(Long.parseLong(aTmp), getBrandId());
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
    @ApiOperation(value = "updateSequence.", notes = "updateSequence.", response = String.class, httpMethod = "POST")
    public static Result updateSequence() {
        ObjectMapper om = new ObjectMapper();
        JsonNode json = request().body().asJson();
        Map<String, List<Integer>> sequences = om.convertValue(json.findPath("ids"), Map.class);
        final Integer[] status = {0};
        Ebean.beginTransaction();
        try {
            sequences.forEach((k,v)->{
                int loop = 0;
                for (Integer dt : v) {
                    OnSale data = OnSale.findById(dt.longValue(), getBrandId());
                    data.updateSequence(loop);
                    status[0] = 1;
                    loop += 1;
                }
            });
            Ebean.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            Ebean.rollbackTransaction();
            status[0] = 0;
        } finally {
            Ebean.endTransaction();
        }

        BaseResponse response = new BaseResponse();
        String message = "";
        if(status[0] == 1){
            flash("success", TITLE + " success updated");
            message = "Data success updated";
        }else{
            flash("error", TITLE + " failed updated");
            message = "Data failed updated";
        }

        response.setBaseResponse(status[0], offset, 1, message, null);
        return ok(Json.toJson(response));
    }
}