package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.enwie.mapper.request.MapAddress;

import javax.persistence.*;

/**
 * @author hendriksaragih
 */
@Entity
public class SalesOrderAddress extends BaseModel {

    private static final long serialVersionUID = 1L;
    public static final boolean ACTIVE = true;
    public static final boolean INACTIVE = false;

    @JsonProperty("full_name")
    public String fullName;
    public String email;
    public String phone;
    @JsonProperty("address")
    @Column(name = "address", columnDefinition = "TEXT")
    public String address;
    @ManyToOne
    public Brand brand;
    @JsonIgnore
    @OneToOne(cascade = { CascadeType.ALL })
    public SalesOrder salesOrder;
    @ManyToOne
    public Province province;
    @ManyToOne
    public City city;

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

    public static Finder<Long, SalesOrderAddress> find = new Finder<>(Long.class, SalesOrderAddress.class);



    public static SalesOrderAddress findById(Long id, Long brandId) {
        return find.where().eq("is_deleted", false).eq("id", id).eq("brand_id", brandId).findUnique();
    }

    public SalesOrderAddress(){

    }

    public SalesOrderAddress(MapAddress address, Brand brand){
        fullName = address.getFullName();
        email = address.getEmail();
        phone = address.getPhone();
        this.address = address.getAddress();
        this.brand = brand;
        province = Province.findById(address.getProvinceId());
        city = City.findById(address.getCityId());
    }

}