package controllers.admin;

import com.enwie.util.Constant;
import com.enwie.util.Encryption;
import controllers.BaseController;
import models.Feature;
import models.RoleFeature;
import models.UserCms;
import models.UserCmsLog;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Result;
import views.html.admin.login;

import java.util.List;

/**
 * @author hendriksaragih
 */
public class AuthController extends BaseController {

    public static Result login() {
        return ok(login.render());
    }

    public static Result postLogin(){
        DynamicForm form = Form.form().bindFromRequest();
        String username = form.get("email");
        String password = form.get("password");
        UserCms userTarget = UserCms.find.where()
                .eq("isDeleted", false).eq("email", username.trim())
                .findUnique();
        if (userTarget != null) {
            if (!userTarget.isActive) {
                flash("error", "User hasn't been activated.");
                return redirect(routes.AuthController.login());
            }

            try {
                String passEnc = Encryption.EncryptAESCBCPCKS5Padding(password);
                if (passEnc.equals(userTarget.password)) {
                    UserCmsLog log = UserCmsLog.loginUser("WEB", "WEB", userTarget);
                    if (log == null) {
                        flash("error", inputParameter);
                        return redirect(routes.AuthController.login());
                    }
                    session().put("email", username);
                    session().put("name", userTarget.fullName);
                    session().put("role", userTarget.role.description);
                    if (userTarget.brand != null){
                        session().put("brand_id", String.valueOf(userTarget.brand.id));
                    }

                    session().put("access_token", log.token);

                    List<Feature> features = Feature.getFeaturesByRole(userTarget.role.id);
                    String listsGroup = "";
                    String listsMenu = "";
                    String tmpGroup = "";
                    for(Feature feature : features){
                        if(feature.name.equals("Dashboard")){
                            listsGroup += "#"+feature.section+"#";
                        }else if(!feature.section.equals(tmpGroup)){
                            listsGroup += "#"+feature.section+"#";
                            listsMenu += "#"+feature.key+"#";
                            tmpGroup = feature.section;
                        }else{
                            listsMenu += "#"+feature.key+"#";
                            tmpGroup = feature.section;
                        }
                    }

                    session().put("listsMenu", listsMenu);
                    session().put("listsGroup", listsGroup);

                    flash("success", "Login success");
                    RoleFeature feature = getUserAccessByFeature("newproduct");
                    if(feature != null  && feature.isAdd())
                        session().put("addproduct", "1");
                    else session().put("addproduct", "0");
                    return redirect(routes.HomeController.index());

                }
            } catch (Exception e) {
                e.printStackTrace();
                flash("error", e.getMessage());
                return redirect(routes.AuthController.login());
            }
        }
        flash("error", "Wrong username or password.");
        return redirect(routes.AuthController.login());
    }

    public static Result logout(){
        UserCmsLog.logoutUser(session("access_token"));
        session().clear();
        flash("success", "You've been logged out");
        return redirect(routes.AuthController.login());
    }

}
