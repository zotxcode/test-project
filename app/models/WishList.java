package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * @author hendriksaragih
 */
@Entity
public class WishList extends BaseModel{

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JsonIgnore
    public Member member;

    @ManyToOne
    public Product product;

    @ManyToOne(cascade = { CascadeType.ALL })
    public Brand brand;


    public static Finder<Long, WishList> find = new Finder<>(Long.class, WishList.class);


    public static String validation(Member member,String productDetailSKU, Long brandId) {
        ProductDetail a = ProductDetail.find.where()
                .eq("sku", productDetailSKU)
                .eq("brand_id", brandId)
                .eq("is_deleted",false).findUnique();
        if (a==null) {
            return "Product detail not found.";
        }

        return null;
    }
}