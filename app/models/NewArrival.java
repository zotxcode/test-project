package models;

import com.avaje.ebean.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import play.libs.Json;

import javax.persistence.*;
import java.beans.Transient;
import java.util.*;


/**
 * @author hendriksaragih
 */
@Entity
public class NewArrival extends BaseModel{
    private static final long serialVersionUID = 1L;
    public static final boolean ACTIVE = true;
    public static final boolean INACTIVE = false;
    private static final String LOG_TYPE = "ADMIN";
    private static final String LOG_TABLE_NAME = "new_arrival";

    public boolean status;
    public int sequence;

    @ManyToOne(cascade = { CascadeType.ALL })
    public Brand brand;

    @ManyToOne(cascade = { CascadeType.ALL })
    public Product product;

    @JsonIgnore
    @JoinColumn(name="user_id")
    @ManyToOne
    public UserCms userCms;

    @javax.persistence.Transient
    public String save;

    @javax.persistence.Transient
    public List<String> category_list;

    @javax.persistence.Transient
    public List<String> subcategory_list;

    @javax.persistence.Transient
    public List<String> product_list;

    @Transient
    @JsonProperty("user_creator")
    public String getUserCreator(){
        return userCms.email;
    }


    @Transient
    public String getStatus() {
        String statusName = "";
        if(status)
            statusName = "Active";
        else statusName = "Inactive";

        return statusName;
    }

    public static Finder<Long, NewArrival> find = new Finder<Long, NewArrival>(Long.class, NewArrival.class);

    public static int getNextSequence(Long brandId){
        SqlQuery sqlQuery = Ebean.createSqlQuery(
                "select max(sequence) as max from new_arrival where status = true and brand_id = :brandId");
        sqlQuery.setParameter("brandId", brandId);
        SqlRow result = sqlQuery.findUnique();
        return (result.getInteger("max")==null ? 0 : result.getInteger("max"))+1;
    }

    public static Page<NewArrival> page(int page, int pageSize, String sortBy, String order, String name, Integer filter, Brand brand) {
        ExpressionList<NewArrival> qry = NewArrival.find
                .where()
                .ilike("product.name", "%" + name + "%")
                .eq("t0.is_deleted", false)
                .eq("brand", brand);

        switch (filter){
            case 3: qry.eq("status", true);
                break;
            case 4: qry.eq("status", false);
                break;
        }

        return
                qry.orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);

    }

    public static int findRowCount(Brand brand) {
        return
                find.where()
                        .eq("is_deleted", false)
                        .eq("brand", brand)
                        .findRowCount();
    }

    public static NewArrival findByProduct(Product product, Brand brand) {
        return
                find.where()
                        .eq("is_deleted", false)
                        .eq("brand", brand)
                        .eq("product", product)
                        .findUnique();
    }

    public String getChangeLogData(NewArrival data){
        HashMap<String, String> map = new HashMap<>();
        map.put("product",(data.product == null)? "":data.product.name);
        map.put("status",(data.status)? "Active":"Inactive");
        
        return Json.toJson(map).toString();
    }

    @Override
    public void save() {
        super.save();
        ChangeLog changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "ADD", null, getChangeLogData(this));
        changeLog.save();
    }

    @Override
    public void update() {
        NewArrival oldBanner = NewArrival.find.byId(id);
        super.update();

        ChangeLog changeLog;
        if(isDeleted){
            changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "DELETE", getChangeLogData(oldBanner), null);
        }else{
            changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", getChangeLogData(oldBanner), getChangeLogData(this));
        }
        changeLog.save();

    }

    public void updateStatus(String newStatus) {
        String oldBannerData = getChangeLogData(this);

        if(newStatus.equals("active"))
            status = NewArrival.ACTIVE;
        else if(newStatus.equals("inactive"))
            status = NewArrival.INACTIVE;

        super.update();

        ChangeLog changeLog;
        changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", oldBannerData, getChangeLogData(this));
        changeLog.save();

    }

    public void updateSequence(int seq) {
        sequence = seq;
        super.update();
    }

    public static NewArrival findById(Long id, Long brandId) {
        return find.where().eq("is_deleted", false).eq("id", id).eq("brand_id", brandId).findUnique();
    }

    public static List<Product> getAllData(Long brandId, Integer limit) {
        List<NewArrival> data = find.where()
                .eq("is_deleted", false)
                .eq("brand_id", brandId)
                .eq("status", true)
                .setMaxRows(limit)
                .order("sequence asc").findList();
        List<Product> products = new LinkedList<>();
        for (NewArrival dt : data){
            products.add(dt.product);
        }

        return products;
    }
}