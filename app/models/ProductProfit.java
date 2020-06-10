package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "product_profit")
public class ProductProfit extends BaseModel {
    public static final String typeNameSS = "Support System";
    public static final String typeNameD = "Distributor";
    public static final String typeNameSD = "Sub Distributor";
    public static final String typeNameR = "Reseller";
    public static final Integer typeSupSystem = 0;
    public static final Integer typeDistributor = 1;
    public static final Integer typeSubDistributor = 2;
    public static final Integer typeReseller = 3;
    public static final String[] typeNames = {typeNameSS, typeNameD, typeNameSD, typeNameR};

    public Integer type;
    public Double percentage;
    public Double value;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    @JsonIgnore
    public Product product;

    public static Finder<Long, ProductProfit> find = new Finder<Long, ProductProfit>(Long.class, ProductProfit.class);

    public ProductProfit(){

    }
}
