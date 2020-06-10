package models;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.beans.Transient;
import java.util.List;

/**
 * @author hendriksaragih
 */
@Entity
public class ProductGroup extends BaseModel{
    private static final long serialVersionUID = 1L;
    private static final String LOG_TYPE = "ADMIN";
    private static final String LOG_TABLE_NAME = "product_group";

    public String name;

    @JsonIgnore
    @JoinColumn(name="lowest_price_product")
    @ManyToOne
    public Product lowestPriceProduct;

    @JsonIgnore
    @JoinColumn(name="user_id")
    @ManyToOne
    public UserCms userCms;

    @javax.persistence.Transient
    public String save;

    @javax.persistence.Transient
    public List<String> product_list;

    @ManyToOne(cascade = { CascadeType.ALL })
    public Brand brand;

    @Transient
    @JsonProperty("user_creator")
    public String getUserCreator(){
        return userCms.email;
    }

    public static Finder<Long, ProductGroup> find = new Finder<Long, ProductGroup>(Long.class, ProductGroup.class);

    public static Page<ProductGroup> page(int page, int pageSize, String sortBy, String order, String filter, Long brandId) {
        return
                find.where()
                        .ilike("name", "%" + filter + "%")
                        .eq("is_deleted", false)
                        .eq("brand_id", brandId)
                        .orderBy(sortBy + " " + order)
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

}