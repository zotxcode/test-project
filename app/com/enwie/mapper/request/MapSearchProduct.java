package com.enwie.mapper.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.enwie.mapper.response.MapSearchProductAttribute;

import java.util.List;

/**
 * @author hendriksaragih
 */
public class MapSearchProduct {
    private String search;
    @JsonProperty("category_id")
    private Long categoryId;
    private List<Long> colors;
    private List<Long> sizes;
    @JsonProperty("price_min")
    private Double priceMin;
    @JsonProperty("price_max")
    private Double priceMax;
    @JsonProperty("attributes")
    private List<MapSearchProductAttribute> attributes;
    private String sort;
    private Integer offset;
    private Integer limit;
    private String banner;

    public String getSearch() {
        return search == null ? "" : search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public Long getCategoryId() {
        return categoryId == null ? 0L : categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public List<Long> getColors() {
        return colors;
    }

    public void setColors(List<Long> colors) {
        this.colors = colors;
    }

    public Double getPriceMin() {
        return priceMin;
    }

    public void setPriceMin(Double priceMin) {
        this.priceMin = priceMin;
    }

    public Double getPriceMax() {
        return priceMax;
    }

    public void setPriceMax(Double priceMax) {
        this.priceMax = priceMax;
    }

    public String getSort() {
        return sort == null ? "" : sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Integer getOffset() {
        return offset == null ? 0 : offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit == null ? 30 : limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getBanner() {
        return banner == null ? "" : banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public List<Long> getSizes() {
        return sizes;
    }

    public void setSizes(List<Long> sizes) {
        this.sizes = sizes;
    }

    public List<MapSearchProductAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<MapSearchProductAttribute> attributes) {
        this.attributes = attributes;
    }
}
