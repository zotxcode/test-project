package models;

import com.avaje.ebean.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.enwie.util.CommonFunction;
import com.enwie.util.Constant;
import com.enwie.util.Encryption;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.persistence.*;
import javax.validation.constraints.Size;

import java.beans.Transient;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author hendriksaragih
 */
@Entity
@Table(name = "member")
public class Member extends BaseModel {
    public static final boolean ACTIVE = true;
    public static final boolean INACTIVE = false;
    private static final String LOG_TYPE = "ADMIN";
    private static final String LOG_TABLE_NAME = "member";

    private static final long serialVersionUID = 1L;

    @JsonIgnore
    public String password;

    @JsonProperty("first_name")
    public String firstName;
    @JsonProperty("last_name")
    public String lastName;

    @JsonProperty("full_name")
    public String fullName;
    @JsonProperty("email")
    public String email;
    @JsonIgnore
    public String emailNotifikasi;
    @JsonProperty("thumbnail_image_url")
    public String thumbnailImageUrl;
    @JsonProperty("medium_image_url")
    public String mediumImageUrl;
    @JsonProperty("large_image_url")
    public String largeImageUrl;
    public String phone;

    @Size(max = 1)
    @Column(length = 1)
    public String gender;
    @Temporal(TemporalType.DATE)
    @Column(name = "birth_date")
    @JsonProperty("birth_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Jakarta")
    public Date birthDate;

    @JsonProperty("billing_address_id")
    public String billingAddressId;
    @JsonProperty("facebook_user_id")
    public String facebookUserId;
    @JsonProperty("google_user_id")
    public String googleUserId;

    @JsonIgnore
    @Column(name = "activation_code")
    public String activationCode;
    @JsonProperty("is_active")
    @Column(name = "is_active")
    public boolean isActive;

    @JsonProperty("news_letter")
    @Column(name = "news_letter")
    public Boolean newsLetter;

    @JsonIgnore
    @Column(name = "reset_token")
    public String resetToken;

    @Column(name = "reset_time")
    public Long resetTime;

    @Column(name = "code_expire")
    public Date codeExpire;

    @JsonProperty("has_password")
    public boolean hasSetPassword() {
        return (this.password != null);
    }

    @JsonProperty("birth_day")
    public String getBirthDay() {
        return CommonFunction.getDate(birthDate);
    }

	@JsonProperty("enwie_signup_respon")
    public String enwieSignupRespon;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @JsonIgnore
    public List<MemberLog> logs = new ArrayList<MemberLog>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    // @JsonIgnore
    @JsonProperty("shipping_addresses")
    public List<ShippingAddress> shippingAddresses = new ArrayList<ShippingAddress>();

    @OneToMany(mappedBy = "reseller")
    public List<ResellerMutationBalance> resellerMutationBalances;

    @Column(name = "last_login")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    public Date lastLogin;

    @Column(name = "last_purchase")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    public Date lastPurchase;

    @JsonIgnore
    @ManyToOne(cascade = { CascadeType.ALL })
    public Brand brand;

    // @JsonIgnore
    @JsonProperty("allergies")
    @ManyToMany(mappedBy = "members",cascade=CascadeType.REMOVE)
    public List<Allergy> allergies;

    @Transient
    public String getIsActive() {
        String statusName = "";
        if(isActive)
            statusName = "Active";
        else statusName = "Inactive";

        return statusName;
    }

    // @JsonProperty("address")
    // @Transient
    // public SalesOrderAddress getLastOrderAddressByMember() {
    //     SalesOrder salesOrder =  SalesOrder.find.where()
    //             .eq("member_id", this.id)
    //             .eq("t0.is_deleted", false)
    //             .setOrderBy("t0.id DESC").setMaxRows(1).findUnique();

    //     return salesOrder == null ? null : salesOrder.salesOrderAddress;
    // }

    @JsonIgnore
    @ManyToOne(cascade = { CascadeType.ALL })
    public Province province;

    @JsonIgnore
    @ManyToOne(cascade = { CascadeType.ALL })
    public City city;

    public String address;

    @Column(name = "is_reseller", columnDefinition = "boolean default false")
    public Boolean isReseller;

    @Column(name = "become_reseller_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    public Date becomeResellerAt;

    @Column(name = "refferal_id")
    public Long refferalId;

    @Column(name = "total_refferal", columnDefinition = "integer default 0")
    public Long totalRefferal;

    @Transient
    @JsonProperty("image_url")
    public String imageUrl(){
		return getImageLink();
    }
    
    @Transient
    @JsonProperty("province_id")
    public Long provinceId(){
		return province.id;
    }
    
    @Transient
    @JsonProperty("city_id")
    public Long cityId(){
		return city.id;
	}

    public static Finder<Long, Member> find = new Finder<>(Long.class, Member.class);

    public Member() {

    }

    public Member(String phone,String code) {
        super();
        this.activationCode = code;
        this.phone = phone;
    }

    public Member(String password, String fullName, String emailAddress, String phone, String gender,
                  String birthDate, Boolean newsLetter, String googleId, String fbId, Brand brand,String enwieSignupRespon, Province province, City city, String address, Long refferalId) throws ParseException {
        super();
        String[] split = fullName.split(" ");
        String firstName = split[0];
        String lastName = " " + firstName;
        if (split.length > 1){
            lastName = fullName.replaceFirst(firstName+" ", "");
        }

        this.password = Encryption.EncryptAESCBCPCKS5Padding(password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.brand = brand;
        this.province = province;
        this.city = city;
        this.address = address;
        if (refferalId > 0){
            this.refferalId = refferalId;
        }
        this.isReseller = false;
        this.totalRefferal = 0L;
		this.enwieSignupRespon = enwieSignupRespon;
        this.emailNotifikasi = this.email = emailAddress;
        this.newsLetter = newsLetter;
        if (!phone.isEmpty()){
            this.phone = phone;
        }
        this.gender = gender;
        if (!birthDate.isEmpty()){
            this.birthDate = new SimpleDateFormat("yyyy-MM-dd").parse(birthDate);
        }
        this.activationCode = "";
        this.isActive = true;
        if (googleId != null && !googleId.isEmpty()){
            this.googleUserId = googleId;
        }
        if (fbId != null && !fbId.isEmpty()){
            this.facebookUserId = fbId;
        }

    }

    public static Member login(String email, String password, Long brandId) {
        String encPassword = Encryption.EncryptAESCBCPCKS5Padding(password);
        Member member = Member
                .find.where()
                .and(Expr.eq("email", email), Expr.eq("password", encPassword))
                .eq("brand_id", brandId)
                .eq("is_active", true).setMaxRows(1).findUnique();
        return member;

    }

    public static Member loginByPhone(String phone, String password) {
        String encPassword = Encryption.EncryptAESCBCPCKS5Padding(password);
        Member member = Member.find.where().and(Expr.eq("phone", phone), Expr.eq("password", encPassword))
                .eq("is_active", true).setMaxRows(1).findUnique();
        return member;
    }

    public static Member findByFacebookId(String facebookUserId, Long brandId) {
        Member member = Member
                .find.where()
                .eq("facebook_user_id", facebookUserId)
                .eq("brand_id", brandId)
                .eq("is_active", true).setMaxRows(1).findUnique();
        return member;
    }

    public static Member findByGoogleId(String googleUserId, Long brandId) {
        Member member = Member
                .find.where()
                .eq("google_user_id", googleUserId)
                .eq("brand_id", brandId)
                .eq("is_active", true).setMaxRows(1).findUnique();
        return member;
    }

    public static Member findByEmail(String email, Long brandId) {
        Member member = Member
                .find.where()
                .eq("email", email)
                .eq("brand_id", brandId)
                .eq("is_active", true).setMaxRows(1).findUnique();
        return member;
    }

    public static Member findByPhone(String phone, Long brandId) {
        Member member = Member
                .find.where()
                .eq("phone", phone)
                .eq("brand_id", brandId)
                .eq("is_active", true).setMaxRows(1).findUnique();
        return member;
    }

    public static Member findActiveReseller(Long provinceId, Long cityId, Long brandId) {
        Member member = null;
        member = Member.find.where().eq("province_id", provinceId).eq("city_id", cityId)
            .eq("brand_id", brandId).eq("is_reseller", true).eq("is_active", true)
            .order("total_refferal desc, become_reseller_at asc").setMaxRows(1).findUnique();
        if (member == null) {
            member = Member.find.where().eq("province_id", provinceId)
                .eq("brand_id", brandId).eq("is_reseller", true).eq("is_active", true)
                .order("total_refferal desc, become_reseller_at asc").setMaxRows(1).findUnique();

            if (member == null) {
                member = Member.find.where()
                    .eq("brand_id", brandId).eq("is_reseller", true).eq("is_active", true)
                    .order("total_refferal desc, become_reseller_at asc").setMaxRows(1).findUnique();
            }
        }
                // .order("sequence asc").findList();
        return member;
    }

    public static Member findResellerByPhone(String phone, Long brandId) {
        Member member = Member
                .find.where()
                .eq("phone", phone)
                .eq("brand_id", brandId)
                .eq("is_reseller", true)
                .eq("is_active", true).setMaxRows(1).findUnique();
        return member;
    }

    public static List<Member> findReferrerList(Long id, Long brandId) {
        List<Member> members = Member
                .find.where()
                .eq("refferal_id", id)
                .eq("brand_id", brandId)
                .eq("is_active", true).findList();
        return members;
    }

    public static Integer countReffererById(Long id, Long brandId) {
        Integer reffererSize = Member
                .find.where()
                .eq("refferal_id", id)
                .eq("brand_id", brandId)
                .eq("is_active", true).findRowCount();
        return reffererSize;
    }

    public static String validation(String email, String password, String confPassword, String phone, Long brandId) {
        if (email != null && !email.matches(CommonFunction.emailRegex)) {
            return "Email format not valid.";
        }
        String[] mails = email.split("@");
        Integer row = BlacklistEmail.find.where()
                .eq("name", "@"+mails[1])
                .eq("is_deleted", false)
                .eq("brand_id", brandId)
                .findRowCount();
        if (row > 0){
            return "The email service provider that you are using can not be used in Hokeba.com Please use another email service provider.";
        }
        Member member = Member.find.where().eq("email", email).eq("brand_id", brandId).setMaxRows(1).findUnique();
        if (member != null) {
            return "The email is already registered.";
        }
//        if (!CommonFunction.passwordValidation(password)) {
//            return "Password must be at least 8 character, has no whitespace, "
//                    + "and have at least 3 variations from uppercase, lowercase, number, or symbol";
//        }

        if (!phone.isEmpty()){
            if (!phone.matches(CommonFunction.phoneRegex)){
                return "Phone format not valid.";
            }
            Member memberPhone = Member.find.where().eq("phone", phone).eq("brand_id", brandId).setMaxRows(1).findUnique();
            if (memberPhone != null) {
                return "The phone is already registered.";
            }
        } else {
            return "The phone is required.";
        }

        if (!CommonFunction.passwordValidation(password)) {
            return "Password must be at least 8 character";
        }
        if (!confPassword.equals(password)) {
            return "Password and confirm password did not match.";
        }

        return null;
    }

    // validate email and password when create new member
    public static String validation(String email, String password, String confPassword) {
        if (email != null && !email.matches(CommonFunction.emailRegex)) {
            return "Email format not valid.";
        }
//        String[] mails = email.split("@");
//        Integer row = BlacklistEmail.find.where().eq("name", "@"+mails[1]).eq("is_deleted", false).findRowCount();
//        if (row > 0){
//            return "The email service provider that you are using can not be used in Hokeba.com Please use another email service provider.";
//        }
        Member member = Member.find.where().eq("email", email).setMaxRows(1).findUnique();
        if (member != null) {
            return "The email is already registered.";
        }
//        if (!CommonFunction.passwordValidation(password)) {
//            return "Password must be at least 8 character, has no whitespace, "
//                    + "and have at least 3 variations from uppercase, lowercase, number, or symbol";
//        }
        if (!CommonFunction.passwordValidation(password)) {
            return "Password must be at least 8 character";
        }
        if (!confPassword.equals(password)) {
            return "Password and confirm password did not match.";
        }
        return null;
    }

    public static String validation(Long id, String email, String phone, Long brandId) {
        if (email != null && !email.matches(CommonFunction.emailRegex)) {
            return "Email format not valid.";
        }
        String[] mails = email.split("@");
        Integer row = BlacklistEmail.find.where()
                .eq("name", "@"+mails[1])
                .eq("is_deleted", false)
                .eq("brand_id", brandId)
                .findRowCount();
        if (row > 0){
            return "The email service provider that you are using can not be used Please use another email service provider.";
        }
        Member member = Member.find.where()
                .eq("email", email)
                .eq("brand_id", brandId)
                .ne("id", id).setMaxRows(1).findUnique();
        if (member != null) {
            return "The email is already registered.";
        }

        if (!phone.isEmpty()){
            if (!phone.matches(CommonFunction.phoneRegex)){
                return "Phone format not valid.";
            }
            Member memberPhone = Member.find.where()
                    .eq("phone", phone)
                    .eq("brand_id", brandId)
                    .ne("id", id).setMaxRows(1).findUnique();
            if (memberPhone != null) {
                return "The phone is already registered.";
            }
        }

        return null;
    }

    public static String validation(String phoneNumber) {
        Member member = Member.find.where().eq("phone", phoneNumber).setMaxRows(1).findUnique();
        if (member != null && member.isActive == false) {
            return "Phone number is already registered but not active.";
        }
        if (member != null) {
            return "Phone number is already registered.";
        }
        return null;
    }

    public static String verification(String phone, String code) {
        Member member = Member.find.where().eq("phone", phone).findUnique();
        if (member != null && !member.activationCode.equals(code)) {
            return "Wrong verification code";
        }
        if (member == null) {
            return "You must register first";
        }
        if (System.currentTimeMillis()>member.codeExpire.getTime()){
            return "Your code is expired";
        }
        if(member.isActive){
            return "Your account is already active";
        }
        return null;
    }

    public static String validation(String firstName, String gender) {
        if (firstName.equals("")) {
            return "First name must not empty";
        }
        // if (!birthDate.matches(
        // "(^(((0[1-9]|1[0-9]|2[0-8])[\\/](0[1-9]|1[012]))|((29|30|31)[\\/](0[13578]|1[02]))|((29|30)[\\/](0[4,6,9]|11)))[\\/](19|[2-9][0-9])\\d\\d$)|(^29[\\/]02[\\/](19|[2-9][0-9])(00|04|08|12|16|20|24|28|32|36|40|44|48|52|56|60|64|68|72|76|80|84|88|92|96)$)"))
        // {
        // return "Birthday format is invalid";
        // }
        if (!gender.equalsIgnoreCase("M") && !gender.equalsIgnoreCase("F")) {
            return "Input gender is not valid";
        }
        return null;
    }

    public static String validation(String firstName, String email, String birthDate, String gender, String password) {
        if (!email.matches(CommonFunction.emailRegex)) {
            return "Email format not valid.";
        }
        Member member = Member.find.where().eq("email", email).setMaxRows(1).findUnique();
        if (member != null) {
            return "The email is already registered.";
        }
        if (firstName.equals("")) {
            return "First name must not empty";
        }
        if (!gender.equalsIgnoreCase("M") && !gender.equalsIgnoreCase("F")) {
            return "Input gender is not valid";
        }
        if (!CommonFunction.passwordValidation(password)) {
            return "Password must be at least 8 character, has no whitespace, "
                    + "and have at least 3 variations from uppercase, lowercase, number, or symbol";
        }
        // if (!birthDate.matches(
        // "(^(((0[1-9]|1[0-9]|2[0-8])[\\/](0[1-9]|1[012]))|((29|30|31)[\\/](0[13578]|1[02]))|((29|30)[\\/](0[4,6,9]|11)))[\\/](19|[2-9][0-9])\\d\\d$)|(^29[\\/]02[\\/](19|[2-9][0-9])(00|04|08|12|16|20|24|28|32|36|40|44|48|52|56|60|64|68|72|76|80|84|88|92|96)$)"))
        // {
        // return "Birthday format is invalid";
        // }
        return null;
    }

    public String changePassword(String oldPass, String newPass, String conPass)
            throws InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        String encPass = Encryption.EncryptAESCBCPCKS5Padding(oldPass);
        if (encPass.equals(this.password)) {
            if (!oldPass.equals(newPass)) {
                if (CommonFunction.passwordValidation(newPass)) {
                    if (conPass.equals(newPass)) {
                        Transaction txn = Ebean.beginTransaction();
                        try {
                            this.password = Encryption.EncryptAESCBCPCKS5Padding(newPass);
                            this.save();
                            Update<MemberLog> upd = Ebean.createUpdate(MemberLog.class,
                                    "UPDATE member_log SET is_active=:isActive WHERE is_active=true and member_id=:memberId");
                            upd.set("isActive", false);
                            upd.set("memberId", this.id);
                            upd.execute();
                            txn.commit();
                            return null;
                        } catch (Exception e) {
                            e.printStackTrace();
                            txn.rollback();
                        } finally {
                            txn.end();
                        }
                        return "500";
                    }
                    return "Password does not match the confirm password";
                }
                return "Password must be at least 8 character, has no whitespace, "
                        + "and have at least 3 variations from uppercase, lowercase, number, or symbol";
            }
            return "Your new password must be different";
        }
        return "Invalid old password";
    }

    public String changePassword(String newPass, String conPass)
            throws InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        String validation = CommonFunction.passwordValidation(newPass, conPass);
        if (validation == null) {
            Transaction txn = Ebean.beginTransaction();
            try {
                this.password = Encryption.EncryptAESCBCPCKS5Padding(newPass);
                this.save();
                Member.removeAllToken(this.id);
                txn.commit();
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }
            return "500";
        }
        return validation;
    }

    public static void removeAllToken(Long id) {
        Update<MemberLog> upd = Ebean.createUpdate(MemberLog.class,
                "UPDATE member_log SET is_active=:isActive WHERE is_active=true and member_id=:memberId");
        upd.set("isActive", false);
        upd.set("memberId", id);
        upd.execute();
    }

    public static Page<Member> page(int page, int pageSize, String sortBy, String order, String name, int status, Long brandId, Long cityId) {
        ExpressionList<Member> qry = Member.find
                .where()
                .ilike("full_name", "%" + name + "%")
                .eq("is_deleted", false)
                .eq("brand_id", brandId);

        if (status >= 0){
            qry.eq("is_active", status==1);
        }

        if (cityId != null){
            qry.eq("city_id", cityId);
        }

        return
                qry.orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);
    }
    
    public static Page<Member> pageMerchant(int page, int pageSize, String sortBy, String order, String name, int status, Long brandId, Long ref_id) {
        ExpressionList<Member> qry = Member.find
                .where()
                .ilike("full_name", "%" + name + "%")
                .eq("is_deleted", false)
                .eq("refferal_id", ref_id)
                .eq("is_reseller", false)
                .eq("brand_id", brandId);

        if (status >= 0){
            qry.eq("is_active", status==1);
        }

        return
                qry.orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);
    }

    public static Member findById(Long id, Long brandId) {
        return find.where().eq("is_deleted", false).eq("id", id).eq("brand_id", brandId).findUnique();
    }

    public String getMediumImageUrl(){
		return getImageLink();
	}

	public String getImageLink(){
		return mediumImageUrl==null || mediumImageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + mediumImageUrl;
	}

    public Boolean getNewsLetter() {
        return newsLetter == null ? true : newsLetter;
    }

    public static Integer RowCount(Long brandId) {
        return find.where().eq("is_deleted", false).eq("brand_id", brandId).findRowCount();
    }

    public String getRegisterDate(){
        return CommonFunction.getDateTime(createdAt);
    }

    public String getLastLogin(){
        return CommonFunction.getDateTime(lastLogin);
    }

    public String getLastPurchase(){
        return CommonFunction.getDateTime(lastPurchase);
    }

    public String getBirthDate(){
        return CommonFunction.getDate(birthDate);
    }

    public String getBecomeResellerAt(){
        return CommonFunction.getDateTime(becomeResellerAt);
    }

    public String getThumbnailImageLink(){
        return thumbnailImageUrl==null || thumbnailImageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + thumbnailImageUrl;
    }
}