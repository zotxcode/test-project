package com.enwie.mapper.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by hendriksaragih on 3/2/17.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapMember {
    private Long id;
    @JsonProperty("full_name")
    public String fullName;
    public String email;
    public String phone;
    public String gender;
    @JsonProperty("birth_day")
    public String birthDay;
    @JsonProperty("news_letter")
    public Boolean newsLetter;
    @JsonProperty("address")
    public String address;
    @JsonProperty("province_id")
    public Long provinceId;
    @JsonProperty("city_id")
    public Long cityId;
    @JsonProperty("image_url")
    public String imageUrl;
    // public MapSalesOrderAddress lastOrderAddress;

    @JsonProperty("allergies")
    public List<MapAllergy> allergies;
    
    @JsonProperty("shipping_addresses")
    public List<MapShippingAddress> shippingAddresses;

    // public MapSalesOrderAddress getLastOrderAddress() {
    //     return lastOrderAddress;
    // }

    // public void setLastOrderAddress(MapSalesOrderAddress lastOrderAddress) {
    //     this.lastOrderAddress = lastOrderAddress;
    // }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Boolean getNewsLetter() {
        return newsLetter;
    }

    public void setNewsLetter(Boolean newsLetter) {
        this.newsLetter = newsLetter;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Long provinceId) {
        this.provinceId = provinceId;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

}
