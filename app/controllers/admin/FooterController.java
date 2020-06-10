package controllers.admin;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.enwie.api.BaseResponse;
import com.enwie.util.CommonFunction;
import com.wordnik.swagger.annotations.ApiOperation;
import controllers.BaseController;
import models.Footer;
import models.KeyValue;
import models.RoleFeature;
import models.StaticPage;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.admin.footerView._form;
import views.html.admin.footerView._seq;
import views.html.admin.footerView.view;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hendriksaragih
 */
public class FooterController extends BaseController {
    private static final String TITLE = "Footer";
    private static final String featureKey = "footer";
    private static Html htmlList(){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return view.render(TITLE, "List", feature);
    }

    private static Html htmlAdd(Form<Footer> data){
        return _form.render(TITLE, "Add", data, routes.FooterController.save(),getOption(), new LinkedHashMap<>());
    }

    private static Html htmlEdit(Form<Footer> data, Map<Integer, String> listStaticPage){
        return _form.render(TITLE, "Edit", data, routes.FooterController.update(), getOption(), listStaticPage);
    }

    private static Map<String, String> getOption(){
        Map<String, String> result = new LinkedHashMap<>();
        result.put("Left", "Left");
//        result.put("Middle", "Middle");
        result.put("Right", "Right");
        return result;
    }

    @Security.Authenticated(Secured.class)
    public static Result seq() {
        Map<KeyValue, List<Footer>> lists = new LinkedHashMap<>();
        final int[] loop = {0};
        getOption().forEach((k,v)->{
            List<Footer> dt = Footer.find.where()
                    .eq("is_deleted", false)
                    .eq("position", k)
                    .eq("brand", getBrand())
                    .orderBy("sequence ASC").findList();
            lists.put(new KeyValue(loop[0], k) , dt);
            loop[0] += 1;
        });
        return ok(_seq.render(TITLE, "Sequence", lists));
    }

    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(htmlList());
    }

    @Security.Authenticated(Secured.class)
    public static Result edit(Long id) {
        Footer dt = Footer.findById(id, getBrandId());
        dt.staticPageId = dt.staticPage.id;
        Map<Integer, String> listStaticPage = new LinkedHashMap<>();
        listStaticPage.put(dt.staticPage.id.intValue(), dt.staticPage.name);
        Form<Footer> formData = Form.form(Footer.class).fill(dt);
        return ok(htmlEdit(formData, listStaticPage));
    }

    @Security.Authenticated(Secured.class)
    public static Result add() {
        Form<Footer> formData = Form.form(Footer.class).fill(new Footer());
        return ok(htmlAdd(formData));
    }

    @Security.Authenticated(Secured.class)
    public static Result lists() {
        RoleFeature feature = getUserAccessByFeature(featureKey);
        Map<String, String[]> params = request().queryString();

        Integer iTotalRecords = Footer.RowCount(getBrand());
        String filter = params.get("search[value]")[0];


        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "name";
        String order = params.get("order[0][dir]")[0];

        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
            case 0 :  sortBy = "id"; break;
            case 1 :  sortBy = "name"; break;
        }

        Page<Footer> datas = Footer.page(page, pageSize, sortBy, order, filter, getBrandId());
        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (Footer dt : datas.getList()) {
            ObjectNode row = Json.newObject();
            String action = "";

            if(feature.isEdit()){
                action += "<a class=\"btn btn-primary btn-sm action\" href=\""+controllers.admin.routes.FooterController.edit(dt.id)+"\"><i class=\"fa fa-pencil\"></i></a>&nbsp;";
            }
            if(feature.isDelete()){
                action += "<a class=\"btn btn-danger btn-sm action\" href=\"javascript:deleteData("+dt.id+");\"><i class=\"fa fa-trash\"></i></a>";
            }

            row.put("0", dt.id.toString());
            row.put("1", num);
            row.put("2", dt.name);
            row.put("3", dt.position);
            row.put("4", action);
            an.add(row);
            num++;
        }

        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public static Result save() {
        Form<Footer> form = Form.form(Footer.class).bindFromRequest();
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlAdd(form));
        }else{
            Footer data = form.get();
            data.slug = CommonFunction.slugGenerate(data.name);
            data.brand = getBrand();
            Footer uniqueCheck = Footer.find.where()
                    .eq("slug", data.slug).eq("isDeleted", false)
                    .eq("brand", getBrand())
                    .findUnique();
            if (uniqueCheck != null){
                flash("error", "Footer with similar name already exist");
                return badRequest(htmlAdd(form));
            }

            if(data.staticPageId != null){
                data.staticPage = StaticPage.find.byId(data.staticPageId);
                data.pageUrl = "/static/"+data.staticPage.slug;
            }

            data.userCms = getUserCms();
            data.save();
            flash("success", TITLE + " instance created");
            if (data.save.equals("1")){
                return redirect(routes.FooterController.index());
            }else{
                return redirect(routes.FooterController.add());
            }
            //return redirect(routes.FooterController.add());
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result update() {
        Form<Footer> form = Form.form(Footer.class).bindFromRequest();
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlEdit(form, new LinkedHashMap<>()));
        }else{
            Footer data = form.get();

            if(data.staticPageId != null){
                data.staticPage = StaticPage.find.byId(data.staticPageId);
                data.pageUrl = "/static/"+data.staticPage.slug;
            }

            data.update();
            flash("success", TITLE + " instance edited");
            return redirect(routes.FooterController.index());
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result delete(String id) {
        Ebean.beginTransaction();
        int status = 0;
        try {
            for (String aTmp : id.split(",")) {
                Footer data = Footer.findById(Long.parseLong(aTmp), getBrandId());
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
    @ApiOperation(value = "updateSeq.", notes = "updateSeq.", response = String.class, httpMethod = "POST")
    public static Result updateSeq() {
        ObjectMapper om = new ObjectMapper();
        JsonNode json = request().body().asJson();
        Map<String, List<Integer>> sequences = om.convertValue(json.findPath("ids"), Map.class);
        final Integer[] status = {0};
        Ebean.beginTransaction();
        try {
            sequences.forEach((k,v)->{
                int loop = 0;
                for (Integer dt : v) {
                    Footer data = Footer.findById(dt.longValue(), getBrandId());
                    data.sequence = loop;
                    data.update();
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