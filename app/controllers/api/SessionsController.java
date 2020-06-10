package controllers.api;

import assets.JsonMask;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.enwie.api.BaseResponse;
import com.enwie.api.UserSession;
import com.enwie.mapper.response.MapFacebookUser;
import com.enwie.mapper.response.MapGooglePeople;
import com.enwie.mapper.response.MapGooglePeopleEmail;
import com.enwie.service.FacebookService;
import com.enwie.service.GoogleService;
import com.enwie.util.CommonFunction;
import com.enwie.util.Constant;
import com.enwie.util.Encryption;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;
import com.enwie.internal.response.model.PromoClient;
import models.VoucherSignUp;
import com.enwie.internal.InternalService;
import controllers.BaseController;
import models.City;
import models.Member;
import models.MemberLog;
import models.Province;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

/**
 * Created by hendriksaragih on 2/28/17.
 */
@Api(value = "/users/sessions", description = "Session")
public class SessionsController extends BaseController {
    private final static Logger.ALogger logger = Logger.of(SessionsController.class);
    @SuppressWarnings("rawtypes")
    private static BaseResponse response = new BaseResponse();

    @ApiOperation(value = "Sign in", notes = "Sign in.\n" + swaggerInfo
            + "", response = UserSession.class, httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "login form", dataType = "temp.swaggermap.LoginForm", required = true, paramType = "body", value = "login form") })
    public static Result signIn() {
        JsonNode json = request().body().asJson();
        Logger.debug("checkAccessAuthorization = "+checkAccessAuthorization("guest"));
        Logger.debug("email = "+json.has("email"));
        if (checkAccessAuthorization("guest") == 200 && json.has("email") && json.has("password")
                && json.has("device_model") && json.has("device_id") && json.has("device_type")) {

            String email = json.findPath("email").asText();
            String password = json.findPath("password").asText();
            String deviceModel = json.findPath("device_model").asText();
            String deviceType = json.findPath("device_type").asText();
            String deviceId = json.findPath("device_id").asText();
            Member member = Member.login(email, password, getBrandId());
//            if (email.matches(CommonFunction.emailRegex)) {
//                member = Member.login(email, password, getBrandId());
//            } else if (email.matches(CommonFunction.phoneRegex)) {
//                member = Member.loginByPhone(email, password);
//            }
            if (member != null) {
                try {
                    MemberLog log = MemberLog.loginMember(deviceModel, deviceType, deviceId, member, getBrand());
                    if (log == null) {
                        response.setBaseResponse(0, 0, 0, inputParameter, null);
                        return badRequest(Json.toJson(response));
                    }
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    UserSession session = new UserSession(log.token, df.format(log.expiredDate), log.memberType);
                    ObjectMapper om = new ObjectMapper();
                    om.addMixInAnnotations(Member.class, JsonMask.class);
                    session.setProfile_data(Json.parse(om.writeValueAsString(member)));
                    response.setBaseResponse(1, 0, 1, success, session);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                response.setBaseResponse(0, 0, 0, error, null);
                return badRequest(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, "Wrong username or password", null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Sign in socmed", notes = "Sign in socmed.\n" + swaggerInfo
            + "", response = UserSession.class, httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "login form socmed", dataType = "temp.swaggermap.LoginFormSocmed", required = true, paramType = "body", value = "login form socmed") })
    public static Result signInWithSocmed() {
        JsonNode json = request().body().asJson();

        Logger.debug("checkAccessAuthorization = "+checkAccessAuthorization("guest"));
        Logger.debug("fb = "+json.has("facebook_user_id"));
        Logger.debug("google = "+json.has("google_user_id"));
        if (checkAccessAuthorization("guest") == 200 && json.has("device_model") && json.has("device_id")
                && json.has("device_type") && json.has("facebook_user_id") && json.has("facebook_token")
                && json.has("google_user_id") && json.has("google_token") ) {

            String deviceModel = json.findPath("device_model").asText();
            String deviceType = json.findPath("device_type").asText();
            String deviceId = json.findPath("device_id").asText();
            String facebookUserId = json.findPath("facebook_user_id").asText();
            String facebookToken = json.findPath("facebook_token").asText();
            String googleUserId = json.findPath("google_user_id").asText();
            String googleToken = json.findPath("google_token").asText();

            try {
                Member user = null;
                Logger.debug("step 1");
                if (!facebookUserId.isEmpty() && !facebookToken.isEmpty()) {
                    com.restfb.types.User me = FacebookService.getMe(facebookToken);
                    
                    if (!me.getId().equals(facebookUserId)) {
                        Logger.debug("fb id is empty");
                        response.setBaseResponse(0, 0, 0, "could not sign in", null);
                        return badRequest(Json.toJson(response));
                    }

                    user = Member.findByFacebookId(me.getId(), getBrandId());
                    if (user == null) {
                        user = Member.findByEmail(me.getEmail(), getBrandId());
                        // if (user != null) {
                        //     user.setFacebookUserId(facebookUserId);
                        // }
                    }
                } else if (!googleUserId.isEmpty() && !googleToken.isEmpty()) {
                    MapGooglePeople me = GoogleService.getMe(googleToken);
                    if (!me.getId().equals(googleUserId)) {
                        Logger.debug("user id is empty");
                        response.setBaseResponse(0, 0, 0, "could not sign in", null);
                        return badRequest(Json.toJson(response));
                    }

                    user = Member.findByGoogleId(me.getId(), getBrandId());
                    if (user != null && me.getEmails() != null) {
                        Logger.debug("user exist");
                        String email = null;
                        for(MapGooglePeopleEmail peopleEmail : me.getEmails()) {
                            if (peopleEmail.getType().equals("account")) {
                                email = peopleEmail.getValue();
                                break;
                            }
                        }
                        if (email != null) {
                            user = Member.findByEmail(email, getBrandId());
                            // if (user != null) {
                                // user.setGoogleUserId(me.getId());
                            // }
                        }
                    }

                } else {
                    Logger.debug("fb id & google id is empty");
                    response.setBaseResponse(0, 0, 0, inputParameter, null);
                    return badRequest(Json.toJson(response));
                }
                Logger.debug("step 2");
                if (user != null) {
                    Logger.debug("user exists");
                    MemberLog log = MemberLog.loginMember(deviceModel, deviceType, deviceId, user, getBrand());
                    if (log == null) {
                        response.setBaseResponse(0, 0, 0, inputParameter, null);
                        return badRequest(Json.toJson(response));
                    }
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    UserSession session = new UserSession(log.token, df.format(log.expiredDate), log.memberType);
                    ObjectMapper om = new ObjectMapper();
                    om.addMixInAnnotations(Member.class, JsonMask.class);
                    session.setProfile_data(Json.parse(om.writeValueAsString(user)));
                    response.setBaseResponse(1, 0, 1, success, session);
                    return ok(Json.toJson(response));
                }else{
                    Logger.debug("user is empty");
                    response.setBaseResponse(0, 0, 0, "could not sign in", null);
                    return badRequest(Json.toJson(response));
                }
            // } catch (HttpException e) {
            //     logger.warn("ERROR : ", e);
            //     response.setBaseResponse(0, 0, 0, "could not sign in", null);
            //     return badRequest(Json.toJson(response));
            // } catch (ValidationErrorException e) {
            //     logger.warn("ERROR : ", e);
            //     response.setBaseResponse(0, 0, 0, "could not sign in", null);
            //     return badRequest(Json.toJson(response));
            } catch (Exception e) {
                Logger.debug("error : " + e);
                // logger.error(getDeviceType(apiKey)+" : ", e);
                response.setBaseResponse(0, 0, 0, "could not sign in", null);
                return badRequest(Json.toJson(response));
            }

        }
        Logger.debug("teu menang asup");
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Refresh token", notes = "Refresh your current token.\n" + swaggerInfo
            + "", response = UserSession.class, httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", dataType = "temp.swaggermap.LoginForm", required = true, paramType = "body", value = "account email") })
    public static Result refreshToken() {
        if (request().headers().get(API_KEY) != null && request().headers().get(TOKEN) != null) {
            String apiKey = request().headers().get(API_KEY)[0];
            String token = request().headers().get(TOKEN)[0];
            MemberLog targetLog = MemberLog.isMemberAuthorized(token, apiKey, getBrandId());
            if (targetLog != null) {
                Member targetMember = targetLog.member;
                // create new token
                MemberLog log = MemberLog.loginMember(targetLog.deviceModel, targetLog.deviceType, targetLog.deviceId, targetMember, getBrand());
                if (log == null) {
                    response.setBaseResponse(0, 0, 0, inputParameter, null);
                    return badRequest(Json.toJson(response));
                }
                // deactivate old token
                targetLog.isActive = false;
                targetLog.save();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                UserSession session = new UserSession(log.token, df.format(log.expiredDate), log.memberType);
                ObjectMapper om = new ObjectMapper();
                om.addMixInAnnotations(Member.class, JsonMask.class);
                try {
                    session.setProfile_data(Json.parse(om.writeValueAsString(targetMember)));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                response.setBaseResponse(1, 0, 1, success, session);
                return ok(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Sign out", notes = "Sign out.", response = BaseResponse.class, httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access token", dataType = "string", required = true, paramType = "header", value = "access token") })
    public static Result signOut() {
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        if (checkAccessAuthorization("member") == 200) {
            String token = request().headers().get(TOKEN)[0];
            if (MemberLog.logoutMember(token, getBrandId())) {
                response.setBaseResponse(1, 0, 1, success, null);
                return ok(Json.toJson(response));
            }
        }
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Forget password", notes = "Forget Password.", response = BaseResponse.class, httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "body", dataType = "Object", required = true, paramType = "body", value = "Forget password") })
    public static Result forgetPassword() {
        JsonNode json = request().body().asJson();
        if (checkAccessAuthorization("guest") == 200 && json.has("email")) {
            String email = json.findPath("email").asText();
            String redirect = Constant.getInstance().getFrontEndUrl() + "/reset-password";

            Member member = Member.find.where()
                    .eq("is_active", true)
                    .eq("email", email)
                    .eq("brand_id", getBrandId())
                    .setMaxRows(1).findUnique();
            if (member != null) {
                Long now = System.currentTimeMillis();
                try {
                    member.resetToken = Encryption.EncryptAESCBCPCKS5Padding(member.email+now);
                    member.resetTime = now;
                    member.update();
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                Thread thread = new Thread(() -> {
//                    try {
//                        MailConfig.sendmail(member.email, MailConfig.subjectForgotPassword,
//                                MailConfig.renderMailForgotPasswordTemplate(member, redirect));
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                });
//                thread.start();
                response.setBaseResponse(1, 0, 1, success, null);
                return ok(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return notFound(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result changePassword() throws InvalidKeyException, InvalidAlgorithmParameterException,
            NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        Member targetMember = checkMemberAccessAuthorization();
        if (targetMember != null) {
            JsonNode json = request().body().asJson();
            if (json.has("old_password") && json.has("new_password") && json.has("confirm_password")) {
                String oldPass = json.findPath("old_password").asText();
                String newPass = json.findPath("new_password").asText();
                String conPass = json.findPath("confirm_password").asText();
                String message = (targetMember.hasSetPassword()) ?
                        targetMember.changePassword(oldPass, newPass, conPass) :
                        targetMember.changePassword(newPass, conPass);
                if (message == null) {
                    response.setBaseResponse(1, 0, 1, updated, null);
                    return ok(Json.toJson(response));
                } else if (message.equals("500")) {
                    response.setBaseResponse(0, 0, 0, error, null);
                    return internalServerError(Json.toJson(response));
                } else {
                    response.setBaseResponse(0, 0, 0, message, null);
                    return badRequest(Json.toJson(response));
                }
            }
            response.setBaseResponse(0, 0, 0, inputParameter, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Reset password", notes = "Reset password.", response = BaseResponse.class, httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "body", dataType = "Object", required = true, paramType = "body", value = "Reset password data.") })
    public static Result resetPassword() {
        JsonNode json = request().body().asJson();
        if (checkAccessAuthorization("guest") == 200 && json.has("key") && json.has("password") && json.has("confirm_password")) {
            String key = json.findPath("key").asText();
            String newPass = json.findPath("password").asText();
            String confPass = json.findPath("confirm_password").asText();

            String check = CommonFunction.passwordValidation(newPass, confPass);
            if (check != null) {
                response.setBaseResponse(0, 0, 0, check, null);
                return badRequest(Json.toJson(response));
            }

            Transaction txn = Ebean.beginTransaction();
            try {
                Member member = Member.find.where().eq("is_active", true).eq("reset_token", key).setMaxRows(1).findUnique();
                if (member != null) {
                    Date requestDate = new Date(member.resetTime);

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(requestDate);
                    cal.add(Calendar.HOUR, 1);
                    if (cal.getTime().before(new Date(System.currentTimeMillis()))) {
                        response.setBaseResponse(0, 0, 0, "Session has expired", null);
                        return badRequest(Json.toJson(response));
                    }

                    member.password = Encryption.EncryptAESCBCPCKS5Padding(newPass);
                    member.resetToken = "";
                    member.save();
                    Member.removeAllToken(member.id);
                    txn.commit();
                    response.setBaseResponse(1, 0, 1, success, null);
                    return ok(Json.toJson(response));
                }
                response.setBaseResponse(0, 0, 0, notFound, null);
                return notFound(Json.toJson(response));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }
            response.setBaseResponse(0, 0, 0, error, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Sign up", notes = "Sign up.", response = BaseResponse.class, httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sign-up form", dataType = "temp.swaggermap.SignUpForm", required = true, paramType = "body", value = "sign-up form") })
    public static Result signUp() {
        JsonNode json = request().body().asJson();
        if (checkAccessAuthorization("guest") == 200 && json.has("email") && json.has("full_name")
                && json.has("confirm_password") && json.has("password")) {
            String email = json.findPath("email").asText();
            String password = json.findPath("password").asText();
            String confPassword = json.findPath("confirm_password").asText();
            String fullName = json.findPath("full_name").asText();
            String phone = json.has("phone") ? json.findPath("phone").asText() : "";
            String provinceId = json.has("province_id") ? json.findPath("province_id").asText() : "";
            String cityId = json.has("city_id") ? json.findPath("city_id").asText() : "";
            String address = json.has("address") ? json.findPath("address").asText() : "";
            String refferalCode = json.has("refferal") ? json.findPath("refferal").asText() : "";
            String gender = json.has("gender") ? json.findPath("gender").asText() : "";
            String birthDate = json.has("birth_date") ? json.findPath("birth_date").asText() : "";
            
            String deviceModel = json.findPath("device_model").asText();
            String deviceType = json.findPath("device_type").asText();
            String deviceId = json.findPath("device_id").asText();
            Boolean newsLetter = !json.has("newsletter") || json.findPath("newsletter").asBoolean();
            String googleId = json.has("google_id") ? json.findPath("google_id").asText() : "";
            String fbId = json.has("fb_id") ? json.findPath("fb_id").asText() : "";

            String validation = Member.validation(email, password, confPassword, phone, getBrandId());

            Long refferalId = 0L;
            Member refferal = null;
            if(!refferalCode.isEmpty()) {
                if (!refferalCode.matches(CommonFunction.phoneRegex)){
                    validation = "Refferal Phone format not valid.";
                }
                refferal = Member.findResellerByPhone(refferalCode, getBrandId());
                if (refferal == null) {
                    validation = "Refferal not exist or not yet a reseller.";
                } else {
                    refferalId = refferal.id;
                }
            } else {
                refferal = Member.findActiveReseller(Long.valueOf(provinceId), Long.valueOf(cityId), getBrandId());
                if (refferal != null) {
                    refferalId = refferal.id;
                }
            }

            Province province = Province.findById(Long.valueOf(provinceId));
            if (province == null) {
                validation = "Province is required.";
            }
            City city = City.findById(Long.valueOf(cityId));
            if (city == null) {
                validation = "City is required.";
            }

            if (validation == null) {
                Transaction txn = Ebean.beginTransaction();
                try {
                    ObjectMapper mapper = new ObjectMapper();
					VoucherSignUp dt = VoucherSignUp.find(getBrandId());
					int minimumBelanja = Integer.valueOf(dt.minimumBelanja.intValue());
					int besaranVoucher = Integer.valueOf(dt.besaranVoucher.intValue());
					PromoClient data = InternalService.getInstance().getPromoClients(email, fullName, dt.periode,  besaranVoucher, minimumBelanja, dt.prefixKupon, dt.clientId);
					String enwieSignupRespon="";
					if(data == null){
						enwieSignupRespon="";
					}else{
						enwieSignupRespon =String.valueOf(Json.toJson(data)); 
                    }
                    
                    Member newMember = new Member(confPassword, fullName, email, phone, gender, birthDate, newsLetter,
                            googleId, fbId, getBrand(), enwieSignupRespon, province, city, address, refferalId);
                    newMember.save();
                    if(refferal != null) {
                        refferal.totalRefferal = Optional.ofNullable(refferal.totalRefferal).orElse(0L) + 1L;
                        refferal.update();
                    }

                    MemberLog log = MemberLog.loginMember(deviceModel, deviceType, deviceId, newMember, getBrand());
                    if (log == null) {
                        response.setBaseResponse(0, 0, 0, inputParameter, null);
                        return badRequest(Json.toJson(response));
                    }

                    txn.commit();
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    UserSession session = new UserSession(log.token, df.format(log.expiredDate), log.memberType);
                    mapper.addMixInAnnotations(Member.class, JsonMask.class);
                    session.setProfile_data(Json.parse(mapper.writeValueAsString(newMember)));
                    response.setBaseResponse(1, 0, 1, success, session);
					return ok(Json.toJson(response));
                    
					//return ok(Json.toJson(data));
					

                } catch (Exception e) {
                    e.printStackTrace();
                    txn.rollback();
                } finally {
                    txn.end();
                }
                response.setBaseResponse(0, 0, 0, error, null);
                return badRequest(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, validation, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
}
