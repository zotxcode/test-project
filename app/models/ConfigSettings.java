package models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * @author hendriksaragih
 */
@Entity
public class ConfigSettings extends BaseModel{
    public String module;

    public String key;

    public String name;

    @Column(columnDefinition = "TEXT")
    public String value;

    @ManyToOne(cascade = { CascadeType.ALL })
    public Brand brand;

    @javax.persistence.Transient
    public String status;

    public static Finder<Long, ConfigSettings> find = new Finder<>(Long.class, ConfigSettings.class);


    public static String validation(String name, String key, String value, String module) {
        if (name==null || name.equals("")) {
            return "Name must not empty.";
        }
        if (key==null || key.equals("")) {
            return "Key must not empty.";
        }
        if (module == null || module.equals("")) {
            return "Description must not empty.";
        }
        return null;
    }

    public static void seed(String module, String featureKey, Long brandId){
        ConfigSettings data = new ConfigSettings();
        data.brand = Brand.find.byId(brandId);
        data.key = String.valueOf(brandId)+"_"+featureKey;
        data.name = featureKey;
        data.module = module;
        data.value = "";
        data.save();
    }

    public static ConfigSettings findByData(Long brandId, String module, String key){
        return find.where().eq("is_deleted", false)
                .eq("module", module)
                .eq("name", key)
                .eq("brand_id", brandId).findUnique();
    }
}