package models;

import com.avaje.ebean.*;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.annotation.CreatedTimestamp;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.enwie.mapper.request.MapOrder;
import com.enwie.mapper.response.*;
import com.enwie.shipping.rajaongkir.response.model.Costs;
import com.enwie.util.CommonFunction;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by hendriksaragih on 4/26/17.
 */
@Entity
public class SalesOrder extends BaseModel {
    private static final long serialVersionUID = 1L;
    public static final Integer PAYMENT_SERVICE_MIDTRANS = 0;
    public static final Integer PAYMENT_SERVICE_MANUAL = 1;

    public static final String PAYMENT_METHOD_COD = "COD";
    public static final String PAYMENT_METHOD_TRANSFER = "TRF";

    public static final String ORDER_STATUS_VERIFY = "OV";
    public static final String ORDER_STATUS_PENDING_PAYMENT = "PP";
    public static final String ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION = "WC";
    public static final String ORDER_STATUS_EXPIRE_PAYMENT = "EX";
    public static final String ORDER_STATUS_PICKING = "PI";
    public static final String ORDER_STATUS_PACKING = "PA";
    public static final String ORDER_STATUS_ON_DELIVERY = "OD";
    public static final String ORDER_STATUS_RECEIVE_BY_CUSTOMER = "RC";
    public static final String ORDER_STATUS_CUSTOMER_NOT_AT_THE_ADDRESS_STATE = "NA";
    public static final String ORDER_STATUS_CANCEL = "CA";
    public static final String ORDER_STATUS_RETURN = "RT";
    public static final String ORDER_STATUS_REPLACED = "RP";
    public static final String ORDER_STATUS_CANCEL_BY_CUSTOMER_SERVICE = "CC";

    public static final String ORDER_STATUS_RESELLER_WAITING = "WAITING";
    public static final String ORDER_STATUS_RESELLER_APPROVE = "APPROVE";
    public static final String ORDER_STATUS_RESELLER_REJECT = "REJECT";
    public static final String ORDER_STATUS_RESELLER_EXPIRED = "EXPIRED";
    public static final List<String> ORDER_STATUS_RESELLER_UPDATE = Arrays.asList(ORDER_STATUS_RESELLER_EXPIRED, ORDER_STATUS_RESELLER_REJECT);

    public static final String PAYMENT_STATUS_EXPIRE = "expire";
    public static final String PAYMENT_STATUS_SETTLEMENT = "settlement";
    public static final String PAYMENT_STATUS_PENDING = "pending";
    public static final String PAYMENT_STATUS_DENY = "deny";

    public static final String PAYMENT_TYPE_CSTORE = "cstore";
    public static final String PAYMENT_TYPE_AKULAKU = "akulaku";
    public static final String PAYMENT_TYPE_GOPAY = "gopay";
    public static final String PAYMENT_TYPE_BANK_TRANSFER = "bank_transfer";
    public static final String PAYMENT_TYPE_BANK_CC = "credit_card";
    public static final String PAYMENT_TYPE_BANK_ECHANNEL = "echannel";
    public static final String PAYMENT_TYPE_BANK_BCA_KLIKPAY = "bca_klikpay";
    public static final String PAYMENT_TYPE_BANK_MANDIRI_CLICKPAY = "mandiri_clickpay";
    public static final String PAYMENT_TYPE_BANK_BRI_EPAY = "bri_epay";
    public static final String PAYMENT_TYPE_BANK_CIMB_CLICKS = "cimb_clicks";
    public static final String PAYMENT_TYPE_BANK_DANAMON_ONLINE = "danamon_online";

    public static final List<String> ORDER_NEW = Arrays.asList(ORDER_STATUS_VERIFY, ORDER_STATUS_PENDING_PAYMENT);
    public static final List<String> ORDER_PAID = Arrays.asList(ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION);
    public static final List<String> ORDER_PROCESSED = Arrays.asList(ORDER_STATUS_PICKING, ORDER_STATUS_PACKING, ORDER_STATUS_ON_DELIVERY);
    public static final List<String> ORDER_COMPLETED = Arrays.asList(ORDER_STATUS_RECEIVE_BY_CUSTOMER);
    public static final List<String> ORDER_RETURN = Arrays.asList(ORDER_STATUS_RETURN);
    public static final List<String> ORDER_FAILED = Arrays.asList(ORDER_STATUS_CANCEL, ORDER_STATUS_CUSTOMER_NOT_AT_THE_ADDRESS_STATE);

    public static final List<String> ORDER_PENDING = Arrays.asList(ORDER_STATUS_PENDING_PAYMENT, ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION, ORDER_STATUS_EXPIRE_PAYMENT);
    public static final List<String> PAYMENT_CANCEL = Arrays.asList(ORDER_STATUS_EXPIRE_PAYMENT, ORDER_STATUS_CANCEL, ORDER_STATUS_CANCEL_BY_CUSTOMER_SERVICE);

    public static Finder<Long, SalesOrder> find = new Finder<>(Long.class, SalesOrder.class);

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    @JsonProperty("order_date")
    public Date orderDate;

    @Column(unique = true)
    @JsonProperty("order_number")
    public String orderNumber;

    public Double discount;

    public Double voucher;

    public Double subtotal;

    @JsonProperty("shipping")
    public Double shipping;

    @JsonProperty("total_price")
    public Double totalPrice;

    @JsonProperty("service_code")
    public String serviceCode;
    @JsonProperty("service_description")
    public String serviceDescription;
    @JsonProperty("service_etd")
    public String serviceEtd;

    @JsonIgnore
    @ManyToOne
    public Member member;

    @JsonIgnore
    @ManyToOne
    public Member reseller;

    @JsonProperty("token")
    public String token;

    @JsonProperty("redirect_url")
    public String redirectUrl;
//
//    @ManyToOne
//    @JsonProperty("shipment_address")
//    public Address shipmentAddress;

    @ManyToOne
    public Courier courier;
	
//	@ManyToOne
//    @JsonProperty("billing_address")
//    public SalesOrderAddress billingAddress;

    @ManyToOne
    public Bank bank;

    public String status;

    @JsonProperty("status_reseller")
    public String statusReseller;

    @JsonProperty("is_manual")
    @Column(name = "is_manual")
    public Boolean isManual;

    @JsonProperty("expired_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    public Date expiredDate;

    public String struct;

    @JsonProperty("shipment_type")
    public String shipmentType;

    @JsonProperty("payment_type")
    public String paymentType;

    @JsonProperty("payment_description")
    public String paymentDescription;

    @JsonProperty("va_number")
    public String vaNumber;

    @JsonProperty("email_notif")
    public String emailNotif;

    @OneToMany(mappedBy = "salesOrder")
    @JsonProperty("details")
    public List<SalesOrderDetail> salesOrderDetail;

    @OneToOne(mappedBy = "salesOrder")
    public SalesOrderAddress salesOrderAddress;

    @ManyToOne(cascade = { CascadeType.ALL })
    public Brand brand;

    @OneToOne(mappedBy = "salesOrder")
    @JsonProperty("sales_order_payment")
    public SalesOrderPayment salesOrderPayment;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    @JsonProperty("approved_date")
    public Date approvedDate;

    @Column(name = "approved_by")
    @JsonIgnore
    @ManyToOne
    public UserCms approvedBy;

    @JsonIgnore
    @JoinColumn(name="user_id")
    @ManyToOne
    public UserCms userCms;

    @Transient
    @JsonProperty("start")
    public String getStart(){
        return CommonFunction.getDateTime(new Date());
    }
	
    @Transient
    @JsonProperty("order_date_string")
    public String getOrderDateString(){
        return CommonFunction.getDate(orderDate);
    }

    @Transient
    @JsonProperty("sub_total")
    public Double getSubTotal(){
        return subtotal - shipping;
    }

    @Transient
    @JsonProperty("total")
    public Double getTotal(){
        return totalPrice;
    }

    @JsonProperty("tracking_number")
    public String trackingNumber;

    @JsonProperty("payment_service")
    public Integer paymentService;

//    @Transient
//	@JsonProperty("str_status")
//    public String getStrStatus(){
//        String result = "";
//        int countStatus = 0;
//        String status = "";
//        Map<String, Integer> mapStatus = new HashMap<>();
//
//        if(countStatus == 1){
//            result = convertStatusName(status.replace(";",""));
//        }else{
//            String tmp = "";
//            for(Map.Entry<String, Integer> entry : mapStatus.entrySet()){
//                tmp += convertStatusName(entry.getKey())+" ["+entry.getValue()+"];";
//            }
//            tmp = tmp.substring(0, tmp.length()-1);
//            result = "<a href=\"#\" onclick=\"showStatusDetail('"+tmp+"')\">View Status</a>";
//        }
//
//        return result;
//    }
	
	
    public String convertStatusName(){
        String result = "";
        switch (status){
            case ORDER_STATUS_VERIFY : result = "Order Verified";break;
            case ORDER_STATUS_PENDING_PAYMENT : result = "Pending Payment";break;
            case ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION : result = "Waiting Payment Confirmation";break;
            case ORDER_STATUS_EXPIRE_PAYMENT : result = "Expire Payment";break;
            case ORDER_STATUS_PICKING : result = "Picking";break;
            case ORDER_STATUS_PACKING : result = "Packing";break;
            case ORDER_STATUS_ON_DELIVERY : result = "On Delivery";break;
            case ORDER_STATUS_RECEIVE_BY_CUSTOMER : result = "Received By Customer";break;
            case ORDER_STATUS_CUSTOMER_NOT_AT_THE_ADDRESS_STATE : result = "Customer Not At The Address State";break;
            case ORDER_STATUS_CANCEL : result = "Cancel";break;
            case ORDER_STATUS_RETURN : result = "Return";break;
            case ORDER_STATUS_REPLACED : result = "Replaced";break;
            case ORDER_STATUS_CANCEL_BY_CUSTOMER_SERVICE : result = "Cancel By Customer Service";break;
            default: result = "Invalid Status";
        }
        return result;
    }

    @Transient
	@JsonProperty("str_payment_type")
    public String getStrPaymentType(){
        String result = "";
        switch (paymentType){
            case PAYMENT_METHOD_COD : result = "COD"; break;
            case PAYMENT_METHOD_TRANSFER : result = "Transfer - "+bank.bankName;break;
            case PAYMENT_TYPE_CSTORE : result = "Store - "+paymentDescription; break;
            case PAYMENT_TYPE_AKULAKU : result = "Aku Laku"; break;
            case PAYMENT_TYPE_GOPAY : result = "Gopay"; break;
            case PAYMENT_TYPE_BANK_TRANSFER : result = "Bank Transfer - "+paymentDescription+"("+vaNumber+")"; break;
            case PAYMENT_TYPE_BANK_CC : result = "Credit Card"; break;
            case PAYMENT_TYPE_BANK_ECHANNEL : result = "Mandiri"; break;
            case PAYMENT_TYPE_BANK_BCA_KLIKPAY : result = "BCA Klikpay"; break;
            case PAYMENT_TYPE_BANK_MANDIRI_CLICKPAY : result = "Mandiri Clickpay"; break;
            case PAYMENT_TYPE_BANK_BRI_EPAY : result = "Epay BRI"; break;
            case PAYMENT_TYPE_BANK_CIMB_CLICKS : result = "CIMB Clicks"; break;
            case PAYMENT_TYPE_BANK_DANAMON_ONLINE : result = "Danamon Online Banking"; break;

//            default: status = "Invalid";
        }

        return result;
    }

    @Transient
	@JsonProperty("payment_status")
    public String getPaymentStatus(){
        String result = "Unpaid";

        if(status.equals(ORDER_STATUS_RETURN)){
            result = "Refund";
        }

        if (salesOrderPayment != null){
            if(salesOrderPayment.status.equals(SalesOrderPayment.PAYMENT_VERIFY)){
                if(status.equals(ORDER_STATUS_EXPIRE_PAYMENT))
                    result = "Cancel";
                else result = "Waiting for Confirmation";
            }else if(salesOrderPayment.status.equals(SalesOrderPayment.VERIFY)){
                result = "Paid";
            }else if(salesOrderPayment.status.equals(SalesOrderPayment.PAYMENT_REJECT) || salesOrderPayment.status.equals(ORDER_STATUS_EXPIRE_PAYMENT)){
                result = "Cancel";
            }
        }

        return result;
    }

    @javax.persistence.Transient
    @JsonProperty("currency")
    public String getCurrency(){
        return "Rp";
    }

    @javax.persistence.Transient
    @JsonProperty("qty")
    public int getQty(){
        return salesOrderDetail.size();
    }


    public static Page<SalesOrder> page(int page, int pageSize, String sortBy, String order, String name, String filter, Long brandId) {
        ExpressionList<SalesOrder> qry = SalesOrder.find
                .where()
                .ilike("orderNumber", "%" + name + "%")
                .eq("t0.brand_id", brandId)
                .eq("t0.is_deleted", false);

        if(!filter.equals("")){
            qry.eq("t0.status", filter);
        }

        return
                qry.orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);

    }
    
    public static Page<SalesOrder> page(int page, int pageSize, String sortBy, String order, String name, String filter, Long brandId, Long resellerId) {
        ExpressionList<SalesOrder> qry = SalesOrder.find
                .where()
                .ilike("orderNumber", "%" + name + "%")
                .eq("t0.reseller_id", resellerId)
                .eq("t0.is_deleted", false);

        if(!filter.equals("")){
            qry.eq("t0.status", filter);
        }

        return
                qry.orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);

    }

    public static int findRowCount() {
        return
                find.where()
                        .eq("t0.is_deleted", false)
                        .findRowCount();
    }

	 @Transient
	@JsonProperty("total_format")
    public String getTotalFormat(){
        return CommonFunction.numberFormat(totalPrice);
    }

    public String getSubTotalFormat(){
        return CommonFunction.currencyFormat(subtotal, "Rp");
    }

    public String getDiscountFormat(){
        return CommonFunction.currencyFormat(discount, "Rp");
    }

    public String getVoucherFormat(){
        return CommonFunction.currencyFormat(voucher, "Rp");
    }

    public String getShippingFormat(){
        return CommonFunction.currencyFormat(shipping, "Rp");
    }

    public String getTanggal(){
        return CommonFunction.getDate(orderDate);
    }

    public String getApprovedDate(){
        return CommonFunction.getDate(approvedDate);
    }

    public String getApprovedBy(){
        return approvedBy != null ? approvedBy.fullName : "";
    }

	@Transient
	@JsonProperty("billing_address")
    public String getBillingAddress(){
        if (salesOrderAddress != null){
            return "" +
                    salesOrderAddress.fullName+"\n" +
                    salesOrderAddress.address+"\n" +
                    salesOrderAddress.city.name+ " " + salesOrderAddress.province.name+ " \n" +
					salesOrderAddress.getPostalCode()+"\n" +
                    salesOrderAddress.phone+"\n" +
                    "";
        }
        return "";
    }

   /* public String getPaymentAddress(){
        if (billingAddress != null){
            return "" +
                    billingAddress.fullName+"\n" +
                    billingAddress.address+"\n" +
                    billingAddress.phone+"\n" +
                    "";
        }

        return "";
    }*/


    public static List<SalesOrder> getOrderByMember(Long member){
        return SalesOrder.find.where()
                .eq("member_id", member)
                .eq("t0.is_deleted", false)
                .setOrderBy("t0.id DESC").setMaxRows(5).findList();
    }

    public static String generateSOCode(Long brandId){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMM");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM");
        SalesOrder so = SalesOrder.find.where("t0.created_at > '"+simpleDateFormat2.format(new Date())+"-01 00:00:00' AND t0.brand_id = "+brandId)
                .order("t0.created_at desc").setMaxRows(1).findUnique();
        String seqNum = "";
        if(so == null){
            seqNum = "00001";
        }else{
            seqNum = so.orderNumber.substring(so.orderNumber.length() - 5);
            int seq = Integer.parseInt(seqNum)+1;
            seqNum = "00000" + String.valueOf(seq);
            seqNum = seqNum.substring(seqNum.length() - 5);
        }
//        String code = (brandId == 1L) ? "EVS" : "MLR";
        String code = "ENW";
        code += simpleDateFormat.format(new Date()) + seqNum;
        return code;
    }

    public static Long fromRequest(Member member, MapOrder map, Brand brand, List<Cart> carts, Costs shipping, Member reseller) {
        SalesOrder model = new SalesOrder();
        model.orderDate = new Date();
        model.orderNumber = generateSOCode(brand.id);
        model.brand = brand;
        model.voucher = model.totalPrice = model.subtotal = model.discount = 0D;
        model.member = member;
        model.reseller = reseller;
        model.status = ORDER_STATUS_VERIFY;
        model.statusReseller = ORDER_STATUS_RESELLER_WAITING;
        model.isManual = false;
        model.expiredDate = PaymentExpiration.getExpired(brand.id);
        model.struct = "";
        model.shipmentType = "";
        model.emailNotif = "";
        model.courier = Courier.findById(map.getCourierId(), brand.id);
        model.shipping = shipping.getCost().get(0).getValue();
        model.serviceCode = shipping.getService();
        model.serviceDescription = shipping.getDescription();
        model.serviceEtd = shipping.getCost().get(0).getEtd();
        model.save();

        SalesOrderAddress address = new SalesOrderAddress(map.getAddress(), brand);
        address.salesOrder = model;
        address.save();

        Double subTotal = 0D;
        Double discount = 0D;
        Double priceTotal = 0D;

        List<VoucherDetail> voucherDetails = VoucherDetail.getVouchers(brand.id, map.getVouchers());

        for (Cart c: carts){
            MapCart mapCart = calculateCartItemVouchers(member, c, voucherDetails);
            Voucher.claimVoucher(member, mapCart.voucherDiscounts);
            SalesOrderDetail detail = new SalesOrderDetail();
            detail.product = c.product;
            detail.salesOrder = model;
            detail.quantity = c.qty;

            Double discountPersen = 0D;
            Double discountAmount = 0D;
            switch (c.product.discountType){
                case 1 :
                    discountAmount =  c.product.discount * detail.quantity;
                    break;
                case 2 :
                    discountPersen = c.product.discount;
                    discountAmount = (c.product.discount/100*c.product.price) * detail.quantity;
                    break;
            }

            detail.discountPersen = discountPersen;
            detail.discountAmount = discountAmount;
            detail.productName = c.product.name;
            detail.status = model.status;
            detail.price = c.product.price;
            detail.subTotal = detail.quantity * detail.price;
            detail.voucher = mapCart.totalVoucherDiscounts;
            detail.tax = 0D;
            detail.taxPrice = detail.subTotal * (detail.tax/100);
            detail.totalPrice = detail.subTotal + detail.taxPrice - detail.discountAmount - mapCart.totalVoucherDiscounts;


            subTotal += detail.subTotal;
            discount += (discountAmount + mapCart.totalVoucherDiscounts);
            priceTotal += detail.totalPrice;

            detail.save();

            c.product.itemCount = c.product.itemCount - detail.quantity;
            c.product.update();
        }

        model.subtotal = subTotal;
        model.discount = discount;
        model.totalPrice = priceTotal + model.shipping;
        Integer paymentService = Optional.ofNullable(map.getPaymentService()).orElse(PAYMENT_SERVICE_MIDTRANS);
        if (paymentService.equals(PAYMENT_SERVICE_MANUAL)) {
            model.paymentType = PAYMENT_METHOD_TRANSFER;
            model.bank = Bank.findById(map.getBankId(), brand.id);
            model.paymentService = PAYMENT_SERVICE_MANUAL;
        } else {
            model.paymentType = "";
            model.paymentService = PAYMENT_SERVICE_MIDTRANS;
        }

        model.update();

        return model.id;
    }

    private static MapCart prepareCartWithVoucher(Cart cart, List<MapCartVoucherDiscount> mapCartVoucherDiscounts, Double totalDiscount) {
        MapCart mapCart = new MapCart();

        Product product = cart.product;

        mapCart.setId(cart.id);
        mapCart.productName = product.name;
        mapCart.productPrice = product.getPrice();
        mapCart.productImage = cart.getImage();
        mapCart.total = cart.getTotal();
        mapCart.qty = cart.qty;
        mapCart.subTotal = cart.getSubTotal();
        mapCart.productDiscount = cart.getDiscount();
        mapCart.totalVoucherDiscounts = totalDiscount;

        mapCart.setVoucherDiscounts(mapCartVoucherDiscounts);

        return mapCart;
    }

    private static MapCartVoucherDiscount prepareCartVoucherDiscount(Product product, VoucherDetail vd, Double disc) {
        MapCartVoucherDiscount mapCartVoucherDiscount = new MapCartVoucherDiscount();

        mapCartVoucherDiscount.productId = product.id;
        mapCartVoucherDiscount.voucherCode = vd.code;
        mapCartVoucherDiscount.voucherDiscount = disc;

        return mapCartVoucherDiscount;
    }

    private static Double getVoucherDiscountAmount(Voucher v, Cart cart) {
        Double disc = 0D;
        switch(v.discountType) {
            case Voucher.DISCOUNT_TYPE_NOMINAL :
                disc = v.discount;
                break;
            case Voucher.DISCOUNT_TYPE_PERCENT :
                disc = cart.product.price * (v.discount / 100);
                break;
        }

        return disc;
    }

    private static boolean isMemberAssignedWithVoucher(Member actor, Voucher v) {
        if(v.assignedTo.equals(Voucher.ASSIGNED_TO_ALL)) {
            return true;
        } else if(v.assignedTo.equals(Voucher.ASSIGNED_TO_CUSTOM)) {
            List<Member> members = v.members;
            for(Member m: members) {
                if(m.id == actor.id) {
                    return true;
                }
            }
        }
        return false;
    }

    public static MapCart calculateCartItemVouchers(Member actor, Cart cart, List<VoucherDetail> voucherDetails) {
        List<MapCartVoucherDiscount> mapCartVoucherDiscounts = new ArrayList<>();
        Double totalDiscount = 0D;
        for(VoucherDetail vd: voucherDetails) {
            Voucher v = vd.voucher;

            // The validation if member is assigned or not to this voucher code
            if(isMemberAssignedWithVoucher(actor, v)) {

                // The variable to SUM the total of applied vouchers
                Double disc = 0D;

                // Check whether this voucher code is for specific product, or all products in categories
                switch (v.filterStatus) {
                    case Voucher.FILTER_STATUS_CATEGORY:
                        List<Category> vCats = v.categories;
                        if (vCats != null) {
                            for (Category cat : vCats) {
                                System.out.println("CATEGORY : " + cart.product.grandParentCategory.id + ", " + cat.id);
                                if (cart.product.grandParentCategory.id == cat.id) {
                                    disc = getVoucherDiscountAmount(v, cart);
                                    mapCartVoucherDiscounts.add(prepareCartVoucherDiscount(cart.product, vd, disc));
                                }
                            }
                        }
                        break;
                    case Voucher.FILTER_STATUS_PRODUCT:
                        List<Product> vProds = v.products;
                        if (vProds != null) {
                            for (Product prod : vProds) {
                                System.out.println("PRODUCT : " + cart.product.id + ", " + prod.id);
                                if (cart.product.id == prod.id) {
                                    disc = getVoucherDiscountAmount(v, cart);
                                    mapCartVoucherDiscounts.add(prepareCartVoucherDiscount(cart.product, vd, disc));
                                }
                            }
                        }
                        break;
                }
                totalDiscount += disc;
            }
        }

        return prepareCartWithVoucher(cart, mapCartVoucherDiscounts, totalDiscount);
    }

    public static List<MapCart> calculateVouchers(Member actor, List<Cart> carts, List<VoucherDetail> voucherDetails) {

        // The validation for can be combined voucher codes
        List<VoucherDetail> canBeCombinedVouchers = VoucherDetail.getCanBeCombinedVoucher(voucherDetails);
        List<VoucherDetail> cannotBeCombinedVouchers = VoucherDetail.getCannotBeCombinedVoucher(voucherDetails);
        List<VoucherDetail> processedVouchers = new ArrayList<>();

        if(canBeCombinedVouchers.size() > 0) {
            processedVouchers = canBeCombinedVouchers;
        } else if(cannotBeCombinedVouchers.size() > 0) {
            processedVouchers = cannotBeCombinedVouchers;
        }

        // The validation for minimum purchase amount
        Double total = Cart.sumFromCarts(carts);
        processedVouchers = VoucherDetail.checkVouchersMinimumPurchase(processedVouchers, total);

        // The process
        List<MapCart> mapCarts = new ArrayList<>();
        for (Cart cart: carts) {
            mapCarts.add(calculateCartItemVouchers(actor, cart, processedVouchers));
        }

        return mapCarts;
    }

    public MapSalesOrderList parseToMapSalesOrderList() {
        MapSalesOrderList mapSalesOrderList = new ObjectMapper().convertValue(this, MapSalesOrderList.class);
        mapSalesOrderList.mapSalesOrderAddress = new ObjectMapper().convertValue(this.salesOrderAddress, MapSalesOrderAddress.class);
        mapSalesOrderList.mapCourier = new ObjectMapper().convertValue(this.courier, MapCourier.class);
        mapSalesOrderList.mapSalesOrderDetails = new ArrayList<>();
        List<SalesOrderDetail> salesOrderDetails = this.salesOrderDetail;
        for(SalesOrderDetail sod : salesOrderDetails) {
            mapSalesOrderList.mapSalesOrderDetails.add(new ObjectMapper().convertValue(sod, MapSalesOrderDetail.class));
        }
        
        return mapSalesOrderList;
    }

    public static List<SalesOrder> salesOrderLists (String type, String status, String name, Member member, Long brandId) {
        ExpressionList<SalesOrder> qry = SalesOrder.find
                .where()
                .eq("member", member)
                .ilike("salesOrderDetail.productName", "%" + name + "%")
                .eq("t0.is_deleted", false)
                .eq("t0.brand_id", brandId);

        if (!status.isEmpty()) {
            qry.eq("status", status);
        } else if (type.equals("ACTIVE")) {
            qry.and(
                Expr.not(Expr.eq("status", SalesOrder.ORDER_STATUS_RECEIVE_BY_CUSTOMER)),
                Expr.not(Expr.eq("status", SalesOrder.ORDER_STATUS_CUSTOMER_NOT_AT_THE_ADDRESS_STATE))
            );
        } else if (type.equals("FINISH")) {
            qry.or(
                Expr.eq("status", SalesOrder.ORDER_STATUS_RECEIVE_BY_CUSTOMER),
                Expr.eq("status", SalesOrder.ORDER_STATUS_CUSTOMER_NOT_AT_THE_ADDRESS_STATE)
            );
        }

        return qry.orderBy("t0.created_at DESC")
        .findList();
    }

}
