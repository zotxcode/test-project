package controllers.api;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.enwie.api.BaseResponse;
import com.enwie.mapper.request.MapMidtransNotification;
import com.enwie.mapper.request.MapMidtransVA;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordnik.swagger.annotations.Api;
import controllers.BaseController;
import models.SalesOrder;
import models.SalesOrderPayment;
import play.libs.Json;
import play.mvc.Result;

import java.util.Date;

/**
 * Created by hendriksaragih on 3/24/17.
 */
@Api(value = "/notification", description = "Notification")
public class NotificationController extends BaseController {
    @SuppressWarnings("rawtypes")
    private static BaseResponse response = new BaseResponse();

    public static Result index() {
        return ok();
    }


    @SuppressWarnings("unchecked")
    public static Result handling() {
        JsonNode json = request().body().asJson();
        ObjectMapper mapper = new ObjectMapper();
        Transaction txn = Ebean.beginTransaction();
        try {
            MapMidtransNotification map = mapper.readValue(json.toString(), MapMidtransNotification.class);
            SalesOrder so = SalesOrder.find.where()
                    .eq("orderNumber", map.getOrderId())
                    .eq("t0.brand_id", getBrandId())
                    .setMaxRows(1).findUnique();
            if (so != null){
                if (so.salesOrderPayment == null){
                    SalesOrderPayment payment = new SalesOrderPayment();
                    payment.salesOrder = so;
                    payment.brand = getBrand();
                    payment.invoiceNo = SalesOrderPayment.generateInvoiceCode(getBrandId());
                    payment.confirmAt = new Date();
                    payment.debitAccountName = "";
                    payment.debitAccountNumber = "";
                    payment.totalTransfer = Double.valueOf(map.getGrossAmount());
                    payment.imageUrl = "";
                    payment.status = map.getTransactionStatus().equals(SalesOrder.PAYMENT_STATUS_SETTLEMENT) ? SalesOrderPayment.VERIFY : SalesOrderPayment.PAYMENT_VERIFY;
                    payment.comments = "";
                    payment.save();
                }

                so.paymentType = map.getPaymentType();
                switch (map.getTransactionStatus()){
                    case SalesOrder.PAYMENT_STATUS_PENDING :
                        so.status = SalesOrder.ORDER_STATUS_PENDING_PAYMENT;
                        break;
                    case SalesOrder.PAYMENT_STATUS_SETTLEMENT :
                        so.status = SalesOrder.ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION;
                        SalesOrderPayment payment = so.salesOrderPayment;
                        payment.status = SalesOrderPayment.VERIFY;
                        payment.save();
                        break;
                    case SalesOrder.PAYMENT_STATUS_EXPIRE :
                        so.status = SalesOrder.ORDER_STATUS_EXPIRE_PAYMENT;
                        break;
                    case SalesOrder.PAYMENT_STATUS_DENY :
                        so.status = SalesOrder.ORDER_STATUS_CANCEL;
                        break;
                }

                switch (map.getPaymentType()){
                    case SalesOrder.PAYMENT_TYPE_CSTORE :
                        so.paymentDescription = map.getStore();
                        break;
                    case SalesOrder.PAYMENT_TYPE_BANK_TRANSFER :
                        if (map.getPermataVaNumber() != null){
                            so.vaNumber = map.getPermataVaNumber();
                            so.paymentDescription = "Permata";
                        }
                        if (map.getVaNumbers() != null && !map.getVaNumbers().isEmpty()){
                            MapMidtransVA va = map.getVaNumbers().get(0);
                            so.vaNumber = va.getVaNumber();
                            so.paymentDescription = va.getBank();
                        }
                        break;
                }

                so.save();

                txn.commit();

                response.setBaseResponse(0, 0, 0, success, null);
                return ok(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return badRequest(Json.toJson(response));
        }catch (Exception e) {
            e.printStackTrace();
            txn.rollback();
        } finally {
            txn.end();
        }

        response.setBaseResponse(0, 0, 0, error, null);
        return badRequest(Json.toJson(response));
    }

}
