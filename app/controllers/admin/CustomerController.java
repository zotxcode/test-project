package controllers.admin;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.enwie.api.BaseResponse;
import com.enwie.util.Constant;
import com.wordnik.swagger.annotations.ApiOperation;
import controllers.BaseController;
import models.Member;
import models.RoleFeature;
import models.UserCms;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.admin.customer.detail;
import views.html.admin.customer.list;
import views.html.admin.customer.referrer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author hendriksaragih
 */
public class CustomerController extends BaseController {
    private final static Logger.ALogger logger = Logger.of(CustomerController.class);
    private static final String TITLE = "Customer";
    private static final String featureKey = "customer";

    private static Html htmlList(){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return list.render(TITLE, "List", feature);
    }

    private static Html htmlDetail(Member data){
        return detail.render(TITLE, "Detail", data);
    }

    private static Html htmlReferrer(List<Member> data){
        return referrer.render(TITLE, "Referrer", data);
    }

    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(htmlList());
    }

    @Security.Authenticated(Secured.class)
    public static Result detail(Long id) {
        Member dt = Member.findById(id, getBrandId());
        return ok(htmlDetail(dt));
    }

    @Security.Authenticated(Secured.class)
    public static Result memberReferrer(Long id) {
        List<Member> dt = Member.findReferrerList(id, getBrandId());
        return ok(htmlReferrer(dt));
    }

    @Security.Authenticated(Secured.class)
    public static Result upload() {
        play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData();
        play.mvc.Http.MultipartFormData.FilePart file = body.getFile("file");
        int status = 0;
        if (file != null) {
            BufferedReader br = null;
            String line;
            String cvsSplitBy = ",";
            boolean isHeader = true;
            try {
                br = new BufferedReader(new FileReader(file.getFile()));
                Ebean.beginTransaction();
                while ((line = br.readLine()) != null) {
                    if (isHeader){
                        isHeader = false;
                        continue;
                    }
                    String[] email = line.split(cvsSplitBy);
                    Member member = Member.find.where().eq("email", email[0]).eq("brand_id", getBrandId()).findUnique();
                    if (member != null){
                        member.isActive = false;
                        member.update();
                    }
                }
                Ebean.commitTransaction();
                status = 1;
            } catch (Exception e) {
                e.printStackTrace();
                flash("error", e.getMessage());
                status = 0;
            }finally {
                Ebean.endTransaction();
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            flash("error", "Missing file");
            status = 0;
        }

        BaseResponse response = new BaseResponse();
        String message = "";

        if(status == 1)
            message = "Upload success";
        else message = "Upload failed";

        response.setBaseResponse(status, offset, 1, message, null);
        return ok(Json.toJson(response));
    }

    @Security.Authenticated(Secured.class)
    public static Result download() {
        response().setContentType("text/csv");
        response().setHeader("Content-Disposition", "attachment;filename=template_blacklist.csv");

        File file = new File(Constant.getInstance().getMediaPath() + "templates" + File.separator + "template_blacklist.csv");
        return ok(file);
    }

    @Security.Authenticated(Secured.class)
    public static Result lists() {
        RoleFeature feature = getUserAccessByFeature(featureKey);
        UserCms actor = getUserCms();
        Map<String, String[]> params = request().queryString();

        Integer iTotalRecords = Member.RowCount(getBrandId());
        String name = params.get("search[value]")[0];
        Integer filter = Integer.valueOf(params.get("filter")[0]);


        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "full_name";
        String order = params.get("order[0][dir]")[0];

        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
            case 1 :  sortBy = "id"; break;
            case 2 :  sortBy = "full_name"; break;
            case 3 :  sortBy = "email"; break;
            case 4 :  sortBy = "phone"; break;
            case 5 :  sortBy = "created_at"; break;
            case 6 :  sortBy = "last_login"; break;
            case 7 :  sortBy = "last_purchase"; break;
        }

        Page<Member> datas = Member.page(page, pageSize, sortBy, order, name, filter, getBrandId(), actor.city == null ? null : actor.city.id);
        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (Member dt : datas.getList()) {
            ObjectNode row = Json.newObject();
            String action = "";
            String status = "";
            String reseller = "";
            if(feature.isView()){
                action += "<a class=\"btn btn-default btn-sm action\" href=\""+controllers.admin.routes.CustomerController.detail(dt.id)+"\"><i class=\"fa fa-search\"></i></a>&nbsp;";
            }

            if ( !Optional.ofNullable(dt.isReseller).orElse(false) ) {
                action +="<input data-id=\""+dt.id+"\" type=\"button\" class=\"btn btn-success upgrade-to-reseller\" value=\"Upgrade to Reseller\" >";
            } else {
                reseller += "<b>Since:</b>&nbsp;"+dt.getBecomeResellerAt()+"<br>";
                reseller += "<b>Total&nbsp;Referrer:</b>&nbsp;<a href=\""+controllers.admin.routes.CustomerController.memberReferrer(dt.id)+"\">"+dt.totalRefferal+"</a>";
            }
            if(feature.isEdit()){
                status +="<input value=\""+dt.id+"\" class=\"cb-status\" data-toggle=\"toggle\" data-on=\"Active\" data-off=\"Inactive\" data-size=\"small\" data-style=\"ios\" data-offstyle=\"danger\" type=\"checkbox\" "+((dt.isActive)?"checked":"")+">";
            }else status += dt.getIsActive();

            row.put("0", dt.id.toString());
            row.put("1", num);
            row.put("2", dt.fullName);
            row.put("3", dt.email);
            row.put("4", dt.phone);
            row.put("5", dt.getRegisterDate());
            row.put("6", dt.getLastLogin());
            row.put("7", dt.getLastPurchase());
            row.put("8", reseller);
            row.put("9", status);
            row.put("10", action);
            an.add(row);
            num++;
        }

        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    @ApiOperation(value = "updateStatus", notes = "updateStatus.", response = String.class, httpMethod = "POST")
    public static Result updateStatus(String newStatus) {
        ObjectMapper om = new ObjectMapper();
        JsonNode json = request().body().asJson();
        Integer[] sequences = om.convertValue(json.findPath("ids"), Integer[].class);
        int status = 0;
        Ebean.beginTransaction();
        try {
            for (Integer dt : sequences) {
                Member data = Member.findById(dt.longValue(), getBrandId());
                if(newStatus.equals("active"))
                    data.isActive = Member.ACTIVE;
                else if(newStatus.equals("inactive"))
                    data.isActive = Member.INACTIVE;
                data.update();
                status = 1;
            }

            Ebean.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            Ebean.rollbackTransaction();
            status = 0;
        } finally {
            Ebean.endTransaction();
        }

        BaseResponse response = new BaseResponse();
        String message = status == 1 ? "Data success updated" : "Data failed updated";

        response.setBaseResponse(status, offset, 1, message, null);
        return ok(Json.toJson(response));
    }

    @Security.Authenticated(Secured.class)
    @ApiOperation(value = "upgradeToReseller", notes = "upgradeToReseller.", response = String.class, httpMethod = "POST")
    public static Result upgradeToReseller() {
        ObjectMapper om = new ObjectMapper();
        JsonNode json = request().body().asJson();
        Integer[] sequences = om.convertValue(json.findPath("ids"), Integer[].class);
        int status = 0;
        Ebean.beginTransaction();
        try {
            for (Integer dt : sequences) {
                Member data = Member.findById(dt.longValue(), getBrandId());
                data.isReseller = true;
                data.becomeResellerAt = new Date();
                data.update();
                status = 1;
            }

            Ebean.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            Ebean.rollbackTransaction();
            status = 0;
        } finally {
            Ebean.endTransaction();
        }

        BaseResponse response = new BaseResponse();
        String message = status == 1 ? "Member successfully upgraded" : "Member failed upgrade";

        response.setBaseResponse(status, offset, 1, message, null);
        return ok(Json.toJson(response));
    }

}