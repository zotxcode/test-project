package models;


import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
/**
 * @author hendriksaragih
 */
@Entity
public class Newsletters extends BaseModel {
	@JsonProperty("title")
    public String title;
	
	@JsonProperty("contents")
    public String contents;
		
	@Transient
    public String save;
	
	public boolean status;
	
	@ManyToOne(cascade = { CascadeType.ALL })
    public Brand brand;

    public static Finder<Long, Newsletters> find = new Finder<>(Long.class, Newsletters.class);

    
    public static Newsletters find(Long brandId) {
        return find.where().eq("is_deleted", false).eq("brand_id", brandId).setMaxRows(1).findUnique();
    }
	
	 public static int findRowCount(Long brandId) {
        return
                find.where()
                        .eq("is_deleted", false)
                        .eq("brand_id", brandId)
                        .findRowCount();
    }
	
	public static Page<Newsletters> page(int page, int pageSize, String sortBy, String order, String name, Integer filter, Long brandId) {
        ExpressionList<Newsletters> qry = Newsletters.find
                .where()
                .ilike("title", "%" + name + "%")
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
	
	@Transient
    public String getStatus() {
        String statusName = "";
        if(status)
            statusName = "Active";
        else statusName = "Inactive";

        return statusName;
    }
	
	public static Newsletters findById(Long id, Long brandId) {
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