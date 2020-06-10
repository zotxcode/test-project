package models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * @author hendriksaragih
 */
@Entity
public class PurchaseOrderDetail extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JsonProperty("po_id")
    public PurchaseOrder po;

    @ManyToOne
    @JsonProperty("product_id")
    public Product product;

    public Integer qty;
    public Double price;
    @JsonProperty("sub_total")
    public Double subTotal;

    public static Finder<Long, PurchaseOrderDetail> find = new Finder<>(Long.class, PurchaseOrderDetail.class);


}
