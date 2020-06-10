package controllers.admin;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;
import com.avaje.ebean.Transaction;
import com.enwie.api.BaseResponse;
import com.enwie.util.CommonFunction;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.BaseController;
import models.*;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.admin.purchaseorder._form;
import views.html.admin.purchaseorder.detail;
import views.html.admin.purchaseorder.list;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hendriksaragih
 */
public class PurchaseOrderController extends BaseController {
    private static final String TITLE = "Purchase Order";
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    private static final String featureKey = "purchase";

    private static Html htmlList(){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return list.render(TITLE, "List", feature);
    }

    private static Html htmlAdd(Form<PurchaseOrder> data){
        return _form.render(TITLE, "Add", data, routes.PurchaseOrderController.save());
    }

    private static Html htmlDetail(PurchaseOrder data){
        return detail.render(TITLE, "Detail", data);
    }

    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(htmlList());
    }

    @Security.Authenticated(Secured.class)
    public static Result add() {
        Form<PurchaseOrder> formData = Form.form(PurchaseOrder.class).fill(new PurchaseOrder());
        return ok(htmlAdd(formData));
    }

    @Security.Authenticated(Secured.class)
    public static Result detail(Long id) {
        PurchaseOrder data = PurchaseOrder.find.byId(id);
        return ok(htmlDetail(data));
    }

    @Security.Authenticated(Secured.class)
    public static Result lists() {
        RoleFeature feature = getUserAccessByFeature(featureKey);
        Map<String, String[]> params = request().queryString();

        Integer iTotalRecords = PurchaseOrder.findRowCount();
        String name = params.get("search[value]")[0];

        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "code";
        String order = params.get("order[0][dir]")[0];

        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
            case 1 :  sortBy = "id"; break;
            case 2 :  sortBy = "code"; break;
        }

        Page<PurchaseOrder> datas = PurchaseOrder.page(page, pageSize, sortBy, order, name, getUserCms());
        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        ArrayNode an = result.putArray("data");
        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (PurchaseOrder dt : datas.getList()) {

            ObjectNode row = Json.newObject();
            String action = "";
            if (feature.isAdd()){
                action += "<a class=\"btn btn-default btn-sm action\" title=\"Detail\" href=\"" + routes.PurchaseOrderController.detail(dt.id) + "\"><i class=\"fa fa-search\"></i></a>&nbsp;";
            }
            if(feature.isEdit()){
                switch (dt.status) {
                    case PurchaseOrder.DRAFT:
                        action += "<a class=\"btn btn-primary btn-sm action\" title=\"Send\" href=\"javascript:sendData(" + dt.id + ");\"><i class=\"fa fa-send\"></i></a>&nbsp;<a class=\"btn btn-danger btn-sm action\" title=\"Delete\" href=\"javascript:deleteData(" + dt.id + ");\"><i class=\"fa fa-trash\"></i></a>&nbsp;";
                        break;
                    case PurchaseOrder.SENT:
                        action += "<a class=\"btn btn-danger btn-sm action\" title=\"Cancel\" href=\"javascript:cancelData(" + dt.id + ");\"><i class=\"fa fa-reply\"></i></a>&nbsp;";
                        break;
//                    case PurchaseOrder.RECEIVED:
//                        action += "<a class=\"btn btn-danger btn-sm action\" title=\"Delete\" href=\"javascript:deleteData(" + dt.id + ");\"><i class=\"fa fa-trash\"></i></a>&nbsp;";
//                        break;
                }
            }
            row.put("0", num);
            row.put("1", dt.code);
            row.put("2", CommonFunction.getDate2(dt.receivedAt));
            row.put("3", CommonFunction.getDate2(dt.createdAt));
            row.put("4", dt.getStatus());
            row.put("5", CommonFunction.currencyFormat(dt.total));
            row.put("6", action);
            an.add(row);
            num++;
        }

        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public static Result save() {
        Form<PurchaseOrder> form = Form.form(PurchaseOrder.class).bindFromRequest();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        if (form.hasErrors()){
            System.out.println(form.globalErrors());
            flash("error", "Please correct errors bellow.");
            return badRequest(htmlAdd(form));
        }else{
            PurchaseOrder data = form.get();
            Transaction txn = Ebean.beginTransaction();
            try {
                List<PurchaseOrderDetail> mapVendor = new ArrayList<>();

                data.receivedAt = (data.receivedDate != null && !data.receivedDate.equals(""))? simpleDateFormat.parse(data.receivedDate):null;

                for(int i = 0; i < data.qty.size(); i++){
                    int qty = Integer.parseInt(data.qty.get(i));
                    if(qty > 0){
                        Product product = Product.find.byId(Long.parseLong(data.ids.get(i)));
                        PurchaseOrderDetail poDetail= new PurchaseOrderDetail();
                        poDetail.product = product;
                        poDetail.qty = qty;
                        poDetail.price = product.price;
                        poDetail.subTotal = poDetail.price * qty;
                        mapVendor.add(poDetail);

                    }
                }

                if(mapVendor.size() > 0){
                    UserCms userCms = getUserCms();

                    List<PurchaseOrderDetail> value = mapVendor;
                    PurchaseOrder newPO = new PurchaseOrder();
                    newPO.code = newPO.generatePOCode();
                    newPO.distributor = userCms;
                    newPO.information = data.information;
                    newPO.status = PurchaseOrder.SENT;
                    newPO.receivedAt = data.receivedAt;
                    newPO.total = 0.0;
                    for(PurchaseOrderDetail newPODetail : value){
                        newPO.total += newPODetail.subTotal;
                    }

                    newPO.save();
                    for(PurchaseOrderDetail newPODetail : value){
                        newPODetail.po = newPO;
                        newPODetail.save();
                    }
                }

                txn.commit();
            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
                flash("error", "Please correct errors bellow.");
                return badRequest(htmlAdd(form));
            } finally {
                txn.end();
            }

            flash("success", TITLE + " instance created");

            return redirect(routes.PurchaseOrderController.index());

        }
    }

    @Security.Authenticated(Secured.class)
    public static Result delete(String id) {
        Ebean.beginTransaction();
        int status = 0;
        try {
            for (String aTmp : id.split(",")) {
                PurchaseOrder data = PurchaseOrder.find.ref(Long.parseLong(aTmp));
                data.isDeleted = true;
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
        String message = status == 1 ? "Data success deleted" : "Data failed deleted";

        response.setBaseResponse(status, offset, 1, message, null);
        return ok(Json.toJson(response));
    }

    @Security.Authenticated(Secured.class)
    public static Result listsProductBySKUName() {
        Map<String, String[]> params = request().queryString();


        Integer iTotalRecords = PurchaseOrder.findRowCount();
        String filter = params.get("filter")[0];


        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "name";
        String order = params.get("order[0][dir]")[0];

        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
            case 1 :  sortBy = "id"; break;
            case 2 :  sortBy = "name"; break;
        }
        Page<Product> datas = null;
        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", 0);
        result.put("recordsFiltered", 0);
        ArrayNode an = result.putArray("data");
        if(!filter.equals("")) {
            datas = Product.find.where()
                    .or(Expr.ilike("sku", "%" + filter + "%"), Expr.ilike("name", "%" + filter + "%"))
                    .eq("is_deleted", false)
                    .orderBy(sortBy + " " + order)
                    .findPagingList(pageSize)
                    .setFetchAhead(false)
                    .getPage(page);

            Integer iTotalDisplayRecords = datas.getTotalRowCount();

            result.put("draw", Integer.valueOf(params.get("draw")[0]));
            result.put("recordsTotal", iTotalRecords);
            result.put("recordsFiltered", iTotalDisplayRecords);

            int num = Integer.valueOf(params.get("start")[0]) + 1;
            for (Product dt : datas.getList()) {

                ObjectNode row = Json.newObject();
                String action = "" +
                        "<button type=\"button\" class=\"btn btn-danger btn-sm action btn-delete\"><i class=\"fa fa-trash\"></i></button>&nbsp;";

                row.put("0", num);
                row.put("1", "<input type=\"hidden\" name=\"ids[]\" value=\""+dt.id.toString()+"\"><input type=\"text\" name=\"qty[]\" maxlength=\"4\" size=\"4\" value=\"0\">");
                row.put("2", CommonFunction.numberFormat(dt.price));
                row.put("3", dt.name);
                row.put("4", action);
                an.add(row);
                num++;
            }

        }


        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public static Result updateStatus(String id, int newStatus) {

        Ebean.beginTransaction();
        int status = 0;
        try {
            String[] tmp = id.split(",");
            for (int i=0; i < tmp.length; i++)
            {

                PurchaseOrder data = PurchaseOrder.find.ref(Long.parseLong(tmp[i]));
                data.updateStatus(newStatus);

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
        String message = "";
        if(status == 1)
            message = "Data success updated";
        else message = "Data failed updated";

        response.setBaseResponse(status, offset, 1, message, null);
        return ok(Json.toJson(response));
    }

    @Security.Authenticated(Secured.class)
    public static Result listsProductDetail(Long id) {
        PurchaseOrder po = PurchaseOrder.find.byId(id);

        Map<String, String[]> params = request().queryString();
        Integer iTotalRecords = PurchaseOrderDetail.find.where()
                .eq("po",po).findRowCount();


        Integer pageSize = Integer.valueOf(params.get("length")[0]);
        Integer page = Integer.valueOf(params.get("start")[0]) / pageSize;

        String sortBy = "name";
        String order = params.get("order[0][dir]")[0];

        switch (Integer.valueOf(params.get("order[0][column]")[0])) {
            case 1 :  sortBy = "id"; break;
            case 2 :  sortBy = "name"; break;
        }
        Page<PurchaseOrderDetail> datas = null;
        ObjectNode result = Json.newObject();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", 0);
        result.put("recordsFiltered", 0);
        ArrayNode an = result.putArray("data");
        datas = PurchaseOrderDetail.find.where()
                .eq("po",po)
                .findPagingList(pageSize)
                .setFetchAhead(false)
                .getPage(page);

        Integer iTotalDisplayRecords = datas.getTotalRowCount();

        result.put("draw", Integer.valueOf(params.get("draw")[0]));
        result.put("recordsTotal", iTotalRecords);
        result.put("recordsFiltered", iTotalDisplayRecords);

        int num = Integer.valueOf(params.get("start")[0]) + 1;
        for (PurchaseOrderDetail dt : datas.getList()) {

            ObjectNode row = Json.newObject();
            String action = "" +
                    "<button type=\"button\" class=\"btn btn-danger btn-sm action btn-delete\"><i class=\"fa fa-trash\"></i></button>&nbsp;";

            row.put("0", num);
            row.put("1", dt.product.name);
            row.put("2", dt.qty);
            row.put("3", CommonFunction.currencyFormat(dt.price, "Rp"));
            row.put("4", CommonFunction.currencyFormat(dt.subTotal, "Rp"));
            an.add(row);
            num++;
        }


        return ok(result);
    }
}
