package controllers.admin;

import com.avaje.ebean.*;
import com.avaje.ebean.Page;
import com.avaje.ebean.Transaction;
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
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.admin.category._form;
import views.html.admin.category.list;

import javax.persistence.PersistenceException;
import java.io.File;
import java.util.*;

/**
 * @author hendriksaragih
 */
public class CategoryController extends BaseController {
    private final static Logger.ALogger logger = Logger.of(CategoryController.class);
    private static final String TITLE = "Category";
    private static final String featureKey = "category";

    private static Html htmlList(){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return list.render(TITLE, "List", feature);
    }


    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(htmlList());
    }

    private static Html htmlEdit(Form<Category> data, HashMap<Integer, String> listsBaseAttribute){
        return _form.render(TITLE, "Edit", data, routes.CategoryController.update(), listsBaseAttribute);
    }

    private static Html htmlAdd(Form<Category> data){
        return _form.render(TITLE, "Add", data, routes.CategoryController.save(), new HashMap<>());
    }

    @Security.Authenticated(Secured.class)
    public static Result saveCategoryJson() {
        DynamicForm form = Form.form().bindFromRequest();
        String name = form.get("name");
        Category category = new Category();
        category.code = name.replace(" ","_");
        category.slug = category.code;
        category.name = name;
        String check = Category.validation(category);
        BaseResponse response = new BaseResponse();

        if (check != null) {
            response.setBaseResponse(0, 0, 0, check, null);
            return badRequest(Json.toJson(response));
        }

        category.createdAt = new Date();
        category.save();
        response.setBaseResponse(1, offset, 1, created, category);
        return ok(Json.toJson(response));

    }

    @Security.Authenticated(Secured.class)
    public static Result add(Long id) {
        Category dt = new Category();
        Category parent = Category.findById(id, getBrandId());
        dt.parent = parent == null ? 0L : parent.id;
        if (parent == null){
            dt.parent =  0L;
            dt.parentName = "";
            dt.level = 1;
        }else{
            dt.parent = parent.id;
            dt.parentName = parent.name;
            dt.level = parent.level + 1;
        }
        Form<Category> formData = Form.form(Category.class).fill(dt);
        return ok(htmlAdd(formData));
    }

    @Security.Authenticated(Secured.class)
    public static Result edit(Long id) {
        Category dt = Category.findById(id, getBrandId());
        dt.imageLink = dt.getImageLink();
        if (dt.getParentCategoryId() == 0){
            dt.parent =  0L;
            dt.parentName = "";
        }else{
            dt.parent = dt.getParentCategoryId();
            dt.parentName = dt.parentCategory.name;
        }

        HashMap<Integer, String> listsBaseAttribute = new HashMap<>();
        for(BaseAttribute  base : dt.listBaseAttribute){
            listsBaseAttribute.put(base.id.intValue(), base.name);
        }

        Form<Category> formData = Form.form(Category.class).fill(dt);
        return ok(htmlEdit(formData, listsBaseAttribute));
    }

    @Security.Authenticated(Secured.class)
    public static Result lists() {
        RoleFeature feature = getUserAccessByFeature(featureKey);
        ObjectNode result = Json.newObject();
        ArrayNode an = result.putArray("data");
        List<Category> datas = Category.find.where()
                .eq("level", Category.START_LEVEL)
                .eq("isDeleted", false)
                .eq("brand", getBrand())
                .orderBy("sequence asc").findList();
        for (Category dt : datas) {
            ObjectNode row = Json.newObject();
            row.put("id", dt.id);
            row.put("name", dt.name);
            row.put("status", generateStatusField(dt, feature));
            row.put("action", generateActionField(dt, feature));
            List<Category> listChildCategory = Category.find.where()
                    .eq("parentCategory", dt)
                    .eq("isDeleted", false)
                    .eq("brand", getBrand())
                    .orderBy("sequence asc").findList();
            if(listChildCategory.size() > 0){
                ArrayNode an2 = row.putArray("children");
                for (Category dt2 : listChildCategory) {
                    ObjectNode row2 = Json.newObject();
                    row2.put("id", dt2.id);
                    row2.put("name", dt2.name);
                    row2.put("status", generateStatusField(dt2, feature));
                    row2.put("action", generateActionField(dt2, feature));
                    List<Category> listGrantChildCategory = Category.find.where()
                            .eq("parentCategory", dt2)
                            .eq("isDeleted", false)
                            .eq("brand", getBrand())
                            .orderBy("sequence asc").findList();
                    if(listGrantChildCategory.size() > 0){
                        ArrayNode an3 = row2.putArray("children");
                        for (Category dt3 : listGrantChildCategory) {
                            ObjectNode row3 = Json.newObject();
                            row3.put("id", dt3.id);
                            row3.put("name", dt3.name);
                            row3.put("status", generateStatusField(dt3, feature));
                            row3.put("action", generateActionField(dt3, feature));
                            an3.add(row3);
                        }
                    }
                    an2.add(row2);
                }
            }
            an.add(row);
        }

        return ok(result);
    }

    private static String generateStatusField(Category dt, RoleFeature feature){
        String status = "";
        if(feature.isEdit()){
            status = "<input value=\""+dt.id+"\" class=\"cb-status\" data-toggle=\"toggle\" data-on=\"Active\" data-off=\"Inactive\" data-size=\"small\" data-style=\"ios\" data-offstyle=\"danger\" type=\"checkbox\" "+((dt.isActive)?"checked":"")+">";
        }else status = dt.getIsActive();

        return status;
    }

    private static String generateActionField(Category dt, RoleFeature feature){
        String action = "";
        if(feature.isAdd() && dt.level < 3){
            action += "<a class=\"btn btn-success btn-sm action\" href=\""+ routes.CategoryController.add(dt.id)+"\"><i class=\"fa fa-plus\"></i></a>&nbsp;";
        }
        if(feature.isEdit()){
            action += "<a class=\"btn btn-primary btn-sm action\" href=\""+ routes.CategoryController.edit(dt.id)+"\"><i class=\"fa fa-pencil\"></i></a>&nbsp;";
        }
        if(feature.isDelete()){
            action += "<a class=\"btn btn-danger btn-sm action\" href=\"javascript:deleteData("+dt.id+");\"><i class=\"fa fa-trash\"></i></a>&nbsp;";
        }

        return action;
    }

    @Security.Authenticated(Secured.class)
    public static Result save() {
        Form<Category> form = Form.form(Category.class).bindFromRequest();
        if (form.hasErrors()){
            System.out.println(form.globalErrors());
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlAdd(form));
        }else{
            Http.MultipartFormData body = request().body().asMultipartFormData();
            Category data = form.get();
            data.brand = getBrand();
            Transaction txn = Ebean.beginTransaction();
            try {
                if (data.parent == 0){
                    data.parentCategory = null;
                    data.rootCategoryCode = data.code;
                    data.sequence = (data.isActive) ? Category.getNextSequence(null, getBrandId()) : 0;
                    data.level = 1;
                    data.slug = CommonFunction.slugGenerate(data.name);
                    data.code = data.slug;
                }else{
                    Category dataParent = Category.find.byId(data.parent);
                    if(dataParent.level == 1){
                        data.parentCategory = dataParent;
                        data.rootCategoryCode = dataParent.rootCategoryCode;
                        data.sequence = (data.isActive) ? Category.getNextSequence(dataParent.id, getBrandId()) : 0;
                        data.level = dataParent.level + 1;
                        data.slug = CommonFunction.slugGenerate(dataParent.rootCategoryCode+"-"+data.name);
                        data.code = data.slug;
                    }else{
                        Category dataGrandParent = Category.find.byId(dataParent.getParentCategoryId());
                        data.parentCategory = dataParent;
                        data.rootCategoryCode = dataGrandParent.rootCategoryCode+"-"+dataParent.rootCategoryCode;
                        data.sequence = (data.isActive) ? Category.getNextSequence(dataParent.id, getBrandId()) : 0;
                        data.level = dataParent.level + 1;
                        data.slug = CommonFunction.slugGenerate(dataGrandParent.rootCategoryCode+"-"+dataParent.rootCategoryCode+"-"+data.name);
                        data.code = data.slug;
                    }
                }

                Category uniqueCheck = Category.find.where()
                        .or(Expr.eq("slug", data.slug), Expr.eq("name", data.name))
                        .eq("isDeleted", false)
                        .eq("brand", getBrand())
                        .setMaxRows(1)
                        .findUnique();
                if (uniqueCheck != null){
                    flash("error", "Category with similar name / code already exist");
                    return badRequest(htmlAdd(form));
                }

                Http.MultipartFormData.FilePart picture = body.getFile("imageUrl");
                File newFile = Photo.uploadImageCrop2(picture, "cat", data.slug, data.imageUrlX, data.imageUrlY, data.imageUrlW, data.imageUrlH, Photo.categoryImageSize, "jpg");

                Http.MultipartFormData.FilePart pictureResponsive = body.getFile("imageUrlResponsive");
                File newFileResponsive = null;
                if(data.level == 1){
                    newFileResponsive = Photo.uploadImageCrop(pictureResponsive, "cat-res", data.slug, Photo.categoryImageResponsiveL1Size, "jpg");
                    newFileResponsive = Photo.uploadImageCrop2(pictureResponsive, "cat-res", data.slug, data.imageUrlResponsiveX, data.imageUrlResponsiveY, data.imageUrlResponsiveW, data.imageUrlResponsiveH, Photo.categoryImageResponsiveL1Size, "jpg");
                }else{
                    newFileResponsive = Photo.uploadImageCrop(pictureResponsive, "cat-res", data.slug, Photo.categoryImageResponsiveL3Size, "jpg");
                    newFileResponsive = Photo.uploadImageCrop2(pictureResponsive, "cat-res", data.slug, data.imageUrlResponsiveX, data.imageUrlResponsiveY, data.imageUrlResponsiveW, data.imageUrlResponsiveH, Photo.categoryImageResponsiveL3Size, "jpg");
                }

                UserCms cms = getUserCms();
                data.userCms = cms;
                data.imageUrl = newFile != null ? Photo.createUrl("cat", newFile.getName()) : "";
                data.imageUrlResponsive = newFileResponsive != null ? Photo.createUrl("cat-res", newFileResponsive.getName()) : "";
                if (data.base_attribute_list != null){
                    List<BaseAttribute> listsBaseAttribute = new ArrayList<>();
                    for(String id : data.base_attribute_list){
                        BaseAttribute base = BaseAttribute.findById(Long.valueOf(id), getBrandId());
                        listsBaseAttribute.add(base);
                    }
                    data.listBaseAttribute = listsBaseAttribute;
                }
                data.save();

                String roleKey = cms.role.key;
                if (newFile != null){
                    Photo.saveRecord("cat", newFile.getName(), "", "", "", picture.getFilename(), cms.id, roleKey,
                            "Category", data.id);
                }
                if (newFileResponsive != null){
                    Photo.saveRecord("cat-res", newFileResponsive.getName(), "", "", "", pictureResponsive.getFilename(), cms.id, roleKey,
                            "Category", data.id);
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
                return redirect(routes.CategoryController.index());
            }else{
                return redirect(routes.CategoryController.add(0L));
            }
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result update() {
        Form<Category> form = Form.form(Category.class).bindFromRequest();
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlEdit(form, new HashMap<>()));
        }else{
            Category data = form.get();
            Transaction txn = Ebean.beginTransaction();
            Http.MultipartFormData body = request().body().asMultipartFormData();

            Category uniqueCheck = Category.find.where()
                    .or(Expr.eq("slug", data.slug), Expr.eq("name", data.name))
                    .eq("isDeleted", false)
                    .eq("brand", getBrand())
                    .setMaxRows(1)
                    .findUnique();

            if (uniqueCheck != null){
                flash("error", "Category with similar name / code already exist");
                return badRequest(htmlEdit(form, new HashMap<>()));
            }

            try {
                Http.MultipartFormData.FilePart picture = body.getFile("imageUrl");
                File newFile = Photo.uploadImageCrop2(picture, "cat", data.slug, data.imageUrlX, data.imageUrlY, data.imageUrlW, data.imageUrlH, Photo.categoryImageSize, "jpg");

                Http.MultipartFormData.FilePart pictureResponsive = body.getFile("imageUrlResponsive");
                File newFileResponsive = null;
                if(data.level == 1){
                    newFileResponsive = Photo.uploadImageCrop(pictureResponsive, "cat-res", data.slug, Photo.categoryImageResponsiveL1Size, "jpg");
                    newFileResponsive = Photo.uploadImageCrop2(pictureResponsive, "cat-res", data.slug, data.imageUrlResponsiveX, data.imageUrlResponsiveY, data.imageUrlResponsiveW, data.imageUrlResponsiveH, Photo.categoryImageResponsiveL1Size, "jpg");
                }else{
                    newFileResponsive = Photo.uploadImageCrop(pictureResponsive, "cat-res", data.slug, Photo.categoryImageResponsiveL3Size, "jpg");
                    newFileResponsive = Photo.uploadImageCrop2(pictureResponsive, "cat-res", data.slug, data.imageUrlResponsiveX, data.imageUrlResponsiveY, data.imageUrlResponsiveW, data.imageUrlResponsiveH, Photo.categoryImageResponsiveL3Size, "jpg");
                }


                UserCms cms = getUserCms();
                String roleKey = cms.role.key;
                if (newFile != null){
                    data.imageUrl = Photo.createUrl("cat", newFile.getName());
                    Photo.saveRecord("cat", newFile.getName(), "", "", "", picture.getFilename(), cms.id, roleKey,
                            "Category", data.id);
                }

                if (newFileResponsive != null){
                    data.imageUrlResponsive = Photo.createUrl("cat-res", newFileResponsive.getName());
                    Photo.saveRecord("cat-res", newFileResponsive.getName(), "", "", "", pictureResponsive.getFilename(), cms.id, roleKey,
                            "Category", data.id);
                }

                List<BaseAttribute> listsBaseAttribute = new ArrayList<>();
                if(data.base_attribute_list != null && data.base_attribute_list.size() > 0) {
                    for (String id : data.base_attribute_list) {
                        BaseAttribute base = BaseAttribute.findById(Long.valueOf(id), getBrandId());
                        listsBaseAttribute.add(base);
                    }
                }
                data.listBaseAttribute = listsBaseAttribute;

                data.update();

                txn.commit();
            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
                flash("error", "Please correct errors bellow.");
                Category category = Category.find.byId(data.id);
                HashMap<Integer, String> listsBaseAttribute = new HashMap<>();
                for(BaseAttribute  base : category.listBaseAttribute){
                    listsBaseAttribute.put(base.id.intValue(), base.name);
                }
                return badRequest(htmlEdit(form, listsBaseAttribute));
            } finally {
                txn.end();
            }
            flash("success", TITLE + " instance edited");
            return redirect(routes.CategoryController.index());
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result delete(String id) {
        Ebean.beginTransaction();
        int status = 0;
        try {
            for (String aTmp : id.split(",")) {
                Category data = Category.findById(Long.parseLong(aTmp), getBrandId());
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
            for (String aTmp : tmp) {

                Category data = Category.findById(Long.parseLong(aTmp), getBrandId());
                recursiveUpdateStatus(String.valueOf(data.id), newStatus);
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

    private static void recursiveUpdateStatus(String id, String newStatus){
        Category data = Category.findById(Long.parseLong(id), getBrandId());
        if(data.subCategory.size() > 0){
            data.updateStatus(newStatus);
            for(Category child : data.subCategory){
                recursiveUpdateStatus(String.valueOf(child.id), newStatus);
            }
        }else {
            data.updateStatus(newStatus);
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result listsSubCategory() {

        Map<String, String[]> params = request().queryString();

        String parentCategory = params.get("q")[0];
        String[] tmp = parentCategory.split(",");
        List<Category> categories = new ArrayList<>();
        for(int i=0; i< tmp.length; i++){
            categories.add(Category.find.byId(Long.parseLong(tmp[i])));
        }

        Integer pageSize = 100;
        Integer page = 0;
        if(params.get("page") != null){
            page = Integer.valueOf(params.get("page")[0]);
        }

        String sortBy = "name";
        String order = "asc";

        ObjectNode result = Json.newObject();

        Page<Category> datas = Category.find.where()
                .ilike("name", "%" + filter + "%")
                .eq("is_deleted", false)
                .eq("brand_id", getBrandId())
                .in("parentCategory", categories)
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .setFetchAhead(false)
                .getPage(page);
        Integer iTotalDisplayRecords;
        try {
            iTotalDisplayRecords = datas.getTotalRowCount();
        }catch (PersistenceException e){
            iTotalDisplayRecords = 0;
        }

        result.put("total_count", iTotalDisplayRecords);
        result.put("incomplete_results", false);

        ArrayNode an = result.putArray("items");
        for (Category dt : datas.getList()) {
            ObjectNode row = Json.newObject();
            row.put("id", dt.id);
            row.put("text", dt.name);
            an.add(row);
        }

        return ok(result);
    }
}