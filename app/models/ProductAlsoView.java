package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.List;

/**
 * @author hendriksaragih
 */
@Entity
public class ProductAlsoView extends BaseModel{
    private static final long serialVersionUID = 1L;
    private static final String LOG_TABLE_NAME = "product_also_view";
	

	//public String productDescription;
	
	@javax.persistence.Transient
    public String save;
	
	@JsonProperty("product_also_view_id")
	@ManyToMany
    public Integer productAlsoViewId;
	
	@JsonProperty("productId")
	@ManyToMany
    public Integer productId;
  
    @JsonIgnore
    @ManyToMany
    public List<Product> products;
	
	@javax.persistence.Transient
    public List<String> product_list;
	
}