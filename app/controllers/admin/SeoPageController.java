package controllers.admin;

import controllers.BaseController;
import models.ConfigSettings;
import models.RoleFeature;
import play.Logger;
import play.data.Form;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.admin.seo._form;
import views.html.admin.seo.detail;

/**
 * @author hendriksaragih
 */
public class SeoPageController extends BaseController {
    private final static Logger.ALogger logger = Logger.of(SeoPageController.class);
    private static final String TITLE = "SEO";
    private static final String featureKey = "seo";
    private static final String module = "preference";

    private static Html htmlDetail(String data){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return detail.render(TITLE, "Detail", data, feature);
    }

    private static Html htmlEdit(Form<ConfigSettings> data){
        return _form.render(TITLE, "Edit", data, routes.SeoPageController.update());
    }

    @Security.Authenticated(Secured.class)
    public static Result index() {
        ConfigSettings dt = ConfigSettings.findByData(getBrandId(), module, featureKey);
        return ok(htmlDetail(dt.value));
    }

    @Security.Authenticated(Secured.class)
    public static Result edit() {
        ConfigSettings dt = ConfigSettings.findByData(getBrandId(), module, featureKey);
        Form<ConfigSettings> formData = Form.form(ConfigSettings.class).fill(dt);
        return ok(htmlEdit(formData));
    }

    @Security.Authenticated(Secured.class)
    public static Result update() {
        Form<ConfigSettings> form = Form.form(ConfigSettings.class).bindFromRequest();
        if (form.hasErrors()){
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlEdit(form));
        }else{
            ConfigSettings data = form.get();
            data.update();
            flash("success", "SEO successfully edited");
            return redirect(routes.SeoPageController.index());
        }
    }

}