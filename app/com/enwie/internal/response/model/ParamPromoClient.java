package com.enwie.internal.response.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author hilmanzaky
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ParamPromoClient {
    private String username;
    private String fullname;
    private Integer duration;
    private Integer cutAmount;
    private Integer minimalAmount;
    private String couponPrefix;
    private String client;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getCutAmount() {
        return cutAmount;
    }

    public void setCutAmount(Integer cutAmount) {
        this.cutAmount = cutAmount;
    }

    public Integer getMinimalAmount() {
        return minimalAmount;
    }

    public void setMinimalAmount(Integer minimalAmount) {
        this.minimalAmount = minimalAmount;
    }

    public String getCouponPrefix() {
        return couponPrefix;
    }

    public void setCouponPrefix(String couponPrefix) {
        this.couponPrefix = couponPrefix;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }
}
