package com.enwie.mapper.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MapGooglePeople {

    @JsonProperty("id")
    private String id;

    @JsonProperty("displayName")
    private String displayName;

    @JsonProperty("image")
    private MapGooglePeopleImage image;

    @JsonProperty("emails")
    private List<MapGooglePeopleEmail> emails;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public MapGooglePeopleImage getImage() {
        return image;
    }

    public void setImage(MapGooglePeopleImage image) {
        this.image = image;
    }

    public List<MapGooglePeopleEmail> getEmails() {
        return emails;
    }

    public void setEmails(List<MapGooglePeopleEmail> emails) {
        this.emails = emails;
    }
}
