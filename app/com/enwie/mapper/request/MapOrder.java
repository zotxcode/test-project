package com.enwie.mapper.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author hendriksaragih
 */
public class MapOrder {
    private List<String> vouchers;
    private MapAddress address;
    @JsonProperty("bank_id")
    private Long bankId;
    @JsonProperty("courier_id")
    private Long courierId;
    @JsonProperty("shipping_address_id")
    private Long shippingAddressId;
    private String service;
    @JsonProperty("payment_service")
    private Integer paymentService;

    public List<String> getVouchers() {
        return vouchers;
    }

    public void setVouchers(List<String> vouchers) {
        this.vouchers = vouchers;
    }

    public MapAddress getAddress() {
        return address;
    }

    public void setAddress(MapAddress address) {
        this.address = address;
    }

    public Long getBankId() {
        return bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public Long getCourierId() {
        return courierId;
    }

    public void setCourierId(Long courierId) {
        this.courierId = courierId;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Integer getPaymentService() {
        return  paymentService;
    }

    public void setPaymentService(Integer paymentService) {
        this.paymentService = paymentService;
    }

    public Long getShippingAddressId() {
        return shippingAddressId;
    }

    public void setShippingAddressId(Long shippingAddressId) {
        this.shippingAddressId = shippingAddressId;
    }
}
