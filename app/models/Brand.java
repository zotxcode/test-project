package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.enwie.util.CommonFunction;
import com.enwie.util.Constant;
import play.data.validation.ValidationError;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hendriksaragih
 */
@Entity
public class Brand extends BaseModel {

    private static final long serialVersionUID = 1L;
    public static final boolean ACTIVE = true;
    public static final boolean INACTIVE = false;

    @Column(unique = true)
    public String name;
    public String domain;
    public String address;
    public String email;
    public String slug;
    public boolean status;

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
    @Column(name = "qty_prodyct")
    public Integer qtyProduct;
    @Column(name = "stock_product")
    public Integer stockProduct;

    @javax.persistence.Transient
    public String save;

    @javax.persistence.Transient
    public String imageLink;

    @javax.persistence.Transient
    public String getStatus() {
        String statusName = "";
        if(status)
            statusName = "Active";
        else statusName = "Inactive";

        return statusName;
    }

    @JsonIgnore
    @JoinColumn(name = "user_id")
    @ManyToOne
    public UserCms userCms;

    @JsonIgnore
    @Column(name = "view_count")
    public int viewCount;

    @JsonIgnore
    public String imageSize;

    @javax.persistence.Transient
    public int imageUrlX;
    @javax.persistence.Transient
    public int imageUrlY;
    @javax.persistence.Transient
    public int imageUrlW;
    @javax.persistence.Transient
    public int imageUrlH;

    public String getImageUrl(){
        return getImageLink();
    }

    public static Finder<Long, Brand> find = new Finder<>(Long.class, Brand.class);

    public Brand(){}

    public Brand(String name, String domain, String address, String email){
        super();
        this.name        = name;
        this.slug = CommonFunction.slugGenerate(name);
        this.domain         = domain;
        this.address = address;
        this.email    = email;
        this.status    = true;
        this.qtyProduct    = 0;
        this.stockProduct    = 0;
    }

    public static void seed(String name, String url, Long id){
        Brand model = new Brand();
        UserCms user = UserCms.find.byId(id);
        model.name = model.imageName = model.imageKeyword = model.imageTitle = model.imageDescription = name;
        model.slug = CommonFunction.slugGenerate(name);
        model.imageUrl = url;
        model.userCms = user;
        model.status = true;
        model.save();

        Photo.saveRecord("brd",url, "", "", "", url, user.id, "admin", "Brand", model.id);
    }

    public static String validation(Brand model) {
        Brand uniqueCheck = Brand.find.where().eq("slug", model.slug).setMaxRows(1).findUnique();
        if (model.name.equals("")) {
            return "Name must not empty.";
        }
        if (uniqueCheck != null && model.id == null) {
            return "Brand with similar name already exist";
        }
        if ((model.imageUrl != null) && ((model.imageName == null || model.imageName.equals(""))
                || (model.imageTitle == null || model.imageTitle.equals(""))
                || (model.imageKeyword == null || model.imageKeyword.equals(""))
                || (model.imageDescription == null))) {
            return "Please describe all information for brand's logo";
        }
        return null;
    }

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if (name == null || name.isEmpty()) {
            errors.add(new ValidationError("name", "Name must not empty."));
        }
        if (domain == null || domain.isEmpty()) {
            errors.add(new ValidationError("domain", "Domain must not empty."));
        }
        if (address == null || address.isEmpty()) {
            errors.add(new ValidationError("address", "Address must not empty."));
        }
        if (email == null || email.isEmpty()) {
            errors.add(new ValidationError("email", "Email must not empty."));
        }
        if (imageName == null || imageName.isEmpty()) {
            errors.add(new ValidationError("imageName", "Image Name must not empty."));
        }
        if (imageTitle == null || imageTitle.isEmpty()) {
            errors.add(new ValidationError("imageTitle", "Meta Title must not empty."));
        }
        if (imageDescription == null || imageDescription.isEmpty()) {
            errors.add(new ValidationError("imageDescription", "Meta Description must not empty."));
        }
        if (imageKeyword == null || imageKeyword.isEmpty()) {
            errors.add(new ValidationError("imageKeyword", "Meta Keyword must not empty."));
        }

        if(errors.size() > 0)
            return errors;

        return null;
    }

    public static Page<Brand> page(int page, int pageSize, String sortBy, String order, String name, Integer filter) {
        ExpressionList<Brand> qry = Brand.find
                .where()
                .ilike("name", "%" + name + "%")
                .eq("is_deleted", false);

        switch (filter){
            case 1: qry.eq("status", true);
                break;
            case 2: qry.eq("status", false);
                break;
        }

        return
                qry.orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);
    }

    public static int findRowCount() {
        return
                find.where()
                        .eq("is_deleted", false)
                        .findRowCount();
    }

    public String getImageLink(){
        return imageUrl==null || imageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl;
    }

    public void updateStatus(String newStatus) {
        if(newStatus.equals("active"))
            status = Brand.ACTIVE;
        else if(newStatus.equals("inactive"))
            status = Brand.INACTIVE;
        super.update();

    }
}