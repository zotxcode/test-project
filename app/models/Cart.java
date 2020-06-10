package models;

import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.avaje.ebean.annotation.CreatedTimestamp;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by hendriksaragih on 4/26/17.
 */
@Entity
public class Cart extends BaseModel {
    private static final long serialVersionUID = 1L;


    public static Finder<Long, Cart> find = new Finder<>(Long.class, Cart.class);

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    @JsonProperty("order_date")
    public Date orderDate;

    @Column(unique = true)
    @JsonProperty("cart_number")
    public String cartNumber;

    @JsonIgnore
    @ManyToOne
    public Member member;

    public Integer qty;

    @ManyToOne
    public Brand brand;

    @ManyToOne
    public Product product;

    @javax.persistence.Transient
    @JsonProperty("product_id")
    public Long getProductId(){
        return product.id;
    }

    @javax.persistence.Transient
    @JsonProperty("product_name")
    public String getName(){
        return product.name;
    }

    @javax.persistence.Transient
    @JsonProperty("product_image")
    public String getImage(){
        return product.getImageUrl();
    }

    @javax.persistence.Transient
    @JsonProperty("product_price")
    public Double getPrice(){
        return product.getPrice();
    }

    @javax.persistence.Transient
    @JsonProperty("product_discount")
    public Double getDiscount(){
        return product.getPrice() - product.getPriceDisplay();
    }

    @javax.persistence.Transient
    @JsonProperty("sub_total")
    public Double getSubTotal(){
        return product.getPrice() * qty;
    }

    @javax.persistence.Transient
    @JsonProperty("total")
    public Double getTotal(){
        return product.getPriceDisplay() * qty;
    }

    public static List<Cart> getCarts(Long memberId, Long brandId) {
        List<Cart> carts = Cart.find.where()
                .eq("member_id", memberId)
                .eq("is_deleted", false)
                .eq("brand_id", brandId)
                .findList();

        return carts;
    }

    public static Double sumFromCarts(List<Cart> carts) {
        Double total = 0D;
        for(Cart cart: carts) {
            total += (cart.getSubTotal());
        }
        return total;
    }
}
