package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author hendriksaragih
 */
@Entity
public class PurchaseOrder extends BaseModel {

    private static final long serialVersionUID = 1L;
    public static final int DRAFT = 0;
    public static final int SENT = 1;
    public static final int APPROVED = 2;
    public static final int RECEIVED = 3;
    public static final int COMPLETED = 4;
    public static final int REJECTED = 5;
    public static final int CANCELED = 6;

    @Column(unique = true)
    public String code;
    public Double total;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "received_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    public Date receivedAt;

    public int status;

    public String information;

    @OneToMany(mappedBy = "po")
    public List<PurchaseOrderDetail> details;

    @ManyToOne
    @JsonProperty("distributor")
    public UserCms distributor;

    @ManyToOne
    @JsonProperty("reseller")
    public Member reseller;

    @JsonIgnore
    @JoinColumn(name="user_id")
    @ManyToOne
    public UserCms userCms;

    @Transient
    public String save;

    @Transient
    public String receivedDate;

    @Transient
    public String productName;

    @Transient
    public List<String> qty;

    @Transient
    public List<String> price;

    @Transient
    public List<String> ids;

    @Transient
    public String getStatus() {
        String statusName = "";
        switch (status){
            case 0 : statusName = "Draft";break;
            case 1 : statusName = "Sent";break;
            case 2 : statusName = "Approved";break;
            case 3 : statusName = "Received";break;
            case 4 : statusName = "Completed";break;
            case 5 : statusName = "Rejected";break;
            case 6 : statusName = "Canceled";break;
        }

        return statusName;
    }

    public String generatePOCode(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        PurchaseOrder po = PurchaseOrder.find.where("created_at > '"+simpleDateFormat2.format(new Date())+" 00:00:00'")
                .order("created_at desc").setMaxRows(1).findUnique();
        String seqNum = "";
        if(po == null){
            seqNum = "000001";
        }else{
            seqNum = po.code.substring(po.code.length() - 6);
            int seq = Integer.parseInt(seqNum)+1;
            seqNum = "000000" + String.valueOf(seq);
            seqNum = seqNum.substring(seqNum.length() - 6);
        }
        String code = "PO";
        code += simpleDateFormat.format(new Date()) + seqNum;
        return code;
    }

    public static Finder<Long, PurchaseOrder> find = new Finder<>(Long.class, PurchaseOrder.class);


    public static Page<PurchaseOrder> page(int page, int pageSize, String sortBy, String order, String name, UserCms distributor) {
        ExpressionList<PurchaseOrder> qry = PurchaseOrder.find
                .where()
                .ilike("code", "%" + name + "%")
                .eq("is_deleted", false);
        if (distributor != null){
            qry.eq("distributor", distributor).isNull("reseller");
        }else {
            qry.isNull("reseller");
        }

        return
                qry.orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);
    }
    public static Page<PurchaseOrder> pageReseller(int page, int pageSize, String sortBy, String order, String name, UserCms distributor) {
        ExpressionList<PurchaseOrder> qry = PurchaseOrder.find
                .where()
                .ilike("code", "%" + name + "%")
                .eq("is_deleted", false);
        if (distributor != null){
            qry.eq("distributor", distributor).isNotNull("reseller");
        }

        return
                qry.orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);
    }
    
    public static Page<PurchaseOrder> pageMerchant(int page, int pageSize, String sortBy, String order, String name, Member reseller) {
        ExpressionList<PurchaseOrder> qry = PurchaseOrder.find
                .where()
                .ilike("code", "%" + name + "%")
                .eq("is_deleted", false)
                .eq("reseller", reseller);

        return
                qry.orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);
    }

    public static int findRowCount() {
        return
                find.where()
                        .eq("is_deleted", false)
                        .findRowCount();
    }

    public void updateStatus(int newStatus) {
        this.status = newStatus;
        super.update();
    }

}