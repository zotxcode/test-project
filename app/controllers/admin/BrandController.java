package controllers.admin;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.enwie.api.BaseResponse;
import com.enwie.util.CommonFunction;
import controllers.BaseController;
import models.Brand;
import models.Photo;
import models.RoleFeature;
import models.UserCms;
import play.Play;
import play.data.Form;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.admin.brand._form;
import views.html.admin.brand.list;

import java.io.File;
import java.util.Map;

/**
 * Created by hendriksaragih on 1/31/17.
 */
public class BrandController extends BaseController {
    private static final String TITLE = "Brand";
    private static final String featureKey = "brand";

    private static Html htmlList() {
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return list.render(TITLE, "List", feature);
    }

    private static Html htmlEdit(Form<Brand> data){
        return _form.render(TITLE, "Edit", data, routes.BrandController.update());
    }

    private static Html htmlAdd(Form<Brand> data){
        return _form.render(TITLE, "Add", data, routes.BrandController.save());
    }

    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(htmlList());
    }

    @Security.Authenticated(Secured.class)
    public static Result add() {
        Form<Brand> formData = Form.form(Brand.class).fill(new Brand());
        return ok(htmlAdd(formData));
    }

    @Security.Authenticated(Secured.class)
    public static Result edit(Long id) {
        Brand dt = Brand.find.byId(id);
        dt.imageLink = dt.getImageLink();
        Form<Brand> formData = Form.form(Brand.class).fill(dt);
        return ok(htmlEdit(formData));
    }

    @Security.Authenticated(Secured.class)
    public static Result lists() {
        RoleFeature feature = getUserAccessByFeature(featureKey);
        Map<String, String[]> params = request().queryString();

        Integer iTotalRecords = Brand.findRowCount();
        String name = params.get("search[value]")[0];
        Integer filter = Integer.valueOf(params.get("filter")[0]);


        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "name";
        String order = params.get("order[0][dir]")[0];

        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
            case 1 :  sortBy = "id"; break;
            case 2 :  sortBy = "name"; break;
        }

        Page<Brand> datas = Brand.page(page, pageSize, sortBy, order, name, filter);
        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        String filePath = Play.application().path() + File.separator + "public" + File.separator + "uploads" + File.separator;

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (Brand dt : datas.getList()) {

            ObjectNode row = Json.newObject();
            String action = "";
            String status = "";
            if(feature.isEdit()){
                action += "<a class=\"btn btn-primary btn-sm action\" href=\""+ routes.BrandController.edit(dt.id)+"\"><i class=\"fa fa-pencil\"></i></a>&nbsp;";
                status += "<input value=\""+dt.id+"\" class=\"cb-status\" data-toggle=\"toggle\" data-on=\"Active\" data-off=\"Inactive\" data-size=\"small\" data-style=\"ios\" data-offstyle=\"danger\" type=\"checkbox\" "+((dt.status)?"checked":"")+">";
            }else status += dt.getStatus();

            row.put("0", dt.id.toString());
            row.put("1", num);
            row.put("2", dt.name);
            row.put("3", dt.qtyProduct);
            row.put("4", dt.stockProduct);
            row.put("5", dt.domain);
            row.put("6", status);
            row.put("7", action);
            an.add(row);
            num++;
        }

        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public static Result save() {
        Form<Brand> form = Form.form(Brand.class).bindFromRequest();
        if (form.hasErrors()){
            System.out.println(form.globalErrors());
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlAdd(form));
        }else{
            Http.MultipartFormData body = request().body().asMultipartFormData();
            Brand data = form.get();
            Transaction txn = Ebean.beginTransaction();
            try {
                data.slug = CommonFunction.slugGenerate(data.name);
                data.stockProduct = data.qtyProduct = 0;
                Brand uniqueCheck = Brand.find.where().eq("slug", data.slug).findUnique();
                if (uniqueCheck != null){
                    flash("error", "Brand with similar code already exist");
                    return badRequest(htmlAdd(form));
                }

                Http.MultipartFormData.FilePart picture = body.getFile("imageUrl");
                File newFile = Photo.uploadImageCrop2(picture, "brd", data.slug, data.imageUrlX, data.imageUrlY, data.imageUrlW, data.imageUrlH, Photo.brandImageSize, "jpg");

                UserCms cms = getUserCms();
                data.userCms = cms;
                data.imageUrl = newFile != null ? Photo.createUrl("brd", newFile.getName()) : "";
                data.save();

                String roleKey = cms.role.key;
                if (newFile != null){
                    Photo.saveRecord("brd", newFile.getName(), "", "", "", picture.getFilename(), cms.id, roleKey,
                            "Brand", data.id);
                }

                txn.commit();
            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
                flash("error", "Please correct errors bellow.");
                return badRequest(htmlEdit(form));
            } finally {
                txn.end();
            }
            flash("success", TITLE + " instance created");
            if (data.save.equals("1")){
                return redirect(routes.BrandController.index());
            }else{
                return redirect(routes.BrandController.add());
            }
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result update() {
        Form<Brand> form = Form.form(Brand.class).bindFromRequest();
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlEdit(form));
        }else{
            Brand data = form.get();
            Transaction txn = Ebean.beginTransaction();
            Http.MultipartFormData body = request().body().asMultipartFormData();
            try {
                Http.MultipartFormData.FilePart picture = body.getFile("imageUrl");
                File newFile = Photo.uploadImageCrop2(picture, "brd", data.slug, data.imageUrlX, data.imageUrlY, data.imageUrlW, data.imageUrlH, Photo.brandImageSize, "jpg");

                UserCms cms = getUserCms();
                String roleKey = cms.role.key;
                if (newFile != null){
                    data.imageUrl = Photo.createUrl("brd", newFile.getName());
                    Photo.saveRecord("brd", newFile.getName(), "", "", "", picture.getFilename(), cms.id, roleKey,
                            "Brand", data.id);
                }

                data.update();
                txn.commit();
            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
                flash("error", "Please correct errors bellow.");
                return badRequest(htmlEdit(form));
            } finally {
                txn.end();
            }
            flash("success", TITLE + " instance edited");
            return redirect(routes.BrandController.index());
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result delete(String id) {
        Ebean.beginTransaction();
        int status = 0;
        try {
            for (String aTmp : id.split(",")) {
                Brand data = Brand.find.ref(Long.parseLong(aTmp));
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
    public static Result updateStatus(String id, String newStatus) {

        Ebean.beginTransaction();
        int status = 0;
        try {
            String[] tmp = id.split(",");
            for (int i=0; i < tmp.length; i++)
            {

                Brand data = Brand.find.ref(Long.parseLong(tmp[i]));
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
}
