package models;

import com.avaje.ebean.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import models.mapper.MapVoucher;
import com.enwie.util.CommonFunction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by nugraha on 5/18/17.
 */
@Entity
public class VoucherDetail extends BaseModel {
    private static final long serialVersionUID = 1L;
    private static final int STATUS_USED = 1;
    private static final int STATUS_UNUSED = 0;

    public static Finder<Long, VoucherDetail> find = new Finder<>(Long.class,
            VoucherDetail.class);

    public String code;

    @JsonIgnore
    @ManyToOne(cascade = { CascadeType.ALL })
    public Voucher voucher;

    @Column(name = "status", columnDefinition = "integer default 0")
    @JsonProperty("status")
    public int status;

    @JsonIgnore
    @ManyToOne
    public Member member;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="Asia/Jakarta")
    @JsonProperty("used_at")
    public Date usedAt;

    @Column(name = "odoo_id")
    public Integer odooId;

    @Transient
    public Long voucherId;

    @Transient
    public Long memberId;


    public String getStatusName(){
        return (status == 0)? "Unused":"Used";
    }


    public String getUsedDate(){
        return CommonFunction.getDate(usedAt);
    }

    public static Page<VoucherDetail> page(Long id, int page, int pageSize, String sortBy, String order, String name) {
        return VoucherDetail.find
                .where()
                .eq("voucher.id", id)
                .ilike("code", "%" + name + "%")
                .eq("t0.is_deleted", false)
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .setFetchAhead(false)
                .getPage(page);

    }

    public static int findRowCount(Long id) {
        return
                find.where()
                        .eq("voucher.id", id)
                        .eq("is_deleted", false)
                        .findRowCount();
    }

    public static List<MapVoucher> getVoucherMember(Long memberId, Long brandId) {
        String sql = "SELECT code, masking, status, valid_to, valid_from FROM (" +
                "SELECT a.code, e.masking, '1' as status, e.valid_to, e.valid_from " +
                "FROM voucher_detail a " +
                "LEFT JOIN voucher e ON e.id = a.voucher_id " +
                "WHERE a.member_id = "+memberId+" AND e.brand_id = "+brandId+" " +
                "UNION " +
                "SELECT f.code, g.masking, '0' as status, g.valid_to, g.valid_from " +
                "FROM voucher_detail f " +
                "LEFT JOIN voucher g ON g.id = f.voucher_id " +
                "LEFT JOIN voucher_member h ON h.voucher_id = g.id " +
                "WHERE h.member_id = "+memberId+" AND g.brand_id = "+brandId+" AND f.status = 0 ) tbl";

        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("code", "code")
                .columnMapping("masking", "masking")
                .columnMapping("valid_from", "startDate")
                .columnMapping("valid_to", "endDate")
                .columnMapping("status", "status")
                .create();
        com.avaje.ebean.Query<MapVoucher> query = Ebean.find(MapVoucher.class);
        query.setRawSql(rawSql);
        List<MapVoucher> resData = query.findList();
        return resData;
    }

    public static List<VoucherDetail> checkVouchersMinimumPurchase(List<VoucherDetail> voucherDetails, Double total) {
        List<VoucherDetail> vds = new ArrayList<>();
        for(VoucherDetail vd: voucherDetails) {
            if(vd.voucher.minPurchase <= total) {
                vds.add(vd);
            }
        }
        return vds;
    }

    public static List<VoucherDetail> getCanBeCombinedVoucher(List<VoucherDetail> voucherDetails) {
        List<VoucherDetail> vds = new ArrayList<>();
        for(VoucherDetail vd: voucherDetails) {
            if(vd.voucher.canBeCombined == 1) {
                vds.add(vd);
            }
        }
        return vds;
    }

    public static List<VoucherDetail> getCannotBeCombinedVoucher(List<VoucherDetail> voucherDetails) {
        List<VoucherDetail> vds = new ArrayList<>();
        for(VoucherDetail vd: voucherDetails) {
            if(vd.voucher.canBeCombined == 0) {
                vds.add(vd);
                break;
            }
        }
        return vds;
    }

    public static List<VoucherDetail> getVouchers(Long brandId, List<String> voucherCodes) {
        String sql = "SELECT id, code, voucher_id, status, member_id, used_at FROM (" +
                "SELECT a.id, a.code, a.voucher_id, a.status, a.member_id, a.used_at " +
                "FROM voucher_detail a " +
                    "LEFT JOIN voucher e ON e.id = a.voucher_id " +
                "WHERE a.member_id IS NULL AND e.brand_id = :brandId " +
                    "AND a.code IN('" + String.join("','", voucherCodes) + "') AND (NOW() BETWEEN e.valid_from AND e.valid_to) AND a.status = 0) tbl";

        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("id", "id")
                .columnMapping("code", "code")
                .columnMapping("voucher_id", "voucherId")
                .columnMapping("status", "status")
                .columnMapping("member_id", "memberId")
                .columnMapping("used_at", "usedAt")
                .create();

        com.avaje.ebean.Query<VoucherDetail> query = Ebean.find(VoucherDetail.class);
        query.setRawSql(rawSql);

        query.setParameter("brandId", brandId);

        List<VoucherDetail> voucherDetails = query.findList();

        return voucherDetails;
    }

}
