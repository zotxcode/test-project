package com.enwie.mapper.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Set;

/**
 * @author hendriksaragih
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapVariant {
    private String id;
    private String name;
    private Set<MapVariantDetail> attributes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<MapVariantDetail> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<MapVariantDetail> attributes) {
        this.attributes = attributes;
    }
}