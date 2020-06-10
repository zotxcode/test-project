package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import play.data.validation.ValidationError;
import play.libs.Json;

import javax.persistence.*;
import java.beans.Transient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author hendriksaragih
 */
@Entity
public class Faq extends BaseModel{
    private static final long serialVersionUID = 1L;
    private static final String LOG_TYPE = "ADMIN";
    private static final String LOG_TABLE_NAME = "faq";
    public static final int TYPE_CUSTOMER = 0;
    public static final int TYPE_MERCHANT = 1;

    @JsonProperty("meta_title")
    public String title;
    public String name;
    public String slug;
    @JsonProperty("meta_description")
    public String description;
    @JsonProperty("meta_keyword")
    public String keyword;
    @Column(columnDefinition = "TEXT")
    public String content;
    @ManyToOne(cascade = { CascadeType.ALL })
    public Brand brand;
    public int sequence;

    @ManyToOne
    @JsonProperty("faq_group")
    public InformationCategoryGroup faqGroup;

    public int type;

    @javax.persistence.Transient
    public String save;

    @javax.persistence.Transient
    public Integer group_id;

    @javax.persistence.Transient
    public Integer type_id;

    @JsonIgnore
    @JoinColumn(name="user_id")
    @ManyToOne
    public UserCms userCms;

    @JsonProperty("view_count")
    public Integer viewCount = 0;

    @Transient
    @JsonProperty("user_creator")
    public String getUserCreator(){
        return userCms.email;
    }

    @Transient
    public String getTypeName() {
        String result = "";
        if(TYPE_CUSTOMER == type)
            result = "Customer";
        else result = "Merchant";

        return result;
    }

    public static Finder<Long, Faq> find = new Finder<>(Long.class, Faq.class);


    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if (name == null || name.isEmpty()) {
            errors.add(new ValidationError("name", "Name must not empty."));
        }
        if (title == null || title.isEmpty()) {
            errors.add(new ValidationError("title", "Meta Title must not empty."));
        }
        if (description == null || description.isEmpty()) {
            errors.add(new ValidationError("description", "Meta Description must not empty."));
        }
        if (keyword == null || keyword.isEmpty()) {
            errors.add(new ValidationError("keyword", "Meta Keyword must not empty."));
        }
        if (content == null || content.isEmpty()) {
            errors.add(new ValidationError("content", "Content must not empty."));
        }
        if (group_id == null || group_id == 0) {
            errors.add(new ValidationError("group_id", "Group must not empty."));
        }

        if(errors.size() > 0)
            return errors;

        return null;
    }

    public static Page<Faq> page(int page, int pageSize, String sortBy, String order, String name, int filter, Brand brand) {
        ExpressionList<Faq> qry = Faq.find
                .where()
                .ilike("name", "%" + name + "%")
                .eq("is_deleted", false)
                .eq("brand", brand);

        if(filter != -1){
            qry.eq("type", filter);
        }

        return
                qry.orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);
    }

    public static Integer RowCount(Brand brand) {
        return find.where().eq("is_deleted", false).eq("brand", brand).findRowCount();
    }

    public String getChangeLogData(Faq data){
        HashMap<String, String> map = new HashMap<>();
        map.put("content",(data.content == null)? "":data.content);
        map.put("title",(data.title == null)? "":data.title);
        map.put("description",(data.description == null)? "":data.description);
        map.put("keyword",(data.keyword == null)? "":data.keyword);
        map.put("slug",(data.slug == null)? "":data.slug);
        map.put("name",(data.name == null)? "":data.name);
        map.put("faq_group",(data.faqGroup == null)? "":data.faqGroup.name);
        return Json.toJson(map).toString();
    }

    @Override
    public void save() {
        super.save();
        ChangeLog changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "ADD", null, getChangeLogData(this));
        changeLog.save();
    }

    @Override
    public void update() {
        Faq oldFaq = Faq.find.byId(id);
        super.update();

        ChangeLog changeLog;
        if(isDeleted){
            changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "DELETE", getChangeLogData(oldFaq), null);
        }else{
            changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", getChangeLogData(oldFaq), getChangeLogData(this));
        }
        changeLog.save();

    }

    public static Faq findById(Long id, Long brandId) {
        return find.where().eq("is_deleted", false).eq("id", id).eq("brand_id", brandId).findUnique();
    }

    public static List<Faq> getHomePage(Long group, String filter, int type, Long brandId) {
        return find.where()
                .eq("faq_group_id", group)
                .ilike("name", "%" + filter + "%")
                .eq("is_deleted", false)
                .eq("type", type)
                .eq("brand_id", brandId)
                .orderBy("sequence ASC").findList();
    }

    public static List<Faq> getPopular(int type, Long brandId) {
        return find.where()
                .eq("is_deleted", false)
                .eq("type", type)
                .eq("brand_id", brandId)
                .setMaxRows(5)
                .orderBy("view_count DESC").findList();
    }
}