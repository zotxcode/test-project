package com.enwie.mapper.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author hendriksaragih
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MapMidtransNotification {
    @JsonProperty("transaction_time")
    private String transactionTime;
    @JsonProperty("transaction_status")
    private String transactionStatus;
    @JsonProperty("transaction_id")
    private String transactionId;
    @JsonProperty("status_message")
    private String statusMessage;
    @JsonProperty("status_code")
    private String statusCode;
    @JsonProperty("signature_key")
    private String signatureKey;
    @JsonProperty("payment_type")
    private String paymentType;
    @JsonProperty("order_id")
    private String orderId;
    @JsonProperty("merchant_id")
    private String merchantId;
    @JsonProperty("gross_amount")
    private String grossAmount;
    @JsonProperty("fraud_status")
    private String fraudStatus;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("store")
    private String store;
    @JsonProperty("bank")
    private String bank;
    @JsonProperty("approval_code")
    private String approvalCode;
    @JsonProperty("permata_va_number")
    private String permataVaNumber;
    @JsonProperty("va_numbers")
    private List<MapMidtransVA> vaNumbers;

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getSignatureKey() {
        return signatureKey;
    }

    public void setSignatureKey(String signatureKey) {
        this.signatureKey = signatureKey;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(String grossAmount) {
        this.grossAmount = grossAmount;
    }

    public String getFraudStatus() {
        return fraudStatus;
    }

    public void setFraudStatus(String fraudStatus) {
        this.fraudStatus = fraudStatus;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getApprovalCode() {
        return approvalCode;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }

    public List<MapMidtransVA> getVaNumbers() {
        return vaNumbers;
    }

    public void setVaNumbers(List<MapMidtransVA> vaNumbers) {
        this.vaNumbers = vaNumbers;
    }

    public String getPermataVaNumber() {
        return permataVaNumber;
    }

    public void setPermataVaNumber(String permataVaNumber) {
        this.permataVaNumber = permataVaNumber;
    }
}
