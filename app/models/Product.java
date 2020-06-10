package models;

import com.avaje.ebean.*;
import com.avaje.ebean.Query;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.enwie.api.*;
import com.enwie.mapper.request.MapSearchProduct;
import com.enwie.mapper.response.*;
import com.enwie.util.CommonFunction;
import com.enwie.util.Constant;
import play.libs.Json;

import javax.persistence.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author hendriksaragih
 */
@Entity
@Table(name = "product")
public class Product extends BaseModel {

    public static final boolean ACTIVE = true;
    public static final boolean INACTIVE = false;
    public static final String PENDING = "P";
    public static final String AUTHORIZED = "A";
    public static final String REJECTED = "R";
	
    public String sku;
    @JsonProperty("sku_seller")
    public String skuSeller;
    public String name;
    @JsonProperty("is_new")
    public boolean isNew;
    @JsonProperty("status")
    public boolean status;
    public String title;
    public String slug;
    public String description ;
    public String keyword;

    @JsonProperty("buy_price")
    public Double buyPrice;
    public Double price;

    public Integer retur;
    @JsonIgnore
    @Column(name = "view_count")
    public int viewCount;

    public boolean stock;
    @JsonProperty("item_count")
    public Long itemCount;

    @JsonProperty("strike_through_display")
    public Double strikeThroughDisplay;

    @JsonProperty("price_display")
    public Double priceDisplay;

    public Double discount;

    @Column(name = "discount_type", columnDefinition = "integer default 0")
    @JsonProperty("discount_type")
    public int discountType;

    @JsonProperty("thumbnail_url")
    public String thumbnailUrl;
    @JsonProperty("image_url")
    public String imageUrl;

    @ManyToMany
    @JsonIgnore
    public Set <BaseAttribute> baseAttributes;

    @ManyToMany
//    @OrderBy("attribute_id ASC")
    @JsonIgnore
    public List <Attribute> attributes;

    @Column(name = "odoo_id")
    public Integer odooId;
    public int position;
    @Column(name = "is_show", columnDefinition = "boolean default true")
    public Boolean isShow;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "brand_id", referencedColumnName = "id")
    public Brand brand;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    public Category category;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_category_id", referencedColumnName = "id")
    public Category parentCategory;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "grand_parent_category_id", referencedColumnName = "id")
    public Category grandParentCategory;
//
//    @OneToMany(mappedBy = "mainProduct")
//    public List<ProductDetail> productDetail;

    @ManyToMany (mappedBy = "products")
    public List<Tag> tags;

    @JsonIgnore
    @JoinColumn(name="product_variant_group_id")
    @ManyToOne
    public ProductVariantGroup productVariantGroup;

    @JsonIgnore
    @JoinTable(name="product_also_view",
        joinColumns=@JoinColumn(name="product_id"),
        inverseJoinColumns = @JoinColumn(name = "product_also_view_id"))
    @ManyToMany
    public List<Product> productAlsoViews;

    @javax.persistence.Transient
    public List<String> product_list;

    @JsonProperty("average_rating")
    public float averageRating;

    @JsonProperty("count_rating")
    public Integer countRating;

    @JsonProperty("num_of_order")
    public Integer numOfOrder;

    @JsonProperty("discount_active_from")
    public Date discountActiveFrom;

    @JsonProperty("discount_active_to")
    public Date discountActiveTo;

	@JsonProperty("article_code")
    public String articleCode;
	
    @JsonIgnore
    @JoinColumn(name="user_id")
    @ManyToOne
    public UserCms userCms;

    @JsonProperty("first_po_status")
    public int firstPoStatus;

    @JsonProperty("like_count")
    public Integer likeCount;

//    ************************************* NEW
    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "currency_cd", referencedColumnName = "code")
    public Currency currency;

    @ManyToMany
    @JsonIgnore
    public Set <Size> sizes;
//    @JsonProperty("product_type")
//    public int productType;
    @JsonIgnore
    @JoinColumn(name="product_group_id")
    @ManyToOne
    public ProductGroup productGroup;

    @OneToMany(mappedBy = "mainProduct")
    public List<ProductDetail> productDetail;
//  *************************************

    @OneToMany(mappedBy = "product")
    public List<ProductProfit> productProfits;

    @JsonProperty("approved_status")
    public String approvedStatus;

    @JsonProperty("approved_note")
    @Column(name = "approved_note", length = 2000)
    public String approvedNote;

    @JsonProperty("approved_information")
    @Column(name = "approved_information", length = 1000)
    public String approvedInformation;

    @Column(name = "approved_by")
    @JsonIgnore
    @ManyToOne
    public UserCms approvedBy;

    @javax.persistence.Transient
    public String voucerCode;

    @javax.persistence.Transient
    public Long brandId;

    @javax.persistence.Transient
    public Long categoryId;

    @javax.persistence.Transient
    public Long subCategoryId;

    @javax.persistence.Transient
    public Long subSubCategoryId;

    @javax.persistence.Transient
    public Double weight;

    @javax.persistence.Transient
    public Double dimension1;

    @javax.persistence.Transient
    public Double dimension2;

    @javax.persistence.Transient
    public Double dimension3;

    @javax.persistence.Transient
    public String currencyCode;

    @javax.persistence.Transient
    public String fromDate = "";

    @javax.persistence.Transient
    public String toDate = "";

    @javax.persistence.Transient
    public String fromTime = "";

    @javax.persistence.Transient
    public String toTime = "";

    @javax.persistence.Transient
    public String whatInTheBox = "";

    @javax.persistence.Transient
    public String feature = "";

    @javax.persistence.Transient
    public String specifications = "";

    @javax.persistence.Transient
    public String waranty = "";

    @javax.persistence.Transient
    public String recomendedCare = "";

    @javax.persistence.Transient
    public String productDescription;

    @javax.persistence.Transient
    public String sizeGuide;

    @javax.persistence.Transient
    public int warrantyType;

    @javax.persistence.Transient
    public int warrantyPeriod;

    @javax.persistence.Transient
    public List<String> listShortDescriptions;

    @javax.persistence.Transient
    public List<Long> listBaseAttribute;

    @javax.persistence.Transient
    public List<Long> listAttribute;

    @javax.persistence.Transient
    public List<Long> sizeids;

    @javax.persistence.Transient
    public String newProduct;

    @javax.persistence.Transient
    public Long idtmp;

    @javax.persistence.Transient
    public Integer numProduct = 1;

    @javax.persistence.Transient
    public Integer numAttributes = 0;

    @javax.persistence.Transient
    public String save;

    @javax.persistence.Transient
    public String idImagetmp;

    @javax.persistence.Transient
    public int imageUrl1X;
    @javax.persistence.Transient
    public int imageUrl1Y;
    @javax.persistence.Transient
    public int imageUrl1W;
    @javax.persistence.Transient
    public int imageUrl1H;

    @javax.persistence.Transient
    public int imageUrl2X;
    @javax.persistence.Transient
    public int imageUrl2Y;
    @javax.persistence.Transient
    public int imageUrl2W;
    @javax.persistence.Transient
    public int imageUrl2H;

    @javax.persistence.Transient
    public int imageUrl3X;
    @javax.persistence.Transient
    public int imageUrl3Y;
    @javax.persistence.Transient
    public int imageUrl3W;
    @javax.persistence.Transient
    public int imageUrl3H;

    @javax.persistence.Transient
    public int imageUrl4X;
    @javax.persistence.Transient
    public int imageUrl4Y;
    @javax.persistence.Transient
    public int imageUrl4W;
    @javax.persistence.Transient
    public int imageUrl4H;

    @javax.persistence.Transient
    public int imageUrl5X;
    @javax.persistence.Transient
    public int imageUrl5Y;
    @javax.persistence.Transient
    public int imageUrl5W;
    @javax.persistence.Transient
    public int imageUrl5H;
	
	@javax.persistence.Transient
    public int thumbnailYoutubeUr1X;
    @javax.persistence.Transient
    public int thumbnailYoutubeUr1Y;
    @javax.persistence.Transient
    public int thumbnailYoutubeUr1W;
    @javax.persistence.Transient
    public int thumbnailYoutubeUr1H;


    @javax.persistence.Transient
    public int imageIcon1X;
    @javax.persistence.Transient
    public int imageIcon1Y;
    @javax.persistence.Transient
    public int imageIcon1W;
    @javax.persistence.Transient
    public int imageIcon1H;

    @javax.persistence.Transient
    public int imageIcon2X;
    @javax.persistence.Transient
    public int imageIcon2Y;
    @javax.persistence.Transient
    public int imageIcon2W;
    @javax.persistence.Transient
    public int imageIcon2H;

    @javax.persistence.Transient
    public int imageIcon3X;
    @javax.persistence.Transient
    public int imageIcon3Y;
    @javax.persistence.Transient
    public int imageIcon3W;
    @javax.persistence.Transient
    public int imageIcon3H;

    @javax.persistence.Transient
    public int imageIcon4X;
    @javax.persistence.Transient
    public int imageIcon4Y;
    @javax.persistence.Transient
    public int imageIcon4W;
    @javax.persistence.Transient
    public int imageIcon4H;

    @javax.persistence.Transient
    public int imageIcon5X;
    @javax.persistence.Transient
    public int imageIcon5Y;
    @javax.persistence.Transient
    public int imageIcon5W;
    @javax.persistence.Transient
    public int imageIcon5H;

    @javax.persistence.Transient
    public Double profitSSPercentage;
    @javax.persistence.Transient
    public Double profitSSValue;

    @javax.persistence.Transient
    public Double profitDPercentage;
    @javax.persistence.Transient
    public Double profitDValue;

    @javax.persistence.Transient
    public Double profitSDPercentage;
    @javax.persistence.Transient
    public Double profitSDValue;

    @javax.persistence.Transient
    public Double profitRPercentage;
    @javax.persistence.Transient
    public Double profitRValue;

    @javax.persistence.Transient
    public String embededYoutube;

    @javax.persistence.Transient
    public String thumbnailYoutubeUrl;

    @javax.persistence.Transient
    public String imageYoutubeLink;

    public String getImageYoutubeLink(){
        return thumbnailYoutubeUrl==null || thumbnailYoutubeUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + thumbnailYoutubeUrl;
    }

	
    @Transient
    @JsonProperty("is_active")
    public Boolean getIsActive(){
        return status;
    }
    @Transient
    @JsonProperty("stock")
    public Long getStock(){
        return itemCount;
    }

    public Integer getReturQty(){
        return retur == null ? 0 : retur;
    }
//
//    @Transient
//    public ProductType getInstanceProductType(){
//        return ProductType.getProductTypeById(productType);
//
//    }

	
    public String getThumbnailUrl(){
        return thumbnailUrl==null || thumbnailUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + thumbnailUrl;
    }
	
	

    public String getImageUrl(){
        return imageUrl==null || imageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl;
    }
	


    @java.beans.Transient
    public String getApproveStatus() {
        if (approvedStatus == null) return "";
        String statusName = "";
        switch (approvedStatus){
            case "P" : statusName = "Pending"; break;
            case "A" : statusName = "Approved"; break;
            case "R" : statusName = "Rejected"; break;
        }

        return statusName;
    }

    @Transient
    @JsonProperty("num_like")
    public Integer numLike(){
        return likeCount == null ? 0 : likeCount;
    }

    @Transient
    @JsonProperty("real_like")
    public Integer getRealLikeCount(){
        List<WishList> data = WishList.find.where()
                .eq("is_deleted", false)
                .eq("product_id", this.id)
                .findList();
        return data != null ? data.size() : 0;
    }

    @javax.persistence.Transient
    @JsonProperty("product_reviews")
    public List<ProductReview> getProductReviews(){
        return ProductReview.getReview(id);
    }

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
    @JsonProperty("categories")
    public List<Category> getCategories(){
        return Arrays.asList(category);
    }
    @JsonProperty("categories2")
    public List<Category> getCategories2(){
        return Arrays.asList(parentCategory);
    }
    @JsonProperty("categories1")
    public List<Category> getCategories1(){
        return Arrays.asList(grandParentCategory);
    }
    @Transient
    @JsonProperty("discount_active_from_s")
    public String getDiscountActiveFromS(){
        return CommonFunction.getDate2(discountActiveFrom);
    }
    @Transient
    @JsonProperty("discount_active_to_s")
    public String getDiscountActiveToS(){
        return CommonFunction.getDate2(discountActiveTo);
    }
    @Transient
    @JsonProperty("discount_type_s")
    public String getDiscountTypeS(){
        return discountType == 0 ? "" : String.valueOf(discountType);
    }

    @Transient
    private Long myWishlist;

    @Transient
    @JsonProperty("is_like")
    public Boolean isLike(){
        return myWishlist != null;
    }
	
	@javax.persistence.Transient
    public int imageUrlX;
    @javax.persistence.Transient
    public int imageUrlY;
    @javax.persistence.Transient
    public int imageUrlW;
    @javax.persistence.Transient
    public int imageUrlH;

    public void setIsLike(Long memberId){
        WishList model = WishList.find.where()
                .eq("is_deleted", false)
                .eq("product_id", id)
                .eq("member_id", memberId)
                .setMaxRows(1).findUnique();

        myWishlist = model != null ? model.id : null;
    }

    public static Finder<Long, Product> find = new Finder<Long, Product>(Long.class, Product.class);

    public Product(){

    }

    public Product(Product prod){
		articleCode = prod.articleCode;
		voucerCode = prod.voucerCode;
		embededYoutube = prod.embededYoutube;
        //sku = prod.sku;
		sku = prod.sku;
        skuSeller = prod.skuSeller;
        name = prod.name;
        isNew = prod.isNew;
        status = prod.status;
        title = prod.title;
        slug = prod.slug;
        description = prod.description;
        keyword = prod.keyword;
        buyPrice = prod.buyPrice;
        price = prod.price;
        retur = prod.retur;
        viewCount = prod.viewCount;
        stock = prod.stock;
        itemCount = prod.itemCount;
        strikeThroughDisplay = prod.strikeThroughDisplay;
        priceDisplay = prod.priceDisplay;
        discount = prod.discount;
        discountType = prod.discountType;
        categoryId = prod.categoryId;
        subCategoryId = prod.subCategoryId;
        subSubCategoryId = prod.subSubCategoryId;
        weight = prod.weight;
        dimension1 = prod.dimension1;
        dimension2 = prod.dimension2;
        dimension3 = prod.dimension3;
        fromDate = prod.fromDate;
        toDate = prod.toDate;
        fromTime = prod.fromTime;
        toTime = prod.toTime;
        whatInTheBox= prod.whatInTheBox;
        feature = prod.feature;
        specifications = prod.specifications;
        waranty = prod.waranty;
        recomendedCare = prod.recomendedCare;
        productDescription = prod.productDescription;
        sizeGuide = prod.sizeGuide;
        warrantyType = prod.warrantyType;
        warrantyPeriod = prod.warrantyPeriod;
        listShortDescriptions = prod.listShortDescriptions;
        listBaseAttribute = prod.listBaseAttribute;
        listAttribute = prod.listAttribute;
        sizeids = prod.sizeids;
        newProduct = prod.newProduct;
        idtmp = prod.idtmp;
        numProduct = prod.numProduct;
        numAttributes = prod.numAttributes;
        save = prod.save;
        idImagetmp = prod.idImagetmp;

    }

    public Product(String sku, String name, Long brandId, Long categoryId, Double price) {
        this.sku = sku;
        this.name = name;
        this.title = name;
        this.slug = CommonFunction.slugGenerate(name);
        this.description = name;
        this.keyword = name;
        this.isNew = true;
        this.status = true;
        this.strikeThroughDisplay = price;
        this.priceDisplay = price;
        this.price=price;
        this.buyPrice=price;
        this.brand = Brand.find.byId(brandId);

        this.category = Category.find.byId(categoryId);
        this.parentCategory = Category.find.byId(this.category.parentCategory.id);
        this.grandParentCategory = Category.find.byId(this.parentCategory.parentCategory.id);
        numOfOrder = 0;
        isShow = true;
    }

    public static Product seed (String sku, String name, Long brandId, Long categoryId, Double price) {
        Product p = new Product(sku, name, brandId, categoryId, price);
        p.save();

        return p;
    }


    public static Map<Integer, String> getListDiscountType(){
        Map<Integer, String> result = new LinkedHashMap<>();
        result.put(1, "Value");
        result.put(2, "Percent");
        return result;
    }


    public Double getPriceDisplay(){
        switch (discountType){
            case 1 : return (price - discount);
            case 2 : return price - (discount/100*price);
        }
        return price;
    }

    public Double getPrice(){
        return price;
    }
//
//    public ProductDetail getProductDetail(){
//        return ProductDetail.find.where().eq("mainProduct.id",id).findUnique();
//    }

    public static Page<Product> page(int page, int pageSize, String sortBy, String order, String filter) {
        return
                find.where()
                        .ilike("name", "%" + filter + "%")
                        .eq("is_deleted", false)
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

    public String generateSKU(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHH");
        int random = new Random().nextInt((2000 - 1000) + 1) + 1000;
        String newSku = "";
        newSku += this.grandParentCategory.name.substring(0,1);
        newSku += this.parentCategory.name.substring(0,1);
        newSku += this.category.name.substring(0,1);
        newSku += this.category.id;
        newSku += simpleDateFormat.format(new Date());
        newSku += String.valueOf(random);
        return newSku;
    }

    public void updateStatus(String newStatus) {
        if(newStatus.equals("active"))
            status = Product.ACTIVE;
        else if(newStatus.equals("inactive"))
            status = Product.INACTIVE;

        super.update();

    }

    public static Product findById(Long id, Long brandId) {
        return find.where().eq("is_deleted", false).eq("id", id).eq("brand_id", brandId).findUnique();
    }

    public static void updateAverageRating(Long id, Long brandId){
        Product dt = Product.findById(id, brandId);
        dt.averageRating = ProductReview.getAverage(id, brandId);
        dt.countRating = ProductReview.getJumlah(id, brandId);
        dt.update();
    }

    public Integer getNumOfOrder() {
        return numOfOrder == null ? 0 : numOfOrder;
    }

    public ProductDetail getProductDetail(Long brandId){
        return ProductDetail.find.where()
                .eq("mainProduct.id",id)
                .findUnique();
    }

    @Override
    public void update(Object id) {
        super.update(id);
    }

    public static void incrementViewCount(Long id, Integer viewCount){
        Product product = Product.find.byId(id);
        product.viewCount = viewCount;
        product.update();
    }

    public static Query<Product> getQueryProductList(Long brandId, Long memberId){
        String sql = "SELECT a.id, sku, name, price, image_url, average_rating, count_rating, category_id, parent_category_id, " +
                "grand_parent_category_id, discount, view_count, c.member_id " +
                "FROM product as a " +
                "LEFT JOIN product_attribute as B ON a.id = b.product_id " +
                "LEFT JOIN wish_list as c ON c.product_id = a.id " +
                "LEFT JOIN product_base_attribute as e ON e.product_id = a.id " +
                "WHERE a.is_deleted = FALSE " +
                "AND a.status = TRUE " +
                "AND a.brand_id = " + brandId +" "+
                "AND a.is_show = TRUE " +
                "AND a.item_count > 0 ";
        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("category_id", "category.id")
                .columnMapping("parent_category_id", "parentCategory.id")
                .columnMapping("grand_parent_category_id", "grandParentCategory.id")
                .columnMapping("c.member_id", "myWishlist")
                .create();
        Query<Product> query = Ebean.find(Product.class);
        query.setRawSql(rawSql);

        return query;
    }

    @SuppressWarnings({ "unchecked", "deprecation" })
    public static <T> BaseResponse<T> getData(Query<T> reqQuery, MapSearchProduct map, Long brandId)
            throws IOException {
        Query<T> query = reqQuery;

        if (!"".equals(map.getSort())) {
            switch (map.getSort()){
                case "newest" :
                    query = query.orderBy("a.created_at ASC");
                    break;
                case "latest" :
                    query = query.orderBy("a.created_at DESC");
                    break;
                case "highest" :
                    query = query.orderBy("price DESC");
                    break;
                case "lowest" :
                    query = query.orderBy("price ASC");
                    break;
                case "name_asc" :
                    query = query.orderBy("a.name ASC");
                    break;
                case "name_desc" :
                    query = query.orderBy("a.name DESC");
                    break;
            }
        }

        // filter
        ExpressionList<T> exp = query.where();
        ObjectNode result = Json.newObject();
        if (!"".equals(map.getBanner())) {
            exp = exp.conjunction();
            extractQueryBanner(map.getBanner(), exp, brandId);
            exp = exp.endJunction();
        }

        List<MapVariant> resFilter = new ArrayList<>();
        if (map.getCategoryId() != 0L){
            Category data = Category.find.where()
                    .eq("id", map.getCategoryId())
                    .eq("brand_id", brandId)
                    .findUnique();
            if (data != null){
                data.viewCount = data.viewCount + 1;
                data.update();
            }

            exp = exp.disjunction();
            ApiResponse.getInstance().setFilter(exp, null, new ApiFilter("category.id", "equals", new ApiFilterValue[]{new ApiFilterValue(map.getCategoryId())}));
            ApiResponse.getInstance().setFilter(exp, null, new ApiFilter("parentCategory.id", "equals", new ApiFilterValue[]{new ApiFilterValue(map.getCategoryId())}));
            ApiResponse.getInstance().setFilter(exp, null, new ApiFilter("grandParentCategory.id", "equals", new ApiFilterValue[]{new ApiFilterValue(map.getCategoryId())}));
            exp = exp.endJunction();
        }

        if(map.getPriceMin() != null)
            ApiResponse.getInstance().setFilter(exp, null, new ApiFilter("price", "greater_than_or_equals", new ApiFilterValue[]{new ApiFilterValue(map.getPriceMin())}));

        if(map.getPriceMax() != null)
            ApiResponse.getInstance().setFilter(exp, null, new ApiFilter("price", "less_than_or_equals", new ApiFilterValue[]{new ApiFilterValue(map.getPriceMax())}));

        if (map.getColors() != null && map.getColors().size() > 0){
            exp.in("b.attribute_id", map.getColors());
        }
        if (map.getSizes() != null && map.getSizes().size() > 0){
            exp.in("d.fashion_size_id", map.getSizes());
        }
        if (map.getAttributes() != null && map.getAttributes().size() > 0){
            for (MapSearchProductAttribute attr: map.getAttributes()){
                exp.eq("e.base_attribute_id", attr.getId());
                exp.in("d.fashion_size_id", attr.getValues());
            }

        }

        query = exp.query();
        int total = query.findList().size();

        if (map.getLimit() != 0) {
            query = query.setMaxRows(map.getLimit());
        }

        List<T> resData = query.findPagingList(map.getLimit()).getPage(map.getOffset()).getList();
        result.put("filter", Json.toJson(resFilter));
        result.put("result", Json.toJson(new ObjectMapper().convertValue(resData, MapProductList[].class)));

        BaseResponse<T> response = new BaseResponse<>();
        response.setData(result);
        response.setMeta(total, map.getOffset(), map.getLimit());
        response.setMessage("Success");

        return response;
    }

    private static void extractQueryBanner(String slug, ExpressionList exp, Long brandId){
        BannerList list = getBannerList(slug, brandId);
        if (list != null){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            if (list.getProducts() != null){
                List<ApiFilterValue> filter = new ArrayList<>();
                for(Product dt : list.getProducts()){
                    filter.add(new ApiFilterValue(dt.id));
                }
                if (filter.size() > 0){
                    ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("id", "in", filter.toArray(new ApiFilterValue[0])));
                }
            }
            if (list.getCategories1() != null){
                List<ApiFilterValue> filter = new ArrayList<>();
                for(Category dt : list.getCategories1()){
                    filter.add(new ApiFilterValue(dt.id));
                }
                if (filter.size() > 0){
                    ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("grand_parent_category_id", "in", filter.toArray(new ApiFilterValue[0])));
                }
            }
            if (list.getCategories2() != null){
                List<ApiFilterValue> filter = new ArrayList<>();
                for(Category dt : list.getCategories2()){
                    filter.add(new ApiFilterValue(dt.id));
                }
                if (filter.size() > 0){
                    ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("parent_category_id", "in", filter.toArray(new ApiFilterValue[0])));
                }
            }
            if (list.getCategories3() != null){
                List<ApiFilterValue> filter = new ArrayList<>();
                for(Category dt : list.getCategories3()){
                    filter.add(new ApiFilterValue(dt.id));
                }
                if (filter.size() > 0){
                    ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("category_id", "in", filter.toArray(new ApiFilterValue[0])));
                }
            }
        }
    }

    public static BannerList getBannerList(String slug, Long brandId){
        String[] split = slug.split("/");
        BannerList list = null;
        switch (split[0]){
            case "banner":
                list = Banner.getDetails(split[1], brandId);
                break;
            case "onsalebanner":
                list = OnSaleBanner.getDetails(split[1], brandId);
                break;
            case "newarrivalbanner":
                list = NewArrivalBanner.getDetails(split[1], brandId);
                break;
            case "instagrambanner":
                list = InstagramBanner.getDetails(split[1], brandId);
                break;
            case "categorybanner":
                list = CategoryBanner.getDetails(split[1], brandId);
                break;
            case "bestsellerbanner":
                list = BestSellerBanner.getDetails(split[1], brandId);
                break;
        }

        return list;
    }

    @javax.persistence.Transient
    @JsonProperty("product_images")
    public List<MapProductImage> getProductImages(){
        List<MapProductImage> images = new ArrayList<>();
        if (productDetail != null && productDetail.size()>0){
            ProductDetail pd = productDetail.get(0);
            String[] image1 = pd.getImage1();
            String[] image2 = pd.getImage2();
            String[] image3 = pd.getImage3();
            String[] image4 = pd.getImageColor();

            int i = 0;
            for(String image:image1){
                if(image != null) {
                    MapProductImage img = new MapProductImage();
                    img.setFullImageUrl(pd.getFullUrlImage(image));
                    img.setThumbnailImageUrl(pd.getFullUrlImage(image3[i]));
                    img.setMediumImageUrl(pd.getFullUrlImage(image2[i]));
                    img.setColorImageUrl("");
                    if (image4!=null && image4.length > i){
                        img.setColorImageUrl(pd.getFullUrlImage(image4[i]));
                    }
                    images.add(img);
                }
                i++;
            }
        }

        return images;
    }
//
//    @javax.persistence.Transient
//    @JsonProperty("product_colors")
//    public List<MapProductImage> getProductColors(){
//        List<MapProductImage> images = new ArrayList<>();
//        if (productDetail != null && productDetail.size()>0){
//            ProductDetail pd = productDetail.get(0);
//            String[] image1 = pd.getImage1();
//            String[] image2 = pd.getImage2();
//            String[] image3 = pd.getImage3();
//            String[] image4 = pd.getImage4();
//
//            int i = 0;
//            for(String image:image1){
//                if(image != null) {
//                    MapProductImage img = new MapProductImage();
//                    img.setFullImageUrl(pd.getFullUrlImage(image));
//                    img.setMediumImageUrl(pd.getFullUrlImage(image2[i]));
//                    img.setThumbnailImageUrl(pd.getFullUrlImage(image3[i]));
//                    images.add(img);
//                }
//                i++;
//            }
//        }
//
//        return images;
//    }

    @javax.persistence.Transient
    @JsonProperty("rating")
    public MapProductRatting rating;

    public void setRating(){
        rating = new MapProductRatting();
        rating.setAverage(ProductReview.getAverage(id, brand.id));
        rating.setBintang1(ProductReview.getJumlah(id, brand.id, 1));
        rating.setBintang2(ProductReview.getJumlah(id, brand.id,2));
        rating.setBintang3(ProductReview.getJumlah(id, brand.id,3));
        rating.setBintang4(ProductReview.getJumlah(id, brand.id,4));
        rating.setBintang5(ProductReview.getJumlah(id, brand.id,5));
        rating.setCount(ProductReview.getJumlah(id, brand.id));
    }

    @Transient
    @JsonProperty("product_variants")
    public List<MapVariantGroup> productVariants;

    public void setVariant(){
        productVariants = ProductVariantGroup.getListRelatedAttribute(productVariantGroup, id);
    }

    @Transient
    @JsonProperty("product_colors")
//    public List<MapProductColor> productColors;
    public List<MapProductColor> getColors(){
//        List<MapProductColor> productColors = new ArrayList<>();
        return ProductVariantGroup.getListRelatedAttributeColor(productVariantGroup, this);
    }
//    public void setColors(){
//        productColors = ProductVariantGroup.getListRelatedAttributeColor(productVariantGroup, this);
//    }

    @JsonProperty("product_details")
    public List<ProductDetail> getProductDetails(){
        return productDetail;
    }

    public Attribute findColorProduct(){
        Attribute attr = null;
        for(Attribute attribute: attributes){
            if (attribute.baseAttribute.id.equals(Constant.getInstance().getColorId())){
                attr = attribute;
                break;
            }
        }

        return attr;
    }

    public Integer getWeightProduct(){
        Integer weight = 0;
        if (productDetail != null && productDetail.size() > 0){
            weight = productDetail.get(0).weight.intValue();
        }
        return weight;
    }

    public static List<MapFastSearchProduct> fastSearch(String strQuery, Long brandId) {
        String sql = "SELECT id, name, sku, slug, grand_parent_category_id FROM (" +
                "SELECT a.id, a.name, a.sku, a.slug, a.grand_parent_category_id " +
                "FROM product a " +
                "WHERE a.status = TRUE AND a.brand_id = :brandId " +
                "AND a.name ILIKE '%" + strQuery + "%') tbl";

        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("id", "id")
                .columnMapping("name", "name")
                .columnMapping("sku", "sku")
                .columnMapping("slug", "slug")
                .columnMapping("grand_parent_category_id", "grandParentCategory.id")
                .create();

        com.avaje.ebean.Query<Product> query = Ebean.find(Product.class);
        query.setRawSql(rawSql);

        query.setParameter("brandId", brandId);

        List<MapFastSearchProduct> mapFastSearchProducts = new ArrayList<>();
        List<Product> products = query.findList();
        for(Product p : products) {
            MapFastSearchProduct fsp = new ObjectMapper().convertValue(p, MapFastSearchProduct.class);
            fsp.categoryId = p.grandParentCategory.id;
            mapFastSearchProducts.add(fsp);
        }

        return mapFastSearchProducts;
    }
	
	
	
	/* public String imageUrlYT(){
        return getImageLinkYT();
    }
	
	public String getImageLinkYT(){
        return imageUrlYT==null || imageUrlYT.isEmpty() ? "" : Constant.getInstance().getImageUrlYT() + imageUrlYT;
    }*/
}