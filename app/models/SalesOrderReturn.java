package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.enwie.util.CommonFunction;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class SalesOrderReturn extends BaseModel {
    private static final long serialVersionUID = 1L;
    public static final String TYPE_REFUND = "F";
    public static final String TYPE_REPLACED = "R";
    public static final String STATUS_PENDING = "P";
    public static final String STATUS_APPROVED = "A";
    public static final String STATUS_REJECTED = "R";
    public static final String STATUS_COMPLETED = "C";
    public static final String STATUS_ONPROGRESS = "O";

    public static Finder<Long, SalesOrderReturn> find = new Finder<>(Long.class,
            SalesOrderReturn.class);

    @JsonIgnore
    @ManyToOne
    public SalesOrder salesOrder;


    @Column(name = "return_number", unique = true, length = 30)
    @JsonProperty("return_no")
    public String returnNumber;

    @JsonIgnore
    @ManyToOne
    public Member member;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    public Date date;

    @Column(name = "document_no")
    @JsonProperty("tracking_number")
    public String documentNumber;

    @Column(name = "type", length =1)
    public String type;

    @Column(name = "status", length =1)
    public String status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "request_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    public Date requestAt;

    @JsonProperty("return_description")
    @Column(name = "description")
    public String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "schedule_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    public Date scheduleAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "send_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    public Date sendAt;

    @JsonProperty("note")
    public String note;
//
//    @Column(name = "approved_by")
//    @JsonIgnore
//    @ManyToOne
//    public UserCms approvedBy;

    @JsonIgnore
    @JoinColumn(name="user_id")
    @ManyToOne
    public UserCms userCms;

    @OneToMany(mappedBy = "salesOrderReturn")
    @JsonProperty("items")
    public List<SalesOrderReturnDetail> salesOrderReturnDetails;

    @Column(name = "odoo_id")
    public Integer odooId;

    @Column(name = "pengeluaran_odoo_id")
    public Integer pengeluaranOdooId;

    @Column(name = "approved_by")
    public String approvedBy;

    @Column(name = "rejected_by")
    public String rejectedBy;

    @Column(name = "shipped_by")
    public String shippedBy;

    @Column(name = "delivered_by")
    public String deliveredBy;

    @ManyToOne(cascade = { CascadeType.ALL })
    public Brand brand;


    public String getStatus(){
        return getStatusName();
    }

    public String getStatusName(){
        String result = "";
        switch (status){
            case STATUS_PENDING : result = "Pending";break;
            case STATUS_APPROVED : result = "Approved";break;
            case STATUS_COMPLETED : result = "Completed";break;
            case STATUS_REJECTED : result = "Rejected";break;
            case STATUS_ONPROGRESS : result = "On Progress";break;
        }
        return result;
    }

    public String getTypeName(){
        if (type == null) return "";
        String result = "";
        switch (type){
            case TYPE_REFUND : result = "Refund";break;
            case TYPE_REPLACED : result = "Replaced";break;
        }
        return result;
    }

    public String getSendDateFormated(){
        return CommonFunction.getDateTime(sendAt);
    }

    public String getRequestDateFormated(){
        return CommonFunction.getDateTime(requestAt);
    }

    public String getScheduleDateFormated(){
        return CommonFunction.getDateTime(scheduleAt);
    }

    public int getQty(){
        int qty = 0;
        for(SalesOrderReturnDetail detail : salesOrderReturnDetails){
            qty += detail.quantity;
        }
        return qty;
    }

    public static Page<SalesOrderReturn> page(Long id, int page, int pageSize, String sortBy, String order, String name) {
        ExpressionList<SalesOrderReturn> qry = SalesOrderReturn.find
                .where()
                .ilike("returnNumber", "%" + name + "%")
                .eq("t0.is_deleted", false)
                .eq("salesOrderReturnGroup.id", id);

        return
                qry.orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);

    }

    public static int findRowCount(Long id) {
        return
                find.where()
                        .eq("t0.is_deleted", false)
                        .eq("salesOrderReturnGroup.id", id)
                        .findRowCount();
    }

}
