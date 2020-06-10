package com.enwie.mapper.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author hendriksaragih
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapBanner {
    private String name;
    private String slug;
    private Integer sequence;

    @JsonProperty("open_new_tab")
    private boolean openNewTab;

    @JsonProperty("link_url")
    private String linkUrl;
    @JsonProperty("image_url")
    private String imageLink;
    @JsonProperty("image_url_responsive")
    public String imageUrlResponsive;

    @JsonProperty("meta_title")
    private String metaTitle;
    @JsonProperty("meta_keyword")
    private String metaKeyword;
    @JsonProperty("meta_description")
    private String metaDescription;
    @JsonProperty("product_detail")
    private Long productDetail;
    @JsonProperty("product_detail_slug")
    private String productDetailSlug;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public boolean isOpenNewTab() {
        return openNewTab;
    }

    public void setOpenNewTab(boolean openNewTab) {
        this.openNewTab = openNewTab;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getImageUrlResponsive() {
        return imageUrlResponsive;
    }

    public void setImageUrlResponsive(String imageUrlResponsive) {
        this.imageUrlResponsive = imageUrlResponsive;
    }

    public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public String getMetaKeyword() {
        return metaKeyword;
    }

    public void setMetaKeyword(String metaKeyword) {
        this.metaKeyword = metaKeyword;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public String isLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Long getProductDetail() {
        return productDetail;
    }

    public void setProductDetail(Long productDetail) {
        this.productDetail = productDetail;
    }

    public String getProductDetailSlug() {
        return productDetailSlug;
    }

    public void setProductDetailSlug(String productDetailSlug) {
        this.productDetailSlug = productDetailSlug;
    }
}