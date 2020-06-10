package models;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.*;

/**
 * @author hendriksaragih
 */
@Entity
public class BaseAttribute extends BaseModel{
    public static String VARCHAR_TYPE = "VARCHAR";
    public static String INTEGER_TYPE = "INTEGER";

    public String name;
    public String type;

    @OneToMany(mappedBy = "baseAttribute")
    @JsonProperty("attributes")
    public Set<Attribute> attributesData;

    @javax.persistence.Transient
    public String save;

    @ManyToOne(cascade = { CascadeType.ALL })
    public Brand brand;

    @JsonProperty("show_filter")
    public boolean showFilter;

    @Transient
    @JsonProperty("values")
    public List<Attribute> getAttributes() {
        List<Attribute> attrs = new ArrayList<>();
        for (Attribute at : attributesData){
            if (!at.isDeleted){
                attrs.add(at);
            }
        }
        return attrs;
    }

    public static Finder<Long, BaseAttribute> find = new Finder<>(Long.class, BaseAttribute.class);


    public BaseAttribute(){
    }

    public BaseAttribute(String name, String type){
        this.name = name.toLowerCase();
        this.type = type.toLowerCase();
        this.attributesData = new HashSet<>();
    }

    public static String validate(BaseAttribute model){
        String res = null;
        model.name = model.name.toLowerCase();
        if (model.name.trim().equals("")){
            res = "Name must not empty";
        } else {
            BaseAttribute uniqueCheck = BaseAttribute.find.where().eq("name", model.name)
                    .eq("brand", model.brand).findUnique();
            if (uniqueCheck != null && !Objects.equals(uniqueCheck.id, model.id)) {
                res = "Base attribute already exist";
            }
        }
        return res;
    }


    public static Page<BaseAttribute> page(int page, int pageSize, String sortBy, String order, String filter, Brand brand) {
        return
                find.where()
                        .ilike("name", "%" + filter + "%")
                        .eq("is_deleted", false)
                        .eq("brand", brand)
                        .orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);
    }

    public static BaseAttribute findById(Long id, Long brandId) {
        return find.where().eq("is_deleted", false).eq("id", id).eq("brand_id", brandId).findUnique();
    }

    public static int findRowCount(Brand brand) {
        return
                find.where()
                        .eq("is_deleted", false)
                        .eq("brand", brand)
                        .findRowCount();
    }

    @java.beans.Transient
    public String getShowFilter() {
        String statusName = "";
        if(showFilter)
            statusName = "active";
        else statusName = "inactive";

        return statusName;
    }

    public static List<BaseAttribute> getAllData(Long brandId) {
        return BaseAttribute.find.where()
                .eq("is_deleted", false)
                .eq("brand_id", brandId)
                .eq("showFilter", true)
                .order("name asc").findList();
    }

}