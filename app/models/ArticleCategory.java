package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import play.data.validation.ValidationError;

import javax.persistence.*;
import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hendriksaragih
 */
@Entity
public class ArticleCategory extends BaseModel{
    public String name;
    public int sequence;
    @ManyToOne(cascade = { CascadeType.ALL })
    public Brand brand;

    @JsonIgnore
    @JoinColumn(name="user_id")
    @ManyToOne
    public UserCms userCms;

    @Transient
    @JsonProperty("user_creator")
    public String getUserCreator(){
        return userCms.email;
    }

    public ArticleCategory() {

    }

    public ArticleCategory(String name, Brand brand){
        this.name = name;
        this.brand = brand;
        this.sequence = ArticleCategory.find.where()
                .eq("is_deleted", false)
                .eq("brand", brand)
                .findRowCount()+1;
    }

    public static Finder<Long, ArticleCategory> find = new Finder<>(Long.class, ArticleCategory.class);

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if (name == null || name.isEmpty()) {
            errors.add(new ValidationError("name", "Name must not empty."));
        }

        if(errors.size() > 0)
            return errors;

        return null;
    }

    public static String validation(ArticleCategory model) {
        String res = null;
        ArticleCategory uniqueCheck = ArticleCategory.find.where()
                .eq("name", model.name)
                .findUnique();
        if (model.name==null||model.name.equals("")) {
            res = "Name must not empty.";
        }
        else if (uniqueCheck!=null && !uniqueCheck.id.equals(model.id)){
            res = "Category with similar name already exist.";
        }
        return res;
    }

    public static int getNextSequence(Long brandId){
        SqlQuery sqlQuery = Ebean.createSqlQuery(
                "select max(sequence) as max from article_category where is_deleted = false AND brand_id = :brandId");
        sqlQuery.setParameter("brandId", brandId);
        SqlRow result = sqlQuery.findUnique();
        int resSequence = (result.getInteger("max")==null ? 0 : result.getInteger("max"))+1;
        return resSequence;
    }

}