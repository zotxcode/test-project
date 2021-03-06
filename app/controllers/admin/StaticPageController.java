package controllers.admin;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.enwie.api.BaseResponse;
import com.enwie.util.CommonFunction;
import controllers.BaseController;
import models.Brand;
import models.RoleFeature;
import models.StaticPage;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.admin.staticPage._form;
import views.html.admin.staticPage.detail;
import views.html.admin.staticPage.list;

import java.util.Map;

/**
 * @author hendriksaragih
 */
public class StaticPageController extends BaseController {
    private final static Logger.ALogger logger = Logger.of(StaticPageController.class);
    private static final String TITLE = "Static Page";
    private static final String featureKey = "staticpage";

    private static Html htmlList(){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return list.render(TITLE, "List", feature);
    }

    private static Html htmlDetail(StaticPage data){
        return detail.render(TITLE, "Detail", data);
    }

    private static Html htmlEdit(Form<StaticPage> data){
        return _form.render(TITLE, "Edit", data, routes.StaticPageController.update());
    }

    private static Html htmlAdd(Form<StaticPage> data){
        return _form.render(TITLE, "Add", data, routes.StaticPageController.save());
    }

    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(htmlList());
    }

    @Security.Authenticated(Secured.class)
    public static Result add() {
        Form<StaticPage> formData = Form.form(StaticPage.class).fill(new StaticPage());
        return ok(htmlAdd(formData));
    }

    @Security.Authenticated(Secured.class)
    public static Result edit(Long id) {
        StaticPage dt = StaticPage.findById(id, getBrandId());
        Form<StaticPage> formData = Form.form(StaticPage.class).fill(dt);
        return ok(htmlEdit(formData));
    }

    @Security.Authenticated(Secured.class)
    public static Result detail(Long id) {
        StaticPage dt = StaticPage.findById(id, getBrandId());
        return ok(htmlDetail(dt));
    }

    @Security.Authenticated(Secured.class)
    public static Result lists() {
        RoleFeature feature = getUserAccessByFeature(featureKey);
        Map<String, String[]> params = request().queryString();


        Integer iTotalRecords = StaticPage.RowCount(getBrand());
        String filter = params.get("search[value]")[0];


        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "name";
        String order = params.get("order[0][dir]")[0];

        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
            case 1 :  sortBy = "id"; break;
            case 2 :  sortBy = "name"; break;
        }

        Page<StaticPage> datas = StaticPage.page(page, pageSize, sortBy, order, filter, getBrandId());
        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (StaticPage dt : datas.getList()) {
            ObjectNode row = Json.newObject();
            String action = "" ;
            if(feature.isView()){
                action += "<a class=\"btn btn-default btn-sm action\" href=\""+controllers.admin.routes.StaticPageController.detail(dt.id)+"\"><i class=\"fa fa-search\"></i></a>&nbsp;";
            }
            if(feature.isEdit()){
                action += "<a class=\"btn btn-primary btn-sm action\" href=\""+controllers.admin.routes.StaticPageController.edit(dt.id)+"\"><i class=\"fa fa-pencil\"></i></a>&nbsp;";
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
        Form<StaticPage> form = Form.form(StaticPage.class).bindFromRequest();
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlAdd(form));
        }else{
            StaticPage data = form.get();
            data.slug = CommonFunction.slugGenerate(data.name);
            data.brand = Brand.find.byId(getBrandId());
            StaticPage uniqueCheck = StaticPage.find.where()
                    .eq("slug", data.slug)
                    .eq("brand_id", getBrandId())
                    .eq("isDeleted", false)
                    .findUnique();
            if (uniqueCheck != null){
                flash("error", "Page with similar title already exist");
                return badRequest(htmlAdd(form));
            }

            data.userCms = getUserCms();
            data.save();
            flash("success", "Static Pages instance created");
            if (data.save.equals("1")){
                return redirect(routes.StaticPageController.detail(data.id));
            }else{
                return redirect(routes.StaticPageController.add());
            }
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result update() {
        Form<StaticPage> form = Form.form(StaticPage.class).bindFromRequest();
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlEdit(form));
        }else{
            StaticPage data = form.get();
            data.brand = Brand.find.byId(getBrandId());
            data.update();
            flash("success", "Static Pages instance edited");
            return redirect(routes.StaticPageController.detail(data.id));
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result delete(String id) {
        Ebean.beginTransaction();
        int status = 0;
        try {
            for (String aTmp : id.split(",")) {
                StaticPage data = StaticPage.find.ref(Long.parseLong(aTmp));
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
    public static Result listsForSelect() {

        Map<String, String[]> params = request().queryString();

        Integer pageSize = 100;
        Integer page = 0;
        if(params.get("page") != null){
            page = Integer.valueOf(params.get("page")[0]);
        }

        String sortBy = "name";
        String order = "asc";
        String filter = params.get("q")[0];

        ObjectNode result = Json.newObject();

        Page<StaticPage> datas = StaticPage.find.where()
                .ilike("name", "%" + filter + "%")
                .eq("is_deleted", false)
                .eq("brand_id", getBrandId())
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .setFetchAhead(false)
                .getPage(page);
        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        result.put("total_count", iTotalDisplayRecords);
        result.put("incomplete_results", false);

        ArrayNode an = result.putArray("items");
        for (StaticPage dt : datas.getList()) {
            ObjectNode row = Json.newObject();
            row.put("id", dt.id);
            row.put("text", dt.name);
            an.add(row);
        }

        return ok(result);
    }
}