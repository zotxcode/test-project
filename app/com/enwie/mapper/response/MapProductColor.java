package com.enwie.mapper.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author hendriksaragih
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapProductColor {
    private String image;
    @JsonProperty("product_images")
    public List<MapProductImage> productImages;
    @JsonProperty("product_id")
    private Long productId;
    @JsonProperty("product_slug")
    private String productSlug;
    @JsonProperty("color")
    private String color;
    @JsonProperty("is_active")
    private Boolean isActive;

    public MapProductColor(){

    }

    public MapProductColor(String image, Long productId, String productSlug, String color, List<MapProductImage> productImages, Boolean isActive) {
        this.image = image;
        this.productId = productId;
        this.productSlug = productSlug;
        this.color = color;
        this.productImages = productImages;
        this.isActive = isActive;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getProductSlug() {
        return productSlug;
    }

    public void setProductSlug(String productSlug) {
        this.productSlug = productSlug;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<MapProductImage> getProductImages() {
        return productImages;
    }

    public void setProductImages(List<MapProductImage> productImages) {
        this.productImages = productImages;
    }

}