package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.enwie.mapper.response.MapAttribute;
import com.enwie.mapper.response.MapKeyValue;
import com.enwie.mapper.response.MapProductImage;
import com.enwie.util.Constant;

import javax.persistence.*;
import java.util.*;

/**
 * @author hendriksaragih
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDetail extends BaseModel {
    @JsonProperty("product_name")
    public String productName;
    @Column(columnDefinition = "text")
    @JsonProperty("short_description")
    public String shortDescriptions;
    @Column(columnDefinition = "text")
    public String description;
    public Double weight;
    public Double dimension1;
    public Double dimension2;
    public Double dimension3;

    @Column(name = "warranty_type", columnDefinition = "integer default 0")
    @JsonProperty("warranty_type")
    public int warrantyType;
    @JsonProperty("warranty_period")
    @Column(name = "warranty_period", columnDefinition = "integer default 0")
    public int warrantyPeriod;
    @JsonProperty("sold_fulfilled_by")
    public String soldFulfilledBy;
    @Column(columnDefinition = "text")
    @JsonProperty("what_in_the_box")
    public String whatInTheBox;

    @Column(columnDefinition = "text")
    @JsonProperty("specifications")
    public String specifications;

    @Column(columnDefinition = "text")
    @JsonProperty("feature")
    public String feature;

    @Column(columnDefinition = "text")
    @JsonProperty("recomended_care")
    public String recomendedCare;

    @Column(columnDefinition = "text")
    @JsonProperty("waranty")
    public String waranty;

    @JsonProperty("total_stock")
    public long totalStock;
    @JsonProperty("free_stock")
    public long freeStock;
    @JsonProperty("reserved_stock")
    public long reservedStock;

    public boolean stock;
    @JsonProperty("limited_stock")
    public boolean limitedStock;
    @JsonProperty("stock_counter")
    public boolean stockCounter;

    @JsonProperty("url_youtube")
    @Column(name = "url_youtube")
    public String embededYoutube;

    @JsonProperty("thumbnail_youtube")
    @Column(name = "thumbnail_youtube")
    public String thumbnailYoutubeUrl;

    public boolean published;

    @JsonIgnore
    @Column(name = "full_image_urls", columnDefinition = "TEXT")
    public String fullImageUrls;
    @JsonIgnore
    @Column(name = "medium_image_urls", columnDefinition = "TEXT")
    public String mediumImageUrls;
    @JsonIgnore
    @Column(name = "thumbnail_image_urls", columnDefinition = "TEXT")
    public String thumbnailImageUrls;
    @JsonIgnore
    @Column(name = "blur_image_urls", columnDefinition = "TEXT")
    public String blurImageUrls;
    @JsonIgnore
    @Column(name = "threesixty_image_urls", columnDefinition = "TEXT")
    public String threesixtyImageUrls;
    @JsonIgnore
    @Column(name = "color_image_urls", columnDefinition = "TEXT")
    public String colorImageUrls;

    @Column(name = "size_guide", columnDefinition = "text")
    public String sizeGuide;

    @ManyToOne(cascade = { CascadeType.ALL })
    public Brand brand;

    @javax.persistence.Transient
    @JsonProperty("dimension_x")
    public Double getDimensionX(){
        return dimension1;
    }
    @javax.persistence.Transient
    @JsonProperty("dimension_y")
    public Double getDimensionY(){
        return dimension2;
    }
    @javax.persistence.Transient
    @JsonProperty("dimension_z")
    public Double getDimensionZ(){
        return dimension3;
    }

    @javax.persistence.Transient
    @JsonProperty("warranty")
    public String getWarranty(){
        String warranty = "";
        if (warrantyType != 0){
            warranty += warrantyPeriod + " Month ";
        }

        return warranty + getWarrantyType(warrantyType);
    }

    @javax.persistence.Transient
    @JsonProperty("dimension")
    public String getDimension(){
        return dimension1 + " x "+dimension2+" x "+dimension3;
    }

    public List<String> getShortDescriptions(){
        if(shortDescriptions != null && !shortDescriptions.equals("")){
            return Arrays.asList(shortDescriptions.split("##"));
        }else return new ArrayList<String>();
    }

    public String getFullUrlImage(String image){
        return image==null || image.isEmpty() ? "" : Constant.getInstance().getImageUrl() + image;
    }

    @javax.persistence.Transient
    @JsonProperty("thumbnail_youtube_url")
    public String getThumbnailUrl(){
        return getFullUrlImage(thumbnailYoutubeUrl);
    }



    public String[] getImage3Link(){
        String[] links = getImage3();

        for(int i = 0; i < links.length; i++){
            if(links[i] == null || links[i].isEmpty() ){
                links[i] = "";
            }else{
                links[i] = Constant.getInstance().getImageUrl() + links[i];
            }

        }

        return links;
    }

    public String[] getImageColorLink(){
        String[] links = getImageColor();

        for(int i = 0; i < links.length; i++){
            if(links[i] == null || links[i].isEmpty() ){
                links[i] = "";
            }else{
                links[i] = Constant.getInstance().getImageUrl() + links[i];
            }

        }

        return links;
    }

    public HashMap<String,String> getImage3And1Link(){
        HashMap<String, String> result = new HashMap<>();
        String[] links5 = getImage3();
        String[] links1 = getImage1();

        for(int i = 0; i < links5.length; i++){
            if(links5[i] != null && !links5[i].isEmpty() && links1[i] != null && !links1[i].isEmpty()){
                result.put(Constant.getInstance().getImageUrl() + links5[i], Constant.getInstance().getImageUrl() + links1[i]);
            }

        }

        return result;
    }

    @Transient
    @JsonProperty("full_image_url")
    public String[] getImage1() {
        try {
            ObjectMapper om = new ObjectMapper();
            return om.readValue(fullImageUrls, String[].class);
        } catch (Exception e) {
            // e.printStackTrace();
            return new String[0];
        }
    }

    @Transient
    @JsonProperty("medium_image_url")
    public String[] getImage2() {
        try {
            ObjectMapper om = new ObjectMapper();
            return om.readValue(mediumImageUrls, String[].class);
        } catch (Exception e) {
            // e.printStackTrace();
            return new String[0];
        }
    }

    @Transient
    @JsonProperty("thumbnail_image_url")
    public String[] getImage3() {
        try {
            ObjectMapper om = new ObjectMapper();
            return om.readValue(thumbnailImageUrls, String[].class);
        } catch (Exception e) {
            // e.printStackTrace();
            return new String[0];
        }
    }

    @Transient
    @JsonProperty("blur_image_url")
    public String[] getImage4() {
        try {
            ObjectMapper om = new ObjectMapper();
            return om.readValue(blurImageUrls, String[].class);
        } catch (Exception e) {
            // e.printStackTrace();
            return new String[0];
        }
    }

    @Transient
    @JsonProperty("threesixty_image_url")
    public String[] getImage5() {
        try {
            ObjectMapper om = new ObjectMapper();
            return om.readValue(threesixtyImageUrls, String[].class);
        } catch (Exception e) {
            // e.printStackTrace();
            return new String[0];
        }
    }

    public String[] getImageColor() {
        String[] results = new String[5];
        try {
            ObjectMapper om = new ObjectMapper();
            results = om.readValue(colorImageUrls, String[].class);
        } catch (Exception e) {
            for (int i = 0; i < 5; i++) {
                results[i] = "";
            }
        }

        return results;
    }


    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    @JsonIgnore
    public Product mainProduct;

    @Transient
    @JsonProperty("main_product_id")
    public Long getMainProductId() {
        if (mainProduct != null)
            return mainProduct.id;
        return null;
    }

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    public List<WishList> wishlist;



    public static Map<Integer, String> getListWarrantyType(){
        Map<Integer, String> result = new LinkedHashMap<>();
        result.put(0, "No Warranty");
        result.put(1, "Warranty");
        return result;
    }

    public String getWarrantyType(int id){
        Map<Integer, String> map = getListWarrantyType();
        String result = "";
        if(map.containsKey(id)){
            result = map.get(id).toString();
        }
        return result;
    }

    public static Finder<Long, ProductDetail> find = new Finder<>(Long.class, ProductDetail.class);

    public ProductDetail() {

    }

    public ProductDetail(Product main, Double listPrice, Double offerPrice, boolean stock,
                         boolean limitedStock, boolean stockCounter, Long stockCount, boolean published, Brand brand) {
        this.mainProduct = main;
        this.productName = main.name;
        this.stock = stock;
        this.limitedStock = limitedStock;
        this.stockCounter = stockCounter;
        this.totalStock = stockCount;
        this.reservedStock = stockCount;
        this.brand = brand;

        this.published = published;
        this.save();
    }

    public void setShortDescriptions(List<String> list){
        shortDescriptions = String.join("##", list);
    }

    public void setAttribute(){
        setAttribute(false);
    }

    @javax.persistence.Transient
    @JsonProperty("attributes")
    public List<MapKeyValue> attributes;

    @javax.persistence.Transient
    @JsonProperty("attributes_s")
    public List<MapAttribute> attributesMerchant;

    public void setAttribute(Boolean fromMerchant){
        attributes = new LinkedList<>();
        if (!fromMerchant){
            attributes.add(setKV("SKU", mainProduct.sku, null));
            for(Attribute attr : mainProduct.attributes){
                attributes.add(setKV(attr.baseAttribute.name, attr.getName(), attr.additional));
            }
            attributes.add(setKV("Warranty period", warrantyPeriod+" Month", null));
            attributes.add(setKV("Warranty type", getWarrantyType(warrantyType), null));
        }else{
            attributesMerchant = new LinkedList<>();
            for(Attribute attr : mainProduct.attributes){
                attributes.add(setKV(attr.baseAttribute.name, attr.getName(), attr.additional));
                attributesMerchant.add(new MapAttribute(attr.baseAttribute.id, attr.id, attr.baseAttribute.name, attr.getName()));
            }
        }
    }

    @javax.persistence.Transient
    @JsonProperty("product_images")
    public List<MapProductImage> getProductImages(){
        List<MapProductImage> images = new ArrayList<>();
        String[] image1 = getImage1();
        String[] image2 = getImage2();
        String[] image3 = getImage3();
        String[] image4 = getImageColor();

        int i = 0;
        for(String image:image1){
            if (image!= null && !image.isEmpty()){
                MapProductImage img = new MapProductImage();
                img.setFullImageUrl(getFullUrlImage(image));
                img.setMediumImageUrl(getFullUrlImage(image2[i]));
                img.setThumbnailImageUrl(getFullUrlImage(image3[i]));
                img.setColorImageUrl("");
                if (image4!=null && image4.length > i){
                    img.setColorImageUrl(getFullUrlImage(image4[i]));
                }

                images.add(img);
            }
            i++;
        }
        return images;
    }

    public MapKeyValue setKV(String name, String value, String additional){
        MapKeyValue data = new MapKeyValue();
        data.setName(name);
        data.setValue(value);
        data.setAdditional(additional);
        return data;
    }

    public Double getWeight() {
        return weight == null ? 0D : weight;
    }
}