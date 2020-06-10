package controllers.admin;

import com.avaje.ebean.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.enwie.api.BaseResponse;
import com.enwie.util.CommonFunction;
import controllers.BaseController;
import models.*;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Http.MultipartFormData;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.admin.socmed._form;
import views.html.admin.socmed.list;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author hendriksaragih
 */
public class SocmedController extends BaseController {
    private final static Logger.ALogger logger = Logger.of(SocmedController.class);
    private static final String TITLE = "Socmed";
    private static final String featureKey = "socmed";

    private static Html htmlList(){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return list.render(TITLE, "List", feature);
    }

    private static Html htmlEdit(Form<Socmed> data){
        return _form.render(TITLE, "Edit", data, routes.SocmedController.update());
    }

    private static Html htmlAdd(Form<Socmed> data){
        return _form.render(TITLE, "Add", data, routes.SocmedController.save());
    }


    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(htmlList());
    }

    @Security.Authenticated(Secured.class)
    public static Result add() {
        Socmed dt = new Socmed();
        Form<Socmed> formData = Form.form(Socmed.class).fill(dt);
        return ok(htmlAdd(formData));
    }

    @Security.Authenticated(Secured.class)
    public static Result edit(Long id) {
        Socmed dt = Socmed.findById(id, getBrandId());
        dt.imageLink = dt.getImageLink();
        dt.imageResponsiveLink = dt.getImageResponsiveLink();
        Form<Socmed> formData = Form.form(Socmed.class).fill(dt);
        return ok(htmlEdit(formData));
    }

    @Security.Authenticated(Secured.class)
    public static Result lists() {
        RoleFeature feature = getUserAccessByFeature(featureKey);

        Map<String, String[]> params = request().queryString();

        Integer iTotalRecords = Socmed.findRowCount(getBrand());
        String name = params.get("search[value]")[0];
        Integer filter = Integer.valueOf(params.get("filter")[0]);


        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "name";
        String order = params.get("order[0][dir]")[0];

        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
            case 0 :  sortBy = "id"; break;
            case 1 :  sortBy = "id"; break;
            case 2 :  sortBy = "name"; break;
        }

        Page<Socmed> datas = Socmed.page(page, pageSize, sortBy, order, name, filter, getBrand());
        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (Socmed dt : datas.getList()) {
            ObjectNode row = Json.newObject();
            String action = "" ;
            if(feature.isEdit()){
                action += "<a class=\"btn btn-primary btn-sm action\" href=\""+ routes.SocmedController.edit(dt.id)+"\"><i class=\"fa fa-pencil\"></i></a>&nbsp;" ;
            }
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
            row.put("2", dt.name);
            row.put("3", dt.getImageUrl());
            row.put("4", action);
            an.add(row);
            num++;
        }

        return ok(result);
    }


    @Security.Authenticated(Secured.class)
    public static Result save() {
        Form<Socmed> form = Form.form(Socmed.class).bindFromRequest();
        DynamicForm dForm = Form.form().bindFromRequest();
        MultipartFormData body = request().body().asMultipartFormData();
        MultipartFormData.FilePart picture = body.getFile("imageUrl");
        MultipartFormData.FilePart pictureResponsive = body.getFile("imageUrlResponsive");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlAdd(form));
        }else{
            Socmed data = form.get();
            Transaction txn = Ebean.beginTransaction();
            try {
                data.brand = getBrand();

                String imageName = CommonFunction.slugGenerate(data.imageName);
                File newFile = null;
                File newFileRes = null;
                newFile = Photo.uploadImageCrop2(picture, "soc", imageName, data.imageUrlX, data.imageUrlY, data.imageUrlW, data.imageUrlH, Photo.socmedSize, "jpg");
                newFileRes = Photo.uploadImageCrop2(pictureResponsive, "soc-res", imageName,
                        data.imageUrlResponsiveX, data.imageUrlResponsiveY, data.imageUrlResponsiveW, data.imageUrlResponsiveH, Photo.socmedResponsiveSize, "jpg");

                UserCms cms = getUserCms();
                data.imageUrl = (newFile == null) ? data.imageUrl
                        : Photo.createUrl("soc", newFile.getName());
                data.imageUrlResponsive = (newFileRes == null) ? data.imageUrlResponsive
                        : Photo.createUrl("soc-res", newFileRes.getName());

                data.userCms = cms;
                data.save();
                data.update();
                String roleKey = cms.role.key;

                if (newFile != null) {
                    Photo.saveRecord("soc", newFile.getName(), "", "", "", picture.getFilename(), cms.id, roleKey,
                            "Socmed", data.id);
                }

                if (newFileRes != null) {
                    Photo.saveRecord("soc-res", newFileRes.getName(), "", "", "", pictureResponsive.getFilename(), cms.id,
                            roleKey, "Socmed", data.id);
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
                return redirect(routes.SocmedController.index());
            }else{
                return redirect(routes.SocmedController.add());
            }

        }
    }

    @Security.Authenticated(Secured.class)
    public static Result update() {
        Form<Socmed> form = Form.form(Socmed.class).bindFromRequest();
        DynamicForm dForm = Form.form().bindFromRequest();
        MultipartFormData body = request().body().asMultipartFormData();
        MultipartFormData.FilePart picture = body.getFile("imageUrl");
        MultipartFormData.FilePart pictureResponsive = body.getFile("imageUrlResponsive");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return redirect(routes.SocmedController.index());
        }else{
            Socmed data = form.get();
            Transaction txn = Ebean.beginTransaction();
            try {
                String imageName = CommonFunction.slugGenerate(data.imageName);
                File newFile = null;
                File newFileRes = null;
                newFile = Photo.uploadImageCrop2(picture, "soc", imageName, data.imageUrlX, data.imageUrlY, data.imageUrlW, data.imageUrlH, Photo.socmedSize, "jpg");
                newFileRes = Photo.uploadImageCrop2(pictureResponsive, "soc-res", imageName,
                        data.imageUrlResponsiveX, data.imageUrlResponsiveY, data.imageUrlResponsiveW, data.imageUrlResponsiveH, Photo.socmedResponsiveSize, "jpg");

                UserCms cms = getUserCms();
                String roleKey = cms.role.key;
                if (newFile != null){
                    data.imageUrl = Photo.createUrl("soc", newFile.getName());
                    Photo.saveRecord("soc", newFile.getName(), "", "", "", picture.getFilename(), cms.id, roleKey,
                            "Socmed", data.id);
                }
                if (newFileRes != null) {
                    data.imageUrlResponsive = Photo.createUrl("soc-res", newFileRes.getName());
                    Photo.saveRecord("soc-res", newFileRes.getName(), "", "", "", pictureResponsive.getFilename(), cms.id,
                            roleKey, "Socmed", data.id);
                }


                data.update();
                txn.commit();
            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
                flash("error", "Please correct errors bellow.");
                return redirect(routes.SocmedController.index());
            } finally {
                txn.end();
            }
            flash("success", TITLE + " instance edited");
            return redirect(routes.SocmedController.index());
        }

    }

    @Security.Authenticated(Secured.class)
    public static Result updateStatus(String id, String newStatus) {

        Ebean.beginTransaction();
        int status = 0;
        try {
            String[] tmp = id.split(",");
            for (String aTmp : tmp) {
                Socmed data = Socmed.findById(Long.parseLong(aTmp), getBrandId());
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
                Socmed data = Socmed.findById(Long.parseLong(aTmp), getBrandId());
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

}