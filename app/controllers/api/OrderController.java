package controllers.api;

import com.avaje.ebean.*;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.enwie.midtrans.MidtransService;
import com.enwie.midtrans.model.TranscationResponse;
import com.enwie.util.Constant;
import com.enwie.util.Encryption;
import com.enwie.util.MailConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.enwie.api.BaseResponse;
import com.enwie.mapper.request.MapAddress;
import com.enwie.mapper.request.MapOrder;
import com.enwie.mapper.request.MapVoucherCode;
import com.enwie.mapper.request.MapRequestCart;
import com.enwie.mapper.response.*;
import com.enwie.shipping.rajaongkir.RajaOngkirService;
import com.enwie.shipping.rajaongkir.response.model.Costs;
import com.enwie.shipping.rajaongkir.response.model.Service;
import com.wordnik.swagger.annotations.Api;
import controllers.BaseController;
import models.*;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author hendriksaragih
 */
@Api(value = "/v1/orders", description = "Orders")
public class OrderController extends BaseController {
    @SuppressWarnings("rawtypes")
    private static BaseResponse response = new BaseResponse();

    public static Result index() {
        return ok();
    }

    public static Result bankLists() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<Bank> query;
            query = Bank.find.where()
                    .eq("is_deleted", false)
                    .eq("status", true)
                    .eq("brand_id", getBrandId())
                    .findList();
            response.setBaseResponse(query.size(), offset, query.size(), success, new ObjectMapper().convertValue(query, MapBank[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result provinceLists() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<Province> query;
            query = Province.find.where()
                    .eq("is_deleted", false)
                    .findList();
            response.setBaseResponse(query.size(), offset, query.size(), success, new ObjectMapper().convertValue(query, MapProvince[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result courierLists() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<Courier> query;
            query = Courier.find.where()
                    .eq("is_deleted", false)
                    .eq("brand", getBrand())
                    .findList();
            response.setBaseResponse(query.size(), offset, query.size(), success, new ObjectMapper().convertValue(query, MapCourier[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result cartLists() {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            List<Cart> data = Cart.find.where()
                    .eq("member", actor)
                    .eq("is_deleted", false)
                    .eq("brand_id", getBrandId())
                    .orderBy("created_at DESC")
                    .findList();

            response.setBaseResponse(data.size(), offset, data.size(), success, new ObjectMapper().convertValue(data, MapCart[].class));
            return ok(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result saveCart() {
        Member currentMember = checkMemberAccessAuthorization();
        if (currentMember != null) {
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            try {
                MapRequestCart map = mapper.readValue(json.toString(), MapRequestCart.class);
                if (map.getId() != null){
                    Cart cart = Cart.find.where()
                            .eq("is_deleted", false)
                            .eq("member_id", currentMember.id)
                            .eq("id", map.getId())
                            .setMaxRows(1).findUnique();
                    if (cart == null){
                        response.setBaseResponse(0, 0, 0, "Invalid Cart Id", null);
                        return notFound(Json.toJson(response));
                    }
                    Product product = Product.find.where()
                            .eq("is_deleted", false)
                            .eq("id", map.getProductId())
                            .setMaxRows(1).findUnique();
                    if (product == null){
                        response.setBaseResponse(0, 0, 0, "Invalid Product", null);
                        return notFound(Json.toJson(response));
                    }

                    cart.product = product;
                    cart.qty = map.getQty();
                    cart.update();
                }else {
                    Product product = Product.find.where()
                            .eq("is_deleted", false)
                            .eq("id", map.getProductId())
                            .setMaxRows(1).findUnique();
                    if (product == null){
                        response.setBaseResponse(0, 0, 0, "Invalid Product", null);
                        return notFound(Json.toJson(response));
                    }else{
                        Cart cart = Cart.find.where()
                                .eq("is_deleted", false)
                                .eq("member_id", currentMember.id)
                                .eq("product_id", map.getProductId())
                                .setMaxRows(1).findUnique();
                        if (cart != null){
                            cart.product = product;
                            cart.qty = map.getQty();
                            cart.update();
                        }else {
                            Cart c = new Cart();
                            c.member = currentMember;
                            c.product = product;
                            c.brand = getBrand();
                            c.qty = map.getQty();
                            c.save();
                        }
                    }

                }

                response.setBaseResponse(1, offset, 1, success, null);
                return ok(Json.toJson(response));
            } catch (IOException e) {
                e.printStackTrace();
            }
            response.setBaseResponse(0, 0, 0, inputParameter, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));

    }

    public static Result deleteCart(Long id) {
        Member currentMember = checkMemberAccessAuthorization();
        if (currentMember != null) {
            Cart model = Cart.find.where()
                    .eq("member", currentMember)
                    .eq("is_deleted", false)
                    .eq("brand_id", getBrandId())
                    .eq("id", id)
                    .setMaxRows(1).findUnique();
            if (model != null) {
                model.delete();

                response.setBaseResponse(1, offset, 1, deleted, null);
                return ok(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return notFound(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result cityLists(Long id) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<City> query;
            query = City.find.where()
                    .eq("is_deleted", false)
                    .eq("province_id", id)
                    .findList();
            response.setBaseResponse(query.size(), offset, query.size(), success, new ObjectMapper().convertValue(query, MapCity[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result countCourier() {
        Member currentMember = checkMemberAccessAuthorization();
        if (currentMember != null) {
            JsonNode json = request().body().asJson();
            if (json.has("city_id") && json.has("courier_id")) {
                Long courier = json.get("courier_id").asLong();
                Long city = json.get("city_id").asLong();
                Courier cr = Courier.find.where()
                        .eq("is_deleted", false)
                        .eq("brand_id", getBrandId())
                        .eq("id", courier)
                        .setMaxRows(1).findUnique();
                if (cr == null){
                    response.setBaseResponse(0, 0, 0, "Invalid Courier", null);
                    return notFound(Json.toJson(response));
                }
                City ct = City.find.where()
                        .eq("is_deleted", false)
                        .eq("id", city)
                        .setMaxRows(1).findUnique();
                if (ct == null){
                    response.setBaseResponse(0, 0, 0, "Invalid City", null);
                    return notFound(Json.toJson(response));
                }
                List<Cart> model = Cart.find.where()
                        .eq("member", currentMember)
                        .eq("is_deleted", false)
                        .eq("brand_id", getBrandId())
                        .findList();

                Integer weight = 0;
                for (Cart c: model){
                    weight += c.product.getWeightProduct();
                }
                Member reseller = Member.find.byId(currentMember.refferalId);
                ArrayList<Service> data = RajaOngkirService.getInstance().getPrice(cr.code, ct.code, reseller.city.code, weight);
                response.setBaseResponse(0, 0, 0, "Sukses", data);
                return ok(Json.toJson(response));

            }
            response.setBaseResponse(0, 0, 0, inputParameter, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));

    }

    public static Result save() {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
			JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            Transaction txn = Ebean.beginTransaction();
            try {
                MapOrder map = mapper.readValue(json.toString(), MapOrder.class);
                StringBuilder message = new StringBuilder();
                List<Cart> carts = Cart.find.where()
                        .eq("member", actor)
                        .eq("is_deleted", false)
                        .eq("brand_id", getBrandId())
                        .findList();
                if (carts.size() == 0){
                    response.setBaseResponse(0, 0, 0, "Empty cart", null);
                    return notFound(Json.toJson(response));
                }
                Integer weight = 0;
                for (Cart c: carts){
                    if (c.product.itemCount < c.qty){
                        message.append(c.product.name).append(" out of stock.\n");
                    }
                    weight += c.product.getWeightProduct();
                }
                if (!message.toString().isEmpty()){
                    response.setBaseResponse(0, 0, 0, message.toString(), null);
                    return badRequest(Json.toJson(response));
                }

                Courier cr = Courier.find.where()
                        .eq("is_deleted", false)
                        .eq("brand_id", getBrandId())
                        .eq("id", map.getCourierId())
                        .setMaxRows(1).findUnique();
                if (cr == null){
                    response.setBaseResponse(0, 0, 0, "Invalid Courier", null);
                    return notFound(Json.toJson(response));
                }
                
                ShippingAddress ct = ShippingAddress.find.where()
                        .eq("is_deleted", false)
                        .eq("id", map.getShippingAddressId())
                        .setMaxRows(1).findUnique();
                if (ct == null){
                    response.setBaseResponse(0, 0, 0, "Invalid Shipping Address", null);
                    return notFound(Json.toJson(response));
                } else {
                    MapAddress addr = new MapAddress();
                    addr.setFullName(ct.recipientName);
                    addr.setEmail(actor.email);
                    addr.setPhone(ct.phone);
                    addr.setAddress(ct.address);
                    addr.setProvinceId(ct.province.id);
                    addr.setCityId(ct.city.id);
                    addr.setPostalCode(ct.city.postalCode);
                    map.setAddress(addr);
                }

                Member reseller = Member.find.byId(actor.refferalId);
                ArrayList<Service> data = RajaOngkirService.getInstance().getPrice(cr.code, ct.city.code, reseller.city.code, weight);
                Costs shipping = null;
                for (Service dt : data){
                    if (dt.getCode().equalsIgnoreCase(cr.code)){
                        for (Costs c : dt.getCosts()){
                            if (c.getService().equals(map.getService())){
                                shipping = c;
                                break;
                            }
                        }
                    }
                    if (shipping != null) break;
                }
                SalesOrder so = SalesOrder.find.byId(SalesOrder.fromRequest(actor, map, getBrand(), carts, shipping, reseller));
                if (map.getPaymentService().equals(SalesOrder.PAYMENT_SERVICE_MIDTRANS)) {
                    TranscationResponse result = MidtransService.getInstance().createTransaction(so);
                    so.token = result.getToken();
                    so.redirectUrl = result.getRedirectUrl();
                } else {
                    Bank bank = Bank.find.where()
                            .eq("is_deleted", false)
                            .eq("id", map.getBankId())
                            .setMaxRows(1).findUnique();
                    if (bank == null) {
                        response.setBaseResponse(0, 0, 0, "Invalid Bank", null);
                        return notFound(Json.toJson(response));
                    } else {
                        so.status = SalesOrder.ORDER_STATUS_PENDING_PAYMENT;
                        so.bank = bank;
                    }
                }

                so.update();


                for (Cart c: carts){
                    c.delete();
                }
//                SalesOrder so = SalesOrder.find.byId(SalesOrder.fromRequest(actor, map));
//                String redirect = Constant.getInstance().getFrontEndUrl() + "/payment-confirmation";
//                String check = SalesOrder.validation(map);
//                if (check != null) {
//                    response.setBaseResponse(0, 0, 0, check, null);
//                    return badRequest(Json.toJson(response));
//                }

                txn.commit();

                if (map.getPaymentService().equals(SalesOrder.PAYMENT_SERVICE_MANUAL)) {
                    String redirect = Constant.getInstance().getFrontEndUrl() + "/payment-confirmation";
                    Thread thread = new Thread(() -> {
                        try {
                            MailConfig.sendmail(actor.emailNotifikasi, MailConfig.subjectConfirmOrder+" - "+so.orderNumber,
                                    MailConfig.renderMailConfirmOrder(actor, redirect, Encryption.EncryptAESCBCPCKS5Padding(so.orderNumber), so));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    thread.start();
                }



				 /*String emailAdmin = Constant.getInstance().getEmailAdmin();

                 Thread thread = new Thread(() -> {
                    try {
                        String cont = "Name : "+"asass"+"<br/>Email : "+"asass"+"<br/><br/>"+"asasas";
                        MailConfig.sendmail(emailAdmin,"[Contact Us] From "+"sdsdsdsd", cont);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                thread.start();*/

                response.setBaseResponse(1, offset, 1, created, so.parseToMapSalesOrderList());
                return ok(Json.toJson(response));
            } catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }

            response.setBaseResponse(0, 0, 0, inputParameter, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result applyVoucher() {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            try {
                MapVoucherCode voucherCode = mapper.readValue(json.toString(), MapVoucherCode.class);
                List<VoucherDetail> voucherDetails = VoucherDetail.getVouchers(getBrandId(), voucherCode.getVoucherCodes());
                List<Cart> carts = Cart.getCarts(actor.id, getBrandId());
                List<MapCart> result = SalesOrder.calculateVouchers(actor, carts, voucherDetails);
                response.setBaseResponse(1, offset, 1, success, result);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                e.printStackTrace();
            }
            response.setBaseResponse(0, 0, 0, inputParameter, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result upload(String order) {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            SalesOrder so = SalesOrder.find.where()
                    .eq("orderNumber", order)
                    .eq("t0.brand_id", getBrandId())
                    .setMaxRows(1).findUnique();
            if (so != null && so.salesOrderPayment == null){
                Transaction txn = Ebean.beginTransaction();
                try {
                    Http.MultipartFormData body = request().body().asMultipartFormData();
                    String comments = "";
                    String acc_no = "";
                    String acc_name = "";
                    if (body != null) {
                        Map<String, String[]> mapData = body.asFormUrlEncoded();

                        if (mapData != null) {
                            if (mapData.containsKey("comments")){
                                comments = mapData.get("comments")[0];
                            }
                            if (mapData.containsKey("acc_no")){
                                acc_no = mapData.get("acc_no")[0];
                            }
                            if (mapData.containsKey("acc_name")){
                                acc_name = mapData.get("acc_name")[0];
                            }
                        }
                    }

                    Http.MultipartFormData.FilePart imageFile = body.getFile("image");
                    File newFiles = Photo.uploadImage(imageFile, "ord", order, null, "jpg");

                    SalesOrderPayment payment = new SalesOrderPayment();
                    payment.salesOrder = so;
                    payment.brand = getBrand();
                    payment.invoiceNo = SalesOrderPayment.generateInvoiceCode(getBrandId());
                    payment.confirmAt = new Date();
                    payment.debitAccountName = acc_name;
                    payment.debitAccountNumber = acc_no;
                    payment.totalTransfer = so.subtotal;
                    payment.imageUrl = newFiles == null ? "" : Photo.createUrl("ord", newFiles.getName());
//                    payment.status = SalesOrderPayment.PAYMENT_VERIFY;
                    payment.status = SalesOrderPayment.VERIFY;
                    payment.comments = comments;
                    payment.save();

                    so.status = SalesOrder.ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION;
                    so.update();


                    txn.commit();
                    response.setBaseResponse(1, offset, 1, success, null);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    e.printStackTrace();
                    txn.rollback();
                } finally {
                    txn.end();
                }
                response.setBaseResponse(0, 0, 0, inputParameter, null);
                return badRequest(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result orderLists(String type, String status, String name, Integer offset, Integer limit) {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            // List<SalesOrder> salesOrders = SalesOrder.find.where()
            //         .eq("member", actor)
            //         .ilike("salesOrderDetail.productName", "%" + name + "%")
            //         .eq("t0.is_deleted", false)
            //         .eq("t0.brand_id", getBrandId())
            //         .orderBy("t0.created_at DESC")
            //         .findList();
            List<SalesOrder> salesOrders = SalesOrder.salesOrderLists(type, status, name, actor, getBrandId());
            try {
                List<MapSalesOrderList> mapSalesOrderLists = new ArrayList<>();
                for (SalesOrder so : salesOrders) {
                    mapSalesOrderLists.add(so.parseToMapSalesOrderList());
                }
                response.setBaseResponse(1, offset, 1, success, mapSalesOrderLists);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                e.printStackTrace();
            }
            response.setBaseResponse(0, 0, 0, inputParameter, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

//	public static Result orderLists() {
//
//		Member actor = checkMemberAccessAuthorization();
//        if (actor != null) {
//            List<SalesOrder> query = SalesOrder.find.where()
//                    .eq("member", actor)
//                    .eq("t0.is_deleted", false)
//                    .eq("t0.brand_id", getBrandId())
//                    .orderBy("t0.created_at DESC")
//                    .findList();
//
//
//
//					ObjectNode result = Json.newObject();
//					ArrayNode soa = result.putArray("sales_order_detail");
//
//						for (int i = 0; i < query.size(); i++) {
//						ObjectNode rowSoa = Json.newObject();
//						ObjectNode rowCr = Json.newObject();
//						rowSoa.put("full_name", query.get(i).getSalesOrderAddress().getFullName());
//						rowSoa.put("phone", query.get(i).getSalesOrderAddress().getPhone());
//						rowSoa.put("address", query.get(i).getSalesOrderAddress().getAddress());
//						rowSoa.put("city", query.get(i).getSalesOrderAddress().getCity().getName());
//						rowSoa.put("city_postal_code", query.get(i).getSalesOrderAddress().getCity().getPostalCode());
//						rowSoa.put("province", query.get(i).getSalesOrderAddress().getProvince().getName());
//						rowSoa.put("postal_code", query.get(i).getSalesOrderAddress().getPostalCode());
//
//
//						rowSoa.put("courier_name",query.get(i).getCourier().getName());
//						rowSoa.put("courier_code",query.get(i).getCourier().getCode());
//
//
//							for(int j=0; j<query.get(i).getSalesOrderDetail().size(); j++){
//								ObjectNode rowDtl = Json.newObject();
//
//								rowSoa.put("status",query.get(i).getSalesOrderDetail().get(j).getStatus());
//								rowSoa.put("price",query.get(i).getSalesOrderDetail().get(j).getPrice());
//								rowSoa.put("image",query.get(i).getSalesOrderDetail().get(j).getImage());
//								rowSoa.put("color",query.get(i).getSalesOrderDetail().get(j).getColor());
//								rowSoa.put("product_name",query.get(i).getSalesOrderDetail().get(j).getProductName());
//								rowSoa.put("quantity",query.get(i).getSalesOrderDetail().get(j).getQuantity());
//								rowSoa.put("discount_persen",query.get(i).getSalesOrderDetail().get(j).getDiscountPersen());
//								rowSoa.put("discount_amount",query.get(i).getSalesOrderDetail().get(j).getDiscountAmount());
//								rowSoa.put("sub_total",query.get(i).getSalesOrderDetail().get(j).getSubTotal());
//								rowSoa.put("total_price",query.get(i).getSalesOrderDetail().get(j).getTotalPrice());
//								rowSoa.put("total",query.get(i).getSalesOrderDetail().get(j).getQuantity());
//
//							}
//
//						soa.add(rowSoa);
//
//					}
//					result.put("sales_order", Json.toJson(new ObjectMapper().convertValue(query, MapSalesOrderList[].class)));
//					result.put("sales_order_detail",Json.toJson(soa));
//					response.setBaseResponse(1, offset, 1, success, result);
//            //response.setBaseResponse(query.size(), offset, query.size(), success, new ObjectMapper().convertValue(query, MapSalesOrderList[].class));
//            return ok(Json.toJson(response));
//        }
//        response.setBaseResponse(0, 0, 0, unauthorized, null);
//        return unauthorized(Json.toJson(response));
//    }
}
