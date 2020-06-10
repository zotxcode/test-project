package controllers.admin;

import models.UserInfo;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

/**
 * @author hendriksaragih
 */
public class Secured extends Security.Authenticator {

    /**
     * Used by authentication annotation to determine if user is logged in.
     * @param ctx The context.
     * @return The email address of the logged in user, or null if not logged in.
     */
    @Override
    public String getUsername(Context ctx) {
//        if (Constant.getInstance().isProduction()){
//            return ctx.session().get("email");
//        }else{
//            return "admin@hokeba.com";
//        }

        return ctx.session().get("email");
    }

    /**
     * Instruct authenticator to automatically redirect to login page if unauthorized.
     * @param context The context.
     * @return The login page.
     */
    @Override
    public Result onUnauthorized(Context context) {
        return redirect(routes.AuthController.login());
    }

    /**
     * Return the email of the logged in user, or null if no logged in user.
     *
     * @param ctx the context containing the session
     * @return The email of the logged in user, or null if user is not logged in.
     */
    public static String getUser(Context ctx) {
        return ctx.session().get("email");
    }

    /**
     * True if there is a logged in user, false otherwise.
     * @param ctx The context.
     * @return True if user is logged in.
     */
    public static boolean isLoggedIn(Context ctx) {
        return (getUser(ctx) != null);
    }

    /**
     * Return the UserInfo of the logged in user, or null if no user is logged in.
     * @param ctx The context.
     * @return The UserInfo, or null.
     */
    public static UserInfo getUserInfo(Context ctx) {
        UserInfo user = new UserInfo(ctx.session().get("name"), ctx.session().get("email"), Long.valueOf(ctx.session().get("brand_id")));
        return user;
    }
}