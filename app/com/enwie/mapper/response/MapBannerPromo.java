package com.enwie.mapper.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.enwie.util.CommonFunction;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author hendriksaragih
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapBannerPromo {
    public String name;
    public String description;
    @JsonProperty("image_url")
    public String imageUrl;

    // public boolean status;


    // @JsonProperty("valid_from")
    // public Date validFrom;

    // @JsonProperty("valid_to")
    // public Date validTo;

    // public Date getValidFrom() {
    //     return validFrom;
    // }

    // public void setValidFrom(Date validFrom) {
    //     this.validFrom = validFrom;
    // }

    // public Date getValidTo() {
    //     return validTo;
    // }

    // public void setValidTo(Date validTo) {
    //     this.validTo = validTo;
    // }
    
}