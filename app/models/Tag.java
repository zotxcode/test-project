package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.*;

/**
 * @author hendriksaragih
 */
@Entity
public class Tag extends BaseModel {
    public String name;

    @JsonIgnore
    @ManyToMany
    public List<Article> articles;

    @ManyToOne(cascade = { CascadeType.ALL })
    public Brand brand;

    @JsonIgnore
    @ManyToMany
    public List<Product> products;

    public static Finder<Long, Tag> find = new Finder<Long, Tag>(Long.class, Tag.class);


    public static List<Tag> applyTag(String stringTags, Brand brand) {
        return applyTag(Arrays.asList(stringTags.split(",")), brand);
    }

    public static List<Tag> applyTag(List<String> set, Brand brand) {
        Set<String> sets = new HashSet<>(set);
        List<Tag> result = new ArrayList<>();
        for (String name : sets) {
            if (!name.trim().equals("")) {
                Tag check = Tag.find.where().ieq("name", name).eq("brand", brand).findUnique();
                if (check == null) {
                    check = new Tag();
                    check.brand = brand;
                    check.name = name.toLowerCase();
                    check.save();
                }
                result.add(check);
            }
        }
        return result;
    }


}