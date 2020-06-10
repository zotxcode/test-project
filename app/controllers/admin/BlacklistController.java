package controllers.admin;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.enwie.api.BaseResponse;
import controllers.BaseController;
import models.BlacklistEmail;
import models.RoleFeature;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.admin.blacklist._form;
import views.html.admin.blacklist.list;

import java.util.Map;

/**
 * @author hendriksaragih
 */
public class BlacklistController extends BaseController {
    private final static Logger.ALogger logger = Logger.of(BlacklistController.class);
    private static final String TITLE = "Blacklist Setting";
    private static final String featureKey = "blacklist";

    private static Html htmlList(){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return list.render(TITLE, "List", feature);
    }

    private static Html htmlEdit(Form<BlacklistEmail> data){
        return _form.render(TITLE, "Edit", data, routes.BlacklistController.update());
    }

    private static Html htmlAdd(Form<BlacklistEmail> data){
        return _form.render(TITLE, "Add", data, routes.BlacklistController.save());
    }

    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(htmlList());
    }

    @Security.Authenticated(Secured.class)
    public static Result add() {
        Form<BlacklistEmail> formData = Form.form(BlacklistEmail.class).fill(new BlacklistEmail());
        return ok(htmlAdd(formData));
    }

    @Security.Authenticated(Secured.class)
    public static Result edit(Long id) {
        BlacklistEmail dt = BlacklistEmail.findById(id, getBrandId());
        Form<BlacklistEmail> formData = Form.form(BlacklistEmail.class).fill(dt);
        return ok(htmlEdit(formData));
    }

    @Security.Authenticated(Secured.class)
    public static Result lists() {
        RoleFeature feature = getUserAccessByFeature(featureKey);
        Map<String, String[]> params = request().queryString();

        Integer iTotalRecords = BlacklistEmail.RowCount(getBrand());
        String filter = params.get("search[value]")[0];


        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "name";
        String order = params.get("order[0][dir]")[0];

        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
            case 1 :  sortBy = "id"; break;
            case 2 :  sortBy = "name"; break;
        }

        Page<BlacklistEmail> datas = BlacklistEmail.page(page, pageSize, sortBy, order, filter, getBrand());
        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (BlacklistEmail dt : datas.getList()) {
            ObjectNode row = Json.newObject();
            String action = "";
            if(feature.isEdit()){
                action += "<a class=\"btn btn-primary btn-sm action\" href=\""+controllers.admin.routes.BlacklistController.edit(dt.id)+"\"><i class=\"fa fa-pencil\"></i></a>&nbsp;";
            }
            if(feature.isDelete()){
                action += "<a class=\"btn btn-danger btn-sm action\" href=\"javascript:deleteData("+dt.id+");\"><i class=\"fa fa-trash\"></i></a>";
            }

            row.put("0", dt.id.toString());
            row.put("1", num);
            row.put("2", dt.name);
            row.put("3", action);
            an.add(row);
            num++;
        }

        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public static Result save() {
        Form<BlacklistEmail> form = Form.form(BlacklistEmail.class).bindFromRequest();
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlAdd(form));
        }else{
            BlacklistEmail data = form.get();
            data.brand = getBrand();
            data.userCms = getUserCms();
            data.save();
            flash("success", TITLE + " instance created");
            if (data.save.equals("1")){
                return redirect(routes.BlacklistController.index());
            }else{
                return redirect(routes.BlacklistController.add());
            }
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result update() {
        Form<BlacklistEmail> form = Form.form(BlacklistEmail.class).bindFromRequest();
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlEdit(form));
        }else{
            BlacklistEmail data = form.get();
            data.update();
            flash("success", TITLE + " instance edited");
            return redirect(routes.BlacklistController.index());
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result delete(String id) {
        Ebean.beginTransaction();
        int status = 0;
        try {
            for (String aTmp : id.split(",")) {
                BlacklistEmail data = BlacklistEmail.findById(Long.parseLong(aTmp), getBrandId());
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
        String message = status == 1 ? "Data success deleted" : "Data failed deleted";

        response.setBaseResponse(status, offset, 1, message, null);
        return ok(Json.toJson(response));
    }
}