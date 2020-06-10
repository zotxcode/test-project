package com.enwie.mapper.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author hilmanzaky
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapSalesOrderDetail {
    private Long id;
    @JsonProperty("sales_order_id")
    public Long salesOrderId;
    @JsonProperty("product_id")
    public Long productId;
    @JsonProperty("product_name")
    public String productName;
    @JsonProperty("price")
    public Double price;
    @JsonProperty("quantity")
    public Integer quantity;
    @JsonProperty("discount_persen")
    public Double discountPersen;
    @JsonProperty("discount_amount")
    public Double discountAmount;
    @JsonProperty("sub_total")
    public Double subTotal;
    @JsonProperty("total_price")
    public Double totalPrice;
    @JsonProperty("voucher")
    public Double voucher;
    @JsonProperty("image_url")
    public String imageUrl;
    public List<MapOrderDetailVoucherDiscount> discounts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSalesOrderId() {
        return salesOrderId;
    }

    public void setSalesOrderId(Long salesOrderId) {
        this.salesOrderId = salesOrderId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getDiscountPersen() {
        return discountPersen;
    }

    public void setDiscountPersen(Double discountPersen) {
        this.discountPersen = discountPersen;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getVoucher() {
        return voucher;
    }

    public void setVoucher(Double voucher) {
        this.voucher = voucher;
    }
}
