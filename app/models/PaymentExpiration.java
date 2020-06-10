package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

/**
 * @author hendriksaragih
 */
@Entity
public class PaymentExpiration extends BaseModel{
    private static final long serialVersionUID = 1L;
    public static final boolean ACTIVE = true;
    public static final boolean INACTIVE = false;
    public static final String HOUR_TYPE = "hour";
    public static final String DAY_TYPE = "day";

    public String type;
    public int total;

    @Transient
    public String save;

    @JsonIgnore
    @JoinColumn(name = "user_id")
    @ManyToOne
    public UserCms userCms;

    @ManyToOne(cascade = { CascadeType.ALL })
    public Brand brand;


    @Transient
    public String getTypeName(){
        return type;
    }

    public static Finder<Long, PaymentExpiration> find = new Finder<>(Long.class, PaymentExpiration.class);

    public static Page<PaymentExpiration> page(int page, int pageSize, String sortBy, String order, String name, Long brandId) {
        int search = 0;
        try {
            search = Integer.parseInt(name);
        }catch (Exception e){

        }
        ExpressionList<PaymentExpiration> qry = PaymentExpiration.find
                .where()
                .eq("is_deleted", false)
                .eq("brand_id", brandId);
        if(search != 0){
            qry.eq("total", search);
        }

        return
                qry.findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);
    }

    public static Integer RowCount(Long brandId) {
        return find.where().eq("is_deleted", false).eq("brand_id", brandId).findRowCount();
    }

    public static PaymentExpiration find(Long brandId) {
        return find.where().eq("is_deleted", false).eq("brand_id", brandId).setMaxRows(1).findUnique();
    }

    public static Date getExpired(Long brandId){
        Date now = new Date();
        Calendar c = Calendar.getInstance();
        PaymentExpiration pe = PaymentExpiration.find(brandId);
        c.setTime(now);
        if (pe.type.equals(DAY_TYPE)){
            c.add(Calendar.DATE, pe.total);
        }else{
            c.add(Calendar.HOUR, pe.total);
        }
        return c.getTime();
    }

}