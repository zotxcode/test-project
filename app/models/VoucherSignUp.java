package models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
/**
 * @author hendriksaragih
 */
@Entity
public class VoucherSignUp extends BaseModel{
   
   
	@JsonProperty("user_param")
    public String userParam;
	
	@JsonProperty("periode")
    public Integer periode;
	
	@JsonProperty("prefix_kupon")
    public String prefixKupon;
	
	@JsonProperty("client_id")
    public String clientId;
	
	@JsonProperty("besaran_voucher")
    public Double besaranVoucher;
	
	@JsonProperty("minimum_belanja")
    public Double minimumBelanja;
	
	@Transient
    public String save;
    
	@JsonIgnore
    @JoinColumn(name = "user_id")
    @ManyToOne
    public UserCms userCms;
	
	 @ManyToOne(cascade = { CascadeType.ALL })
    public Brand brand;

    public static Finder<Long, VoucherSignUp> find = new Finder<>(Long.class, VoucherSignUp.class);

    
    public static VoucherSignUp find(Long brandId) {
        return find.where().eq("is_deleted", false).eq("brand_id", brandId).setMaxRows(1).findUnique();
    }
	
	

}