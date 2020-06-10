package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.enwie.util.CommonFunction;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.beans.Transient;
import java.util.List;

/**
 * @author hendriksaragih
 */
@Entity
public class InformationCategoryGroup extends BaseModel {
    private static final long serialVersionUID = 1L;
    
    public String name;
    @JsonProperty("module_type")
    public String moduleType;
    public String slug;
    public int sequence;
    @JsonIgnore
    @JoinColumn(name = "user_id")
    @ManyToOne
    public UserCms userCms;
    @ManyToOne(cascade = { CascadeType.ALL })
    public Brand brand;

    @Transient
    @JsonProperty("user_creator")
    public String getUserCreator() {
        return userCms.email;
    }

    public static Finder<Long, InformationCategoryGroup> find = new Finder<>(Long.class,
            InformationCategoryGroup.class);

    public InformationCategoryGroup (){

    }

    public InformationCategoryGroup(String name, String moduleType, Brand brand){
        this.name = name;
        this.moduleType = moduleType;
        this.slug = CommonFunction.slugGenerate(name);
        this.brand = brand;
        this.sequence = InformationCategoryGroup.find.where()
                .eq("is_deleted", false)
                .eq("module_type", moduleType)
                .eq("brand", brand)
                .findRowCount()+1;
    }

    public static List<InformationCategoryGroup> listInformationGroupIn(String moduleType, Brand brand) {
        return InformationCategoryGroup.find.where()
                .eq("module_type", moduleType)
                .eq("brand", brand)
                .findList();
    }

    public static String validation(InformationCategoryGroup model) {
        String res = null;
        InformationCategoryGroup uniqueCheck = InformationCategoryGroup.find.where()
                .eq("module_type", model.moduleType)
                .eq("slug", model.slug)
                .findUnique();
        if (model.name==null||model.name.equals("")) {
            res = "Name must not empty.";
        }
        else if (model.moduleType==null||model.moduleType.equals("")){
            res = "Empty module type.";
        }
        else if (uniqueCheck!=null && !uniqueCheck.id.equals(model.id)){
            res = "Group with similar name already exist.";
        }
        return res;
    }

    public static int getNextSequence(String moduleType, Long brandId){
        SqlQuery sqlQuery = Ebean.createSqlQuery(
                "select max(sequence) as max from information_category_group where is_deleted = false and module_type = :moduleType and brand_id = :brandId");
        sqlQuery.setParameter("moduleType", moduleType);
        sqlQuery.setParameter("brandId", brandId);
        SqlRow result = sqlQuery.findUnique();
        int resSequence = (result.getInteger("max")==null ? 0 : result.getInteger("max"))+1;
        return resSequence;
    }

    public static List<InformationCategoryGroup> getHomePage(String type, Long brandId) {
        return find.where()
                .eq("module_type", type)
                .eq("is_deleted", false)
                .eq("brand_id", brandId)
                .orderBy("sequence ASC").findList();
    }

}