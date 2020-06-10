package controllers.admin;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import controllers.BaseController;
import models.PaymentExpiration;
import models.RoleFeature;
import models.UserCms;
import play.data.Form;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.admin.paymentexpiration._form;
import views.html.admin.paymentexpiration.list;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author hendriksaragih
 */
public class PaymentExpirationController extends BaseController {
    private static final String TITLE = "Setting Expire Payment";
    private static final String featureKey = "expirepaymentsetting";

    private static Html htmlList(PaymentExpiration data){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return list.render(TITLE, "List", data, feature);
    }

    private static Html htmlEdit(Form<PaymentExpiration> data){
        return _form.render(TITLE, "Edit", data, routes.PaymentExpirationController.update(), getListType());
    }

    private static Html htmlAdd(Form<PaymentExpiration> data){
        return _form.render(TITLE, "Add", data, routes.PaymentExpirationController.save(), getListType());
    }

    @Security.Authenticated(Secured.class)
    public static Result index() {
        PaymentExpiration dt = PaymentExpiration.find(getBrandId());
        return ok(htmlList(dt));
    }

    @Security.Authenticated(Secured.class)
    public static Result add() {
        Form<PaymentExpiration> formData = Form.form(PaymentExpiration.class).fill(new PaymentExpiration());
        return ok(htmlAdd(formData));
    }

    @Security.Authenticated(Secured.class)
    public static Result edit() {
        PaymentExpiration dt = PaymentExpiration.find(getBrandId());
        Form<PaymentExpiration> formData = Form.form(PaymentExpiration.class).fill(dt);
        return ok(htmlEdit(formData));
    }

    private static Map<String, String> getListType(){
        Map<String, String> result = new LinkedHashMap<>();
        result.put(PaymentExpiration.HOUR_TYPE,"Hour");
        result.put(PaymentExpiration.DAY_TYPE,"Day");
        return result;
    }

    @Security.Authenticated(Secured.class)
    public static Result save() {
        Form<PaymentExpiration> form = Form.form(PaymentExpiration.class).bindFromRequest();
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlAdd(form));
        }else{
            PaymentExpiration data = form.get();
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
                return redirect(routes.PaymentExpirationController.index());
            }else{
                return redirect(routes.PaymentExpirationController.add());
            }
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result update() {
        Form<PaymentExpiration> form = Form.form(PaymentExpiration.class).bindFromRequest();
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlEdit(form));
        }else{
            PaymentExpiration data = form.get();

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
            return redirect(routes.PaymentExpirationController.index());
        }
    }

}