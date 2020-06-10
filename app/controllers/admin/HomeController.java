package controllers.admin;

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import views.html.admin.index;

/**
 * @author hendriksaragih
 */
public class HomeController extends Controller {
    @Security.Authenticated(Secured.class)
    public static Result index() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

        String successMsg = "";
        String errorMsg = "Well done!. You successfully read this important alert message.";
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -30);
        Date lastMonth = cal.getTime();

        String strToday = simpleDateFormat.format(today);
        String strLastMonth = simpleDateFormat.format(lastMonth);
        int countMerchants = 0;
        int countOrders = 0;
        int countProducts = 0;
        int countVisits = 0;
        int countRevenue = 0;
        int countMembers = 0;

        return ok(index.render(successMsg, errorMsg, countMerchants, countOrders, countProducts, countRevenue, countMembers, countVisits, strToday, strLastMonth));
    }

}
