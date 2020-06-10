package models;

import com.avaje.ebean.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import models.mapper.MapPromotion;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by hilmanzaky.
 */
@Entity
public class PromotionProduct extends BaseModel {
    private static final long serialVersionUID = 1L;
    private static final int STATUS_USED = 1;
    private static final int STATUS_UNUSED = 0;

    public static Finder<Long, PromotionProduct> find = new Finder<>(Long.class,
            PromotionProduct.class);

    @JsonIgnore
    @ManyToOne(cascade = { CascadeType.ALL })
    public Promotion promotion;

    @JsonIgnore
    @ManyToOne(cascade = { CascadeType.ALL })
    public Product product;

    @JsonIgnore
    public Long discount;


    public PromotionProduct(Promotion promotion, Product product, Long discount) {
        this.promotion = promotion;
        this.product = product;
        this.discount = discount;
    }

    public static Page<PromotionProduct> page(Long id, int page, int pageSize, String sortBy, String order, String name) {
        return PromotionProduct.find
                .where()
                .eq("promotion.id", id)
                .eq("t0.is_deleted", false)
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .setFetchAhead(false)
                .getPage(page);

    }

    public static int findRowCountByPromotionId(Long id) {
        return
                find.where()
                        .eq("promotion.id", id)
                        .eq("is_deleted", false)
                        .findRowCount();
    }

    public static boolean isExist(Long promotionId, Long productId) {
        PromotionProduct promotionProduct = find.where()
                        .eq("promotion_id", promotionId)
                        .eq("product_id", productId)
                        .eq("is_deleted", false)
                        .findUnique();

        return promotionProduct != null;
    }

    public static List<MapPromotion> getPromotionProduct(Long brandId) {
        String sql = "SELECT c.id, b.name, b.description, b.valid_from, b.valid_to, a.discount" +
                " FROM promotion_product a" +
                " LEFT JOIN promotion b ON b.id = a.promotion_id" +
                " LEFT JOIN product c ON c.id = a.product_id" +
                " WHERE b.brand_id = "+brandId+" AND b.is_deleted = FALSE AND b.status = TRUE";

        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("c.id", "productId")
                .columnMapping("b.name", "promotionName")
                .columnMapping("b.description", "promotionDesc")
                .columnMapping("b.valid_from", "startDate")
                .columnMapping("b.valid_to", "endDate")
                .columnMapping("a.discount", "discount")
                .create();
        com.avaje.ebean.Query<MapPromotion> query = Ebean.find(MapPromotion.class);
        query.setRawSql(rawSql);
        List<MapPromotion> resData = query.findList();
        return resData;
    }

    public void updateDiscountPromotion() {
        Promotion p = this.promotion;
        if((p.validFrom.compareTo(new Date()) < 0 || p.validFrom.compareTo(new Date()) == 0) &&
                (p.validTo.compareTo(new Date()) > 0 || p.validTo.compareTo(new Date()) == 0)) {
            Product prod = this.product;
            prod.discountType = 2;
            prod.discount = Double.valueOf(this.discount);
            prod.save();
        }
    }

    private static List<MapPromotion> getActivePromotionProduct(Long brandId) {
        String sql = "SELECT c.id, b.name, b.description, b.valid_from, b.valid_to, a.discount" +
                " FROM promotion_product a" +
                " LEFT JOIN promotion b ON b.id = a.promotion_id" +
                " LEFT JOIN product c ON c.id = a.product_id" +
                " WHERE b.brand_id = "+brandId+" AND b.is_deleted = FALSE AND b.status = TRUE AND NOW() BETWEEN valid_from AND valid_to";

        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("c.id", "productId")
                .columnMapping("b.name", "promotionName")
                .columnMapping("b.description", "promotionDesc")
                .columnMapping("b.valid_from", "startDate")
                .columnMapping("b.valid_to", "endDate")
                .columnMapping("a.discount", "discount")
                .create();
        com.avaje.ebean.Query<MapPromotion> query = Ebean.find(MapPromotion.class);
        query.setRawSql(rawSql);
        List<MapPromotion> resData = query.findList();
        return resData;
    }

    public static void updatePromotion(Long brandId) {
        Transaction txn = Ebean.beginTransaction();
        try {
            List<MapPromotion> mapPromotions = getActivePromotionProduct(brandId);
            for(MapPromotion mapPromotion: mapPromotions) {
                Product p = Product.findById(mapPromotion.getProductId(), brandId);
                p.discountType = 2;
                p.discount = mapPromotion.getDiscount();
                p.save();
            }
            txn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            txn.rollback();
        }

    }

}
