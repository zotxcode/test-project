package controllers.admin;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.enwie.api.BaseResponse;
import com.enwie.util.Encryption;
import com.enwie.util.MailConfig;
import controllers.BaseController;
import models.*;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.admin.user.*;

import javax.persistence.PersistenceException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hendriksaragih
 */
public class UserController extends BaseController {
    private final static Logger.ALogger logger = Logger.of(UserController.class);
    private static final String TITLE = "User";
    private static final String featureKey = "user";

    private static Html htmlList(){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return list.render(TITLE, "List", feature);
    }

    private static Html htmlAdd(Form<UserCms> data){
        return _form.render(TITLE, "Add", data, routes.UserController.save(), getRole(), getBrands(), getProvince(), new LinkedHashMap<>());
    }

    private static Html htmlEdit(Form<UserCms> data){
        return _form.render(TITLE, "Edit", data, routes.UserController.update(), getRole(), getBrands(), getProvince(), new LinkedHashMap<>());
    }

    private static Html htmlDetail(UserCms data){
        return detail.render(TITLE, "Detail", data);
    }

    private static Html htmlProfile(UserCms data){
        return profile.render("Profile", "Detail", data);
    }

    private static Html htmlChangePassword(Form<UserCms> data){
        return _change_password.render("", "Edit Profile", data, routes.UserController.savePassword());
    }

    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(htmlList());
    }

    @Security.Authenticated(Secured.class)
    public static Result lists() {
        RoleFeature feature = getUserAccessByFeature(featureKey);
        Map<String, String[]> params = request().queryString();

        Integer iTotalRecords = UserCms.RowCount();
        String filter = params.get("search[value]")[0];

        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "fullName";
        String order = params.get("order[0][dir]")[0];

        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
            case 1 :  sortBy = "id"; break;
            case 2 :  sortBy = "fullName"; break;
        }

        Page<UserCms> datas = UserCms.page(page, pageSize, sortBy, order, filter);
        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (UserCms dt : datas.getList()) {
            ObjectNode row = Json.newObject();
            String action = "";
            if(feature.isView()){
                action += "<a class=\"btn btn-default btn-sm action\" href=\""+ routes.UserController.detail(dt.id)+"\"><i class=\"fa fa-search\"></i></a>&nbsp;";
            }
            if(feature.isEdit()){
                action += "<a class=\"btn btn-primary btn-sm action\" href=\""+controllers.admin.routes.UserController.edit(dt.id)+"\"><i class=\"fa fa-pencil\"></i></a>&nbsp;";
            }
            if(feature.isDelete() && dt.id != 1){
                action += "<a class=\"btn btn-danger btn-sm action btn_delete\" href=\"#\" onclick=\"deleteData("+dt.id+")\"><i class=\"fa fa-trash\"></i></a>&nbsp;";
            }

            row.put("0", dt.id.toString());
            row.put("1", num);
            row.put("2", dt.fullName);
            row.put("3", dt.getBrandName());
            row.put("4", dt.role.name);
            row.put("5", dt.getIsDistributorStr());
            row.put("6", action);
            an.add(row);
            num++;
        }

        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public static Result add() {
        Form<UserCms> formData = Form.form(UserCms.class).fill(new UserCms());
        return ok(htmlAdd(formData));
    }

    @Security.Authenticated(Secured.class)
    public static Result edit(Long id) {
        UserCms dt = UserCms.find.byId(id);
        dt.roleId = dt.role.id;
        dt.brandId = dt.brand.id;
        Form<UserCms> formData = Form.form(UserCms.class).fill(dt);
        return ok(htmlEdit(formData));
    }

    private static Map<Integer, String> getRole(){
        Map<Integer, String> result = new LinkedHashMap<>();
        Role.find.where()
                .eq("is_deleted", false)
                .findList().forEach(dt->result.put(dt.id.intValue(), dt.name));
        return result;
    }

    private static Map<Integer, String> getBrands(){
        Map<Integer, String> result = new LinkedHashMap<>();
        Brand.find.where()
                .eq("is_deleted", false)
                .findList().forEach(dt->result.put(dt.id.intValue(), dt.name));
        return result;
    }

    private static Map<Integer, String> getProvince(){
        Map<Integer, String> result = new LinkedHashMap<>();
        Province.find.where()
                .eq("is_deleted", false)
                .findList().forEach(dt->result.put(dt.id.intValue(), dt.name));
        return result;
    }

    @Security.Authenticated(Secured.class)
    public static Result save() {
        Form<UserCms> form = Form.form(UserCms.class).bindFromRequest();
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlAdd(form));
        }else{

            String password = generatePassword();
            UserCms data = form.get();

            List<UserCms> listUser = UserCms.find.where().eq("isDeleted", false).eq("email", data.email).findList();
            if(listUser.size() > 0){
                flash("error", "Email already exists.");
                return badRequest(htmlAdd(form));
            }

            data.fullName = data.firstName+" "+data.lastName;
            data.role = Role.find.byId(data.roleId);
            data.brand = Brand.find.byId(data.brandId);

            if(data.isDistributor){
                Province province = Province.findById(data.provinceId);
                if (province != null) data.province = province;
                City city = City.findById(data.cityId);
                if (city != null) data.city = city;

                List<UserCms> list2 = UserCms.find.where().eq("isDeleted", false).eq("city", city).findList();
                if(list2.size() > 0){
                    flash("error", "Distributor with "+city.name+" city already exists.");
                    return badRequest(htmlAdd(form));
                }
            }else {
                data.province = null;
                data.city = null;
            }

            data.password = Encryption.EncryptAESCBCPCKS5Padding(password);
            data.save();
            Thread thread = new Thread(() -> {
                try {
                    MailConfig.sendmail(data.email, MailConfig.subjectActivation,
                            MailConfig.renderMailSendPasswordUserCMSTemplate(data, password));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            thread.start();
            flash("success", TITLE + " instance created");
            if (data.save.equals("1")){
                return redirect(routes.UserController.detail(data.id));
            }else{
                return redirect(routes.UserController.add());
            }
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result update() {
        Form<UserCms> form = Form.form(UserCms.class).bindFromRequest();
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlAdd(form));
        }else{
            UserCms data = form.get();
            data.fullName = data.firstName+" "+data.lastName;
            data.role = Role.find.byId(data.roleId);
            data.brand = Brand.find.byId(data.brandId);
            data.update();
            flash("success", TITLE + " instance updated");
            return redirect(routes.UserController.index());
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result delete(String id) {
        Ebean.beginTransaction();
        int status = 0;
        try {
            for (String aTmp : id.split(",")) {
                UserCms data = UserCms.find.ref(Long.parseLong(aTmp));
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
        UserCms dt = UserCms.find.ref(id);
        return ok(htmlDetail(dt));
    }

    @Security.Authenticated(Secured.class)
    public static Result profile() {
        UserCms dt = getUserCms();
        return ok(htmlProfile(dt));
    }

    @Security.Authenticated(Secured.class)
    public static Result changePassword() {
        UserCms dt = getUserCms();
        Form<UserCms> formData = Form.form(UserCms.class).fill(dt);
        return ok(htmlChangePassword(formData));
    }

    @Security.Authenticated(Secured.class)
    public static Result savePassword() {
        Form<UserCms> form = Form.form(UserCms.class).bindFromRequest();
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlChangePassword(form));
        }else{
            UserCms data = form.get();


            try {
                if(!data.newPassword.equals("")){
                    if(!data.password.equals(Encryption.EncryptAESCBCPCKS5Padding(data.oldPassword))){
                        flash("error", "Current password is invalid.");
                        return badRequest(htmlChangePassword(form));
                    }
                }
                data.fullName = data.firstName+" "+data.lastName;
                data.isActive = true;
                if(!data.newPassword.equals("")){
                    data.password = Encryption.EncryptAESCBCPCKS5Padding(data.newPassword);
                }
                data.update();
            } catch (Exception e) {
                e.printStackTrace();
            }
            flash("success", TITLE + " instance updated");
            return redirect(routes.UserController.profile());
        }
    }

    public static String generatePassword(){
        return getRandomString("CHAR", 6);
    }

    private static String getRandomString(String type, int length){
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numbers = "0123456789";
        SecureRandom rnd = new SecureRandom();

        String tmp;
        if(type.equals("CHAR")){
            tmp = chars;
        }else tmp = numbers;

        StringBuilder sb = new StringBuilder( length );
        for( int i = 0; i < length; i++ )
            sb.append( tmp.charAt( rnd.nextInt(tmp.length()) ) );
        return sb.toString();
    }

    @Security.Authenticated(Secured.class)
    public static Result cityLists(Long id) {
        ObjectNode result = Json.newObject();
        List<City> datas = City.find.where()
                .eq("is_deleted", false)
                .eq("province_id", id)
                .findList();


        result.put("total_count", datas.size());
        result.put("incomplete_results", false);
        ArrayNode an = result.putArray("items");
        for (City dt : datas) {
            ObjectNode row = Json.newObject();
            row.put("id", dt.id);
            row.put("text", dt.name);
            an.add(row);
        }

        return ok(result);
    }
}