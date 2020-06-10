package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.enwie.util.Constant;

import javax.persistence.*;
import java.util.List;

/**
 * @author hendriksaragih
 */
@Entity
public class Allergy extends BaseModel {

    private static final long serialVersionUID = 1L;
    public static final boolean ACTIVE = true;
    public static final boolean INACTIVE = false;

    @JsonProperty("name")
    public String name;
    
    @JsonIgnore
    @Transient
    public String save;

    @JsonIgnore
    @ManyToMany
    public List<Member> members;

    public static Finder<Long, Allergy> find = new Finder<>(Long.class, Allergy.class);

    public static Page<Allergy> page(int page, int pageSize, String sortBy, String order, String name, Integer filter) {
        ExpressionList<Allergy> qry = Allergy.find
                .where()
                .ilike("name", "%" + name + "%")
                .eq("is_deleted", false);

        return
                qry.orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);
    }

    public static int findRowCount() {
        return
                find.where()
                        .eq("is_deleted", false)
                        .findRowCount();
    }

    public static List<Allergy> getAllData() {
        return find.where()
                .eq("is_deleted", false).findList();
    }

    public static List<Allergy> getAllData(Long memberId) {
        return find.where()
                .eq("member.id", memberId)
                .eq("is_deleted", false).findList();
    }

    public static Allergy findById(Long id) {
        return find.where().eq("is_deleted", false).eq("id", id).findUnique();
    }

}