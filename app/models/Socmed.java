package models;

import com.avaje.ebean.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.enwie.util.Constant;
import play.data.validation.ValidationError;
import play.libs.Json;

import javax.persistence.*;
import java.beans.Transient;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * @author hendriksaragih
 */
@Entity
public class Socmed extends BaseModel{
    private static final long serialVersionUID = 1L;
    public static final boolean ACTIVE = true;
    public static final boolean INACTIVE = false;
    private static final String LOG_TYPE = "ADMIN";
    private static final String LOG_TABLE_NAME = "socmed";

    public String name;
    public String url;
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
    @JsonIgnore
    public String imageSize;

    @JsonProperty("image_url_responsive")
    public String imageUrlResponsive;
    @JsonIgnore
    public String imageResponsiveSize;

    @ManyToOne(cascade = { CascadeType.ALL })
    public Brand brand;

    @JsonIgnore
    @JoinColumn(name="user_id")
    @ManyToOne
    public UserCms userCms;

    @javax.persistence.Transient
    public String save;

    @javax.persistence.Transient
    public String imageLink;

    @javax.persistence.Transient
    public String imageResponsiveLink;

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
    @JsonProperty("user_creator")
    public String getUserCreator(){
        return userCms.email;
    }

    public String getImageUrl(){
        return getImageLink();
    }

    public String getImageUrlResponsive(){
        return getImageResponsiveLink();
    }

    @Transient
    public String getStatus() {
        String statusName = "";
        if(status)
            statusName = "Active";
        else statusName = "Inactive";

        return statusName;
    }

    public String getImage() {
        return Images.getImage(imageUrl);
    }

    public static Finder<Long, Socmed> find = new Finder<Long, Socmed>(Long.class, Socmed.class);

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if (name == null || name.isEmpty()) {
            errors.add(new ValidationError("name", "Name must not empty."));
        }
        if (url == null || url.isEmpty()) {
            errors.add(new ValidationError("url", "URL must not empty."));
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

    public static Page<Socmed> page(int page, int pageSize, String sortBy, String order, String name, Integer filter, Brand brand) {
        ExpressionList<Socmed> qry = Socmed.find
                .where()
                .ilike("name", "%" + name + "%")
                .eq("is_deleted", false)
                .eq("brand", brand);

        switch (filter){
            case 3: qry.eq("status", true);
                break;
            case 4: qry.eq("status", false);
                break;
        }

        return
                qry.orderBy(sortBy + " " + order)
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

    public String getImageLink(){
        return imageUrl==null || imageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl;
    }

    public String getImageResponsiveLink(){
        return imageUrlResponsive==null || imageUrlResponsive.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrlResponsive;
    }


    public String getChangeLogData(Socmed data){
        HashMap<String, String> map = new HashMap<>();
        map.put("name",(data.name == null)? "":data.name);
        map.put("url",(data.url == null)? "":data.url);
        map.put("status",(data.status)? "Active":"Inactive");
        map.put("image_name",(data.imageName == null)? "":data.imageName);
        map.put("image_title",(data.imageTitle == null)? "":data.imageTitle);
        map.put("image_keyword",(data.imageKeyword == null)? "":data.imageKeyword);
        map.put("image_description",(data.imageDescription == null)? "":data.imageDescription);

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
        Socmed oldBanner = Socmed.find.byId(id);
        super.update();

        ChangeLog changeLog;
        if(isDeleted){
            changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "DELETE", getChangeLogData(oldBanner), null);
        }else{
            changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", getChangeLogData(oldBanner), getChangeLogData(this));
        }
        changeLog.save();

    }

    public void updateStatus(String newStatus) {
        String oldBannerData = getChangeLogData(this);

        if(newStatus.equals("active"))
            status = Socmed.ACTIVE;
        else if(newStatus.equals("inactive"))
            status = Socmed.INACTIVE;

        super.update();

        ChangeLog changeLog;
        changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", oldBannerData, getChangeLogData(this));
        changeLog.save();

    }

    public static Socmed findById(Long id, Long brandId) {
        return find.where().eq("is_deleted", false).eq("id", id).eq("brand_id", brandId).findUnique();
    }

    public static List<Socmed> getAllSocmed(Long brandId, Integer limit) {
        Date now = new Date();
        return Socmed.find
                .where()
                .eq("is_deleted", false)
                .eq("status", true)
                .eq("brand_id", brandId)
                .setMaxRows(limit)
                .order("name asc").findList();
    }

}