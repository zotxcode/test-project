package com.enwie.mapper.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * @author hendriksaragih
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapAttributeAll {
    private Long id;
    private String name;
    private List<MapAttributeFilter> values;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MapAttributeFilter> getValues() {
        return values;
    }

    public void setValues(List<MapAttributeFilter> values) {
        this.values = values;
    }
}