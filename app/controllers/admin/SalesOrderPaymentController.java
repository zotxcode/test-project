package controllers.admin;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Joiner;
import controllers.BaseController;
import models.*;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.admin.salesorderpayment.list;
import views.html.admin.salesorderpayment.detail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SalesOrderPaymentController extends BaseController {
    private static final String TITLE = "Payment Confirmation";
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

    private static Html htmlList(){
        return list.render(TITLE, "List");
    }

    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(htmlList());
    }
	
	
	@Security.Authenticated(Secured.class)
    public static Result lists() {
        Map<String, String[]> params = request().queryString();


        Integer iTotalRecords = SalesOrderPayment.findRowCount();
        String name = params.get("search[value]")[0];
        String filter = params.get("filter")[0];


        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "salesOrder.orderNumber";
        String order = params.get("order[0][dir]")[0];

        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
            case 1 :  sortBy = "id"; break;
            case 2 :  sortBy = "salesOrder.orderNumber"; break;
        }

        Page<SalesOrderPayment> datas = SalesOrderPayment.page(page, pageSize, sortBy, order, name, filter);
        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (SalesOrderPayment dt : datas.getList()) {

            ObjectNode row = Json.newObject();
		
            String action = "<a class=\"btn btn-default btn-sm action\" href=\""+ routes.SalesOrderPaymentController.detail(dt.id)+"\"><i class=\"fa fa-search\"></i></a>&nbsp;";
           /* if (!dt.status.equals(SalesOrderPayment.VERIFY) && !dt.status.equals(SalesOrderPayment.PAYMENT_REJECT)){
                action += "<a class=\"btn btn-primary btn-sm action\" title=\"Approve\" href=\"javascript:approveData('"+dt.id+"');\"><i class=\"fa fa-check\"></i></a>&nbsp;";
                action += "<a class=\"btn btn-danger btn-sm action\" title=\"Reject\" href=\"javascript:rejectData('"+dt.id+"');\"><i class=\"fa fa-minus\"></i></a>";
            }*/

            row.put("0", num);
            row.put("1", "<span id=\"name_"+dt.id+"\">"+(dt.invoiceNo != null? dt.invoiceNo:"")+"</span>");
            row.put("2", dt.salesOrder.orderNumber);
            row.put("3", "");//row.put("3", CommonFunction.getDate2(dt.confirmAt));
            row.put("4", dt.salesOrder.getTotalFormat());
            row.put("5", dt.getTotalFormat());
            row.put("6", "");//dt.salesOrder.member.fullName
            row.put("7", dt.debitAccountNumber);
            row.put("8", "");//dt.salesOrder.bank.bankName+ " ("+dt.salesOrder.bank.accountName+")"
            row.put("9", dt.getImageLink());
            row.put("10", dt.getStrStatus());
            row.put("11", action);
            an.add(row);
            num++;
        }

        return ok(result);
    }

	private static Html htmlDetail(SalesOrderPayment sod){
        return detail.render("Payment Confirmation", "Detail", sod);
    }
	@Security.Authenticated(Secured.class)
    public static Result detail(Long id) {
        SalesOrderPayment sod = SalesOrderPayment.find.byId(id);
        return ok(htmlDetail(sod));
    }
   
}
