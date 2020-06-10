package controllers.admin;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.enwie.api.BaseResponse;
import controllers.BaseController;
import models.Newsletters;
import models.RoleFeature;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.admin.newsletter.list;
import views.html.admin.newsletter._form;

import java.util.Map;

/**
 * @author hendriksaragih
 */
public class NewsletterController extends BaseController {
    private static final String TITLE = "Newsletter";
    private static final String featureKey = "newsletter";

	  private static Html htmlList(){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return list.render(TITLE, "List", feature);
    }
	
    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(htmlList());
    }
	
	private static Html htmlAdd(Form<Newsletters> data){
        return _form.render(TITLE, "Add", data, routes.NewsletterController.save());
    }
	
	@Security.Authenticated(Secured.class)
    public static Result add() {
        Form<Newsletters> formData = Form.form(Newsletters.class).fill(new Newsletters());
        return ok(htmlAdd(formData));
    }
	
	private static Html htmlEdit(Form<Newsletters> data){
        return _form.render(TITLE, "Edit", data, routes.NewsletterController.update());
    }
	
	
	@Security.Authenticated(Secured.class)
    public static Result edit(Long id) {
        Newsletters dt = Newsletters.findById(id, getBrandId());
        Form<Newsletters> formData = Form.form(Newsletters.class).fill(dt);
        return ok(htmlEdit(formData));
    }
	
	@Security.Authenticated(Secured.class)
    public static Result update() {
		Form<Newsletters> form = Form.form(Newsletters.class).bindFromRequest();
		
		if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlEdit(form));
        }else{
            Newsletters data = form.get();

            Transaction txn = Ebean.beginTransaction();
            try {

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
            return redirect(routes.NewsletterController.index());
        }
        
    }
	
	
	@Security.Authenticated(Secured.class)
    public static Result lists() {
        RoleFeature feature = getUserAccessByFeature(featureKey);
        Map<String, String[]> params = request().queryString();

        Integer iTotalRecords = Newsletters.findRowCount(getBrandId());
        String name = params.get("search[value]")[0];
        Integer filter = Integer.valueOf(params.get("filter")[0]);


        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "title";
        String order = params.get("order[0][dir]")[0];

        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
            case 1 :  sortBy = "id"; break;
            case 2 :  sortBy = "title"; break;
        }

        Page<Newsletters> datas = Newsletters.page(page, pageSize, sortBy, order, name, filter, getBrandId());
        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (Newsletters dt : datas.getList()) {

            ObjectNode row = Json.newObject();
            String action = "";
            String status = "";
			
            if(feature.isEdit()){
                action += "<a class=\"btn btn-primary btn-sm action\" href=\""+ routes.NewsletterController.edit(dt.id)+"\"><i class=\"fa fa-pencil\"></i></a>&nbsp;";
                status += "<input value=\""+dt.id+"\" class=\"cb-status\" data-toggle=\"toggle\" data-on=\"Active\" data-off=\"Inactive\" data-size=\"small\" data-style=\"ios\" data-offstyle=\"danger\" type=\"checkbox\" "+((dt.status)?"checked":"")+">";
            }else status = dt.getStatus();
            if(feature.isDelete()){
                action += "<a class=\"btn btn-danger btn-sm action\" href=\"javascript:deleteData("+dt.id+");\"><i class=\"fa fa-trash\"></i></a>&nbsp;";
            }


            row.put("0", dt.id.toString());
            row.put("1", num);
            row.put("2", dt.title);
            row.put("3", dt.contents);
            row.put("4", status);
            row.put("5", action);
            an.add(row);
            num++;
        }

        return ok(result);
    }
	
	
	@Security.Authenticated(Secured.class)
    public static Result save() {
        Form<Newsletters> form = Form.form(Newsletters.class).bindFromRequest();
        if (form.hasErrors()){
            System.out.println(form.globalErrors());
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlAdd(form));
        }else{
            Http.MultipartFormData body = request().body().asMultipartFormData();
            Newsletters data = form.get();
            Transaction txn = Ebean.beginTransaction();
            try {
               
                Newsletters uniqueCheck = Newsletters.find.where()
                        .eq("title", data.title).eq("isDeleted", false)
                        .eq("brand", getBrand())
                        .setMaxRows(1)
                        .findUnique();
                if (uniqueCheck != null){
                    flash("error", "Newslatter with similar name already exist");
                    return badRequest(htmlAdd(form));
                }

               
                data.brand = getBrand();
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
                return redirect(routes.NewsletterController.index());
            }else{
                return redirect(routes.NewsletterController.add());
            }
        }
    }
	
	@Security.Authenticated(Secured.class)
    public static Result delete(String id) {
        Ebean.beginTransaction();
        int status = 0;
        try {
            for (String aTmp : id.split(",")) {
                Newsletters data = Newsletters.findById(Long.parseLong(aTmp), getBrandId());
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

                Newsletters data = Newsletters.findById(Long.parseLong(tmp[i]), getBrandId());
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