package com.enwie.mapper.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

/**
 * Created by hendriksaragih on 3/24/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MapArticleDetail {
    public Long id;
    private String title;
    private String slug;
    @JsonProperty("image_name")
    private String imageName;
    @JsonProperty("image_title")
    private String imageTitle;
    @JsonProperty("image_alternate")
    private String imageAlternate;
    @JsonProperty("image_description")
    private String imageDescription;
    @JsonProperty("image_url")
    private String imageUrl;
    @JsonProperty("meta_title")
    private String metaTitle;
    @JsonProperty("meta_keyword")
    private String metaKeyword;
    @JsonProperty("meta_description")
    private String metaDescription;
    @JsonProperty("created_at_str")
    private String createdAtStr;
    private String content;
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    public Date createdAt;
    private List<MapArticleComment> comments;
    @JsonProperty("tags")
    private List<String> tags;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public String getMetaKeyword() {
        return metaKeyword;
    }

    public void setMetaKeyword(String metaKeyword) {
        this.metaKeyword = metaKeyword;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<MapArticleComment> getComments() {
        return comments;
    }

    public void setComments(List<MapArticleComment> comments) {
        this.comments = comments;
    }

    public String getCreatedAtStr() {
        return createdAtStr;
    }

    public void setCreatedAtStr(String createdAtStr) {
        this.createdAtStr = createdAtStr;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageTitle() {
        return imageTitle;
    }

    public void setImageTitle(String imageTitle) {
        this.imageTitle = imageTitle;
    }

    public String getImageAlternate() {
        return imageAlternate;
    }

    public void setImageAlternate(String imageAlternate) {
        this.imageAlternate = imageAlternate;
    }

    public String getImageDescription() {
        return imageDescription;
    }

    public void setImageDescription(String imageDescription) {
        this.imageDescription = imageDescription;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
