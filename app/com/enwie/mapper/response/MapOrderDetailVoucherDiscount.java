package com.enwie.mapper.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author hilmanzaky
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapOrderDetailVoucherDiscount {
    private Long id;

    @JsonProperty("product_id")
    public Long productId;

    @JsonProperty("voucher_code")
    public String voucherCode;

    @JsonProperty("voucher_discount")
    public Double voucherDiscount;
}
