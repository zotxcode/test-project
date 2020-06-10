package models;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.enwie.mapper.response.MapProductColor;
import com.enwie.mapper.response.MapVariantGroup;
import com.enwie.mapper.response.MapVariantGroupList;
import com.enwie.util.Constant;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author hendriksaragih
 */
@Entity
public class ProductVariantGroup extends BaseModel{
    private static final long serialVersionUID = 1L;
    private static final String LOG_TYPE = "ADMIN";
    private static final String LOG_TABLE_NAME = "product_variant_group";

    public String name;

    @JsonIgnore
    @ManyToMany
    public List<BaseAttribute> baseAttributes;

    @JsonIgnore
    @JoinColumn(name="lowest_price_product")
    @ManyToOne
    public Product lowestPriceProduct;

    @JsonIgnore
    @JoinColumn(name="user_id")
    @ManyToOne
    public UserCms userCms;

    @ManyToOne
    @JsonIgnore
    public Brand brand;

    @javax.persistence.Transient
    public String save;

    @javax.persistence.Transient
    public List<String> product_list;

    @javax.persistence.Transient
    public List<String> base_attribute_list;

    @Transient
    @JsonProperty("user_creator")
    public String getUserCreator(){
        return userCms.email;
    }

    public static Finder<Long, ProductVariantGroup> find = new Finder<>(Long.class, ProductVariantGroup.class);

    public static Page<ProductVariantGroup> page(int page, int pageSize, String sortBy, String order, String filter, Long brandId) {
        return
                find.where()
                        .ilike("name", "%" + filter + "%")
                        .eq("is_deleted", false)
                        .eq("brand_id", brandId)
                        .orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);
    }

    public static ProductVariantGroup findById(Long id, Long brandId) {
        return find.where().eq("is_deleted", false).eq("id", id).eq("brand_id", brandId).findUnique();
    }

    public static int findRowCount(Long brandId) {
        return
                find.where()
                        .eq("is_deleted", false)
                        .eq("brand_id", brandId)
                        .findRowCount();
    }

    public static List<MapVariantGroup> getListRelatedAttribute(ProductVariantGroup variant, Long productId){
        List<MapVariantGroup> result = new ArrayList<>();

        if (variant != null){
            for(BaseAttribute attr : variant.baseAttributes){
                List<MapVariantGroupList> lists = new ArrayList<>();
                if (attr.id != Constant.getInstance().getColorId()){
                    for (Attribute at : attr.getAttributes()){

                        Product product = Product.find.where()
                                .eq("productVariantGroup", variant)
                                .eq("baseAttributes.id", attr.id)
                                .eq("attributes.id", at.id)
                                .ne("t0.id", productId)
                                .eq("t0.is_deleted", false)
                                .orderBy("t0.price ASC").setMaxRows(1).findUnique();

                        if (product != null){
                            lists.add(new MapVariantGroupList(at.getName(), product.id, product.sku));
                        }

                    }
                }
                if (!lists.isEmpty()){
                    result.add(new MapVariantGroup(attr.id, attr.name, lists));
                }
            }
        }

        return result;
    }

    public static List<MapProductColor> getListRelatedAttributeColor(ProductVariantGroup variant, Product prod){
        List<MapProductColor> result = new ArrayList<>();
        Attribute color = prod.findColorProduct();
        if (variant != null && color != null){
            for(BaseAttribute attr : variant.baseAttributes){
                if (Objects.equals(attr.id, Constant.getInstance().getColorId())){
                    for (Attribute at : attr.getAttributes()){
                        Product product = Product.find.where().eq("productVariantGroup", variant)
                                .eq("baseAttributes.id", attr.id)
                                .eq("attributes.id", at.id)
                                .ne("t0.id", prod.id)
                                .ne("attributes.id", color.id)
                                .eq("t0.is_deleted", false)
                                .orderBy("t0.price ASC").setMaxRows(1).findUnique();

                        if (result.size() == 0){
                            result.add(new MapProductColor(prod.getImageUrl(), prod.id, prod.slug, at.additional, prod.getProductImages(), true));
                        }else {
                            if (product != null){
                                result.add(new MapProductColor(product.getImageUrl(), product.id, product.slug, at.additional, product.getProductImages(), false));
                            }
                        }

                    }
                    break;
                }
            }
        }

        return result;
    }
}