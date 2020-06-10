package models;

import com.avaje.ebean.ExpressionList;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

/**
 * @author hendriksaragih
 */
@Entity
@Table(name = "product_stock")
public class ProductStock extends BaseModel {

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    public Product product;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "distributor_id", referencedColumnName = "id")
    public UserCms distributor;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "reseller_id", referencedColumnName = "id")
    public Member reseller;

    @JsonProperty("stock")
    public Long stock;


    public static Finder<Long, ProductStock> find = new Finder<Long, ProductStock>(Long.class, ProductStock.class);

    public static ProductStock getProductDistributor(UserCms distributor, Product product) {
        ExpressionList<ProductStock> qry = ProductStock.find
                .where()
                .isNull("reseller")
                .eq("product", product)
                .eq("distributor", distributor);

        return qry.setMaxRows(1).findUnique();
    }

    public static ProductStock getProductReseller(Member reseller, Product product) {
        ExpressionList<ProductStock> qry = ProductStock.find
                .where()
                .eq("product", product)
                .eq("reseller", reseller);

        return qry.setMaxRows(1).findUnique();
    }
}