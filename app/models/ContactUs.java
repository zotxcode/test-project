package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.enwie.util.CommonFunction;

import javax.persistence.*;

/**
 * @author hendriksaragih
 */
@Entity
public class ContactUs extends BaseModel{
    private static final long serialVersionUID = 1L;

    public String email;
    public String name;
    @Column(columnDefinition = "TEXT")
    public String content;
    @ManyToOne(cascade = { CascadeType.ALL })
    public Brand brand;

    @javax.persistence.Transient
    public String save;


    public static Finder<Long, ContactUs> find = new Finder<>(Long.class, ContactUs.class);


    public static String validate(String email, String name, String content) {

        if (name == null || name.isEmpty()) {
            return "Name must not empty.";
        }
        if (email == null || email.isEmpty()) {
            return "Email must not empty.";
        }else if (!email.matches(CommonFunction.emailRegex)) {
            return "Email format not valid.";
        }
        if (content == null || content.isEmpty()) {
            return "Content must not empty.";
        }

        return null;
    }

    public static Page<ContactUs> page(int page, int pageSize, String sortBy, String order, String name, int filter, Brand brand) {
        ExpressionList<ContactUs> qry = ContactUs.find
                .where()
                .ilike("name", "%" + name + "%")
                .eq("is_deleted", false)
                .eq("brand", brand);

        if(filter != -1){
            qry.eq("type", filter);
        }

        return
                qry.orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);
    }

    public String getCreatedDate(){
        return CommonFunction.getDateTime2(createdAt);
    }

    public static Integer RowCount(Brand brand) {
        return find.where().eq("is_deleted", false).eq("brand", brand).findRowCount();
    }

    public static ContactUs findById(Long id, Long brandId) {
        return find.where().eq("is_deleted", false).eq("id", id).eq("brand_id", brandId).findUnique();
    }

}