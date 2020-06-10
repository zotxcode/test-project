package controllers.admin;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.enwie.api.BaseResponse;
import controllers.BaseController;
import models.*;
import play.data.Form;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.admin.attribute._form;
import views.html.admin.attribute._form_detail;
import views.html.admin.attribute.list;
import views.html.admin.attribute.list_detail;
import com.enwie.util.Constant;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author hendriksaragih
 */
public class AttributeController extends BaseController {
    private static final String TITLE = "Master Attribute";
    private static final String featureKey = "attribute";
    private static final String TITLE_DETAIL = "Attribute Detail";

    private static Html htmlList(){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return list.render(TITLE, "List", feature);
    }
    private static Html htmlListDetail(BaseAttribute base){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return list_detail.render(TITLE_DETAIL, TITLE, "List", base, feature);
    }

    private static Html htmlEdit(Form<BaseAttribute> data){
        return _form.render(TITLE, "Edit", data, getListType(), routes.AttributeController.update());
    }

    private static Html htmlAdd(Form<BaseAttribute> data){
        return _form.render(TITLE, "Add", data, getListType(), routes.AttributeController.save());
    }

    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(htmlList());
    }

    @Security.Authenticated(Secured.class)
    public static Result add() {
        Form<BaseAttribute> formData = Form.form(BaseAttribute.class).fill(new BaseAttribute());
        return ok(htmlAdd(formData));
    }

    @Security.Authenticated(Secured.class)
    public static Result edit(Long id) {
        BaseAttribute dt = BaseAttribute.findById(id, getBrandId());
        Form<BaseAttribute> formData = Form.form(BaseAttribute.class).fill(dt);
        return ok(htmlEdit(formData));
    }

    public static Map<String, String> getListType(){
        Map<String, String> result = new HashMap<>();
        result.put(BaseAttribute.VARCHAR_TYPE, BaseAttribute.VARCHAR_TYPE);
        result.put(BaseAttribute.INTEGER_TYPE, BaseAttribute.INTEGER_TYPE);
        return result;
    }

    @Security.Authenticated(Secured.class)
    public static Result detail(Long id) {
        BaseAttribute base = BaseAttribute.findById(id, getBrandId());

        return ok(htmlListDetail(base));
    }

    @Security.Authenticated(Secured.class)
    public static Result lists() {
        RoleFeature feature = getUserAccessByFeature(featureKey);
        Map<String, String[]> params = request().queryString();

        Integer iTotalRecords = BaseAttribute.findRowCount(getBrand());
        String filter = params.get("search[value]")[0];


        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "name";
        String order = params.get("order[0][dir]")[0];

        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
            case 1 :  sortBy = "id"; break;
            case 2 :  sortBy = "name"; break;
        }

        Page<BaseAttribute> datas = BaseAttribute.page(page, pageSize, sortBy, order, filter, getBrand());
        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (BaseAttribute dt : datas.getList()) {
            ObjectNode row = Json.newObject();
            String action = "";
            if(feature.isView()){
                action += "<a class=\"btn btn-default btn-sm action\" href=\""+ routes.AttributeController.detail(dt.id)+"\"><i class=\"fa fa-search\"></i></a>&nbsp;";
            }
            if(feature.isEdit()){
                action += "<a class=\"btn btn-primary btn-sm action\" href=\""+ routes.AttributeController.edit(dt.id)+"\"><i class=\"fa fa-pencil\"></i></a>&nbsp;";
            }
            if(feature.isDelete()){
                action += "<a class=\"btn btn-danger btn-sm action\" href=\"javascript:deleteData("+dt.id+");\"><i class=\"fa fa-trash\"></i></a>";
            }

            row.put("0", dt.id.toString());
            row.put("1", num);
            row.put("2", dt.name);
            row.put("3", dt.type);
            row.put("4", action);
            an.add(row);
            num++;
        }

        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public static Result listsDetail(Long id) {
        RoleFeature feature = getUserAccessByFeature(featureKey);
        Map<String, String[]> params = request().queryString();
        BaseAttribute base = BaseAttribute.findById(id, getBrandId());
        Integer iTotalRecords = Attribute.findRowCount(id, getBrandId());
        String filter = params.get("search[value]")[0];

        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "value";
        String order = params.get("order[0][dir]")[0];

        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
            case 1 :  sortBy = "id"; break;
            case 2 :  sortBy = "value"; break;
        }

        Page<Attribute> datas = Attribute.page(id, page, pageSize, sortBy, order, filter, getBrandId());
        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (Attribute dt : datas.getList()) {
            ObjectNode row = Json.newObject();
            String action = "";
            if(feature.isEdit()){
                action += "<a class=\"btn btn-primary btn-sm action\" href=\""+ routes.AttributeController.editDetail(dt.id)+"\"><i class=\"fa fa-pencil\"></i></a>&nbsp;";
            }
            if(feature.isDelete()){
                action += "<a class=\"btn btn-danger btn-sm action\" href=\"javascript:deleteData("+dt.id+");\"><i class=\"fa fa-trash\"></i></a>&nbsp;";
            }

            row.put("0", dt.id.toString());
            row.put("1", num);
            row.put("2",  "<img src="+Constant.getInstance().getImageUrl() + dt.imageUrl+"  height=\"50\" width=\"50\">");
            row.put("3", dt.value);
            row.put("4", action);
            an.add(row);
            num++;
        }

        return ok(result);
    }

    private static Html htmlEditDetail(Form<Attribute> data, BaseAttribute base){
        return _form_detail.render(TITLE_DETAIL, TITLE, "Edit", base, data, routes.AttributeController.updateDetail(base.id));
    }

    private static Html htmlAddDetail(Form<Attribute> data, BaseAttribute base){
        return _form_detail.render(TITLE_DETAIL, TITLE, "Add", base, data, routes.AttributeController.saveDetail(base.id));
    }

    @Security.Authenticated(Secured.class)
    public static Result addDetail(Long id) {
        BaseAttribute dt = BaseAttribute.findById(id, getBrandId());
        Form<Attribute> formData = Form.form(Attribute.class).fill(new Attribute());
        return ok(htmlAddDetail(formData, dt));
    }

    @Security.Authenticated(Secured.class)
    public static Result editDetail(Long id) {
        Attribute dt = Attribute.findById(id, getBrandId());
        Form<Attribute> formData = Form.form(Attribute.class).fill(dt);
        return ok(htmlEditDetail(formData, dt.baseAttribute));
    }

    @Security.Authenticated(Secured.class)
    public static Result save() {
        Form<BaseAttribute> form = Form.form(BaseAttribute.class).bindFromRequest();
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlAdd(form));
        }else{
            BaseAttribute data = form.get();
            data.brand = getBrand();
            data.save();
            flash("success", TITLE + " instance created");
            if (data.save.equals("1")){
                return redirect(routes.AttributeController.index());
            }else{
                return redirect(routes.AttributeController.add());
            }
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result update() {
        Form<BaseAttribute> form = Form.form(BaseAttribute.class).bindFromRequest();
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlEdit(form));
        }else{
            BaseAttribute data = form.get();
            data.update();
            flash("success", TITLE + " instance edited");
            return redirect(routes.AttributeController.index());
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result delete(String id) {
        Ebean.beginTransaction();
        int status = 0;
        try {
            String[] tmp = id.split(",");
            for (int i=0; i < tmp.length; i++)
            {
                BaseAttribute data = BaseAttribute.findById(Long.parseLong(tmp[i]), getBrandId());
                for(Attribute attribute : data.attributesData){
                    attribute.isDeleted = true;
                    attribute.update();
                }
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
    public static Result saveDetail(Long id) {
        Form<Attribute> form = Form.form(Attribute.class).bindFromRequest();
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart picture = body.getFile("imageUrl");
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlAddDetail(form, BaseAttribute.findById(id, getBrandId())));
        }else{
            Attribute data = form.get();
            Transaction txn = Ebean.beginTransaction();
            try {
                data.brand = getBrand();
                data.baseAttribute = BaseAttribute.findById(id, getBrandId());

                UUID uuid = UUID.randomUUID();
                String imageName = uuid.toString();
                File newFile = Photo.uploadImageCrop2(picture, "col", imageName, data.imageUrlX, data.imageUrlY, data.imageUrlW, data.imageUrlH, Photo.colorSize, "jpg");
                data.imageUrl = (newFile == null) ? data.imageUrl
                        : Photo.createUrl("col", newFile.getName());

                data.save();

                txn.commit();
            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
                flash("error", "Please correct errors bellow.");
                return badRequest(htmlAddDetail(form, data.baseAttribute));
            } finally {
                txn.end();
            }

            flash("success", TITLE + " instance created");
            if (data.save.equals("1")){
                return redirect(routes.AttributeController.detail(id));
            }else{
                return redirect(routes.AttributeController.addDetail(id));
            }
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result updateDetail(Long id) {
        Form<Attribute> form = Form.form(Attribute.class).bindFromRequest();
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlEditDetail(form, BaseAttribute.findById(id, getBrandId())));
        }else{
            Attribute data = form.get();
            data.update();
            flash("success", TITLE + " instance edited");
            return redirect(routes.AttributeController.detail(id));
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result deleteDetail(String id) {
        Ebean.beginTransaction();
        int status = 0;
        try {
            String[] tmp = id.split(",");
            for (int i=0; i < tmp.length; i++)
            {
                Attribute data = Attribute.findById(Long.parseLong(tmp[i]), getBrandId());
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
    public static Result listsBaseAttribute() {
        Map<String, String[]> params = request().queryString();

        ObjectNode result = Json.newObject();
        Integer iTotalRecords = BaseAttribute.findRowCount(getBrand());
        String filter = params.get("search[value]")[0];

        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "name";
        String order = params.get("order[0][dir]")[0];

        Page<BaseAttribute> datas = BaseAttribute.find.where()
                .ilike("name", "%" + filter + "%")
                .eq("is_deleted", false)
                .eq("brand", getBrand())
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .setFetchAhead(false)
                .getPage(page);
        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (BaseAttribute dt : datas.getList()) {
            ObjectNode row = Json.newObject();
            String action = "<button class=\"btn btn-primary\" onclick=\"selectAttribute("+dt.id+", '"+dt.name+"')\">Select</button>";
            row.put("0", num);
            row.put("1", dt.name);
            row.put("2", action);
//            ArrayNode anVal = row.putArray("value");
//            Attribute.find.where().eq("baseAttribute",dt)
//                    .findList().forEach(dt2->anVal.add(Json.newObject() (dt2.id.intValue(), dt2.name));

            an.add(row);
            num++;
        }
        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public static Result listsBaseAttributeForSelect() {

        Map<String, String[]> params = request().queryString();
        ObjectNode result = Json.newObject();
        String filter = params.get("q")[0];

        Integer pageSize = 30;
        Integer page = 0;
        if(params.get("page") != null){
            page = Integer.valueOf(params.get("page")[0]);
        }

        String sortBy = "name";
        String order = "asc";

        Page<BaseAttribute> datas = BaseAttribute.find.where()
                .ilike("name", "%" + filter + "%")
                .eq("is_deleted", false)
                .eq("brand", getBrand())
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .setFetchAhead(false)
                .getPage(page);
        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        result.put("total_count", iTotalDisplayRecords);
        result.put("incomplete_results", false);

        ArrayNode an = result.putArray("items");
        for (BaseAttribute dt : datas.getList()) {
            ObjectNode row = Json.newObject();
            row.put("id", dt.id);
            row.put("name", dt.name);
            an.add(row);
        }

        return ok(result);
    }



    @Security.Authenticated(Secured.class)
    public static Result listsBaseAttributeForSelectByCategory() {

        Map<String, String[]> params = request().queryString();

        String categoryId = params.get("q")[0];
        Category category = Category.findById(new Long(categoryId), getBrandId());

        ObjectNode result = Json.newObject();

        Integer iTotalDisplayRecords = category.listBaseAttribute.size();

        result.put("total_count", iTotalDisplayRecords);
        result.put("incomplete_results", false);

        ArrayNode an = result.putArray("items");
        for (BaseAttribute dt : category.listBaseAttribute) {
            ObjectNode row = Json.newObject();
            row.put("id", dt.id);
            row.put("text", dt.name);
            ArrayNode and = row.putArray("items");
            List<Attribute> lists = Attribute.find.where()
                    .eq("is_deleted", false)
                    .eq("brand", getBrand())
                    .eq("baseAttribute", dt)
                    .orderBy("value asc ").findList();
            ObjectNode rowDefaultDetail = Json.newObject();
            rowDefaultDetail.put("id", 0);
            rowDefaultDetail.put("text", "Not Available");
            and.add(rowDefaultDetail);
            for (Attribute d : lists) {
                ObjectNode rowd = Json.newObject();
                rowd.put("id", d.id);
                rowd.put("text", d.value);
                and.add(rowd);
            }
            an.add(row);
        }

        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public static Result listsAttributeForSelect() {

        Map<String, String[]> params = request().queryString();

        String baseAttributeId = params.get("q")[0];

        Integer pageSize = 100;
        Integer page = 0;
        if(params.get("page") != null){
            page = Integer.valueOf(params.get("page")[0]);
        }

        String sortBy = "value";
        String order = "asc";

        ObjectNode result = Json.newObject();

        Page<Attribute> datas = Attribute.find.where()
                .eq("is_deleted", false)
                .eq("baseAttribute.id", Long.parseLong(baseAttributeId))
                .eq("brand", getBrand())
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .setFetchAhead(false)
                .getPage(page);
        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        result.put("total_count", iTotalDisplayRecords);
        result.put("incomplete_results", false);

        ArrayNode an = result.putArray("items");
        for (Attribute dt : datas.getList()) {
            ObjectNode row = Json.newObject();
            row.put("id", dt.id);
            row.put("text", dt.value);
            an.add(row);
        }

        return ok(result);
    }

}