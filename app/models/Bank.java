package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.enwie.util.Constant;

import javax.persistence.*;
import java.util.List;

/**
 * @author hendriksaragih
 */
@Entity
public class Bank extends BaseModel {

    private static final long serialVersionUID = 1L;
    public static final boolean ACTIVE = true;
    public static final boolean INACTIVE = false;

    @JsonProperty("bank_name")
    public String bankName;
    @JsonProperty("account_name")
    public String accountName;
    @JsonProperty("account_number")
    public String accountNumber;
    public String description;
    @JsonProperty("image_name")
    @Column(name = "image_name", columnDefinition = "TEXT")
    public String imageName;
    @JsonProperty("image_keyword")
    public String imageKeyword;
    @JsonProperty("image_title")
    public String imageTitle;
    @JsonProperty("image_description")
    @Column(name = "image_description", columnDefinition = "TEXT")
    public String imageDescription;
    @JsonProperty("image_url")
    public String imageUrl;
    public boolean status;
    @Column(name = "odoo_id")
    public Integer odooId;
    @Column(name = "partner_bank_id")
    public Integer partnerBankId;
    @Column(name = "account_journal_id")
    public Integer accoountJournalId;
    @Transient
    public String save;
    @ManyToOne(cascade = { CascadeType.ALL })
    public Brand brand;

    @Transient
    public String imageLink;

    @Transient
    public String getStatus() {
        String statusName = "";
        if(status)
            statusName = "Active";
        else statusName = "Inactive";

        return statusName;
    }

    @JsonIgnore
    @JoinColumn(name = "user_id")
    @ManyToOne
    public UserCms userCms;

    public String getImageLink(){
        return imageUrl==null || imageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl;
    }

    public String getImageUrl(){
        return getImageLink();
    }

    public static Finder<Long, Bank> find = new Finder<>(Long.class, Bank.class);

    public static Page<Bank> page(int page, int pageSize, String sortBy, String order, String name, Integer filter, Long brandId) {
        ExpressionList<Bank> qry = Bank.find
                .where()
                .ilike("bankName", "%" + name + "%")
                .eq("brand_id", brandId)
                .eq("is_deleted", false);

        switch (filter){
            case 1: qry.eq("status", true);
                break;
            case 2: qry.eq("status", false);
                break;
        }

        return
                qry.orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);
    }

    public static int findRowCount(Long brandId) {
        return
                find.where()
                        .eq("is_deleted", false)
                        .eq("brand_id", brandId)
                        .findRowCount();
    }

    public static List<Bank> getHomePage(Long brandId) {
        return Bank.find.where()
                .eq("is_deleted", false)
                .eq("status", true)
                .eq("brand_id", brandId)
                .setMaxRows(10).findList();
    }

    public static Bank findById(Long id, Long brandId) {
        return find.where().eq("is_deleted", false).eq("id", id).eq("brand_id", brandId).findUnique();
    }

    public void updateStatus(String newStatus) {
        if(newStatus.equals("active"))
            status = Bank.ACTIVE;
        else if(newStatus.equals("inactive"))
            status = Bank.INACTIVE;
        super.update();

    }
}