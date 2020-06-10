package controllers.api;

import assets.JsonMask;
import com.avaje.ebean.*;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;
import com.avaje.ebean.Transaction;
import com.enwie.util.Encryption;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.enwie.api.BaseResponse;
import com.enwie.api.UserSession;
import com.enwie.mapper.response.MapMember;
import com.enwie.mapper.response.MapProduct;
import com.enwie.mapper.response.MapShippingAddress;
import controllers.BaseController;
import controllers.admin.routes;

import com.enwie.util.CommonFunction;

import models.*;
import models.mapper.MapVoucher;
import play.data.validation.ValidationError;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.nio.file.Paths;
import java.io.File;


/**
 * Created by hendriksaragih on 3/19/17.
 */
public class MerchantController extends BaseController {
    @SuppressWarnings("rawtypes")
    private static BaseResponse response = new BaseResponse();

    public static Result index() {
        return ok();
    }

    public static Result getProductList() throws JsonProcessingException {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            if (actor.isReseller.equals(true)) {
            	List<Object> res = new LinkedList<>();
            	JsonNode json = request().body().asJson();
            	String name = json.findPath("name").asText();
            	System.out.println("name===========================");
                System.out.println(name);
            	List<ProductStock> sa = ProductStock.find.where()
                         .eq("reseller", actor)
                         .ilike("product.name", "%" + name + "%")
                         .eq("product.isDeleted", false)
                         .findList();
            	for (ProductStock dt : sa) {
            		Map<String, Object> prd = new HashMap<>();
            		prd.put("id", dt.product.id);
            		prd.put("product_name", dt.product.name);
            		prd.put("category_name", dt.product.category.name);
            		prd.put("price", dt.product.price);
            		prd.put("formated_price", CommonFunction.numberFormat(dt.product.price));
            		prd.put("stock", dt.stock);
            		prd.put("like_count", Optional.ofNullable(dt.product.likeCount).orElse(0));
            		res.add(prd);
            	}

                response.setBaseResponse(sa.size(), offset, sa.size(), success, res);
                return ok(Json.toJson(response));
            } else {
                response.setBaseResponse(0, 0, 0, unauthorized, null);
                return unauthorized(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    public static Result getProductDetail(Long id) throws JsonProcessingException {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            if (actor.isReseller.equals(true)) {
            	try {
                    Product product = Product.find.byId(id);
            		ProductStock productStock = ProductStock.find.where()
                            .eq("product", product)
                            .eq("is_deleted", false)
                            .setMaxRows(1).findUnique();
                	Map<String, Object> res = new HashMap<>();
                    product.itemCount = productStock.stock;
                    
                    ProductDetail proddetail = product.getProductDetail(getBrandId());
                    List<ProductReview> reviews = ProductReview.find.where()
                            .eq("is_deleted", false)
                            .eq("brand_id", getBrandId())
                            .eq("product", product)
                            .orderBy("createdAt").findList();
                    
                    res.put("id", product.id);
                    res.put("name", product.name);
                    res.put("category_name", product.category.name);
                    res.put("brand_name", product.brand.name);
                    res.put("weight", proddetail.getWeight());
                    res.put("image_url", product.getImageUrl());
                    res.put("price", product.price);
                    res.put("formated_price", CommonFunction.currencyFormat(product.price));
                    res.put("discount", product.discount);
                    res.put("discount_type", product.discountType);
                    res.put("discount_active_from", CommonFunction.getDateTime(product.discountActiveFrom));
                    res.put("discount_active_to", CommonFunction.getDateTime(product.discountActiveTo));
                    res.put("stock", product.itemCount);
                    
//            		res.put("product_detail", proddetail);
            		res.put("reviews", reviews);

                    response.setBaseResponse(1, offset, 1, success, res);
                    return ok(Json.toJson(response));
				} catch (Exception e) {
					System.out.println("==================================");
					System.out.println(e);
					System.out.println("==================================");
					response.setBaseResponse(0, 0, 0, inputParameter, null);
		            return badRequest(Json.toJson(response));
				}
            } else {
                response.setBaseResponse(0, 0, 0, unauthorized, null);
                return unauthorized(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    public static Result findProducts() throws JsonProcessingException {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            if (actor.isReseller.equals(true)) {
            	JsonNode json = request().body().asJson();

                Integer iTotalRecords = PurchaseOrder.findRowCount();
                String filter = json.findPath("filter").asText();
                Integer pageSize = Integer.valueOf(json.findPath("length").asText());
                Integer page = Integer.valueOf(json.findPath("start").asText()) / pageSize;

                String sortBy = "name";
                String order = "ASC";

                switch (Integer.valueOf(json.findPath("order_by").asText())) {
		            case 1 :  sortBy = "id"; break;
		            case 2 :  sortBy = "name"; break;
                }

                List<Object> res = new LinkedList<>();
                if(!filter.isEmpty()) {
                	Page<Product> datas = Product.find.where()
	                    .or(Expr.ilike("sku", "%" + filter + "%"), Expr.ilike("name", "%" + filter + "%"))
	                    .eq("is_deleted", false)
	                    .orderBy(sortBy + " " + order)
	                    .findPagingList(pageSize)
	                    .setFetchAhead(false)
	                    .getPage(page);
    	            Integer iTotalDisplayRecords = datas.getTotalRowCount();
 
	                for (Product dt : datas.getList()) {
	              	  	Map<String, Object> row = new HashMap<>();
	              	  	row.put("id", dt.id);
	  	                row.put("price", CommonFunction.numberFormat(dt.price));
	  	                row.put("name", dt.name);
	            		
	            		res.add(row);
	            	}
                }
            	response.setBaseResponse(res.size(), offset, pageSize, success, res);
                return ok(Json.toJson(response));
            } else {
                response.setBaseResponse(0, 0, 0, unauthorized, null);
                return unauthorized(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    public static Result savePurchaseOrder() throws JsonProcessingException {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            if (actor.isReseller.equals(true)) {
            	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            	JsonNode json = request().body().asJson();

                String receivedDateStr = json.findPath("received_date").asText();
                String information = json.findPath("information").asText();
                JsonNode products = json.findPath("products");

                Date receivedDate = null;
                try {
                	receivedDate = simpleDateFormat.parse(receivedDateStr);
				} catch (Exception e) {}

                if (receivedDate == null || information.isEmpty()) {
                	response.setBaseResponse(0, 0, 0, inputParameter, null);
		            return badRequest(Json.toJson(response));
                }

                Transaction txn = Ebean.beginTransaction();
                Boolean success = false;
                try {
                	List<PurchaseOrderDetail> mapVendor = new ArrayList<>();
	                for(int i = 0; i < products.size(); i++){
		              int qty = products.get(i).findPath("qty").asInt();
		              if(qty > 0){
		                  Product product = Product.find.byId(products.get(i).findPath("id").asLong());
		                  PurchaseOrderDetail poDetail= new PurchaseOrderDetail();
		                  poDetail.product = product;
		                  poDetail.qty = qty;
		                  poDetail.price = product.price;
		                  poDetail.subTotal = poDetail.price * qty;
		                  mapVendor.add(poDetail);
		              }
	                }
	                
	                if(mapVendor.size() > 0){
	                  PurchaseOrder newPO = new PurchaseOrder();
	                  newPO.code = newPO.generatePOCode();
	                  newPO.distributor = UserCms.getDistributor(actor.city);
	                  newPO.reseller = actor;
	                  newPO.information = information;
	                  newPO.status = PurchaseOrder.SENT;
	                  newPO.receivedAt = receivedDate;
	                  newPO.total = 0.0;
	                  for(PurchaseOrderDetail newPODetail : mapVendor){
	                      newPO.total += newPODetail.subTotal;
	                  }
	                  newPO.save();
	                  for(PurchaseOrderDetail newPODetail : mapVendor){
	                      newPODetail.po = newPO;
	                      newPODetail.save();
	                  }
	                }
	                txn.commit();
	                success = true;
                }catch (Exception e) {
                  e.printStackTrace();
                  txn.rollback();
              	} finally {
                  txn.end();
              	}

                if (success.equals(true)) {
                	response.setBaseResponse(1, offset, 1, created, null);
                	return ok(Json.toJson(response));
                } else {
                	response.setBaseResponse(0, 0, 0, inputParameter, null);
		            return badRequest(Json.toJson(response));
                }
            } else {
                response.setBaseResponse(0, 0, 0, unauthorized, null);
                return unauthorized(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result removePurchaseOrder(Long id) throws JsonProcessingException {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            if (actor.isReseller.equals(true)) {
              Ebean.beginTransaction();
              int status = 0;
              try {
                  PurchaseOrder data = PurchaseOrder.find.ref(id);
                  data.isDeleted = true;
                  data.update();
                  status = 1;
      
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
            } else {
                response.setBaseResponse(0, 0, 0, unauthorized, null);
                return unauthorized(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    public static Result updateStatusPurchaseOrder(Long id, Integer newStatus) throws JsonProcessingException {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            if (actor.isReseller.equals(true)) {
            	Ebean.beginTransaction();
                int status = 0;
                try {
                    PurchaseOrder data = PurchaseOrder.find.ref(id);
                    data.updateStatus(newStatus);

                    status = 1;

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
            } else {
                response.setBaseResponse(0, 0, 0, unauthorized, null);
                return unauthorized(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    public static Result getPurchaseOrders() throws JsonProcessingException {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            if (actor.isReseller.equals(true)) {
            	JsonNode json = request().body().asJson();
                String name = json.findPath("value").asText();

                Integer iTotalRecords = PurchaseOrder.findRowCount();
//                Integer filter = Integer.valueOf(json.findPath("filter").asText());
                Integer pageSize = Integer.valueOf(json.findPath("length").asText());
                Integer page = Integer.valueOf(json.findPath("start").asText()) / pageSize;

                String sortBy = "code";
                String order = "ASC";

                switch (Integer.valueOf(json.findPath("order_by").asText())) {
	                case 1 :  sortBy = "id"; break;
	                case 2 :  sortBy = "code"; break;
                }
            	
            	Page<PurchaseOrder> datas = PurchaseOrder.pageMerchant(page, pageSize, sortBy, order, name, actor);
            	Integer iTotalDisplayRecords = datas.getTotalRowCount();
            	
            	List<Object> res = new LinkedList<>();
            	for (PurchaseOrder dt : datas.getList()) {
            		Map<String, Object> row = new HashMap<>();

	                row.put("id", dt.id);
	                row.put("po_id", dt.code);
	                row.put("distributor", dt.distributor.fullName);
	                row.put("received_date", CommonFunction.getDate2(dt.receivedAt));
	                row.put("created_date", CommonFunction.getDate2(dt.createdAt));
	                row.put("status", dt.getStatus());
	                row.put("total", CommonFunction.currencyFormat(dt.total));
            		
            		res.add(row);
            	}

            	response.setBaseResponse(iTotalDisplayRecords, offset, pageSize, success, res);
                return ok(Json.toJson(response));
            } else {
                response.setBaseResponse(0, 0, 0, unauthorized, null);
                return unauthorized(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    public static Result getPurchaseOrderDetail(Long id) throws JsonProcessingException {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            if (actor.isReseller.equals(true)) {
              	try {
              		PurchaseOrder data = PurchaseOrder.find.byId(id);
                    Map<String, Object> res = new HashMap<>();

                    res.put("po_id", data.code);
                    res.put("distributor", data.distributor.fullName);
                    res.put("total", CommonFunction.currencyFormat(data.total, "Rp"));
                    res.put("received_date", CommonFunction.getDate2(data.receivedAt));
                    res.put("information", data.information);

                    List<Object> details = new LinkedList<>();
                    for (PurchaseOrderDetail pod: data.details) {
                    	Map<String, Object> det = new HashMap<>();
                    	
                    	det.put("name", pod.product.name);
                        det.put("quantity", pod.qty);
                        det.put("price", CommonFunction.currencyFormat(pod.price, "Rp"));
                        det.put("sub_total", CommonFunction.currencyFormat(pod.subTotal, "Rp"));
                        details.add(det);
                    }
                    res.put("product_details", details);
                    

              		response.setBaseResponse(1, offset, 1, success, res);
                  	return ok(Json.toJson(response));
				} catch (Exception e) {
					System.out.println("==================================");
					System.out.println(e);
					System.out.println("==================================");
					response.setBaseResponse(0, 0, 0, inputParameter, null);
		            return badRequest(Json.toJson(response));
				}
            } else {
                response.setBaseResponse(0, 0, 0, unauthorized, null);
                return unauthorized(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    public static Result getCustomers() throws JsonProcessingException {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            if (actor.isReseller.equals(true)) {
              JsonNode json = request().body().asJson();
              String name = json.findPath("value").asText();

              Integer iTotalRecords = Member.RowCount(getBrandId());
              Integer filter = Integer.valueOf(json.findPath("filter").asText());
              Integer pageSize = Integer.valueOf(json.findPath("length").asText());
              Integer page = Integer.valueOf(json.findPath("start").asText()) / pageSize;

              String sortBy = "full_name";
              String order = "ASC";

              switch (Integer.valueOf(json.findPath("order_by").asText())) {
                  case 1 :  sortBy = "id"; break;
                  case 2 :  sortBy = "full_name"; break;
                  case 3 :  sortBy = "email"; break;
                  case 4 :  sortBy = "phone"; break;
                  case 5 :  sortBy = "created_at"; break;
                  case 6 :  sortBy = "last_login"; break;
                  case 7 :  sortBy = "last_purchase"; break;
              }

              Page<Member> datas = Member.pageMerchant(page, pageSize, sortBy, order, name, filter, getBrandId(), actor.id);
              Integer iTotalDisplayRecords = datas.getTotalRowCount();

             
              List<Object> res = new LinkedList<>();
              for (Member dt : datas.getList()) {
            	  Map<String, Object> row = new HashMap<>();
	              String status = "";
	              String reseller = "";
	              status += dt.getIsActive();
	              row.put("full_name", dt.fullName);
	              row.put("email", dt.email);
	              row.put("phone", dt.phone);
	              row.put("register_date", dt.getRegisterDate());
	              row.put("last_login", dt.getLastLogin());
	              row.put("last_purchase", dt.getLastPurchase());
	              row.put("reseller", reseller);
	              row.put("status", status);
          		
          		
          		res.add(row);
          	  }

              response.setBaseResponse(iTotalDisplayRecords, offset, pageSize, success, res);
              return ok(Json.toJson(response));

            } else {
                response.setBaseResponse(0, 0, 0, unauthorized, null);
                return unauthorized(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result saveCustomer() throws JsonProcessingException {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            if (actor.isReseller.equals(true)) {
                JsonNode json = request().body().asJson();

                String fullName = json.findPath("full_name").asText();
                String email = json.findPath("email").asText();
                String phone = json.findPath("phone").asText();
                String password = json.findPath("password").asText();
                String address = json.findPath("address").asText();

                List<Member> listUser = Member.find.where().eq("isDeleted", false).eq("email", email).findList();
                if(listUser.size() > 0){
                    response.setBaseResponse(0, 0, 0, "Email already exists.", null);
                    return badRequest(Json.toJson(response));
                }
                listUser = Member.find.where().eq("isDeleted", false).eq("phone", phone).findList();
                if(listUser.size() > 0){
                    response.setBaseResponse(0, 0, 0, "Phone already exists.", null);
                    return badRequest(Json.toJson(response));
                }

                Transaction txn = Ebean.beginTransaction();
                Boolean success = false;
                try {
                    Member data = new Member();
                    data.refferalId = actor.id;
                    data.province = actor.province;
                    data.city = actor.city;
                    data.fullName = fullName;
                    data.phone = phone;
                    data.email = email;
                    data.address = address;
                    data.brand = getBrand();
                    data.isReseller = false;
                    data.isActive = true;
                    data.password = Encryption.EncryptAESCBCPCKS5Padding(password);
                    data.save();

                    txn.commit();
                    success = true;
                }catch (Exception e) {
                    e.printStackTrace();
                    txn.rollback();
                } finally {
                    txn.end();
                }

                if (success.equals(true)) {
                    response.setBaseResponse(1, offset, 1, created, null);
                    return ok(Json.toJson(response));
                } else {
                    response.setBaseResponse(0, 0, 0, inputParameter, null);
                    return badRequest(Json.toJson(response));
                }

            } else {
                response.setBaseResponse(0, 0, 0, unauthorized, null);
                return unauthorized(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result getOrders() throws JsonProcessingException {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            if (actor.isReseller.equals(true)) {
                JsonNode json = request().body().asJson();

                Integer iTotalRecords = SalesOrder.findRowCount();
                String name = json.findPath("value").asText();

                String filter = json.findPath("filter").asText();
                Integer pageSize = Integer.valueOf(json.findPath("length").asText());
                Integer page = Integer.valueOf(json.findPath("start").asText()) / pageSize;

                String sortBy = "orderDate";
                String order = "ASC";

                switch (Integer.valueOf(json.findPath("order_by").asText())) {
                    case 1 :  sortBy = "id"; break;
                    case 2 :  sortBy = "orderDate"; break;
                }

                Page<SalesOrder> datas = SalesOrder.page(page, pageSize, sortBy, order, name, filter, getBrandId(), actor.id);
                Integer iTotalDisplayRecords = datas.getTotalRowCount();

                List<Object> res = new LinkedList<>();
                for (SalesOrder dt : datas.getList()) {
                	Map<String, Object> row = new HashMap<>();

                	row.put("order_id", dt.id);
                    row.put("order_number", dt.orderNumber);
                    row.put("order_date", dt.getTanggal());
                    row.put("customer", dt.member.fullName);
                    row.put("payment_status", dt.getPaymentStatus());
                    row.put("order_total", dt.getTotalFormat());
                    row.put("payment_method", dt.getStrPaymentType());
                    row.put("order_status", dt.convertStatusName());
                    row.put("status_reseller", dt.statusReseller);
                    res.add(row);
                }

              response.setBaseResponse(iTotalDisplayRecords, offset, pageSize, success, res);
              return ok(Json.toJson(response));

            } else {
                response.setBaseResponse(0, 0, 0, unauthorized, null);
                return unauthorized(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result getOrderDetail(Long id) throws JsonProcessingException {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            if (actor.isReseller.equals(true)) {
              	try {
              		SalesOrder data = SalesOrder.find.byId(id);
                    
                    Map<String, Object> res = new HashMap<>();
                    res.put("order_number", data.orderNumber);
                    res.put("order_date", data.getTanggal());
                    res.put("customer", data.member.fullName);
                    res.put("reseller", data.reseller.fullName);
                    res.put("order_total", data.getTotalFormat());
                    res.put("tracking_number", data.trackingNumber);
                    res.put("billing_address", data.getBillingAddress());
                    res.put("sub_total", data.getSubTotalFormat());
                    res.put("discount", data.getDiscountFormat());
                    res.put("voucher", data.getVoucherFormat());
                    res.put("shipping", data.getShippingFormat());
                    res.put("billing_address", data.getBillingAddress());
                    res.put("billing_address", data.getBillingAddress());
                    res.put("order_detail", data.salesOrderDetail);
              		
              		response.setBaseResponse(1, offset, 1, success, res);
                  	return ok(Json.toJson(response));
				} catch (Exception e) {
					System.out.println("==================================");
					System.out.println(e);
					System.out.println("==================================");
					response.setBaseResponse(0, 0, 0, inputParameter, null);
		            return badRequest(Json.toJson(response));
				}
            } else {
                response.setBaseResponse(0, 0, 0, unauthorized, null);
                return unauthorized(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    public static Result approveOrder(Long id) throws JsonProcessingException {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            if (actor.isReseller.equals(true)) {
                SalesOrder so = SalesOrder.find.where()
                        .eq("t0.status", SalesOrder.ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION)
                        .eq("t0.status_reseller", SalesOrder.ORDER_STATUS_RESELLER_WAITING)
                        .eq("t0.id", id)
                        .eq("t0.is_deleted", false)
                        .setMaxRows(1).findUnique();
                Boolean outOfStock = false;
                if (so != null){
                    Transaction txn = Ebean.beginTransaction();
                    try {
                        so.statusReseller = SalesOrder.ORDER_STATUS_RESELLER_APPROVE;
                        so.status = SalesOrder.ORDER_STATUS_PICKING;
                        so.update();
                        Member member = actor;
                        for (SalesOrderDetail sod : so.salesOrderDetail){
                            Product product = sod.product;
                            ProductStock productStock = ProductStock.getProductReseller(member, product);
                            if(sod.quantity > productStock.stock.intValue()){
                                outOfStock = true;
                                break;
                            }else {
                                productStock.stock = productStock.stock - sod.quantity;
                                productStock.update();
                                UserCms distributor = UserCms.getDistributor(member.city);
                                ProductMutationStock.saveStock(member, distributor, product, sod.quantity,
                                        ProductMutationStock.CREDIT, "Penjualan SO "+so.orderNumber, so.orderDate);

                                ResellerMutationBalance.saveBalance(member, product, sod.price, sod.quantity,
                                        ProductMutationStock.DEBIT, "Penjualan SO "+so.orderNumber, so.orderDate);
                                DistributorMutationBalance.saveBalance(distributor, product, sod.price, sod.quantity,
                                        ProductMutationStock.DEBIT, "Penjualan SO "+so.orderNumber, so.orderDate);
                            }
                        }

                        if (!outOfStock) txn.commit();
                        else txn.rollback();
                    }catch (Exception ex){
                        ex.printStackTrace();
                        txn.rollback();
                    }finally {
                        txn.end();
                    }
                }

                if (!outOfStock){
                    response.setBaseResponse(1, offset, 1, updated, null);
                    return ok(Json.toJson(response));
                }else {
                    response.setBaseResponse(1, offset, 1, "Out of stock", null);
                    return badRequest(Json.toJson(response));
                }                

            } else {
                response.setBaseResponse(0, 0, 0, unauthorized, null);
                return unauthorized(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    public static Result rejectOrder(Long id) throws JsonProcessingException {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            if (actor.isReseller.equals(true)) {
            	System.out.println("================================");
            	System.out.println(id);
            	System.out.println("================================");
            	SalesOrder so = SalesOrder.find.where()
                        .eq("t0.status", SalesOrder.ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION)
                        .eq("t0.status_reseller", SalesOrder.ORDER_STATUS_RESELLER_WAITING)
                        .eq("t0.id", id)
                        .eq("t0.is_deleted", false)
                        .setMaxRows(1).findUnique();
                if (so != null){
                	Transaction txn = Ebean.beginTransaction();
                    try {
                        so.statusReseller = SalesOrder.ORDER_STATUS_RESELLER_REJECT;
                        so.update();
                        txn.commit();
                    }catch (Exception ex){
                        ex.printStackTrace();
                        txn.rollback();
                    }finally {
                        txn.end();
                    }

                    response.setBaseResponse(1, offset, 1, updated, null);
                    return ok(Json.toJson(response));
                }else {
                	response.setBaseResponse(0, 0, 0, inputParameter, null);
		            return badRequest(Json.toJson(response));
                }                

            } else {
                response.setBaseResponse(0, 0, 0, unauthorized, null);
                return unauthorized(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    public static Result updateStatusOrder(Long id) throws JsonProcessingException {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            if (actor.isReseller.equals(true)) {
            	JsonNode json = request().body().asJson();
                String status = json.findPath("status").asText();
                String trackingNumber = json.findPath("tracking_number").asText();
            	
                SalesOrder so = SalesOrder.find.where()
                        .eq("t0.status_reseller", SalesOrder.ORDER_STATUS_RESELLER_APPROVE)
                        .eq("t0.id", id)
                        .eq("t0.is_deleted", false)
                        .setMaxRows(1).findUnique();
                if (so != null && status.isEmpty() == false){
                	Transaction txn = Ebean.beginTransaction();
                    try {
                    	so.status = status;
                        if (status.equals(SalesOrder.ORDER_STATUS_ON_DELIVERY)) so.trackingNumber = trackingNumber;
                        so.update();
                        txn.commit();
                    }catch (Exception ex){
                        ex.printStackTrace();
                        txn.rollback();
                    }finally {
                        txn.end();
                    }

                    response.setBaseResponse(1, offset, 1, updated, null);
                    return ok(Json.toJson(response));
                }else {
                	response.setBaseResponse(0, 0, 0, inputParameter, null);
		            return badRequest(Json.toJson(response));
                }                

            } else {
                response.setBaseResponse(0, 0, 0, unauthorized, null);
                return unauthorized(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
}
