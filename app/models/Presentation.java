package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.enwie.mapper.response.MapAttribute;
import com.enwie.mapper.response.MapKeyValue;
// import com.enwie.mapper.response.MapProductImage;
import com.enwie.util.Constant;

import javax.persistence.*;
import java.util.*;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Presentation extends BaseModel {
    public static final boolean ACTIVE = true;
    public static final boolean INACTIVE = false;

    public boolean status;
    public String title;
    @JsonProperty("short_desc")
    public String shortDesc;
    @Column(columnDefinition = "text")
    public String description;
    public String author;
    
    @JsonIgnore
    @Column(name = "image_urls", columnDefinition = "TEXT")
    public String imageUrls;

    @Transient
    @JsonProperty("image_urls")
    public ArrayList<String> getImageUrl() {
        ArrayList<String> imgUrls = new ArrayList<String>();
        String[] imgFilenames = Optional.ofNullable(imageUrls).orElse("").split(",");
        for (String imgFname : imgFilenames) {
            imgUrls.add(getFullUrlImage(imgFname));
        }
        return imgUrls;
    }

    public String getFullUrlImage(String image){
        return image==null || image.isEmpty() ? "" : Constant.getInstance().getImageUrl() + "presentation/" + image;
    }

    @javax.persistence.Transient
    public String save;


    public static Finder<Long, Presentation> find = new Finder<>(Long.class, Presentation.class);

    public Presentation() {
        super();
    }

    public Presentation(Presentation pres) {
        this.status = pres.status;
        this.title = pres.title;
        this.shortDesc = pres.shortDesc;
        this.save = pres.save;
    }

    public static int findRowCount() {
        return
                find.where()
                        .eq("is_deleted", false)
                        .findRowCount();
    }

    public static Presentation findById(Long id) {
        return find.where().eq("is_deleted", false).eq("id", id).findUnique();
    }

}