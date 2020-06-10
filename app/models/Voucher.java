package models;

import com.avaje.ebean.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.enwie.mapper.response.MapCartVoucherDiscount;
import com.enwie.util.CommonFunction;

import javax.persistence.*;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;

/**
 * Created by nugraha on 5/18/17.
 */
@Entity
public class Voucher extends BaseModel {
    private static final long serialVersionUID = 1L;
    public static final String TYPE_DISCOUNT = "DISCOUNT";
    public static final String TYPE_FREE_DELIVERY = "FREE DELIVERY";
    public static final int DISCOUNT_TYPE_NOMINAL = 1;
    public static final int DISCOUNT_TYPE_PERCENT = 2;
    public static final String FILTER_STATUS_ALL = "A";
    public static final String FILTER_STATUS_PRODUCT = "P";
    public static final String FILTER_STATUS_CATEGORY = "C";
    public static final String ASSIGNED_TO_ALL = "A";
    public static final String ASSIGNED_TO_CUSTOM = "C";

    public static Finder<Long, Voucher> find = new Finder<>(Long.class, Voucher.class);

//    @Column(unique = true)
    public String name;
    public String description;

//    @Column(unique = true)
    public String masking;

    public String type;

    public boolean status;

    public Double discount;

    @Column(name = "discount_type", columnDefinition = "integer default 0")
    @JsonProperty("discount_type")
    public int discountType;

    public int count;

    @JsonProperty("max_value")
    public Double maxValue;

    @JsonProperty("min_purchase")
    public Double minPurchase;

    public int priority;

    @JsonProperty("stop_further_rule_porcessing")
    public int stopFurtherRulePorcessing;

    @JsonProperty("can_be_combined")
    public Integer canBeCombined;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="Asia/Jakarta")
    @JsonProperty("valid_from")
    public Date validFrom;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="Asia/Jakarta")
    @JsonProperty("valid_to")
    public Date validTo;

    @JsonProperty("filter_status")
    public String filterStatus;

//    @JsonIgnore
//    @ManyToMany
//    public List<Brand> brands;
    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "brand_id", referencedColumnName = "id")
    public Brand brand;

    @JsonIgnore
    @ManyToMany
    public List<Category> categories;

    @JsonIgnore
    @ManyToMany
    public List<Product> products;

    @JsonProperty("assigned_to")
    public String assignedTo;

    @JsonIgnore
    @ManyToMany
    public List<Member> members;

    @JsonIgnore
    @JoinColumn(name="created_by")
    @ManyToOne
    public UserCms createdBy;

    @JsonIgnore
    @JoinColumn(name="updated_by")
    @ManyToOne
    public UserCms updatedBy;

    @OneToMany(mappedBy = "voucher")
    @JsonProperty("details")
    public List<VoucherDetail> voucherDetail;

    @Transient
    public String save;

    @Transient
    public List<String> merchant_list;

    @Transient
    public List<String> brand_list;

    @Transient
    public List<String> category_list;

    @Transient
    public List<String> subcategory_list;

    @Transient
    public List<String> product_list;

    @Transient
    public List<String> member_list;

    @Transient
    public String fromDate = "";

    @Transient
    public String toDate = "";

    @Transient
    public String fromTime = "";

    @Transient
    public String toTime = "";

    public static Page<Voucher> page(int page, int pageSize, String sortBy, String order, String name, Long brandId) {
        ExpressionList<Voucher> qry = Voucher.find
                .where()
                .ilike("name", "%" + name + "%")
                .eq("brand_id", brandId)
                .eq("t0.is_deleted", false);

//        if(!filter.equals("")){
//            qry.eq("t0.status", filter);
//        }

        return
                qry.orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);

    }

    public static Voucher findById(Long id, Long brandId) {
        return find.where().eq("is_deleted", false).eq("id", id).eq("brand_id", brandId).findUnique();
    }

    public static int findRowCount() {
        return
                find.where()
                        .eq("t0.is_deleted", false)
                        .findRowCount();
    }

    public String getDiscountFormat(){
        if (discountType == 1){
            return CommonFunction.numberFormat(discount);
        }else{
            return CommonFunction.discountFormat(discount);
        }
    }

    public String getMaxValueFormat(){
        return CommonFunction.numberFormat(maxValue);
    }
    public String getMinPurchaseFormat(){
        return CommonFunction.numberFormat(minPurchase);
    }

    public String getTypeView(){
        return (type.equals(Voucher.TYPE_DISCOUNT))? "Discount":"Free Delivery";
    }

    public String getFilterStatusView(){
        String result = "";
        switch (filterStatus){
            case FILTER_STATUS_ALL : result = "All";break;
            case FILTER_STATUS_PRODUCT : result = "Product";break;
            case FILTER_STATUS_CATEGORY : result = "Category";break;
        }
        return result;
    }

    public String getAssignedToView(){
        String result = "";
        switch (assignedTo){
            case ASSIGNED_TO_ALL : result = "All";break;
            case ASSIGNED_TO_CUSTOM : result = "Custom";break;
        }
        return result;
    }

    public String getType(){
        return type;
    }

    public String getValidFrom(){
        return CommonFunction.getDateTime2(validFrom);
    }

    public String getValidTo(){
        return CommonFunction.getDateTime2(validTo);
    }

    public String getStopFurther(){
        return stopFurtherRulePorcessing == 1 ? "Yes" : "No";
    }

    public static String[] generateCode(int length){
        String[] result = new String[length];
        for(int i = 0; i < length; i++){
            result[i] = generateCode();
        }
        return result;
    }

    public static String generateCode(){
        return getRandomString("CHAR", 4)+"-"+getRandomString("NUMBERS", 4)+"-"+getRandomString("CHAR", 4);
    }

    private static String getRandomString(String type, int length){
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numbers = "0123456789";
        SecureRandom rnd = new SecureRandom();

        String tmp;
        if(type.equals("CHAR")){
            tmp = chars;
        }else tmp = numbers;

        StringBuilder sb = new StringBuilder( length );
        for( int i = 0; i < length; i++ )
            sb.append( tmp.charAt( rnd.nextInt(tmp.length()) ) );
        return sb.toString();
    }

    public void updateStatus(String newStatus) {
//        String oldBannerData = getChangeLogData(this);

        if(newStatus.equals("active"))
            status = Banner.ACTIVE;
        else if(newStatus.equals("inactive"))
            status = Banner.INACTIVE;

        super.update();

//        ChangeLog changeLog;
//        changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", oldBannerData, getChangeLogData(this));
//        changeLog.save();

    }

    public static void claimVoucher(Member member, List<MapCartVoucherDiscount> voucherDetails){
        for(MapCartVoucherDiscount mvd : voucherDetails) {
            VoucherDetail vd = VoucherDetail.find.where()
                    .eq("code", mvd.voucherCode)
                    .findUnique();
            vd.status = 1;
            vd.member = member;
            vd.usedAt = new Date();
            vd.update();
        }
    }
}
