package com.enwie.mapper.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author hendriksaragih
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapCart {
    private Long id;
    @JsonProperty("product_id")
    public Long productId;
    @JsonProperty("product_name")
    public String productName;
    @JsonProperty("product_image")
    public String productImage;
    @JsonProperty("product_price")
    public Double productPrice;
    @JsonProperty("product_discount")
    public Double productDiscount;
    public Integer qty;
    @JsonProperty("sub_total")
    public Double subTotal;
    public Double total;
    @JsonProperty("total_voucher_discounts")
    public Double totalVoucherDiscounts;
    @JsonProperty("voucher_discounts")
    public List<MapCartVoucherDiscount> voucherDiscounts;

    public Double getTotalVoucherDiscounts() {
        return totalVoucherDiscounts;
    }

    public void setTotalVoucherDiscount(Double totalVoucherDiscounts) {
        this.totalVoucherDiscounts = totalVoucherDiscounts;
    }

    public List<MapCartVoucherDiscount> getVoucherDiscounts() {
        return voucherDiscounts;
    }

    public void setVoucherDiscounts(List<MapCartVoucherDiscount> voucherDiscounts) {
        this.voucherDiscounts = voucherDiscounts;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public Double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Double productPrice) {
        this.productPrice = productPrice;
    }

    public Double getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(Double productDiscount) {
        this.productDiscount = productDiscount;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}