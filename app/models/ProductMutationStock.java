package models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Date;

/**
 * @author hendriksaragih
 */
@Entity
@Table(name = "product_mutation_stock")
public class ProductMutationStock extends BaseModel {
    public static final Integer DEBIT = 1;
    public static final Integer CREDIT = -1;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    public Product product;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "distributor_id", referencedColumnName = "id")
    public UserCms distributor;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "reseller_id", referencedColumnName = "id")
    public Member reseller;

    @JsonProperty("qty")
    public Integer qty;

    @JsonProperty("type")
    public Integer type;

    @JsonProperty("description")
    public String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    public Date date;


    public static Finder<Long, ProductMutationStock> find = new Finder<Long, ProductMutationStock>(Long.class, ProductMutationStock.class);


    public static void saveStock(UserCms distributor, Product product, Integer qty, Integer type, String description, Date date) {
        ProductMutationStock data = new ProductMutationStock();
        data.distributor = distributor;
        data.product = product;
        data.qty = qty;
        data.type = type;
        data.description = description;
        data.date = date;
        data.save();
    }

    public static void saveStock(Member reseller, UserCms distributor, Product product, Integer qty, Integer type, String description, Date date) {
        ProductMutationStock data = new ProductMutationStock();
        data.reseller = reseller;
        data.distributor = distributor;
        data.product = product;
        data.qty = qty;
        data.type = type;
        data.description = description;
        data.date = date;
        data.save();
    }
}