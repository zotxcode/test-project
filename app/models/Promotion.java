package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.enwie.util.CommonFunction;
import com.enwie.util.Constant;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by hilmanzaky.
 */
@Entity
public class Promotion extends BaseModel {
    private static final long serialVersionUID = 1L;

    public static Finder<Long, Promotion> find = new Finder<>(Long.class, Promotion.class);

//    @Column(unique = true)
    public String name;
    public String description;
    @JsonProperty("image_url")
    public String imageUrl;


    public boolean status;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="Asia/Jakarta")
    @JsonProperty("valid_from")
    public Date validFrom;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="Asia/Jakarta")
    @JsonProperty("valid_to")
    public Date validTo;

//    @JsonIgnore
//    @ManyToMany
//    public List<Brand> brands;
    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "brand_id", referencedColumnName = "id")
    public Brand brand;

    @JsonIgnore
    @ManyToMany
    public List<Product> products;

    @JsonIgnore
    @JoinColumn(name="created_by")
    @ManyToOne
    public UserCms createdBy;

    @JsonIgnore
    @JoinColumn(name="updated_by")
    @ManyToOne
    public UserCms updatedBy;

    @OneToMany(mappedBy = "promotion")
    @JsonProperty("details")
    public List<PromotionProduct> promotionProducts;

    @Transient
    public String save;

    @Transient
    public List<String> brand_list;

    @Transient
    public List<String> product_list;

    @Transient
    public String fromDate = "";

    @Transient
    public String toDate = "";

    @Transient
    public String fromTime = "";

    @Transient
    public String toTime = "";

    public static Page<Promotion> page(int page, int pageSize, String sortBy, String order, String name, Long brandId) {
        ExpressionList<Promotion> qry = Promotion.find
                .where()
                .ilike("name", "%" + name + "%")
                .eq("brand_id", brandId)
                .eq("t0.is_deleted", false);

//        if(!filter.equals("")){
//            qry.eq("t0.status", filter);
//        }

        return
                qry.orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);

    }

    public static Promotion findById(Long id, Long brandId) {
        return find.where().eq("is_deleted", false).eq("id", id).eq("brand_id", brandId).findUnique();
    }

    public static int findRowCount() {
        return
                find.where()
                        .eq("t0.is_deleted", false)
                        .findRowCount();
    }

    public String getValidFrom(){
        return CommonFunction.getDateTime2(validFrom);
    }

    public String getValidTo(){
        return CommonFunction.getDateTime2(validTo);
    }

    public void updateStatus(String newStatus) {
//        String oldBannerData = getChangeLogData(this);

        if(newStatus.equals("active"))
            status = Banner.ACTIVE;
        else if(newStatus.equals("inactive"))
            status = Banner.INACTIVE;

        super.update();

//        ChangeLog changeLog;
//        changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", oldBannerData, getChangeLogData(this));
//        changeLog.save();

    }

    public Promotion addPromotionProduct(Product product, Long discount) {
        PromotionProduct newPromotionProduct = new PromotionProduct(this, product, discount);
        newPromotionProduct.save();
        this.promotionProducts.add(newPromotionProduct);
        newPromotionProduct.updateDiscountPromotion();
        return this;
    }

	public String getImageUrl(){
		return getImageLink();
	}

	public String getImageLink(){
		return imageUrl==null || imageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl;
	}
}
