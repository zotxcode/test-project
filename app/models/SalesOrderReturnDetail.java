package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by nugraha on 6/3/17.
 */
@Entity
public class SalesOrderReturnDetail extends BaseModel {
    private static final long serialVersionUID = 1L;

    public static Finder<Long, SalesOrderReturnDetail> find = new Finder<>(Long.class,
            SalesOrderReturnDetail.class);

    @JsonIgnore
    @ManyToOne
    public SalesOrderReturn salesOrderReturn;

    @JsonIgnore
    @ManyToOne
    public Product product;

    @ManyToOne(cascade = { CascadeType.ALL })
    public Brand brand;

    public int quantity;

    @javax.persistence.Transient
    @JsonProperty("product_id")
    public Long getProductId(){
        return product.id;
    }

    @javax.persistence.Transient
    @JsonProperty("product_name")
    public String getProductName(){
        return product.name;
    }

    @javax.persistence.Transient
    @JsonProperty("status")
    public String getStatus(){
        return salesOrderReturn.getStatusName();
    }

}
