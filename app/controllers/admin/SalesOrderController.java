package controllers.admin;

import com.avaje.ebean.*;
import com.enwie.api.BaseResponse;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.BaseController;
import models.*;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;


import views.html.admin.salesorder.list;
import views.html.admin.salesorder.detail;

import java.text.SimpleDateFormat;
import java.util.*;

public class SalesOrderController extends BaseController {
    private static final String TITLE = "Order";
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

	
    private static Html htmlList(){
        return list.render(TITLE, "List", getReseller());
    }

    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(htmlList());
    }

    private static Map<Integer, String> getReseller(){
        Map<Integer, String> result = new LinkedHashMap<>();
        Member.find.where()
                .eq("is_deleted", false)
                .eq("is_reseller", true)
                .findList().forEach(dt->result.put(dt.id.intValue(), dt.fullName));
        return result;
    }
	
	@Security.Authenticated(Secured.class)
    public static Result lists() {
        Map<String, String[]> params = request().queryString();


        Integer iTotalRecords = SalesOrder.findRowCount();
        String name = params.get("search[value]")[0];


        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "orderDate";
        String order = params.get("order[0][dir]")[0];

        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
            case 1 :  sortBy = "id"; break;
            case 2 :  sortBy = "orderDate"; break;
        }

        Page<SalesOrder> datas = SalesOrder.page(page, pageSize, sortBy, order, name, filter, getBrandId());
        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (SalesOrder dt : datas.getList()) {

            ObjectNode row = Json.newObject();
            String action = "<a class=\"btn btn-default btn-sm action\" title=\"Detail\" href=\"" + routes.SalesOrderController.detail(dt.id) + "\"><i class=\"fa fa-search\"></i></a>&nbsp;";
/*//            boolean isHaveOwnProduct = false;
            String status = "";
            for(SalesOrderSeller item : dt.salesOrderSellers){
                if(item.vendor != null) {
//                    isHaveOwnProduct = true;
                    status = item.status;
                }
            }
//            if(isHaveOwnProduct){
//                action += "<a class=\"btn btn-primary btn-sm action\" title=\"Update Status\" href=\"#\" onclick=\"updateStatus("+dt.id+", '"+status+"')\"><i class=\"fa fa-pencil\"></i></a>&nbsp;";
//            }*/

            if (SalesOrder.ORDER_STATUS_RESELLER_UPDATE.contains(dt.statusReseller)){
                action += "<a class=\"btn btn-primary btn-sm action\" title=\"Update Reseller\" href=\"#\" onclick=\"updateReseller("+dt.id+")\"><i class=\"fa fa-pencil\"></i></a>&nbsp;";
            }

            row.put("0", num);
            row.put("1", dt.orderNumber);
            row.put("2", dt.getTanggal());
            row.put("3", dt.member.fullName);
            row.put("4", dt.reseller.fullName);
            row.put("5", dt.getPaymentStatus());
            row.put("6", dt.getTotalFormat());
            row.put("7", dt.convertStatusName());
            row.put("8", dt.statusReseller);
            row.put("9", action);
            an.add(row);
            num++;
        }

        return ok(result);
    }
	@Security.Authenticated(Secured.class)
    public static Result detail(Long id) {
        SalesOrder data = SalesOrder.find.byId(id);
        return ok(htmlDetail(data));
    }
	
	private static Html htmlDetail(SalesOrder data){
        return detail.render(TITLE, "Detail", data);
    }

    @Security.Authenticated(Secured.class)
    public static Result updateReseller() {
        BaseResponse response = new BaseResponse();
        DynamicForm form = Form.form().bindFromRequest();
        String id = form.get("id");
        String reseller = form.get("reseller");
        if(id != null){
            Transaction txn = Ebean.beginTransaction();
            try {
                SalesOrder so = SalesOrder.find.where()
                        .eq("t0.id", Long.valueOf(id))
                        .eq("t0.is_deleted", false)
                        .setMaxRows(1).findUnique();
                if (so != null){
                    so.reseller = Member.find.byId(Long.valueOf(reseller));
                    so.statusReseller = SalesOrder.ORDER_STATUS_RESELLER_WAITING;
                    so.update();
                }

                txn.commit();
                flash("success", TITLE + " success updated");
            }catch (Exception ex){
                txn.rollback();
                flash("error", TITLE + " failed updated");
            }finally {
                txn.end();
            }
        }
        response.setBaseResponse(1, offset, 1, updated, null);
        return ok(Json.toJson(response));
    }
}
