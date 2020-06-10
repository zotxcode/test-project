package controllers;

import assets.JsonMask;
import com.enwie.util.Constant;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.enwie.api.BaseResponse;
import com.enwie.api.UserSession;
import models.Member;
import models.MemberLog;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Result;
import play.libs.Json;
import views.html.oauth.login;

import java.text.DateFormat;
import java.text.SimpleDateFormat;


/**
 * @author hilmanzaky
 */
public class OAuthController extends BaseController {

    public static final String DEV_TYPE_WEB = "WEB";

    @SuppressWarnings("rawtypes")
    private static BaseResponse response = new BaseResponse();

    public static Result login(String responseType, String clientId) {
        String oauthClientId = Constant.getInstance().getOauthClientId();
        try {
            if (clientId.equals(oauthClientId)) {
                String appName = Constant.getInstance().getOauthDomainName();
                return ok(login.render(appName, responseType, clientId));
            } else {
                return notFound("<h1>Page not found !</h1>").as("text/html");
            }
        } catch (Exception e) {
            e.printStackTrace();
            flash("error", e.getMessage());
            return redirect(routes.OAuthController.login(responseType, clientId));
        }
    }

    public static Result postLogin(){
        DynamicForm form = Form.form().bindFromRequest();

        String email = form.get("email");
        String password = form.get("password");
        String clientId = form.get("client_id");
        String responseType = form.get("response_type");
        String redirectURI = Constant.getInstance().getOauthRedirectURI();

        try {
            // The validation
            String oauthClientId = Constant.getInstance().getOauthClientId();
            if (clientId.equals(oauthClientId)) {
                Member member = Member.login(email, password, getBrandId());
                if (member != null) {
                    MemberLog log = MemberLog.loginMember(null, DEV_TYPE_WEB, null, member, getBrand());
                    if (log == null) {
                        flash("error", "User hasn't been activated.");
                        return redirect(routes.OAuthController.login(responseType, clientId));
                    }

                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    UserSession session = new UserSession(log.token, df.format(log.expiredDate), log.memberType);
                    ObjectMapper om = new ObjectMapper();
                    om.addMixInAnnotations(Member.class, JsonMask.class);
                    session.setProfile_data(Json.parse(om.writeValueAsString(member)));
                    response.setBaseResponse(1, 0, 1, success, session);

                    return redirect(redirectURI + "?token=" + log.token);
                } else {
                    flash("error", "Wrong username or password");
                    return redirect(routes.OAuthController.login(responseType, clientId));
                }
            } else {
                return notFound("<h1>Page not found</h1>").as("text/html");
            }

        } catch (Exception e) {
            e.printStackTrace();
            flash("error", e.getMessage());
            return redirect(routes.OAuthController.login(responseType, clientId));
        }
    }



//    public static Result logout(){
//        UserCmsLog.logoutUser(session("access_token"));
//        session().clear();
//        flash("success", "You've been logged out");
////        return redirect(routes.OAuthController.login());
//    }

}
