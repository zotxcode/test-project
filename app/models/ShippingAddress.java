package models;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import play.data.validation.ValidationError;
import play.libs.Json;

import javax.persistence.*;
import java.beans.Transient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author hendriksaragih
 */
@Entity
public class ShippingAddress extends BaseModel{
    private static final String LOG_TYPE = "ADMIN";
    private static final String LOG_TABLE_NAME = "shipping_address";

    @JsonProperty("recipient_name")
    @Column(name = "recipient_name")
    public String recipientName;

    public String phone;
    public String address;

    @JsonProperty("is_default")
    @Column(name = "is_default")
    public Boolean isDefault;

    @ManyToOne
    @JsonBackReference
    public Member member;

    @JsonIgnore
    @ManyToOne(cascade = { CascadeType.ALL })
    public Province province;

    @JsonIgnore
    @ManyToOne(cascade = { CascadeType.ALL })
    public City city;

    @JsonProperty("postal_code")
    @Column(name = "postal_code")
    public String postalCode;

    @Transient
    @JsonProperty("province_id")
    public Long getProvinceId() {
        return this.province.id;
    }

    @Transient
    @JsonProperty("city_id")
    public Long getCityId() {
        return this.city.id;
    }

    @Transient
    @JsonProperty("province_name")
    public String getProvinceName() {
        return this.province.name;
    }

    @Transient
    @JsonProperty("city_name")
    public String getCityName() {
        return this.city.name;
    }

    @Transient
    @JsonProperty("postal_code")
    public String getPostalCode() {
        return this.city.postalCode;
    }

    public static Finder<Long, ShippingAddress> find = new Finder<>(Long.class, ShippingAddress.class);

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if (recipientName == null || recipientName.isEmpty()) {
            errors.add(new ValidationError("recipient_name", "Recipient name cannot be empty."));
        }

        if (phone == null || phone.isEmpty()) {
            errors.add(new ValidationError("phone", "Phone cannot be empty."));
        }

        if (address == null || address.isEmpty()) {
            errors.add(new ValidationError("address", "Address cannot be empty."));
        }

        if (province == null) {
            errors.add(new ValidationError("province", "Province cannot be empty."));
        }

        if (city == null) {
            errors.add(new ValidationError("city", "City cannot be empty."));
        }

        // if (postalCode == null || postalCode.isEmpty()) {
        //     errors.add(new ValidationError("postal_code", "Postal Code cannot be empty."));
        // }

        if(errors.size() > 0)
            return errors;

        return null;
    }

    public static Page<ShippingAddress> page(int page, int pageSize, String sortBy, String order, String filter) {
        return
                find.where()
                        .ilike("recipient_name", "%" + filter + "%")
                        .ilike("phone", "%" + filter + "%")
                        .ilike("address", "%" + filter + "%")
                        .eq("is_deleted", false)
                        .orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);
    }

    public static List<ShippingAddress> getAllData() {
        return find.where().eq("is_deleted", false).orderBy("id ASC").findList();
    }

    public static List<ShippingAddress> getAllData(Long memberId) {
        return find.where().eq("member_id", memberId).eq("is_deleted", false).orderBy("id ASC").findList();
    }

    public static Integer RowCount() {
        return find.where()
                .eq("is_deleted", false)
                .findRowCount();
    }

    public static ShippingAddress findById(Long id) {
        return find.where().eq("is_deleted", false).eq("id", id).findUnique();
    }
    
}