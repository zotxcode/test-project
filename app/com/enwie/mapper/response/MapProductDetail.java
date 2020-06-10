package com.enwie.mapper.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author hendriksaragih
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapProductDetail {

    public Double weight;
    @JsonProperty("dimension_x")
    public Double dimensionX;
    @JsonProperty("dimension_y")
    public Double dimensionY;
    @JsonProperty("dimension_z")
    public Double dimensionZ;
    @JsonProperty("what_in_the_box")
    public String whatInTheBox;

    public String warranty;
    public String description;
    public String dimension;
    public List<MapKeyValue> attributes;
    @JsonProperty("attributes_s")
    public List<MapAttribute> attributesS;
    @JsonProperty("short_description")
    public List<String> shortDescription;
    @JsonProperty("product_images")
    public List<MapProductImage> productImages;

    @JsonProperty("warranty_type")
    public int warrantyType;
    @JsonProperty("warranty_period")
    public int warrantyPeriod;

    @JsonProperty("recomended_care")
    public String recomendedCare;
    @JsonProperty("waranty")
    public String waranty;
    @JsonProperty("feature")
    public String feature;
    @JsonProperty("specifications")
    public String specifications;

    @JsonProperty("url_youtube")
    public String embededYoutube;

    @JsonProperty("thumbnail_youtube_url")
    public String thumbnailYoutubeUrl;

}