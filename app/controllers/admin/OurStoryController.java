package controllers.admin;

import controllers.BaseController;
import models.ConfigSettings;
import models.RoleFeature;
import play.Logger;
import play.data.Form;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.admin.ourStory._form;
import views.html.admin.ourStory.detail;

/**
 * @author hendriksaragih
 */
public class OurStoryController extends BaseController {
    private final static Logger.ALogger logger = Logger.of(OurStoryController.class);
    private static final String TITLE = "Our Story";
    private static final String featureKey = "our_story";
    private static final String module = "information";

    private static Html htmlDetail(String data){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return detail.render(TITLE, "Detail", data, feature);
    }

    private static Html htmlEdit(Form<ConfigSettings> data){
        return _form.render(TITLE, "Edit", data, routes.OurStoryController.update());
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
            flash("success", "Location instance edited");
            return redirect(routes.OurStoryController.index());
        }
    }

}