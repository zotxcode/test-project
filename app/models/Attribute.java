package models;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.enwie.util.Constant;

import javax.persistence.*;
import java.util.List;

/**
 * @author hendriksaragih
 */
@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"value", "base_attribute_id"})})
public class Attribute extends BaseModel{

    @JsonProperty("name")
    public String value;
    @JsonProperty("image_url")
    public String imageUrl;
    @JsonProperty("is_default")
    public boolean isDefault;
    @JsonProperty("additional")
    public String additional;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "base_attribute_id")
    public BaseAttribute baseAttribute;

    @javax.persistence.Transient
    public String save;

    @ManyToOne(cascade = { CascadeType.ALL })
    public Brand brand;

    @Transient
    @JsonProperty("name")
    public String getName(){
        return value;
    }

    @Transient
    @JsonProperty("color")
    public String getColor(){
        return additional;
    }

    @javax.persistence.Transient
    public int imageUrlX;
    @javax.persistence.Transient
    public int imageUrlY;
    @javax.persistence.Transient
    public int imageUrlW;
    @javax.persistence.Transient
    public int imageUrlH;

    @javax.persistence.Transient
    public String imageLink;

    @Transient
    @JsonProperty("images")
    public String getAdditionalImage() {
        return getImageLink();
    }

    public static Finder<Long, Attribute> find = new Finder<Long, Attribute>(Long.class, Attribute.class);

    public Attribute(){

    }

    public Attribute(String value, BaseAttribute baseAttribute){
        this.value = value;
        this.baseAttribute = baseAttribute;
        this.isDefault = false;
    }

    public Attribute(String value, BaseAttribute baseAttribute, Integer odooId){
        this.value = value;
        this.baseAttribute = baseAttribute;
        this.isDefault = false;
//		this.odooId = odooId;
    }

    public static String validate(Attribute model){
        String res = null;
        if (model.value==null||model.value.trim().equals("")){
            res = "Value must not empty";
        } else if (model.baseAttribute==null&&model.id==null){
            res = "Base attribute not found";
        } else {
            Attribute uniqueCheck = Attribute.find.where().eq("value", model.value).eq("base_attribute_id", model.baseAttribute.id).findUnique();
            if (uniqueCheck != null && uniqueCheck.id != model.id) {
                res = "Attribute already exist.";
            }
        }
        return res;
    }

    public static Page<Attribute> page(Long baseId, int page, int pageSize, String sortBy, String order, String filter, Long brandId) {
        return
                find.where()
                        .ilike("value", "%" + filter + "%")
                        .eq("is_deleted", false)
                        .eq("baseAttribute.id", baseId)
                        .eq("brand_id", brandId)
                        .orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);
    }

    public static Attribute findById(Long id, Long brandId) {
        return find.where().eq("is_deleted", false).eq("id", id).eq("brand_id", brandId).findUnique();
    }

    public String getImageLink(){
        return imageUrl==null || imageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl;
    }

    public static int findRowCount(Long baseId, Long brandId) {
        return
                find.where()
                        .eq("is_deleted", false)
                        .eq("baseAttribute.id", baseId)
                        .eq("brand_id", brandId)
                        .findRowCount();
    }

    public static List<Attribute> getColor(Long brandId) {
        return find
                .where()
                .eq("is_deleted", false)
                .eq("brand_id", brandId)
                .eq("base_attribute_id", Constant.getInstance().getColorId())
                .order("value asc").findList();
    }
}