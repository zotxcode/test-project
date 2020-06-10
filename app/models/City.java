package models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * @author hendriksaragih
 */
@Entity
public class City extends BaseModel {
    private static final long serialVersionUID = 1L;
    
    public String code;
    @JsonProperty("city")
    public String name;
    public String type;
    @Column(name = "postal_code")
    @JsonProperty("postal_code")
    public String postalCode;
    @ManyToOne(cascade = { CascadeType.ALL })
    public Province province;


    public static Finder<Long, City> find = new Finder<>(Long.class, City.class);

    public static City findById(Long id) {
        return find.where().eq("is_deleted", false).eq("id", id).findUnique();
    }
}