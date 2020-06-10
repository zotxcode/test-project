package com.enwie.mapper.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

/**
 * @author hendriksaragih
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapSalesOrder {
    private Long id;
    @JsonProperty("order_number")
    public String orderNumber;
    @JsonProperty("order_date")
    public Date orderDate;
	@JsonProperty("payment_status")
    public String paymentStatus;
	@JsonProperty("total")
    public Double total;
	@JsonProperty("str_payment_type")
    public String strPaymentType;
	@JsonProperty("approved_date")
    public String approvedDate;
	@JsonProperty("str_status")
    public String strStatus;
	@JsonProperty("shipping")
    public Double shipping;
	@JsonProperty("token")
    public String token;
    @JsonProperty("redirect_url")
    public String redirectUrl;

	@JsonProperty("destination_address")
    public MapSalesOrderAddress mapSalesOrderAddress;

	@JsonProperty("order_detail")
    public List<MapSalesOrderDetail> mapSalesOrderDetails;

	@JsonProperty("courier")
    public MapCourier mapCourier;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getStrPaymentType() {
        return strPaymentType;
    }

    public void setStrPaymentType(String strPaymentType) {
        this.strPaymentType = strPaymentType;
    }

    public String getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(String approvedDate) {
        this.approvedDate = approvedDate;
    }

    public String getStrStatus() {
        return strStatus;
    }

    public void setStrStatus(String strStatus) {
        this.strStatus = strStatus;
    }

    public Double getShipping() {
        return shipping;
    }

    public void setShipping(Double shipping) {
        this.shipping = shipping;
    }

    public MapSalesOrderAddress getMapSalesOrderAddress() {
        return mapSalesOrderAddress;
    }

    public void setMapSalesOrderAddress(MapSalesOrderAddress mapSalesOrderAddress) {
        this.mapSalesOrderAddress = mapSalesOrderAddress;
    }

    public List<MapSalesOrderDetail> getMapSalesOrderDetails() {
        return mapSalesOrderDetails;
    }

    public void setMapSalesOrderDetails(List<MapSalesOrderDetail> mapSalesOrderDetails) {
        this.mapSalesOrderDetails = mapSalesOrderDetails;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public MapCourier getMapCourier() {
        return mapCourier;
    }

    public void setMapCourier(MapCourier mapCourier) {
        this.mapCourier = mapCourier;
    }
}