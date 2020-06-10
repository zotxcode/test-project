package com.enwie.mapper.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author hendriksaragih
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapSocmed {
    private String name;
    private String url;
    @JsonProperty("image_url")
    private String imageLink;
    @JsonProperty("image_url_responsive")
    public String imageUrlResponsive;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
}