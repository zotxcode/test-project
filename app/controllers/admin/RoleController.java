package controllers.admin;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.SqlUpdate;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.enwie.api.BaseResponse;
import controllers.BaseController;
import models.Feature;
import models.Role;
import models.RoleFeature;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.admin.role._form;
import views.html.admin.role.detail;
import views.html.admin.role.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hendriksaragih
 */
public class RoleController extends BaseController {
    private final static Logger.ALogger logger = Logger.of(RoleController.class);
    private static final String TITLE = "Role";
    private static final String featureKey = "role";

    private static Html htmlList(){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return list.render(TITLE, "List", feature);
    }

    private static Html htmlAdd(Form<Role> data){
        return _form.render(TITLE, "Add", data, routes.RoleController.save(), getListFeature(), new ArrayList<>());
    }

    private static Html htmlEdit(Form<Role> data, List<RoleFeature> features){
        return _form.render(TITLE, "Edit", data, routes.RoleController.update(), getListFeature(), features);
    }

    private static Html htmlDetail(Role data, List<RoleFeature> features){
        return detail.render(TITLE, "Detail", data, getListFeature(), features);
    }

    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(htmlList());
    }

    @Security.Authenticated(Secured.class)
    public static Result add() {
        Form<Role> formData = Form.form(Role.class).fill(new Role());
        return ok(htmlAdd(formData));
    }

    @Security.Authenticated(Secured.class)
    public static Result edit(Long id) {
        Role dt = Role.find.byId(id);
        Form<Role> formData = Form.form(Role.class).fill(dt);
        List<RoleFeature> listFeature = RoleFeature.getFeaturesByRole(id);
        Logger.debug("size = "+listFeature.size());
        return ok(htmlEdit(formData, listFeature));
    }

    private static List<Feature> getListFeature(){
        List<Feature> result = Feature.find.where().eq("is_deleted", false).eq("is_active", true).order("sequence asc").findList();
        return result;
    }

    @Security.Authenticated(Secured.class)
    public static Result lists() {
        RoleFeature feature = getUserAccessByFeature(featureKey);
        Map<String, String[]> params = request().queryString();

        Integer iTotalRecords = Role.RowCount();
        String filter = params.get("search[value]")[0];

        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "name";
        String order = params.get("order[0][dir]")[0];

        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
            case 1 :  sortBy = "id"; break;
            case 2 :  sortBy = "name"; break;
        }

        Page<Role> datas = Role.page(page, pageSize, sortBy, order, filter);
        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (Role dt : datas.getList()) {
            ObjectNode row = Json.newObject();

            String action = "";
            if(feature.isView()){
                action += "<a class=\"btn btn-default btn-sm action\" href=\""+ routes.RoleController.detail(dt.id)+"\"><i class=\"fa fa-search\"></i></a>&nbsp;";
            }
            if(feature.isEdit()){
                action += "<a class=\"btn btn-primary btn-sm action\" href=\""+controllers.admin.routes.RoleController.edit(dt.id)+"\"><i class=\"fa fa-pencil\"></i></a>&nbsp;";
            }
            if(feature.isDelete() && dt.id != 1){
                action += "<a class=\"btn btn-danger btn-sm action btn_delete\" href=\"#\" onclick=\"deleteData("+dt.id+")\"><i class=\"fa fa-trash\"></i></a>&nbsp;";
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
        Form<Role> form = Form.form(Role.class).bindFromRequest();
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlAdd(form));
        }else{
            Role data = form.get();
            Transaction txn = Ebean.beginTransaction();
            try {
                data.save();

                HashMap<Long,Integer> mapMenuAccess = new HashMap<>();
                if (data.feature_list != null) {
                    for (String tmp : data.feature_list) {
                        String[] tmp2 = tmp.split("-");
                        Long id = Long.valueOf(tmp2[0]);
                        int access = Integer.valueOf(tmp2[1]);
                        if (mapMenuAccess.containsKey(id)) {
                            mapMenuAccess.put(id, mapMenuAccess.get(id) * access);
                        } else {
                            mapMenuAccess.put(id, access);
                        }

                    }
                    for (Map.Entry<Long, Integer> entry : mapMenuAccess.entrySet()) {
                        RoleFeature feature = new RoleFeature();
                        feature.role = data;
                        feature.feature = Feature.find.byId(entry.getKey());
                        feature.access = entry.getValue();
                        feature.save();
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
                return redirect(routes.RoleController.detail(data.id));
            }else{
                return redirect(routes.RoleController.add());
            }
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result update() {
        Form<Role> form = Form.form(Role.class).bindFromRequest();
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlAdd(form));
        }else{
            Role data = form.get();
            Transaction txn = Ebean.beginTransaction();
            try {
                data.update();

                SqlUpdate tangoDown = Ebean.createSqlUpdate("DELETE FROM role_feature WHERE role_id = "+data.id);
                tangoDown.execute();

                HashMap<Long,Integer> mapMenuAccess = new HashMap<>();
                if (data.feature_list != null) {
                    for (String tmp : data.feature_list) {
                        String[] tmp2 = tmp.split("-");
                        Long id = Long.valueOf(tmp2[0]);
                        int access = Integer.valueOf(tmp2[1]);
                        if (!mapMenuAccess.containsKey(id)) {
                            mapMenuAccess.put(id, access);
                        } else {
                            mapMenuAccess.put(id, mapMenuAccess.get(id) * access);
                        }

                    }
                    for (Map.Entry<Long, Integer> entry : mapMenuAccess.entrySet()) {
                        RoleFeature feature = new RoleFeature();
                        feature.role = data;
                        feature.access = entry.getValue();
                        feature.feature = Feature.find.byId(entry.getKey());
                        feature.save();
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

            flash("success", TITLE + " instance updated");
            return redirect(routes.RoleController.index());
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result delete(String id) {
        Ebean.beginTransaction();
        int status = 0;
        try {
            for (String aTmp : id.split(",")) {
                Role data = Role.find.ref(Long.parseLong(aTmp));
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

    @Security.Authenticated(Secured.class)
    public static Result detail(Long id) {
        Role dt = Role.find.ref(id);
        List<RoleFeature> listFeature = RoleFeature.getFeaturesByRole(id);
        return ok(htmlDetail(dt, listFeature));
    }

}