package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.enwie.util.CommonFunction;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 * Created by hendriksaragih on 4/26/17.
 */
@Entity
public class SalesOrderDetail extends BaseModel {
    private static final long serialVersionUID = 1L;

    public static Finder<Long, SalesOrderDetail> find = new Finder<>(Long.class,
            SalesOrderDetail.class);

    @JsonIgnore
    @ManyToOne
    public Product product;

    public String status;

    @JsonIgnore
    @ManyToOne(cascade = { CascadeType.ALL })
    public SalesOrder salesOrder;

    @JsonProperty("product_name")
    public String productName;

    public double price;
    @JsonProperty("quantity")
    public int quantity;
    @JsonProperty("discount_persen")
    public Double discountPersen;
    @JsonProperty("discount_amount")
    public Double discountAmount;
    @JsonProperty("sub_total")
    public double subTotal;
    @JsonProperty("total_price")
    public double totalPrice;
    public double tax;
    @JsonProperty("tax_price")
    public double taxPrice;
    @JsonProperty("voucher")
    public Double voucher;

//    @Column(name = "fashion_size")
//    @JsonProperty("size")
//    public String sizeName;

    @ManyToOne(cascade = { CascadeType.ALL })
    public Brand brand;
//
//    @JsonIgnore
//    @ManyToOne
//    public Size fashionSize;
//
//    @JsonIgnore
//    @ManyToMany
//    public List<VoucherDetail> voucherDetails;

    @javax.persistence.Transient
    @JsonProperty("name")
    public String getName(){
        return productName;
    }
    @javax.persistence.Transient
    @JsonProperty("sku")
    public String getSku(){
        return product.sku;
    }
    @javax.persistence.Transient
    @JsonProperty("image_url")
    public String getImageUrl(){
        return getImage();
    }
    @javax.persistence.Transient
    @JsonProperty("product_id")
    public Long getProductId(){
        return product.id;
    }

    @javax.persistence.Transient
    @JsonProperty("currency")
    public String getCurrency(){
        return "IDR";
    }

    @Transient
    @JsonProperty("total")
    public Double getTotal(){
        return subTotal;
    }

    public String getStatus(){
        String result = "";
        switch (status){
            case SalesOrder.ORDER_STATUS_VERIFY : result = "Order Verified";break;
            case SalesOrder.ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION : result = "Waiting Payment Confirmation";break;
            case SalesOrder.ORDER_STATUS_EXPIRE_PAYMENT : result = "Expire Payment";break;
            case SalesOrder.ORDER_STATUS_PICKING : result = "Picking";break;
            case SalesOrder.ORDER_STATUS_PACKING : result = "Packing";break;
            case SalesOrder.ORDER_STATUS_ON_DELIVERY : result = "On Delivery";break;
            case SalesOrder.ORDER_STATUS_RECEIVE_BY_CUSTOMER : result = "Received By Customer";break;
            case SalesOrder.ORDER_STATUS_CUSTOMER_NOT_AT_THE_ADDRESS_STATE : result = "Customer Not At The Address State";break;
            case SalesOrder.ORDER_STATUS_CANCEL : result = "Cancel";break;
            case SalesOrder.ORDER_STATUS_RETURN : result = "Return";break;
            case SalesOrder.ORDER_STATUS_REPLACED : result = "Replaced";break;
            case SalesOrder.ORDER_STATUS_CANCEL_BY_CUSTOMER_SERVICE : result = "Cancel By Customer Service";break;
            default: result = "Invalid Status";
        }
        return result;
    }

    public String getImage(){
        if (product != null){
            return product.getThumbnailUrl();
        }
        return "";
    }

    public String getPriceString(){
        return CommonFunction.currencyFormat(price, "Rp");
    }

    public String getColor(){
        String color = null;
        Set<BaseAttribute> baseAttributes = product.baseAttributes;
        List<Attribute> attributes = product.attributes;
        for(Attribute att : attributes){
            if(att.baseAttribute != null && att.baseAttribute.name.equalsIgnoreCase("color")){
                color = att.getName();
            }
        }
        return color;
    }

    public String getProductNameFull(){
//        String name = productName;
//        String color = getColor();
//        if (!color.isEmpty()){
//            name = name.concat(" (Color : ").concat(color).concat(")");
//        }
//        if (sizeName != null && !sizeName.isEmpty()){
//            name = name.concat(" (Size : ").concat(sizeName).concat(")");
//        }
        return productName;
    }
}
