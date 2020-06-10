package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

/**
 * @author hendriksaragih
 */
@Entity
@Table(name = "fashion_size")
public class Size extends BaseModel {
    private static final long serialVersionUID = 1L;

    public String international;
    public int sequence;

    @JsonIgnore
    @JoinColumn(name = "user_id")
    @ManyToOne
    public UserCms userCms;
    @ManyToOne(cascade = { CascadeType.ALL })
    public Brand brand;

    @Transient
    public String save;

    public static Finder<Long, Size> find = new Finder<>(Long.class, Size.class);

    public static Page<Size> page(int page, int pageSize, String sortBy, String order, String name, Long brandId) {
        ExpressionList<Size> qry = Size.find
                .where()
                .ilike("international", "%" + name + "%")
                .eq("is_deleted", false)
                .eq("brand_id", brandId);

        return
                qry.orderBy("sequence asc")
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);
    }

    public static int findRowCount(Long brandId) {
        return
                find.where()
                        .eq("is_deleted", false)
                        .eq("brand_id", brandId)
                        .findRowCount();
    }

    public static Size findById(Long id, Long brandId) {
        return find.where().eq("is_deleted", false).eq("id", id).eq("brand_id", brandId).findUnique();
    }

    public static List<Size> getSize(Long brandId) {
        return find
                .where()
                .eq("is_deleted", false)
                .eq("brand_id", brandId)
                .order("international asc").findList();
    }
}
