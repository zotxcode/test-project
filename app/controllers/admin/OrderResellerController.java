package controllers.admin;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.enwie.api.BaseResponse;
import com.enwie.util.CommonFunction;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.BaseController;
import models.*;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.admin.orderreseller.detail;
import views.html.admin.orderreseller.list;

import java.util.Map;

/**
 * @author hendriksaragih
 */
public class OrderResellerController extends BaseController {
    private static final String TITLE = "Order Reseller";
    private static final String featureKey = "orderreseller";

    private static Html htmlList(){
        RoleFeature feature = getUserAccessByFeature(featureKey);
        return list.render(TITLE, "List", feature);
    }

    private static Html htmlDetail(PurchaseOrder data){
        return detail.render(TITLE, "Detail", data);
    }

    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(htmlList());
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

        Page<PurchaseOrder> datas = PurchaseOrder.pageReseller(page, pageSize, sortBy, order, name, getUserCms());
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
            if (feature.isView()){
                action += "<a class=\"btn btn-default btn-sm action\" title=\"Detail\" href=\"" + routes.OrderResellerController.detail(dt.id) + "\"><i class=\"fa fa-search\"></i></a>&nbsp;";
            }
            if(feature.isEdit()){
                switch (dt.status) {
                    case 1:
                        action += "<a class=\"btn btn-success btn-sm action\" title=\"Approve\" href=\"javascript:approveData(" + dt.id + ");\"><i class=\"fa fa-check\"></i></a>&nbsp;";
                        break;
                }
            }
            row.put("0", num);
            row.put("1", dt.code);
            row.put("2", dt.reseller.fullName);
            row.put("3", CommonFunction.getDate2(dt.receivedAt));
            row.put("4", CommonFunction.getDate2(dt.createdAt));
            row.put("5", dt.getStatus());
            row.put("6", CommonFunction.currencyFormat(dt.total));
            row.put("7", action);
            an.add(row);
            num++;
        }

        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public static Result updateStatus(String id) {

        Ebean.beginTransaction();
        int status = 0;
        Boolean outOfStock = false;
        try {
            String[] tmp = id.split(",");
            for (int i=0; i < tmp.length; i++){
                PurchaseOrder data = PurchaseOrder.find.ref(Long.parseLong(tmp[i]));
                data.updateStatus(PurchaseOrder.COMPLETED);
                data.userCms = getUserCms();
                status = 1;

                for (PurchaseOrderDetail pd : data.details){
                    ProductStock ps = ProductStock.getProductDistributor(data.distributor, pd.product);
                    if (ps != null){
                        if(pd.qty > ps.stock.intValue()){
                            outOfStock = true;
                            break;
                        }
                        ps.stock = ps.stock - pd.qty.longValue();
                        ps.update();
                        ProductMutationStock.saveStock(data.distributor, pd.product, pd.qty,
                                ProductMutationStock.CREDIT, "Order Reseller "+data.reseller.fullName+", PO "+data.code, data.receivedAt);

                        ProductStock ps2 = ProductStock.getProductReseller(data.reseller, pd.product);
                        if (ps2 == null){
                            ps2 = new ProductStock();
                            ps2.distributor = data.distributor;
                            ps2.reseller = data.reseller;
                            ps2.product = pd.product;
                            ps2.stock = pd.qty.longValue();
                            ps2.save();
                        }else {
                            ps2.stock = ps2.stock + pd.qty.longValue();
                            ps2.update();
                        }

                        ProductMutationStock.saveStock(data.reseller, data.distributor, pd.product, pd.qty,
                                ProductMutationStock.DEBIT, "Penerimaan PO "+data.code, data.receivedAt);
                    }
                }
            }
            if (!outOfStock) Ebean.commitTransaction();
            else Ebean.rollbackTransaction();
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

        if (!outOfStock){
            response.setBaseResponse(status, offset, 1, message, null);
        }else {
            response.setBaseResponse(status, offset, 1, "Out of stock", null);
        }
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
