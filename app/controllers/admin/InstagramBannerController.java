package controllers.admin;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.Query;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.enwie.api.BaseResponse;
import com.enwie.util.CommonFunction;
import com.enwie.util.Helper;
import com.wordnik.swagger.annotations.ApiOperation;
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
import views.html.admin.instagramBanner._form;
import views.html.admin.instagramBanner.list;
import views.html.admin.instagramBanner.sequence;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author hendriksaragih
 */
public class InstagramBannerController extends BaseController {
    private final static Logger.ALogger logger = Logger.of(InstagramBannerController.class);
    private static final String TITLE = "Instagram Banner";
    private static final String featureKey = "instagram_banner";

    private static Html htmlList(){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return list.render(TITLE, "List", feature);
    }

    private static Html htmlEdit(Form<InstagramBanner> data, Map<Integer, String> listsCategory, Map<Integer, String> listsSubCategory, Map<Integer, String> listsProduct){
        return _form.render(TITLE, "Edit", data, routes.InstagramBannerController.update(), listsCategory, listsSubCategory, listsProduct, getType());
    }

    private static Html htmlAdd(Form<InstagramBanner> data){
        return _form.render(TITLE, "Add", data, routes.InstagramBannerController.save(), new LinkedHashMap<>(), new LinkedHashMap<>(), new LinkedHashMap<>(), getType());
    }


    @Security.Authenticated(Secured.class)
    public static Result sequence() {
        return ok(sequence.render(TITLE, "Sequence", getListBannerByType()));
    }

    private static List<InstagramBanner> getListBannerByType() {
        Query<InstagramBanner> qry = InstagramBanner.find.where()
                .eq("is_deleted", false)
                .eq("brand", getBrand())
                .eq("status", true)
                .orderBy("sequence ASC");

        return qry.findList();
    }

    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(htmlList());
    }

    @Security.Authenticated(Secured.class)
    public static Result add() {
        InstagramBanner dt = new InstagramBanner();
        dt.fromDate = Helper.getDateFromTimeStamp(new Date());
        Date toDate = new Date();
        toDate.setDate(toDate.getDate()+1);
        dt.toDate = Helper.getDateFromTimeStamp(toDate);
        Form<InstagramBanner> formData = Form.form(InstagramBanner.class).fill(dt);
        return ok(htmlAdd(formData));
    }

    @Security.Authenticated(Secured.class)
    public static Result edit(Long id) {
        InstagramBanner dt = InstagramBanner.findById(id, getBrandId());
        dt.imageLink = dt.getImageLink();
        dt.imageResponsiveLink = dt.getImageResponsiveLink();
        dt.fromDate = Helper.getDateFromTimeStamp(dt.activeFrom);
        dt.toDate = Helper.getDateFromTimeStamp(dt.activeTo);
        dt.toTime = Helper.getTimeFromTimeStamp(dt.activeTo);
        dt.fromTime = Helper.getTimeFromTimeStamp(dt.activeFrom);
        Map<Integer, String> listsCategory = new LinkedHashMap<>();
        Map<Integer, String> listsSubCategory = new LinkedHashMap<>();
        for(Category category : dt.categories){
            listsSubCategory.put(category.id.intValue(), category.name);
            listsCategory.put(category.parentCategory.id.intValue(), category.parentCategory.name);
        }

        Map<Integer, String> listsProduct = new LinkedHashMap<>();
        for(Product product : dt.products){
            listsProduct.put(product.id.intValue(), product.name);
        }
        if (dt.externalUrl != null && !dt.externalUrl.isEmpty()){
            dt.urlType = 2;
        }
        Form<InstagramBanner> formData = Form.form(InstagramBanner.class).fill(dt);
        return ok(htmlEdit(formData, listsCategory, listsSubCategory, listsProduct));
    }

    private static Map<Integer, String> getType(){
        Map<Integer, String> result = new LinkedHashMap<>();
        result.put(1, "Internal Url");
        result.put(2, "External Url");
        return result;
    }

    @Security.Authenticated(Secured.class)
    public static Result lists() {
        RoleFeature feature = getUserAccessByFeature(featureKey);

        Map<String, String[]> params = request().queryString();

        Integer iTotalRecords = InstagramBanner.findRowCount(getBrand());
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
            case 3 :  sortBy = "activeFrom"; break;
            case 4 :  sortBy = "activeTo"; break;
            case 5 :  sortBy = "status"; break;
        }

        Page<InstagramBanner> datas = InstagramBanner.page(page, pageSize, sortBy, order, name, filter, getBrand());
        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (InstagramBanner dt : datas.getList()) {
            ObjectNode row = Json.newObject();
            String action = "" ;
            if(feature.isEdit()){
                action += "<a class=\"btn btn-primary btn-sm action\" href=\""+ routes.InstagramBannerController.edit(dt.id)+"\"><i class=\"fa fa-pencil\"></i></a>&nbsp;" ;
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
            row.put("3", dt.getDateFrom());
            row.put("4", dt.getDateTo());
            row.put("5", status);
            row.put("6", action);
            an.add(row);
            num++;
        }

        return ok(result);
    }


    @Security.Authenticated(Secured.class)
    public static Result save() {
        Form<InstagramBanner> form = Form.form(InstagramBanner.class).bindFromRequest();
        DynamicForm dForm = Form.form().bindFromRequest();
        MultipartFormData body = request().body().asMultipartFormData();
        MultipartFormData.FilePart picture = body.getFile("imageUrl");
        MultipartFormData.FilePart pictureResponsive = body.getFile("imageUrlResponsive");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        if (form.hasErrors()){
            System.out.println(form.errors());
            flash("error", "Please correct errors bellow2.");
            return badRequest(htmlAdd(form));
        }else{
            InstagramBanner data = form.get();
            Transaction txn = Ebean.beginTransaction();
            try {
                data.brand = getBrand();

                String imageName = CommonFunction.slugGenerate(data.bannerImageName);
                File newFile = null;
                File newFileRes = null;
                newFile = Photo.uploadImageCrop2(picture, "ig-ban", imageName, data.imageUrlX, data.imageUrlY, data.imageUrlW, data.imageUrlH, Photo.instagramBannerSize, "jpg");
                newFileRes = Photo.uploadImageCrop2(pictureResponsive, "ig-ban-res", imageName,
                        data.imageUrlResponsiveX, data.imageUrlResponsiveY, data.imageUrlResponsiveW, data.imageUrlResponsiveH, Photo.instagramBannerResponsiveSize, "jpg");
                data.sequence = InstagramBanner.getNextSequence(getBrandId());

                UserCms cms = getUserCms();
                data.imageUrl = (newFile == null) ? data.imageUrl
                        : Photo.createUrl("ig-ban", newFile.getName());
                data.imageUrlResponsive = (newFileRes == null) ? data.imageUrlResponsive
                        : Photo.createUrl("ig-ban-res", newFileRes.getName());


                List<Category> subcategories = new ArrayList<>();
                if(data.subcategory_list != null){
                    for(String category : data.subcategory_list){
                        subcategories.add(Category.findById(Long.parseLong(category), getBrandId()));
                    }
                }
                data.categories = subcategories;

                List<Product> products = new ArrayList<>();
                if(data.product_list != null) {
                    for (String product : data.product_list) {
                        products.add(Product.find.byId(Long.parseLong(product)));
                    }
                }
                data.products = products;

                data.activeFrom = simpleDateFormat.parse(dForm.get("fromDate") +" "+ dForm.get("fromTime"));
                data.activeTo = simpleDateFormat.parse(dForm.get("toDate") +" "+ dForm.get("toTime"));
                data.userCms = cms;
                data.save();
                data.slug = CommonFunction.slugGenerate(data.name+"-"+data.id);
                data.update();
                String roleKey = cms.role.key;

                if (newFile != null) {
                    Photo.saveRecord("ig-ban", newFile.getName(), "", "", "", picture.getFilename(), cms.id, roleKey,
                            "Banner", data.id);
                }

                if (newFileRes != null) {
                    Photo.saveRecord("ig-ban-res", newFileRes.getName(), "", "", "", pictureResponsive.getFilename(), cms.id,
                            roleKey, "Banner", data.id);
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
                return redirect(routes.InstagramBannerController.index());
            }else{
                return redirect(routes.InstagramBannerController.add());
            }

        }
    }

    @Security.Authenticated(Secured.class)
    public static Result update() {
        Form<InstagramBanner> form = Form.form(InstagramBanner.class).bindFromRequest();
        DynamicForm dForm = Form.form().bindFromRequest();
        MultipartFormData body = request().body().asMultipartFormData();
        MultipartFormData.FilePart picture = body.getFile("imageUrl");
        MultipartFormData.FilePart pictureResponsive = body.getFile("imageUrlResponsive");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return redirect(routes.InstagramBannerController.index());
        }else{
            InstagramBanner data = form.get();
            Transaction txn = Ebean.beginTransaction();
            try {
                String imageName = CommonFunction.slugGenerate(data.bannerImageName);
                File newFile = null;
                File newFileRes = null;
                newFile = Photo.uploadImageCrop2(picture, "ig-ban", imageName, data.imageUrlX, data.imageUrlY, data.imageUrlW, data.imageUrlH, Photo.instagramBannerSize, "jpg");
                newFileRes = Photo.uploadImageCrop2(pictureResponsive, "ig-ban-res", imageName,
                        data.imageUrlResponsiveX, data.imageUrlResponsiveY, data.imageUrlResponsiveW, data.imageUrlResponsiveH, Photo.instagramBannerResponsiveSize, "jpg");

                UserCms cms = getUserCms();
                String roleKey = cms.role.key;
                if (newFile != null){
                    data.imageUrl = Photo.createUrl("ig-ban", newFile.getName());
                    Photo.saveRecord("ig-ban", newFile.getName(), "", "", "", picture.getFilename(), cms.id, roleKey,
                            "Banner", data.id);
                }
                if (newFileRes != null) {
                    data.imageUrlResponsive = Photo.createUrl("ig-ban-res", newFileRes.getName());
                    Photo.saveRecord("ig-ban-res", newFileRes.getName(), "", "", "", pictureResponsive.getFilename(), cms.id,
                            roleKey, "Banner", data.id);
                }

                List<Category> subcategories = new ArrayList<>();
                if(data.subcategory_list != null){
                    for(String category : data.subcategory_list){
                        subcategories.add(Category.findById(Long.parseLong(category), getBrandId()));
                    }
                }
                data.categories = subcategories;

                List<Product> products = new ArrayList<>();
                if(data.product_list != null) {
                    for (String product : data.product_list) {
                        products.add(Product.find.byId(Long.parseLong(product)));
                    }
                }
                data.products = products;

                data.activeFrom = simpleDateFormat.parse(dForm.get("fromDate") +" "+ dForm.get("fromTime"));
                data.activeTo = simpleDateFormat.parse(dForm.get("toDate") +" "+ dForm.get("toTime"));

                data.update();
                txn.commit();
            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
                flash("error", "Please correct errors bellow.");
                return redirect(routes.InstagramBannerController.index());
            } finally {
                txn.end();
            }
            flash("success", TITLE + " instance edited");
            return redirect(routes.InstagramBannerController.index());
        }

    }

    @Security.Authenticated(Secured.class)
    public static Result updateStatus(String id, String newStatus) {

        Ebean.beginTransaction();
        int status = 0;
        try {
            String[] tmp = id.split(",");
            for (String aTmp : tmp) {
                InstagramBanner data = InstagramBanner.findById(Long.parseLong(aTmp), getBrandId());
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
                InstagramBanner data = InstagramBanner.findById(Long.parseLong(aTmp), getBrandId());
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
    @ApiOperation(value = "updateSequence.", notes = "updateSequence.", response = String.class, httpMethod = "POST")
    public static Result updateSequence() {
        ObjectMapper om = new ObjectMapper();
        JsonNode json = request().body().asJson();
        Map<String, List<Integer>> sequences = om.convertValue(json.findPath("ids"), Map.class);
        final Integer[] status = {0};
        Ebean.beginTransaction();
        try {
            sequences.forEach((k,v)->{
                int loop = 0;
                for (Integer dt : v) {
                    InstagramBanner data = InstagramBanner.findById(dt.longValue(), getBrandId());
                    data.updateSequence(loop);
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