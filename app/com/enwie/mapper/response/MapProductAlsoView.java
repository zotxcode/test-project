package com.enwie.mapper.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * @author hendriksaragih
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapProductAlsoView {
    public Long id;
    public String name;
    public String sku;
    @JsonProperty("slug")
    public String slug;
    @JsonProperty("article_code")
    public String articleCode;
    @JsonProperty("meta_title")
    private String metaTitle;
    @JsonProperty("meta_keyword")
    private String metaKeyword;
    @JsonProperty("meta_description")
    private String metaDescription;
    @JsonProperty("discount_type")
    private Integer discountType;
    @JsonProperty("discount_type_s")
    private String discountTypeS;
    @JsonProperty("discount_active_from")
    public Date discountActiveFrom;
    @JsonProperty("discount_active_to")
    public Date discountActiveTo;
    @JsonProperty("discount_active_from_s")
    public String discountActiveFromS;
    @JsonProperty("discount_active_to_s")
    public String discountActiveToS;
    @JsonProperty("product_type")
    public int productType;

    @JsonProperty("strike_through_display")
    public Double strikeThroughDisplay;
    @JsonProperty("price")
    public Double price;
    @JsonProperty("price_display")
    public Double priceDisplay;
    @JsonProperty("discount")
    public Double discount;
    @JsonProperty("average_rating")
    public float averageRating;
    @JsonProperty("count_rating")
    public float countRating;

    @JsonProperty("image_url")
    public String imageUrl;
    public Long stock;

}