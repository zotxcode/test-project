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
import models.Faq;
import models.InformationCategoryGroup;
import models.RoleFeature;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.admin.faq.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hendriksaragih
 */
public class FaqController extends BaseController {
    private static final String TITLE = "FAQ";
    private static final String featureKey = "faq";

    private static Html htmlList(){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return list.render(TITLE, "List", feature);
    }

    private static Html htmlDetail(Faq data){
        return detail.render(TITLE, "Detail", data);
    }

    private static Html htmlEdit(Form<Faq> data){
        return _form.render(TITLE, "Edit", data, routes.FaqController.update(), getGroup(), getType());
    }

    private static Html htmlAdd(Form<Faq> data){
        return _form.render(TITLE, "Add", data, routes.FaqController.save(), getGroup(), getType());
    }

    private static Map<Integer, String> getType(){
        Map<Integer, String> result = new LinkedHashMap<>();
        result.put(Faq.TYPE_CUSTOMER, "Customer");
        result.put(Faq.TYPE_MERCHANT, "Merchant");
        return result;
    }

    private static Map<Integer, String> getGroup(){
        Map<Integer, String> result = new LinkedHashMap<>();
        InformationCategoryGroup.find.where()
                .eq("is_deleted", false)
                .eq("module_type", featureKey)
                .eq("brand", getBrand())
                .findList().forEach(dt->result.put(dt.id.intValue(), dt.name));
        return result;
    }

    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(htmlList());
    }

    @Security.Authenticated(Secured.class)
    public static Result add() {
        Form<Faq> formData = Form.form(Faq.class).fill(new Faq());
        return ok(htmlAdd(formData));
    }

    @Security.Authenticated(Secured.class)
    public static Result seqGroup() {
        List<InformationCategoryGroup> lists = InformationCategoryGroup.find.where()
                .eq("is_deleted", false).eq("module_type", featureKey)
                .eq("brand", getBrand())
                .orderBy("sequence ASC").findList();
        return ok(seqgroup.render(TITLE, "Sequence Group", lists));
    }

    @Security.Authenticated(Secured.class)
    public static Result seqFaq() {
        Map<InformationCategoryGroup, List<Faq>> listsCustomer = new LinkedHashMap<>();
        InformationCategoryGroup.find.where()
                .eq("is_deleted", false).eq("module_type", featureKey)
                .eq("brand", getBrand())
                .orderBy("sequence ASC").findList().forEach(data->{
            List<Faq> dt = Faq.find.where().eq("is_deleted", false)
                    .eq("faq_group_id", data.id)
                    .eq("type", 0)
                    .orderBy("sequence ASC").findList();
            listsCustomer.put(data, dt);
        });

        Map<InformationCategoryGroup, List<Faq>> listsMerchant = new LinkedHashMap<>();
        InformationCategoryGroup.find.where()
                .eq("is_deleted", false).eq("module_type", featureKey)
                .eq("brand", getBrand())
                .orderBy("sequence ASC").findList().forEach(data->{
            List<Faq> dt = Faq.find.where().eq("is_deleted", false)
                    .eq("faq_group_id", data.id)
                    .eq("type", 1)
                    .orderBy("sequence ASC").findList();
            listsMerchant.put(data, dt);
        });

        return ok(seqfaq.render(TITLE, "Sequence", listsCustomer, listsMerchant));
    }

    @Security.Authenticated(Secured.class)
    public static Result edit(Long id) {
        Faq dt = Faq.findById(id, getBrandId());
        dt.group_id = dt.faqGroup.id.intValue();
        Form<Faq> formData = Form.form(Faq.class).fill(dt);
        return ok(htmlEdit(formData));
    }

    @Security.Authenticated(Secured.class)
    public static Result detail(Long id) {
        Faq dt = Faq.findById(id, getBrandId());
        return ok(htmlDetail(dt));
    }

    @Security.Authenticated(Secured.class)
    public static Result lists() {
        RoleFeature feature = getUserAccessByFeature(featureKey);
        Map<String, String[]> params = request().queryString();

        Integer iTotalRecords = Faq.RowCount(getBrand());
        String name = params.get("search[value]")[0];
//        Integer filter = Integer.valueOf(params.get("filter")[0]);


        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "name";
        String order = params.get("order[0][dir]")[0];

        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
            case 1 :  sortBy = "id"; break;
            case 2 :  sortBy = "name"; break;
        }

        Page<Faq> datas = Faq.page(page, pageSize, sortBy, order, name, -1, getBrand());
        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (Faq dt : datas.getList()) {
            ObjectNode row = Json.newObject();
            String action = "";
            if(feature.isView()){
                action += "<a class=\"btn btn-default btn-sm action\" href=\""+controllers.admin.routes.FaqController.detail(dt.id)+"\"><i class=\"fa fa-search\"></i></a>&nbsp;";
            }
            if(feature.isEdit()){
                action += "<a class=\"btn btn-primary btn-sm action\" href=\""+controllers.admin.routes.FaqController.edit(dt.id)+"\"><i class=\"fa fa-pencil\"></i></a>&nbsp;";
            }
            if(feature.isDelete()){
                action += "<a class=\"btn btn-danger btn-sm action\" href=\"javascript:deleteData("+dt.id+");\"><i class=\"fa fa-trash\"></i></a>";
            }

            row.put("0", dt.id.toString());
            row.put("1", num);
            row.put("2", dt.faqGroup.name);
            row.put("3", dt.name);
//            row.put("4", dt.getTypeName());
            row.put("4", action);
            an.add(row);
            num++;
        }

        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public static Result save() {
        Form<Faq> form = Form.form(Faq.class).bindFromRequest();
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlAdd(form));
        }else{
            Faq data = form.get();
            data.faqGroup = InformationCategoryGroup.find.ref(Long.valueOf(data.group_id));
            data.brand = getBrand();
            data.type = 0;
            String typeSlug = "customer";
//            if(data.type == 1){
//                typeSlug = "merchant";
//            }
            data.slug = typeSlug + "-" + data.faqGroup.slug + "-" + CommonFunction.slugGenerate(data.name);
            Faq uniqueCheck = Faq.find.where()
                    .eq("slug", data.slug).eq("isDeleted", false)
                    .eq("brand", getBrand())
                    .findUnique();
            if (uniqueCheck != null){
                flash("error", "Page with similar title already exist");
                return badRequest(htmlAdd(form));
            }

            data.userCms = getUserCms();
            data.save();
            flash("success", TITLE + " instance created");
            if (data.save.equals("1")){
                return redirect(routes.FaqController.detail(data.id));
            }else{
                return redirect(routes.FaqController.add());
            }
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result update() {
        Form<Faq> form = Form.form(Faq.class).bindFromRequest();
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlEdit(form));
        }else{
            Faq data = form.get();
            data.faqGroup = InformationCategoryGroup.find.ref(Long.valueOf(data.group_id));
            data.update();
            flash("success", TITLE + " instance edited");
            return redirect(routes.FaqController.detail(data.id));
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result delete(String id) {
        Ebean.beginTransaction();
        int status = 0;
        try {
            for (String aTmp : id.split(",")) {
                Faq data = Faq.findById(Long.parseLong(aTmp), getBrandId());
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
    @ApiOperation(value = "updateSeqGroup.", notes = "updateSeqGroup.", response = String.class, httpMethod = "POST")
    public static Result updateSeqGroup() {
        ObjectMapper om = new ObjectMapper();
        JsonNode json = request().body().asJson();
        Integer[] sequences = om.convertValue(json.findPath("ids"), Integer[].class);
        final Integer[] status = {0};
        Ebean.beginTransaction();
        try {
            int loop = 0;
            for (Integer dt : sequences) {
                InformationCategoryGroup data = InformationCategoryGroup.find.ref(dt.longValue());
                data.sequence = loop;
                data.update();
                status[0] = 1;
                loop += 1;
            }

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

    @Security.Authenticated(Secured.class)
    @ApiOperation(value = "updateSeqFaq.", notes = "updateSeqFaq.", response = String.class, httpMethod = "POST")
    public static Result updateSeqFaq() {
        ObjectMapper om = new ObjectMapper();
        JsonNode json = request().body().asJson();
        Map<String, List<Integer>> sequences = om.convertValue(json.findPath("ids"), Map.class);
        final Integer[] status = {0};
        Ebean.beginTransaction();
        try {
            sequences.forEach((k,v)->{
                int loop = 0;
                for (Integer dt : v) {
                    Faq data = Faq.findById(dt.longValue(), getBrandId());
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

    @Security.Authenticated(Secured.class)
    public static Result saveGroup() {
        DynamicForm form = Form.form().bindFromRequest();
        String name = form.get("name");
        InformationCategoryGroup newGroup = new InformationCategoryGroup(name, featureKey, getBrand());
        String check = InformationCategoryGroup.validation(newGroup);
        BaseResponse response = new BaseResponse();

        if (check != null) {
            response.setBaseResponse(0, 0, 0, check, null);
            return badRequest(Json.toJson(response));
        }

        newGroup.userCms = getUserCms();
        newGroup.sequence = InformationCategoryGroup.getNextSequence(featureKey, getBrandId());
        newGroup.save();
        response.setBaseResponse(1, offset, 1, created, newGroup);
        return ok(Json.toJson(response));

    }
}