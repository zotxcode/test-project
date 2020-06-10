package com.enwie.mapper.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author hendriksaragih
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapVariantDetail {
    private String id;
    private String value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
