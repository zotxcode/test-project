package com.enwie.mapper.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author hendriksaragih
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapProductList {
    @JsonProperty("name")
    public String name;
    @JsonProperty("id")
    public String id;
    @JsonProperty("product_code")
    public String productCode;
    @JsonProperty("slug")
    public String slug;

    @JsonProperty("strike_through_display")
    public Double strikeThroughDisplay;
    @JsonProperty("price")
    public Double price;
    @JsonProperty("discount")
    public int discount;
    @JsonProperty("discount_type")
    private Integer discountType;
    @JsonProperty("average_rating")
    public float averageRating;
    @JsonProperty("count_rating")
    public float countRating;
    @JsonProperty("price_display")
    public Double priceDisplay;
    @JsonProperty("num_of_order")
    public Integer numOfOrder;

    @JsonProperty("image_url")
    public String imageUrl;

    @JsonProperty("is_like")
    public Boolean isLike;

    @JsonProperty("num_like")
    public Integer numLike;

    @JsonProperty("real_like")
    public Integer realLike;

    @JsonProperty("item_count")
    public Long itemCount;

    @JsonProperty("product_images")
    public List<MapProductImage> productImages;

    @JsonProperty("product_colors")
    public List<MapProductColor> productColors;

    private MapSize[] sizes;
}
