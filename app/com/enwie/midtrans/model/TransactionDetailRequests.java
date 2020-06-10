package com.enwie.midtrans.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author hendriksaragih
 */
public class TransactionDetailRequests {
    @JsonProperty("order_id")
    private String orderId;
    @JsonProperty("gross_amount")
    private Integer grossAmount;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(Integer grossAmount) {
        this.grossAmount = grossAmount;
    }
}
