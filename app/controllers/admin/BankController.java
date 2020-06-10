package controllers.admin;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.enwie.api.BaseResponse;
import com.enwie.util.CommonFunction;
import controllers.BaseController;
import models.Bank;
import models.Photo;
import models.RoleFeature;
import models.UserCms;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.admin.bank._form;
import views.html.admin.bank.list;

import java.io.File;
import java.util.Map;

/**
 * @author hendriksaragih
 */
public class BankController extends BaseController {
    private static final String TITLE = "Bank";
    private static final String featureKey = "bank";

    private static Html htmlList(){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return list.render(TITLE, "List", feature);
    }

    private static Html htmlEdit(Form<Bank> data){
        return _form.render(TITLE, "Edit", data, routes.BankController.update());
    }

    private static Html htmlAdd(Form<Bank> data){
        return _form.render(TITLE, "Add", data, routes.BankController.save());
    }

    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(htmlList());
    }

    @Security.Authenticated(Secured.class)
    public static Result add() {
        Form<Bank> formData = Form.form(Bank.class).fill(new Bank());
        return ok(htmlAdd(formData));
    }

    @Security.Authenticated(Secured.class)
    public static Result edit(Long id) {
        Bank dt = Bank.findById(id, getBrandId());
        dt.imageLink = dt.getImageLink();
        Form<Bank> formData = Form.form(Bank.class).fill(dt);
        return ok(htmlEdit(formData));
    }

    @Security.Authenticated(Secured.class)
    public static Result lists() {
        RoleFeature feature = getUserAccessByFeature(featureKey);
        Map<String, String[]> params = request().queryString();

        Integer iTotalRecords = Bank.findRowCount(getBrandId());
        String name = params.get("search[value]")[0];
        Integer filter = Integer.valueOf(params.get("filter")[0]);


        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "bankName";
        String order = params.get("order[0][dir]")[0];

        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
            case 1 :  sortBy = "id"; break;
            case 2 :  sortBy = "bankName"; break;
        }

        Page<Bank> datas = Bank.page(page, pageSize, sortBy, order, name, filter, getBrandId());
        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (Bank dt : datas.getList()) {

            ObjectNode row = Json.newObject();
            String action = "";
            String status = "";
            if(feature.isEdit()){
                action += "<a class=\"btn btn-primary btn-sm action\" href=\""+ routes.BankController.edit(dt.id)+"\"><i class=\"fa fa-pencil\"></i></a>&nbsp;";
                status += "<input value=\""+dt.id+"\" class=\"cb-status\" data-toggle=\"toggle\" data-on=\"Active\" data-off=\"Inactive\" data-size=\"small\" data-style=\"ios\" data-offstyle=\"danger\" type=\"checkbox\" "+((dt.status)?"checked":"")+">";
            }else status = dt.getStatus();
            if(feature.isDelete()){
                action += "<a class=\"btn btn-danger btn-sm action\" href=\"javascript:deleteData("+dt.id+");\"><i class=\"fa fa-trash\"></i></a>&nbsp;";
            }

            row.put("0", dt.id.toString());
            row.put("1", num);
            row.put("2", dt.bankName);
            row.put("3", dt.accountName);
            row.put("4", dt.accountNumber);
            row.put("5", status);
            row.put("6", action);
            an.add(row);
            num++;
        }

        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public static Result save() {
        Form<Bank> form = Form.form(Bank.class).bindFromRequest();
        if (form.hasErrors()){
            System.out.println(form.globalErrors());
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlAdd(form));
        }else{
            Http.MultipartFormData body = request().body().asMultipartFormData();
            Bank data = form.get();
            Transaction txn = Ebean.beginTransaction();
            try {
                String filename = CommonFunction.slugGenerate(data.imageName);
                Http.MultipartFormData.FilePart picture = body.getFile("imageUrl");
                File newFile = Photo.uploadImageCrop(picture, "bank", filename, Photo.bankImageSize, "jpg");

                Bank uniqueCheck = Bank.find.where()
                        .eq("bank_name", data.bankName).eq("isDeleted", false)
                        .eq("brand", getBrand())
                        .setMaxRows(1)
                        .findUnique();
                if (uniqueCheck != null){
                    flash("error", "Bank with similar name already exist");
                    return badRequest(htmlAdd(form));
                }

                UserCms cms = getUserCms();
                data.userCms = cms;
                data.brand = getBrand();
                data.imageUrl = newFile != null ? Photo.createUrl("bank", newFile.getName()) : "";
                data.save();

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
                return redirect(routes.BankController.index());
            }else{
                return redirect(routes.BankController.add());
            }
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result update() {
        Form<Bank> form = Form.form(Bank.class).bindFromRequest();
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlEdit(form));
        }else{
            Bank data = form.get();
            Http.MultipartFormData body = request().body().asMultipartFormData();
            Http.MultipartFormData.FilePart picture = body.getFile("imageUrl");
            String filename = CommonFunction.slugGenerate(data.imageName);

            Transaction txn = Ebean.beginTransaction();
            try {
                File newFile = Photo.uploadImageCrop(picture, "bank", filename, Photo.bankImageSize, "jpg");
                UserCms cms = getUserCms();
                String roleKey = cms.role.key;
                if (newFile != null){
                    data.imageUrl = Photo.createUrl("bank", newFile.getName());
                    Photo.saveRecord("bank", newFile.getName(), "", "", "", picture.getFilename(), cms.id, roleKey,
                            "Bank", data.id);
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
            return redirect(routes.BankController.index());
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result delete(String id) {
        Ebean.beginTransaction();
        int status = 0;
        try {
            for (String aTmp : id.split(",")) {
                Bank data = Bank.findById(Long.parseLong(aTmp), getBrandId());
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
        Logger.debug("id update = "+id);
        Ebean.beginTransaction();
        int status = 0;
        try {
            String[] tmp = id.split(",");
            for (int i=0; i < tmp.length; i++)
            {

                Bank data = Bank.findById(Long.parseLong(tmp[i]), getBrandId());
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