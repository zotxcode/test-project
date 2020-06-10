package controllers.admin;

import com.avaje.ebean.Ebean;
import com.enwie.api.BaseResponse;
import controllers.BaseController;
import models.ConfigSettings;
import models.RoleFeature;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.admin.cartbutton.detail;

/**
 * @author hendriksaragih
 */
public class CartButtonController extends BaseController {
    private final static Logger.ALogger logger = Logger.of(CartButtonController.class);
    private static final String TITLE = "Cart Button";
    private static final String featureKey = "cartbutton";
    private static final String module = "preference";

    private static Html htmlDetail(Form<ConfigSettings> data){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return detail.render(TITLE, "Detail", data, feature);
    }

    @Security.Authenticated(Secured.class)
    public static Result index() {
        ConfigSettings dt = ConfigSettings.findByData(getBrandId(), module, featureKey);
        dt.status = dt.value.equalsIgnoreCase("t") ? "active" : "inactive";
        Form<ConfigSettings> formData = Form.form(ConfigSettings.class).fill(dt);
        return ok(htmlDetail(formData));
    }


    @Security.Authenticated(Secured.class)
    public static Result update(String newStatus) {
        Ebean.beginTransaction();
        int status = 0;
        try {
            ConfigSettings dt = ConfigSettings.findByData(getBrandId(), module, featureKey);
            if(newStatus.equals("active"))
                dt.value = "t";
            else if(newStatus.equals("inactive"))
                dt.value = "f";
            status = 1;
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