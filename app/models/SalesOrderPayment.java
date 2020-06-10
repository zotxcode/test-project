package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.enwie.util.CommonFunction;
import com.enwie.util.Constant;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class SalesOrderPayment extends BaseModel {
    private static final long serialVersionUID = 1L;
    public static final String VERIFY = "V";
    public static final String PAYMENT_VERIFY = "P";
    public static final String PAYMENT_REJECT = "R";
    public static final String COD_VERIFY = "COD";

    public static Finder<Long, SalesOrderPayment> find = new Finder<>(Long.class,
            SalesOrderPayment.class);

    @JsonIgnore
    @OneToOne(cascade = { CascadeType.ALL })
    public SalesOrder salesOrder;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "confirm_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    public Date confirmAt;

    @Column(name = "confirm_by")
    @JsonIgnore
    @ManyToOne
    public UserCms confirmBy;

    @JsonProperty("total_transfer")
    public Double totalTransfer;

    @Column(unique = true)
    @JsonProperty("invoice_no")
    public String invoiceNo;

    @JsonProperty("debit_acc_name")
    public String debitAccountName;

    @JsonProperty("debit_acc_no")
    public String debitAccountNumber;

    @JsonProperty("image_url")
    public String imageUrl;

    @JsonProperty("comments")
    public String comments;

    public String status;

    @ManyToOne(cascade = { CascadeType.ALL })
    public Brand brand;

    public String getImageLink(){
        return imageUrl==null || imageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl;
    }

    public String getConfirmAt(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");;
        return confirmAt!=null? simpleDateFormat.format(confirmAt):"";
    }

    public String getBank(){
        return salesOrder.bank != null ? salesOrder.bank.bankName+ " ("+salesOrder.bank.accountName+")" : "";
    }

    public String getTotalFormat(){
        return CommonFunction.numberFormat(totalTransfer==null ? 0D : totalTransfer);
    }

    public boolean isAllowApproved(){
        return (status.equals(PAYMENT_VERIFY) || status.equals(COD_VERIFY)) && !salesOrder.status.equals(SalesOrder.ORDER_STATUS_EXPIRE_PAYMENT);
    }

    @Transient
    public String getStrStatus(){
        String result = "";
        switch (status){
            case VERIFY : result = "Verify";break;
            case PAYMENT_VERIFY :
                result = salesOrder.status.equals(SalesOrder.ORDER_STATUS_EXPIRE_PAYMENT) ? "Expired" : "Payment Verify";
                break;
            case COD_VERIFY :
                result = salesOrder.status.equals(SalesOrder.ORDER_STATUS_EXPIRE_PAYMENT) ? "Expired" : "COD Verify";
                break;
            case PAYMENT_REJECT : result = "Rejected";break;
            case SalesOrder.ORDER_STATUS_EXPIRE_PAYMENT : result = "Expired";break;
            default: status = "Invalid Status";
        }

        return result;
    }

    public static Page<SalesOrderPayment> page(int page, int pageSize, String sortBy, String order, String name, String filter) {
        ExpressionList<SalesOrderPayment> qry = SalesOrderPayment.find
                .where()
                .ilike("invoiceNo", "%" + name + "%")
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

    public static String generateInvoiceCode(Long brandId){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMM");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM");
        SalesOrderPayment so = SalesOrderPayment.find
                .where("t0.created_at > '"+simpleDateFormat2.format(new Date())+"-01 00:00:00' and invoice_no is not null AND t0.brand_id = "+brandId)
                .order("t0.created_at desc").setMaxRows(1).findUnique();
        String seqNum = "";
        if(so == null){
            seqNum = "00001";
        }else{
            seqNum = so.invoiceNo.substring(so.invoiceNo.length() - 5);
            int seq = Integer.parseInt(seqNum)+1;
            seqNum = "00000" + String.valueOf(seq);
            seqNum = seqNum.substring(seqNum.length() - 5);
        }
        String code = (brandId == 1L) ? "EIV" : "MIV";
        code += simpleDateFormat.format(new Date()) + seqNum;
        return code;
    }

}
