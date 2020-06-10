package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.enwie.util.CommonFunction;
import com.enwie.util.Constant;
import play.data.validation.ValidationError;

import javax.persistence.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * @author hendriksaragih
 */
@Entity
public class Category extends BaseModel {
    public static final int START_LEVEL = 1;
    public static final boolean ACTIVE = true;
    public static final boolean INACTIVE = false;
    // This is sales category, not master category

    // @JsonProperty("parent_id")
    // public Long parentId; // parent category id

    //	@Column(nullable = false, unique = true)
    public String code;
    @JsonProperty("root_category_code")
    public String rootCategoryCode; // root code category

    @JsonProperty("is_active")
    public boolean isActive;

    public String name;
    public String title;
    public String description;
    public String keyword;
    public String alias;
    public Integer level;
    public Integer sequence;
    public String slug;
    @JsonProperty("share_profit")
    public Double shareProfit;

    @JsonProperty("image_name")
    @Column(name = "image_name", columnDefinition = "TEXT")
    public String imageName;
    @JsonProperty("image_keyword")
    public String imageKeyword;
    @JsonProperty("image_title")
    public String imageTitle;
    @JsonProperty("image_description")
    @Column(name = "image_description", columnDefinition = "TEXT")
    public String imageDescription;
    @JsonProperty("image_url")
    public String imageUrl;
    @JsonProperty("image_url_responsive")
    public String imageUrlResponsive;

    @ManyToOne(cascade = { CascadeType.ALL })
    public Brand brand;

    @javax.persistence.Transient
    public String save;

    @javax.persistence.Transient
    public Long parent;

    @javax.persistence.Transient
    public String parentName;

    @javax.persistence.Transient
    public String imageLink;

    @javax.persistence.Transient
    public String imageLinkResponsive;

    @JsonIgnore
    @JoinColumn(name = "user_id")
    @ManyToOne
    public UserCms userCms;

    @Transient
    @JsonProperty("has_child")
    public boolean getHasChild() {
        return subCategory.size() != 0;
    }

    @Transient
    @JsonProperty("parent_category_id")
    public Long getParentCategoryId() {
        if (parentCategory != null)
            return parentCategory.id;
        return 0L;
    }

    @JsonIgnore
    @Column(name = "view_count")
    public int viewCount;

    @Transient
    @JsonProperty("sub_category")
    public List<Category> childCategory = new ArrayList<>();

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    public Category parentCategory;

    @OneToMany(mappedBy = "parentCategory")
    @Column(insertable = false, updatable = false)
    @JsonIgnore
    public Set<Category> subCategory = new HashSet<Category>();

    @JsonIgnore
    public String imageSize;
    @JsonProperty("image_size")
    public int[] getImageSize() throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper om = new ObjectMapper();
        return (imageSize==null) ? null : om.readValue(imageSize, int[].class);
    }

    @ManyToMany//(mappedBy = "parentCategory")
    @JsonIgnore
    public List<BaseAttribute> listBaseAttribute;

    @Transient
    public List<String> base_attribute_list;

    @javax.persistence.Transient
    public int imageUrlX;
    @javax.persistence.Transient
    public int imageUrlY;
    @javax.persistence.Transient
    public int imageUrlW;
    @javax.persistence.Transient
    public int imageUrlH;

    @javax.persistence.Transient
    public int imageUrlResponsiveX;
    @javax.persistence.Transient
    public int imageUrlResponsiveY;
    @javax.persistence.Transient
    public int imageUrlResponsiveW;
    @javax.persistence.Transient
    public int imageUrlResponsiveH;

    @Transient
    @JsonProperty("icon")
    public String getIcon(){
        return getImageLinkResponsive();
    }
    @Transient
    @JsonProperty("top_brands")
    public List<Brand> topBrands = new ArrayList<>();

    public String getImageUrl(){
        return getImageLink();
    }

    public String getImageUrlResponsive(){
        return getImageLinkResponsive();
    }

    @Transient
    public String getIsActive() {
        String statusName = "";
        if(isActive)
            statusName = "Active";
        else statusName = "Inactive";

        return statusName;
    }

    public static Finder<Long, Category> find = new Finder<Long, Category>(Long.class, Category.class);

    public static String validation(Category model) {
        Category uniqueCheck = Category.find.where().eq("slug", model.slug).findUnique();
        Category uniqueCheck2 = Category.find.where().eq("code", model.code).findUnique();
        if (model.name.equals("")) {
            return "Name must not empty.";
        }
        if (uniqueCheck != null && model.id==null)
        {
            return "Category with similar name already exist";
        }
        if (model.code.equals("")) {
            return "Code must not empty.";
        }
        if (uniqueCheck2!=null && !uniqueCheck2.id.equals(model.id)){
            return "category with similar code already exist";
        }
        return null;
    }

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


        if(errors.size() > 0)
            return errors;

        return null;
    }


    public static Page<Category> page(int page, int pageSize, String sortBy, String order, String filter, Brand brand) {
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

    public static int findRowCount(Brand brand) {
        return
                find.where()
                        .eq("is_deleted", false)
                        .eq("brand", brand)
                        .findRowCount();
    }

    public static Integer RowCount(Brand brand) {
        return findRowCount(brand);
    }

    public static int getNextSequence(Long parentId, Long brandId) {
        SqlQuery sqlQuery;
        if (parentId == null) {
            sqlQuery = Ebean.createSqlQuery(
                    "select max(sequence) as max from category where is_active = true and parent_id is null and brand_id = :brandId");
            sqlQuery.setParameter("brandId", brandId);
        } else {
            sqlQuery = Ebean.createSqlQuery(
                    "select max(sequence) as max from category where is_active = true and parent_id = :parentId and brand_id = :brandId");
            sqlQuery.setParameter("parentId", parentId);
            sqlQuery.setParameter("brandId", brandId);
        }
        SqlRow result = sqlQuery.findUnique();
        return (result.getInteger("max") == null ? 0 : result.getInteger("max")) + 1;
    }

    public String getImageLink(){
        return imageUrl==null || imageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl;
    }

    private String getImageLinkResponsive(){
        return imageUrlResponsive==null || imageUrlResponsive.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrlResponsive;
    }

    public static Category findById(Long id, Long brandId) {
        return find.where().eq("is_deleted", false).eq("id", id).eq("brand_id", brandId).findUnique();
    }

    public void updateStatus(String newStatus) {
        if(newStatus.equals("1"))
            isActive = Category.ACTIVE;
        else if(newStatus.equals("2"))
            isActive = Category.INACTIVE;

        super.update();

    }

    public static Category seed(String name, Long id, Long parent, Long brandId) {
        Category model = new Category();
        UserCms user = UserCms.find.byId(id);
        model.name = model.title = model.description = model.keyword = model.imageName = model.imageKeyword =
                model.imageTitle = model.imageDescription = name;
        model.code = model.slug = CommonFunction.slugGenerate(name);
        model.brand = Brand.find.byId(brandId);
        model.userCms = user;
        model.isActive = true;
        if (parent == 0L){
            model.parentCategory = null;
            model.rootCategoryCode = model.code;
            model.sequence =Category.getNextSequence(null, brandId);
            model.level = 1;
        }else{
            Category dataParent = Category.find.byId(parent);
            model.parentCategory = dataParent;
            model.rootCategoryCode = dataParent.rootCategoryCode;
            model.sequence = Category.getNextSequence(dataParent.id, brandId);
            model.level = dataParent.level + 1;
        }
        model.save();

        return model;
    }

    public static List<Category> recGetAllChildCategory(Long id, Long brandId) {
        List<Category> result = Category.getAllChildCategory(id, brandId);
        for (Category category : result) {
            category.childCategory = recGetAllChildCategory(category.id, brandId);
        }
        return result;
    }

    private static List<Category> getAllChildCategory(Long id, Long brandId) {
        return Category.find.where()
                .eq("parent_id", id)
                .eq("is_deleted", false)
                .eq("is_active", true)
                .eq("brand_id", brandId)
                .order("sequence asc").findList();
    }

}