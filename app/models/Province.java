package models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;

/**
 * @author hendriksaragih
 */
@Entity
public class Province extends BaseModel {
    private static final long serialVersionUID = 1L;

    public String code;
    @JsonProperty("province")
    public String name;

    public static Finder<Long, Province> find = new Finder<>(Long.class, Province.class);

    public static Province findById(Long id) {
        return find.where().eq("is_deleted", false).eq("id", id).findUnique();
    }
}