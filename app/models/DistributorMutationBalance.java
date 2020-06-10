package models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;
import java.util.Optional;

/**
 * @author hendriksaragih
 */
@Entity
@Table(name = "distributor_mutation_balance")
public class DistributorMutationBalance extends BaseModel {

    public Integer type;
    public Double value;
    public String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Jakarta")
    public Date date;

    @ManyToOne
    @JoinColumn(name = "user_cms_id", referencedColumnName = "id")
    @JsonIgnore
    public UserCms distributor;

    public static Finder<Long, DistributorMutationBalance> find = new Finder<Long, DistributorMutationBalance>(Long.class, DistributorMutationBalance.class);

    public DistributorMutationBalance(){

    }

    public static void saveBalance(UserCms distributor, Product product, Double price, Integer qty, Integer type, String description, Date date) {
        DistributorMutationBalance data = new DistributorMutationBalance();
        data.date = date;
        data.description = description;
        data.distributor = distributor;
        data.type = type;
        data.value = 0D;
        ProductProfit profit = new ProductProfit().find.where().eq("type", ProductProfit.typeDistributor)
                .eq("is_deleted", false).eq("product", product).findUnique();
        if (profit != null) {
            Double value = Optional.ofNullable(profit.value).orElse(0D);
            Double percent = Optional.ofNullable(profit.percentage).orElse(0D);
            if (percent > 0) {
                data.value = (percent/100*price) * qty;
            } else {
                data.value = value * qty;
            }
        }
        data.save();
    }

}
