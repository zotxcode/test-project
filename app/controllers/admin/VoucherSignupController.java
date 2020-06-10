package controllers.admin;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import controllers.BaseController;
import models.VoucherSignUp;
import models.RoleFeature;
import models.UserCms;
import play.data.Form;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.admin.vouchersignup._form;
import views.html.admin.vouchersignup.list;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author hendriksaragih
 */
public class VoucherSignupController extends BaseController {
    private static final String TITLE = "Setting Voucher Signup Value";
    private static final String featureKey = "vouchersignupvaluesetting";

    
    private static Html htmlList(VoucherSignUp data){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return list.render(TITLE, "List", data, feature);
    }
    @Security.Authenticated(Secured.class)
    public static Result index() {
        VoucherSignUp dt = VoucherSignUp.find(getBrandId());
        return ok(htmlList(dt));
    }
	
	private static Html htmlAdd(Form<VoucherSignUp> data){
        return _form.render(TITLE, "Add", data, routes.VoucherSignupController.save());
    }
	@Security.Authenticated(Secured.class)
    public static Result add() {
        Form<VoucherSignUp> formData = Form.form(VoucherSignUp.class).fill(new VoucherSignUp());
        return ok(htmlAdd(formData));
    }
	
	
	private static Html htmlEdit(Form<VoucherSignUp> data){
        return _form.render(TITLE, "Edit", data, routes.VoucherSignupController.update());
    }
	@Security.Authenticated(Secured.class)
    public static Result edit() {
        VoucherSignUp dt = VoucherSignUp.find(getBrandId());
        Form<VoucherSignUp> formData = Form.form(VoucherSignUp.class).fill(dt);
        return ok(htmlEdit(formData));
    }
	
	@Security.Authenticated(Secured.class)
    public static Result update() {
		Form<VoucherSignUp> form = Form.form(VoucherSignUp.class).bindFromRequest();
		
		if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlEdit(form));
        }else{
            VoucherSignUp data = form.get();

            Transaction txn = Ebean.beginTransaction();
            try {
                UserCms cms = getUserCms();
                String roleKey = cms.role.key;

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
            return redirect(routes.VoucherSignupController.index());
        }
        
    }
	
	@Security.Authenticated(Secured.class)
    public static Result save() {
        Form<VoucherSignUp> form = Form.form(VoucherSignUp.class).bindFromRequest();
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlAdd(form));
        }else{
            VoucherSignUp data = form.get();
            Transaction txn = Ebean.beginTransaction();
            try {
                UserCms cms = getUserCms();
                data.userCms = cms;
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
                return redirect(routes.VoucherSignupController.index());
            }else{
                return redirect(routes.VoucherSignupController.add());
            }
        }
    }
	
	


}