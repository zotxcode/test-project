package models;

import com.avaje.ebean.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.enwie.util.CommonFunction;
import com.enwie.util.Constant;
import play.data.validation.ValidationError;
import play.libs.Json;

import javax.persistence.*;
import java.beans.Transient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * @author hendriksaragih
 */
@Entity
public class Banner extends BaseModel{
    private static final long serialVersionUID = 1L;
    public static final boolean ACTIVE = true;
    public static final boolean INACTIVE = false;
    public static final String TYPE_WEB = "Web";
    public static final String TYPE_MOBILE = "Mobile";
    private static final String LOG_TYPE = "ADMIN";
    private static final String LOG_TABLE_NAME = "banner";

    public String name;
    public String caption1;
    public String caption2;
    public String title;
    public String slug;
    public String description;
    public String keyword;
    public boolean status;

    @JsonProperty("external_url")
    public String externalUrl;

    @javax.persistence.Transient
    public Integer urlType = 1;

    public int sequence;
    @JsonProperty("sequence_mobile")
    public int sequenceMobile;

    @JsonProperty("type_id")
    public Integer typeId;
    @JsonProperty("position_id")
    public Integer positionId;

    @JsonProperty("banner_image_name")
    @Column(name = "banner_image_name", columnDefinition = "TEXT")
    public String bannerImageName;
    @JsonProperty("banner_image_keyword")
    public String bannerImageKeyword;
    @JsonProperty("banner_image_title")
    public String bannerImageTitle;
    @JsonProperty("banner_image_description")
    @Column(name = "banner_image_description", columnDefinition = "TEXT")
    public String bannerImageDescription;

    @JsonIgnore
    @ManyToMany
    public List<Product> products;

    @JsonProperty("image_url")
    public String imageUrl;
    @JsonIgnore
    public String bannerSize;
    @JsonProperty("banner_size")
    public int[] getBannerSize() throws IOException{
        ObjectMapper om = new ObjectMapper();
        return (bannerSize==null) ? null : om.readValue(bannerSize, int[].class);
    }
    @JsonProperty("image_url_responsive")
    public String imageUrlResponsive;
    @JsonIgnore
    public String bannerResponsiveSize;
    @JsonProperty("banner_responsive_size")
    public int[] getBannerResponsiveSize() throws IOException{
        ObjectMapper om = new ObjectMapper();
        return (bannerResponsiveSize==null) ? null : om.readValue(bannerResponsiveSize, int[].class);
    }
    @JsonProperty("image_url_mobile")
    public String imageUrlMobile;
    @JsonIgnore
    public String bannerMobileSize;
    @JsonProperty("banner_mobile_size")
    public int[] getBannerMobileSize() throws IOException{
        ObjectMapper om = new ObjectMapper();
        return (bannerMobileSize==null) ? null : om.readValue(bannerMobileSize, int[].class);
    }

    @JsonIgnore
    @ManyToMany
    public List<Category> categories;

    @ManyToOne(cascade = { CascadeType.ALL })
    public Brand brand;

//    @JsonIgnore
//    @ManyToMany
//    public List<Product> products;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="Asia/Jakarta")
    @JsonProperty("active_from")
    public Date activeFrom;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="Asia/Jakarta")
    @JsonProperty("active_to")
    public Date activeTo;

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
    public String imageMobileLink;

    @javax.persistence.Transient
    public List<String> category_list;

    @javax.persistence.Transient
    public List<String> subcategory_list;

    @javax.persistence.Transient
    public List<String> product_list;


    @javax.persistence.Transient
    public String fromDate = "";

    @javax.persistence.Transient
    public String toDate = "";

    @javax.persistence.Transient
    public String fromTime = "";

    @javax.persistence.Transient
    public String toTime = "";

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

    @javax.persistence.Transient
    public int imageUrlMobileX;
    @javax.persistence.Transient
    public int imageUrlMobileY;
    @javax.persistence.Transient
    public int imageUrlMobileW;
    @javax.persistence.Transient
    public int imageUrlMobileH;

    @javax.persistence.Transient
    @JsonProperty("meta_title")
    public String getMetaTitle(){
        return title;
    }
    @javax.persistence.Transient
    @JsonProperty("meta_keyword")
    public String getMetaKeyword(){
        return keyword;
    }
    @javax.persistence.Transient
    @JsonProperty("meta_description")
    public String getMetaDescription(){
        return description;
    }

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
    @JsonProperty("product_detail")
    public Long getProductDetail() {
        return (products != null && products.size() == 1) ? products.get(0).id : null;
    }

    @Transient
    @JsonProperty("product_detail_slug")
    public String getProductDetailSlug() {
        return (products != null && products.size() == 1) ? products.get(0).slug : null;
    }

    public String getSlug(){
        return "banner/"+slug;
    }

    @javax.persistence.Transient
    @JsonProperty("link_url")
    public String getLinkUrl(){
        return externalUrl;
    }

    @Transient
    public String getStatus() {
        String statusName = "";
        if(status)
            statusName = "Active";
        else statusName = "Inactive";

        return statusName;
    }

    @Transient
    public String getPosition() {
        switch (positionId){
            case 1:
            case 4:
                return "Main Banner";
            case 2:
            case 5:
                return "Middle Banner";
            case 3:
            case 6:
                return "Footer Banner";
        }
        return "";
    }

    public String getType(){
        String type = "";
        switch (typeId){
            case 0: type = "Web & Mobile"; break;
            case 1: type = "Web"; break;
            case 2: type = "Mobile"; break;
        }
        return type;
    }

    @Transient
    public String getDateFrom() {
        return CommonFunction.getDateTime2(activeFrom);
    }

    @Transient
    public String getDateTo() {
        return CommonFunction.getDateTime2(activeTo);
    }

    public String getImage() {
        return Images.getImage(imageUrl);
    }

    public static Finder<Long, Banner> find = new Finder<Long, Banner>(Long.class, Banner.class);

    public static String validation(Banner model){
        if(model.name==null || model.name.equals("")){
            return "Please insert banner name";
        } else if((model.bannerImageName==null || model.bannerImageName.equals(""))||
                (model.bannerImageTitle==null || model.bannerImageTitle.equals(""))||
                (model.bannerImageKeyword==null || model.bannerImageKeyword.equals(""))||
                (model.bannerImageDescription==null)){
            return "Please describe all information for banner's image";
        } else if(model.activeFrom==null||model.activeTo==null||model.activeFrom.after(model.activeTo)){
            return "Please input valid active date range";
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

        if (bannerImageName == null || bannerImageName.isEmpty()) {
            errors.add(new ValidationError("bannerImageName", "Image Name must not empty."));
        }
        if (bannerImageTitle == null || bannerImageTitle.isEmpty()) {
            errors.add(new ValidationError("bannerImageTitle", "Meta Title must not empty."));
        }
        if (bannerImageDescription == null || bannerImageDescription.isEmpty()) {
            errors.add(new ValidationError("bannerImageDescription", "Meta Description must not empty."));
        }
        if (bannerImageKeyword == null || bannerImageKeyword.isEmpty()) {
            errors.add(new ValidationError("bannerImageKeyword", "Meta Keyword must not empty."));
        }

        if(errors.size() > 0)
            return errors;

        return null;
    }

    public static int getNextSequence(int positionId, Long brandId){
        SqlQuery sqlQuery = Ebean.createSqlQuery(
                "select max(sequence) as max from banner where status = true and position_id = :positionId and brand_id = :brandId");
        sqlQuery.setParameter("positionId", positionId);
        sqlQuery.setParameter("brandId", brandId);
        SqlRow result = sqlQuery.findUnique();
        return (result.getInteger("max")==null ? 0 : result.getInteger("max"))+1;
    }

    public static int getNextSequenceMobile(int positionId, Long brandId){
        SqlQuery sqlQuery = Ebean.createSqlQuery(
                "select max(sequence_mobile) as max from banner where status = true and position_id = :positionId and brand_id = :brandId");
        sqlQuery.setParameter("positionId", positionId);
        SqlRow result = sqlQuery.findUnique();
        return (result.getInteger("max")==null ? 0 : result.getInteger("max"))+1;
    }

    public static Page<Banner> page(int page, int pageSize, String sortBy, String order, String name, Integer filter, Brand brand) {
        ExpressionList<Banner> qry = Banner.find
                .where()
                .ilike("name", "%" + name + "%")
                .eq("is_deleted", false)
                .eq("brand", brand);

        switch (filter){
            case 0: qry.eq("type_id", 0);
                break;
            case 1: qry.eq("type_id", 2);
                break;
            case 2: qry.eq("type_id", 1);
                break;
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

    public String getImageMobileLink(){
        return imageUrlMobile==null || imageUrlMobile.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrlMobile;
    }

    public String getChangeLogData(Banner data){
        HashMap<String, String> map = new HashMap<>();
        map.put("title",(data.title == null)? "":data.title);
        map.put("external_url",(data.externalUrl == null)? "":data.externalUrl);
        map.put("description",(data.description == null)? "":data.description);
        map.put("keyword",(data.keyword == null)? "":data.keyword);
        map.put("caption1",(data.caption1 == null)? "":data.caption1);
        map.put("caption2",(data.caption2 == null)? "":data.caption2);
        map.put("name",(data.name == null)? "":data.name);
        map.put("type",(data.typeId == 1)? "Web":"Mobile");
        map.put("status",(data.status)? "Active":"Inactive");
        map.put("position",data.getPosition());
        map.put("banner_image_name",(data.bannerImageName == null)? "":data.bannerImageName);
        map.put("banner_image_title",(data.bannerImageTitle == null)? "":data.bannerImageTitle);
        map.put("banner_image_keyword",(data.bannerImageKeyword == null)? "":data.bannerImageKeyword);
        map.put("banner_image_description",(data.bannerImageDescription == null)? "":data.bannerImageDescription);
        map.put("active_from", data.getDateFrom());
        map.put("active_to",data.getDateTo());

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
        Banner oldBanner = Banner.find.byId(id);
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
            status = Banner.ACTIVE;
        else if(newStatus.equals("inactive"))
            status = Banner.INACTIVE;

        super.update();

        ChangeLog changeLog;
        changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", oldBannerData, getChangeLogData(this));
        changeLog.save();

    }

    public void updateSequence(int seq) {
        sequence = seq;
        super.update();
    }

    public static Banner findById(Long id, Long brandId) {
        return find.where().eq("is_deleted", false).eq("id", id).eq("brand_id", brandId).findUnique();
    }

    public static List<Banner> getAllBanner(Long brandId, Integer limit) {
        Date now = new Date();
        List<Banner> baners = Banner.find
                .where()
//                .le("active_from", now)
//                .ge("active_to", now)
                .eq("status", true)
                .eq("is_deleted", false)
                .eq("brand_id", brandId)
                .setMaxRows(limit)
                .order("status DESC, created_at DESC, sequence ASC").findList();

        if (baners == null || baners.size() == 0){
            baners = Banner.find
                    .where()
                    .eq("is_deleted", false)
                    .eq("brand_id", brandId)
                    .setMaxRows(1)
                    .order("status DESC, created_at DESC, sequence ASC").findList();
        }

        return baners;

    }

    public static void findOutOfRange(Long brandId) {
        Date now = new Date();
        List<Banner> datas = Banner.find
                .where()
                .not(Expr.and(Expr.le("active_from", now), Expr.ge("active_to", now)))
                .eq("is_deleted", false)
                .eq("status", true)
                .eq("brand_id", brandId).findList();
        Transaction txn = Ebean.beginTransaction();
        try {
            for (Banner dt : datas){
                dt.status = false;
                dt.update();
            }
            txn.commit();
        }catch (Exception e) {
            e.printStackTrace();
            txn.rollback();
        } finally {
            txn.end();
        }

    }

    public static BannerList getDetails(String slug, Long brandId){
        Date now = new Date();
        Banner banner = Banner.find.where()
//                .eq("status", true)
                .eq("is_deleted", false)
                .eq("slug", slug)
                .eq("brand_id", brandId)
//                .le("active_from", now)
//                .ge("active_to", now)
                .setMaxRows(1).findUnique();

        return banner != null ? new BannerList(banner) : null;
    }

}