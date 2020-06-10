package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;
import java.util.List;

/**
 * @author hendriksaragih
 */
@Entity
public class Feature extends BaseModel{
    //overloading data from BaseModel to be ignored
    @JsonIgnore
    public Long id;
    @JsonIgnore
    public Date createdAt;
    @JsonIgnore
    public Date updatedAt;
    @JsonIgnore
    public boolean isDeleted;


    public String name;
    @Column(unique=true, updatable=false)
    public String key;
    public String section;
    public String description;
    @JsonProperty("is_active")
    public boolean isActive;

    //    @JsonIgnore
//    @ManyToMany(mappedBy="features", cascade=CascadeType.ALL)
//    public List<Feature> roles;
    public int sequence;

    public static Finder<Long, Feature> find = new Finder<Long, Feature>(Long.class, Feature.class);

    public Feature(String name, String section, String description, boolean isActive){
        super();
        this.name        = name;
        this.key         = name.toLowerCase().replace(' ', '_');
        this.section     = section;
        this.description = description;
        this.isActive    = isActive;
    }


    public static List<Feature> getFeaturesByRole(Long id){
        String sql = "SELECT f.id, f.name, f.section FROM feature f " +
                "LEFT JOIN role_feature rf ON f.id = rf.feature_id " +
                "LEFT JOIN role r ON r.id=rf.role_id " +
                "WHERE r.id = "+id+" " +
                "AND f.is_deleted = FALSE " +
                "AND f.is_active = TRUE " +
                "ORDER BY sequence ASC";

        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("f.id", "id")
                .columnMapping("f.name", "name")
                .columnMapping("f.section", "section")
                .create();
        com.avaje.ebean.Query<Feature> query = Ebean.find(Feature.class);
        query.setRawSql(rawSql);
        List<Feature> resData = query.findList();

        return resData;
    }

}