package controllers.admin;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.BaseController;
import models.ContactUs;
import models.RoleFeature;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.admin.contactUs.*;

import java.util.Map;

/**
 * @author hendriksaragih
 */
public class ContactUsController extends BaseController {
    private static final String TITLE = "Contact Us";
    private static final String featureKey = "contact_us";

    private static Html htmlList(){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return list.render(TITLE, "List", feature);
    }

    private static Html htmlDetail(ContactUs data){
        return detail.render(TITLE, "Detail", data);
    }


    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(htmlList());
    }

    @Security.Authenticated(Secured.class)
    public static Result detail(Long id) {
        ContactUs dt = ContactUs.findById(id, getBrandId());
        return ok(htmlDetail(dt));
    }

    @Security.Authenticated(Secured.class)
    public static Result lists() {
        RoleFeature feature = getUserAccessByFeature(featureKey);
        Map<String, String[]> params = request().queryString();

        Integer iTotalRecords = ContactUs.RowCount(getBrand());
        String name = params.get("search[value]")[0];
        Integer filter = Integer.valueOf(params.get("filter")[0]);


        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "name";
        String order = params.get("order[0][dir]")[0];

        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
            case 0 :  sortBy = "id"; break;
            case 1 :  sortBy = "id"; break;
            case 2 :  sortBy = "name"; break;
            case 3 :  sortBy = "email"; break;
            case 4 :  sortBy = "content"; break;
            case 5 :  sortBy = "created_at"; break;
        }

        Page<ContactUs> datas = ContactUs.page(page, pageSize, sortBy, order, name, filter, getBrand());
        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (ContactUs dt : datas.getList()) {
            ObjectNode row = Json.newObject();
            String action = "";
            if(feature.isView()){
                action += "<a class=\"btn btn-default btn-sm action\" href=\""+ routes.ContactUsController.detail(dt.id)+"\"><i class=\"fa fa-search\"></i></a>&nbsp;";
            }

            row.put("0", dt.id.toString());
            row.put("1", num);
            row.put("2", dt.name);
            row.put("3", dt.email);
            row.put("4", dt.content);
            row.put("5", dt.getCreatedDate());
            row.put("6", action);
            an.add(row);
            num++;
        }

        return ok(result);
    }

}