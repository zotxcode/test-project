package controllers.admin;

import com.avaje.ebean.*;
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
import views.html.admin.banner._form;
import views.html.admin.banner.list;
import views.html.admin.banner.sequence;

import javax.persistence.PersistenceException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author hendriksaragih
 */
public class BannerController extends BaseController {
    private final static Logger.ALogger logger = Logger.of(BannerController.class);
    private static final String TITLE = "Slider Banner";
    private static final String featureKey = "banner";

    private static Html htmlList(){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return list.render(TITLE, "List", feature);
    }

    private static Html htmlEdit(Form<Banner> data, Map<Integer, String> listsCategory, Map<Integer, String> listsSubCategory, Map<Integer, String> listsProduct){
        return _form.render(TITLE, "Edit", data, routes.BannerController.update(), listsCategory, listsSubCategory, listsProduct, getType());
    }

    private static Html htmlAdd(Form<Banner> data){
        return _form.render(TITLE, "Add", data, routes.BannerController.save(), new LinkedHashMap<>(), new LinkedHashMap<>(), new LinkedHashMap<>(), getType());
    }
	

    private static Map<Integer, String> getType(){
        Map<Integer, String> result = new LinkedHashMap<>();
        result.put(1, "Internal Url");
        result.put(2, "External Url");
        return result;
    }


    @Security.Authenticated(Secured.class)
    public static Result sequence() {
        return ok(sequence.render(TITLE, "Sequence", getListBannerByType()));
    }

    private static List<Banner> getListBannerByType() {
        Query<Banner> qry = Banner.find.where()
                .eq("is_deleted", false)
                .eq("brand", getBrand())
                .eq("status", true)
                .eq("position_id", 1)
                .orderBy("sequence ASC");

        return qry.findList();
    }

    @Security.Authenticated(Secured.class)
    public static Result index() {
        Banner.findOutOfRange(getBrandId());
        return ok(htmlList());
    }
	
	
     @Security.Authenticated(Secured.class)
    public static Result add() {
        Banner dt = new Banner();
        dt.fromDate = Helper.getDateFromTimeStamp(new Date());
        Date toDate = new Date();
        toDate.setDate(toDate.getDate()+1);
        dt.toDate = Helper.getDateFromTimeStamp(toDate);
        Form<Banner> formData = Form.form(Banner.class).fill(dt);
        return ok(htmlAdd(formData));
    }


    @Security.Authenticated(Secured.class)
    public static Result edit(Long id) {
        Banner dt = Banner.findById(id, getBrandId());
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
        Form<Banner> formData = Form.form(Banner.class).fill(dt);
        return ok(htmlEdit(formData, listsCategory, listsSubCategory, listsProduct));
    }

    @Security.Authenticated(Secured.class)
    public static Result lists() {
        RoleFeature feature = getUserAccessByFeature(featureKey);

        Map<String, String[]> params = request().queryString();

        Integer iTotalRecords = Banner.findRowCount(getBrand());
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

        Page<Banner> datas = Banner.page(page, pageSize, sortBy, order, name, filter, getBrand());
        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (Banner dt : datas.getList()) {
            ObjectNode row = Json.newObject();
            String action = "" ;
            if(feature.isEdit()){
                action += "<a class=\"btn btn-primary btn-sm action\" href=\""+controllers.admin.routes.BannerController.edit(dt.id)+"\"><i class=\"fa fa-pencil\"></i></a>&nbsp;" ;
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
        Form<Banner> form = Form.form(Banner.class).bindFromRequest();
        DynamicForm dForm = Form.form().bindFromRequest();
        MultipartFormData body = request().body().asMultipartFormData();
        MultipartFormData.FilePart picture = body.getFile("imageUrl");
        MultipartFormData.FilePart pictureResponsive = body.getFile("imageUrlResponsive");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlAdd(form));
        }else{
            Banner data = form.get();
            Transaction txn = Ebean.beginTransaction();
            try {
                data.positionId = 1; //all banner set position to main banner
                data.typeId = 1;
                data.brand = getBrand();

                String imageName = CommonFunction.slugGenerate(data.bannerImageName);
                File newFile = null;
                File newFileRes = null;
                newFile = Photo.uploadImageCrop2(picture, "ban", imageName, data.imageUrlX, data.imageUrlY, data.imageUrlW, data.imageUrlH, Photo.mainBannerSize, "jpg");
                newFileRes = Photo.uploadImageCrop2(pictureResponsive, "ban-res", imageName,
                        data.imageUrlResponsiveX, data.imageUrlResponsiveY, data.imageUrlResponsiveW, data.imageUrlResponsiveH, Photo.mainBannerResponsiveSize, "jpg");
                data.sequence = Banner.getNextSequence(data.positionId, getBrandId());

                UserCms cms = getUserCms();
                data.imageUrl = (newFile == null) ? data.imageUrl
                        : Photo.createUrl("ban", newFile.getName());
                data.imageUrlResponsive = (newFileRes == null) ? data.imageUrlResponsive
                        : Photo.createUrl("ban-res", newFileRes.getName());


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
                    Photo.saveRecord("ban", newFile.getName(), "", "", "", picture.getFilename(), cms.id, roleKey,
                            "Banner", data.id);
                }

                if (newFileRes != null) {
                    Photo.saveRecord("ban-res", newFileRes.getName(), "", "", "", pictureResponsive.getFilename(), cms.id,
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
                return redirect(routes.BannerController.index());
            }else{
                return redirect(routes.BannerController.add());
            }

        }
    }

    @Security.Authenticated(Secured.class)
    public static Result update() {
        Form<Banner> form = Form.form(Banner.class).bindFromRequest();
        DynamicForm dForm = Form.form().bindFromRequest();
        MultipartFormData body = request().body().asMultipartFormData();
        MultipartFormData.FilePart picture = body.getFile("imageUrl");
        MultipartFormData.FilePart pictureResponsive = body.getFile("imageUrlResponsive");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return redirect(routes.BannerController.index());
        }else{
            Banner data = form.get();
            data.positionId = 1; //all banner set position to main banner
            data.typeId = 1;
            Transaction txn = Ebean.beginTransaction();
            try {
                String imageName = CommonFunction.slugGenerate(data.bannerImageName);
                File newFile = null;
                File newFileRes = null;
                newFile = Photo.uploadImageCrop2(picture, "ban", imageName, data.imageUrlX, data.imageUrlY, data.imageUrlW, data.imageUrlH, Photo.mainBannerSize, "jpg");
                newFileRes = Photo.uploadImageCrop2(pictureResponsive, "ban-res", imageName,
                        data.imageUrlResponsiveX, data.imageUrlResponsiveY, data.imageUrlResponsiveW, data.imageUrlResponsiveH, Photo.mainBannerResponsiveSize, "jpg");

                UserCms cms = getUserCms();
                String roleKey = cms.role.key;
                if (newFile != null){
                    data.imageUrl = Photo.createUrl("ban", newFile.getName());
                    Photo.saveRecord("ban", newFile.getName(), "", "", "", picture.getFilename(), cms.id, roleKey,
                            "Banner", data.id);
                }
                if (newFileRes != null) {
                    data.imageUrlResponsive = Photo.createUrl("ban-res", newFileRes.getName());
                    Photo.saveRecord("ban-res", newFileRes.getName(), "", "", "", pictureResponsive.getFilename(), cms.id,
                            roleKey, "Banner", data.id);
                }

                if(data.urlType == 1) {
                    data.externalUrl = "";

                    List<Category> subcategories = new ArrayList<>();
                    if (data.subcategory_list != null) {
                        for (String category : data.subcategory_list) {
                            subcategories.add(Category.findById(Long.parseLong(category), getBrandId()));
                        }
                    }
                    data.categories = subcategories;

                    List<Product> products = new ArrayList<>();
                    if (data.product_list != null) {
                        for (String product : data.product_list) {
                            products.add(Product.find.byId(Long.parseLong(product)));
                        }
                    }
                    data.products = products;
                } else {
                    data.categories.removeAll(data.subcategory_list);
                    data.products.removeAll(data.product_list);
                }

                data.activeFrom = simpleDateFormat.parse(dForm.get("fromDate") +" "+ dForm.get("fromTime"));
                data.activeTo = simpleDateFormat.parse(dForm.get("toDate") +" "+ dForm.get("toTime"));

                data.update();
                txn.commit();
            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
                flash("error", "Please correct errors bellow.");
                return redirect(routes.BannerController.index());
            } finally {
                txn.end();
            }
            flash("success", TITLE + " instance edited");
            return redirect(routes.BannerController.index());
        }

    }

    @Security.Authenticated(Secured.class)
    public static Result updateStatus(String id, String newStatus) {

        Ebean.beginTransaction();
        int status = 0;
        try {
            String[] tmp = id.split(",");
            for (String aTmp : tmp) {
                Banner data = Banner.findById(Long.parseLong(aTmp), getBrandId());
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
                Banner data = Banner.findById(Long.parseLong(aTmp), getBrandId());
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
    public static Result listsCategory() {

        Map<String, String[]> params = request().queryString();

        String filter = params.get("q")[0];

        Integer pageSize = 30;
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
                .eq("level", 2)
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .setFetchAhead(false)
                .getPage(page);
        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        result.put("total_count", iTotalDisplayRecords);
        result.put("incomplete_results", false);

        ArrayNode an = result.putArray("items");
        for (Category dt : datas.getList()) {
            ObjectNode row = Json.newObject();
            row.put("id", dt.id);
            row.put("name", dt.name);
            an.add(row);
        }

        return ok(result);
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
                    Banner data = Banner.findById(dt.longValue(), getBrandId());
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

    @Security.Authenticated(Secured.class)
    public static Result listsCategoryAjax() {
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
        String sql = "SELECT id, name FROM category WHERE parent_id IS NULL AND category.brand_id = "+getBrandId()+" AND category.name ILIKE '%"+filter+"%' ";
        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("name", "name")
                .columnMapping("id", "id")
                .create();

        Query<Category> query = Ebean.find(Category.class);
        query.setRawSql(rawSql);

        Page<Category> datas = query
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
            row.put("name", dt.name);
            an.add(row);
        }

        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public static Result listsSubCategory() {

        Map<String, String[]> params = request().queryString();

        String parentCategory = params.get("q")[0];
        String[] tmp = parentCategory.split(",");
        List<Category> categories = new ArrayList<>();
        for(int i=0; i< tmp.length; i++){
            categories.add(Category.findById(Long.parseLong(tmp[i]), getBrandId()));
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
                .eq("brand", getBrand())
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

    @Security.Authenticated(Secured.class)
    public static Result listsProduct() {
        Map<String, String[]> params = request().queryString();

        ObjectNode result = Json.newObject();
        {
            Integer iTotalRecords = Product.findRowCount(getBrand());
            String filter = params.get("search[value]")[0];

            Integer pageSize = Integer.valueOf(params.get("length")[0]);
            Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

            String sortBy = "name";
            String order = params.get("order[0][dir]")[0];



            List<Category> categories = new ArrayList<>();
            if((params.get("category")[0] != null && !params.get("category")[0].equals("null") && !params.get("category")[0].equals(""))){
                String category = params.get("category")[0];
                String[] tmp = category.split(",");
                for (String aTmp : tmp) {
                    categories.add(Category.findById(Long.parseLong(aTmp), getBrandId()));
                }
            }

            switch (Integer.valueOf(params.get("order[0][column]")[0])) {
                case 0:
                    sortBy = "id";
                    break;
                case 1:
                    sortBy = "name";
                    break;
                case 2:
                    sortBy = "name";
                    break;
            }
            Page<Product> datas = null;
            ExpressionList<Product> exp = Product.find.where()
                    .ilike("name", "%" + filter + "%")
                    .eq("t0.is_deleted", false)
                    .eq("status", true)
                    .eq("is_show", true)
                    .eq("brand", getBrand())
                    .isNotNull("category");
            if(categories.size() > 0){
                exp = exp.in("parentCategory", categories);
            }
            datas =  exp.orderBy(sortBy + " " + order)
                    .findPagingList(pageSize)
                    .setFetchAhead(false)
                    .getPage(page);

            Integer iTotalDisplayRecords;
            try {
                iTotalDisplayRecords = datas.getTotalRowCount();
            }catch (PersistenceException e){
                iTotalDisplayRecords = 0;
            }

            result.put("draw", Integer.valueOf(params.get("draw")[0]));
            result.put("recordsTotal", iTotalRecords);
            result.put("recordsFiltered", iTotalDisplayRecords);

            ArrayNode an = result.putArray("data");
            int num = Integer.valueOf(params.get("start")[0]) + 1;
            for (Product dt : datas.getList()) {
                ObjectNode row = Json.newObject();
                row.put("0", "<input type=\"checkbox\" class=\"cb-action\" name=\"" + dt.name + "\" id=\"cb-action-" + dt.id + "\" value=\"" + dt.id + "\">");
                row.put("1", num);
                row.put("2", dt.name);
                an.add(row);
                num++;
            }
        }
        return ok(result);
    }
}