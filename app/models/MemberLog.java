package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.enwie.util.Constant;
import com.enwie.util.Encryption;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author hendriksaragih
 */
@Entity
@Table(name = "member_log")
public class MemberLog extends BaseModel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static final String DEV_TYPE_WEB = "WEB";
    public static final String DEV_TYPE_IOS = "IOS";
    public static final String DEV_TYPE_ANDROID = "ANDROID";

    @JsonProperty("member_type")
    public String memberType;
    public boolean isActive;

    // our
    public String token;
    @JsonProperty("expired_date")
    public Date expiredDate; // new DateTime(createdAt).plusDays(1);
    @JsonProperty("device_model")
    public String deviceModel; // device model number
    @JsonProperty("device_type")
    public String deviceType;
    @JsonProperty("device_id")
    public String deviceId;
    @JsonProperty("api_key")
    public String apiKey;

    @ManyToOne(cascade = { CascadeType.ALL })
    public Brand brand;

    @ManyToOne
    @JsonBackReference
    public Member member;

    @Transient
    public Long getMemberId() {
        if (member != null)
            return member.id;
        return new Long(0);
    }

    public static Finder<Long, MemberLog> find = new Finder<Long, MemberLog>(Long.class, MemberLog.class);

    private static String generateToken(String username, String password) throws NoSuchAlgorithmException {
        return Encryption.SHA1(new Date().toString() + "ENWIEATOKEN" + username + password);
    }

    public static MemberLog loginMember(String deviceModel, String deviceType, String deviceId, Member member, Brand brand) {
        MemberLog log = new MemberLog();
        try {
            if (deviceType.equalsIgnoreCase(DEV_TYPE_ANDROID) || deviceType.equalsIgnoreCase(DEV_TYPE_IOS)) {
                log.expiredDate = new DateTime(new Date()).plusDays(30).toDate();
            } else if (deviceType.equalsIgnoreCase(DEV_TYPE_WEB)) {
                log.expiredDate = new DateTime(new Date()).plusDays(1).toDate();
            } else {
                return null;
            }
            String userCode = "";
            String passCode = "";
            if(member.email!=null){
                userCode = member.email;
                passCode = member.password;
            } else if(member.phone!=null){
                userCode = member.phone;
                passCode = "PHONE";
            } else if(member.facebookUserId!=null){
                userCode = member.facebookUserId;
                passCode = "FACEBOOK";
            } else if(member.googleUserId!=null){
                userCode = member.googleUserId;
                passCode = "GOOGLE";
            } else {
                return null;
            }
            log.token = generateToken(userCode, passCode);
            // log.token = generateToken("", new Date().toString());
            log.deviceModel = deviceModel;
            log.deviceType = deviceType;
            log.deviceId = deviceId;
            log.brand = brand;
            log.isActive = true;
            log.memberType = "member";
            log.member = member;
            log.save();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
        return log;
    }

    public static boolean logoutMember(String token, Long brandId) {
        MemberLog log = MemberLog.find.where().eq("token", token)
                .eq("is_active", true)
                .eq("member_type", "member")
                .eq("brand_id", brandId)
                .setMaxRows(1).findUnique();
        if (log != null) {
            log.isActive = false;
            log.update();
            return true;
        }
        return false;
    }

    public static MemberLog isMemberAuthorized(String token, String apiKey, Long brandId) {
        // validate token
        MemberLog log = MemberLog.find.where().eq("token", token)
                .eq("is_active", true)
                .eq("member_type", "member")
                .eq("brand_id", brandId)
                .setMaxRows(1).findUnique();

        // validate api key
        String keyWeb = Constant.getInstance().getApiKeyWeb();
        String keyIos = Constant.getInstance().getApiKeyIOS();
        String keyAndroid = Constant.getInstance().getApiKeyAndroid();

        Date today = new Date();
        if (log != null && ((log.deviceType.equalsIgnoreCase(MemberLog.DEV_TYPE_WEB) && apiKey.equalsIgnoreCase(keyWeb))
                || (log.deviceType.equalsIgnoreCase(MemberLog.DEV_TYPE_IOS) && apiKey.equalsIgnoreCase(keyIos))
                || (log.deviceType.equalsIgnoreCase(MemberLog.DEV_TYPE_ANDROID)
                && apiKey.equalsIgnoreCase(keyAndroid)))) {
            if (today.before(log.expiredDate)) {
                return log;
            } else {
                log.isActive = false;
                log.save();
            }
        }
        return null;
    }

    public static MemberLog getByToken(String token, Long brandId) {
        return MemberLog.find.where()
                .eq("token", token)
                .eq("is_active", true)
                .eq("brand_id", brandId)
                .setMaxRows(1).findUnique();
    }

    public static List<MemberLog> getListMemberLog(Member member, Long brandId) {
        return MemberLog.find.where()
                .eq("is_active", true)
                .eq("member", member)
                .eq("brand_id", brandId)
                .in("device_type", Arrays.asList("IOS", "ANDROID"))
                .findList();
    }

}