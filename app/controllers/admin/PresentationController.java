package controllers.admin;

import com.avaje.ebean.*;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.enwie.api.BaseResponse;
import com.enwie.util.CommonFunction;
import com.enwie.util.Constant;
import com.enwie.util.Helper;
import controllers.BaseController;
import models.*;
import models.Currency;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import play.Logger;
import play.api.mvc.Call;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.admin.presentation._form;
import views.html.admin.presentation.detail;
import views.html.admin.presentation.list;

import javax.persistence.PersistenceException;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class PresentationController  extends BaseController {
    private final static Logger.ALogger logger = Logger.of(PresentationController.class);
    private static final String TITLE = "New Presentation";
    private static final String TITLE_EDIT = "Edit Presentation";
    private static final String featureKey = "presentation";
    private static final int defaultPosition = 10000;


    private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");

    private static Html htmlAdd(Form<Presentation> data){
        return _form.render(TITLE, "ADD", routes.PresentationController.save(), data);
    }

    private static Html htmlEdit(Form<Presentation> data){
        return _form.render(TITLE_EDIT, "EDIT", routes.PresentationController.update(), data);
    }

    @Security.Authenticated(Secured.class)
    public static Result add() {
        Presentation presentation = new Presentation();
        Form<Presentation> formData = Form.form(Presentation.class).fill(presentation);

        return ok(htmlAdd(formData));
    }

    @Security.Authenticated(Secured.class)
    public static Result edit(Long id) {

        Presentation dt = Presentation.findById(id);
        
        Form<Presentation> formData = Form.form(Presentation.class).fill(dt);
        return ok(htmlEdit(formData));
    }

    @Security.Authenticated(Secured.class)
    public static Result detail(Long id) {
        Presentation dt = Presentation.findById(id);
        return ok(detail.render(TITLE, "Detail", dt));
    }

    private static Html htmlList(){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return list.render("Presentation List", "List", feature);
    }

    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(htmlList());
    }

    @Security.Authenticated(Secured.class)
    public static Result lists() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        RoleFeature feature = getUserAccessByFeature(featureKey);
        Map<String, String[]> params = request().queryString();

        Integer iTotalRecords = Presentation.findRowCount();
        String title = params.get("search[value]")[0];
        Integer filter = Integer.valueOf(params.get("filter")[0]);


        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "title";
        String order = params.get("order[0][dir]")[0];

        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
            case 1 :  sortBy = "id"; break;
            case 2 :  sortBy = "title"; break;
        }

        ExpressionList<Presentation> qry = Presentation.find
                .where()
                .ilike("title", "%" + title + "%")
                .eq("is_deleted", false);

        switch (filter){
            case 1: qry.eq("status", true);
                break;
            case 2: qry.eq("status", false);
                break;
        }

        Page<Presentation> datas = qry
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .setFetchAhead(false)
                .getPage(page);
        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (Presentation dt : datas.getList()) {
            ObjectNode row = Json.newObject();
            String action = "";
            String status = "";
            if(feature.isView()){
                action += "<a class=\"btn btn-default btn-sm action\" title=\"View\" href=\""+ routes.PresentationController.detail(dt.id)+"\"><i class=\"fa fa-search\"></i></a>&nbsp;";
            }
            if(feature.isEdit()){
                action += "<a class=\"btn btn-primary btn-sm action\" title=\"Edit\" href=\""+ routes.PresentationController.edit(dt.id)+"\"><i class=\"fa fa-pencil\"></i></a>&nbsp;" ;
            }
            if(feature.isEdit()) {
                status += "<input value=\"" + dt.id + "\" class=\"cb-status\" data-toggle=\"toggle\" data-on=\"Active\" data-off=\"Inactive\" data-size=\"small\" data-style=\"ios\" data-offstyle=\"danger\" type=\"checkbox\" " + ((dt.status) ? "checked" : "") + ">";
            }

            row.put("0", dt.id.toString());
            row.put("1", num);
            row.put("2", dt.title);
            row.put("3", dt.author);
            row.put("4", dt.shortDesc);
            row.put("5", dt.description);
            row.put("6", simpleDateFormat.format(dt.createdAt));
            row.put("7", status);
            row.put("8", action);
            an.add(row);
            num++;
        }

        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public static Result save() {
        Form<Presentation> form = Form.form(Presentation.class).bindFromRequest();
        Http.MultipartFormData body = request().body().asMultipartFormData();
		
        if (form.hasErrors()){
            System.out.println(form.globalErrors());
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlAdd(form));
        }else{
            Presentation data = form.get();
            System.out.print(data.title);

            Transaction txn = Ebean.beginTransaction();
            try {
                Presentation uniqueCheck = Presentation.find.where()
                        .eq("title", data.title).eq("isDeleted", false)
                        .setMaxRows(1)
                        .findUnique();
                if (uniqueCheck != null){
                    flash("error", "Presentation with similar title already exist");
                    return badRequest(htmlEdit(form));
                }

                List<FilePart> images = body.getFiles();
                List<File> imageFiles = Photo.uploadImages(images, "presentation", data.title, Photo.presentationImageSize, "jpg");
                data.imageUrls = imageFiles.stream().map(img -> img.getName()).collect(Collectors.joining(","));

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
                return redirect(routes.PresentationController.index());
            }else{
                return redirect(routes.PresentationController.add());
            }

        }
    }

    @Security.Authenticated(Secured.class)
    public static Result update() {
        Form<Presentation> form = Form.form(Presentation.class).bindFromRequest();
        Http.MultipartFormData body = request().body().asMultipartFormData();
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlEdit(form));
        }else{
            Presentation data = form.get();
            Transaction txn = Ebean.beginTransaction();
            try {
                Presentation uniqueCheck = Presentation.find.where()
                        .eq("title", data.title).eq("isDeleted", false)
                        .not(Expr.eq("id", data.id))
                        .setMaxRows(1)
                        .findUnique();
                if (uniqueCheck != null){
                    flash("error", "Presentation with similar title already exist");
                    return badRequest(htmlEdit(form));
                }

                Presentation currentData = Presentation.findById(data.id);
                data.status = currentData.status;

                List<FilePart> images = body.getFiles();
                if (images.size() > 0  ) {
                    
                    Photo.deleteImages(currentData.imageUrls, "presentation");
                    List<File> imageFiles = Photo.uploadImages(images, "presentation", CommonFunction.slugGenerate(data.title), Photo.presentationImageSize, "jpg");
                    data.imageUrls = imageFiles.stream().map(img -> img.getName()).collect(Collectors.joining(","));
                } else {
                    data.imageUrls = currentData.imageUrls;
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
            return redirect(routes.PresentationController.index());
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result updateStatus(String id, String newStatus) {

        Ebean.beginTransaction();
        int status = 0;
        try {
            String[] tmp = id.split(",");
            for (int i=0; i < tmp.length; i++)
            {
                Boolean sts;
                Presentation data = Presentation.findById(Long.parseLong(tmp[i]));
                if(newStatus.equals("active")) {
                    data.status = Presentation.ACTIVE;
                }else if(newStatus.equals("inactive")) {
                    data.status = Presentation.INACTIVE;
                }
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
            message = "Data success updated";
        else message = "Data failed updated";

        response.setBaseResponse(status, offset, 1, message, null);
        return ok(Json.toJson(response));
    }

}