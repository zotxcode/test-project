package com.enwie.mapper.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by hilmanzaky
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MapVoucherCode {
    @JsonProperty("voucher_codes")
    private List<String> voucherCodes;

    public List<String> getVoucherCodes() {
        return voucherCodes;
    }

    public void setVoucherCodes(List<String> voucherCodes) {
        this.voucherCodes = voucherCodes;
    }
}
