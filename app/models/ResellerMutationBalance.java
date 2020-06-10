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
@Table(name = "member_mutation_balance")
public class ResellerMutationBalance extends BaseModel {
    public static final Integer DEBIT = 1;
    public static final Integer CREDIT = -1;

    public Integer type;
    public Double value;
    public String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Jakarta")
    public Date date;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    @JsonIgnore
    public Member reseller;

    public static Finder<Long, ResellerMutationBalance> find = new Finder<Long, ResellerMutationBalance>(Long.class, ResellerMutationBalance.class);

    public ResellerMutationBalance(){

    }

    public static void saveBalance(Member reseller, Product product, Double price, Integer qty, Integer type, String description, Date date) {
        ResellerMutationBalance data = new ResellerMutationBalance();
        data.date = date;
        data.description = description;
        data.reseller = reseller;
        data.type = type;
        data.value = 0D;
        ProductProfit profit = new ProductProfit().find.where().eq("type", ProductProfit.typeReseller)
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
