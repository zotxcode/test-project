package controllers.api;

import assets.JsonMask;
import com.avaje.ebean.*;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.enwie.api.BaseResponse;
import com.enwie.api.UserSession;
import com.enwie.mapper.response.MapMember;
import com.enwie.mapper.response.MapShippingAddress;
import controllers.BaseController;
import com.enwie.util.CommonFunction;
import models.Allergy;
import models.City;
import models.Member;
import models.MemberLog;
import models.Photo;
import models.Province;
import models.ShippingAddress;
import play.data.validation.ValidationError;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.nio.file.Paths;
import java.io.File;


/**
 * Created by hendriksaragih on 3/19/17.
 */
public class ProfileController extends BaseController {
    @SuppressWarnings("rawtypes")
    private static BaseResponse response = new BaseResponse();
    private static final String featureKey = "profile";

    public static Result index() {
        return ok();
    }

    public static Result getMemberProfile() throws JsonProcessingException {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            ObjectMapper om = new ObjectMapper();
            om.addMixInAnnotations(Member.class, JsonMask.class);
            response.setBaseResponse(1, offset, 1, success, Json.parse(om.writeValueAsString(actor)));
            return ok(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result myAccount() throws JsonProcessingException {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            response.setBaseResponse(1, offset, 1, success, new ObjectMapper().convertValue(actor, MapMember.class));
            return ok(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result findReferral() throws JsonProcessingException {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            Member referral = Member.find.where()
                .eq("id", actor.refferalId)
                .eq("t0.is_deleted", false)
                .setOrderBy("t0.id DESC").setMaxRows(1).findUnique();
            if (referral != null) {
                response.setBaseResponse(1, offset, 1, success, new ObjectMapper().convertValue(referral, MapMember.class));
                return ok(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, inputParameter, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result updateProfile() {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            JsonNode json = request().body().asJson();
            // json.has("birth_date") && json.has("gender") 
            if (json.has("email") && json.has("full_name") && json.has("phone") && json.has("province_id") && json.has("city_id")  && json.has("address")) {
                String email = json.findPath("email").asText();
                String fullName = json.findPath("full_name").asText();
                String phone = json.has("phone") ? json.findPath("phone").asText() : "";
                String address = json.findPath("address").asText();
                Long provinceId = json.findPath("province_id").asLong();
                Long cityId = json.findPath("city_id").asLong();

                String validation = Member.validation(actor.id, email, phone, getBrandId());
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
                        // Boolean newsLetter = !json.has("newsletter") || json.findPath("newsletter").asBoolean();
                        ObjectMapper mapper = new ObjectMapper();
                        Member model = Member.find.byId(actor.id);
                        model.fullName = fullName;
                        model.phone = phone;
                        model.province = province;
                        model.city = city;
                        model.address = address;
                        // model.email = email;
                        // model.emailNotifikasi = email;
                        // model.gender = json.findPath("gender").asText();
                        // model.birthDate = new SimpleDateFormat("yyyy-MM-dd").parse(json.findPath("birth_date").asText());
                        // model.newsLetter = newsLetter;

                        model.update();
                        txn.commit();

                        String apiKey = request().headers().get(API_KEY)[0];
                        String token = request().headers().get(TOKEN)[0];
                        MemberLog log = MemberLog.isMemberAuthorized(token, apiKey, getBrandId());
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        UserSession session = new UserSession(log.token, df.format(log.expiredDate), log.memberType);
                        ObjectMapper om = new ObjectMapper();
                        om.addMixInAnnotations(Member.class, JsonMask.class);
                        try {
                            session.setProfile_data(Json.parse(om.writeValueAsString(model)));
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }

                        response.setBaseResponse(1, offset, 1, updated, session);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        e.printStackTrace();
                        txn.rollback();
                    } finally {
                        txn.end();
                    }
                }
                response.setBaseResponse(0, 0, 0, validation, null);
                return badRequest(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, inputParameter, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result uploadPhotoProfile() {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            Http.MultipartFormData body = request().body().asMultipartFormData();
            Transaction txn = Ebean.beginTransaction();
            try {
                ObjectMapper mapper = new ObjectMapper();
                Member model = Member.find.byId(actor.id);
                Http.MultipartFormData.FilePart picture = body.getFile("image_url");
                // File newFile = Photo.uploadImageCrop2(picture, "profile", CommonFunction.slugGenerate(data.name), data.imageUrlX, data.imageUrlY, data.imageUrlW, data.imageUrlH, Photo.profileImageSize, "jpg");

                // File newFile = Photo.uploadImage(picture, "profile", CommonFunction.slugGenerate(data.name), data.imageUrlX, data.imageUrlY, data.imageUrlW, data.imageUrlH, Photo.profileImageSize, "jpg");
                File newFile = Photo.uploadImage(picture, "profile", CommonFunction.slugGenerate(model.fullName), Photo.profileImageSize, "jpg");

                model.mediumImageUrl = newFile != null ? Photo.createUrl("profile", newFile.getName()) : "";

                if (newFile != null){
                    Photo.saveRecord("profile", newFile.getName(), "", "", "", picture.getFilename(), model.id, "member", "Profile", model.id);
                }
                model.update();
                txn.commit();
                response.setBaseResponse(1, offset, 1, success, new ObjectMapper().convertValue(model, MapMember.class));
                return ok(Json.toJson(response));
            }catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }
            response.setBaseResponse(0, 0, 0, inputParameter, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result updateAllergyInfo() {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            JsonNode json = request().body().asJson();
            Member model = Member.find.byId(actor.id);
            Transaction txn = Ebean.beginTransaction();
            try {
                List<Allergy> listAllergy = new ArrayList<>();
                for (JsonNode id : json.withArray("ids")) {
                    if (id.asLong() > 0) {
                        Allergy allergy = Allergy.findById(id.asLong());
                        if (allergy != null) {
                            listAllergy.add(allergy);
                        }
                    }
                }

                model.allergies = listAllergy;
                System.out.println(listAllergy);
                model.saveManyToManyAssociations("allergies");
                model.update();
                txn.commit();
                response.setBaseResponse(1, offset, 1, updated, model);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }
            response.setBaseResponse(0, 0, 0, inputParameter, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result getShippingAddress() {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            JsonNode json = request().body().asJson();
            List<ShippingAddress> sa = ShippingAddress.find.where()
                .eq("member", actor)
                .eq("is_deleted", false)
                .orderBy("id ASC")
                .findList();

            response.setBaseResponse(sa.size(), offset, sa.size(), success, new ObjectMapper().convertValue(sa, MapShippingAddress[].class));
            return ok(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result addShippingAddress() {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            JsonNode json = request().body().asJson();
            String recipientName = json.findPath("recipient_name").asText();
            String phone = json.findPath("phone").asText();
            String address = json.findPath("address").asText();
            String provinceId = json.has("province_id") ? json.findPath("province_id").asText() : "";
            String cityId = json.has("city_id") ? json.findPath("city_id").asText() : "";
            String postalCode = json.has("postal_code") ? json.findPath("postal_code").asText() : "";
            Boolean isDefault = !json.has("is_default") || json.findPath("is_default").asBoolean();

            Member model = Member.find.byId(actor.id);
            Transaction txn = Ebean.beginTransaction();
            try {
                Province prov = Province.findById(Long.valueOf(provinceId));
                City city = City.findById(Long.valueOf(cityId));
                ShippingAddress sa = new ShippingAddress();
                sa.member = model;
                sa.recipientName = recipientName;
                sa.phone = phone;
                sa.address = address;
                sa.isDefault = isDefault;
                sa.province = prov;
                sa.city = city;
                sa.postalCode = postalCode;

                List<ValidationError> errors = sa.validate();
                if (errors != null && errors.size() > 0) {
                    response.setBaseResponse(0, 0, 0, inputParameter, errors);
                    return badRequest(Json.toJson(response));
                }
                
                ShippingAddress defSA = ShippingAddress.find.where()
                    .eq("member", model)
                    .eq("is_deleted", false)
                    .eq("is_default", true)
                    .setMaxRows(1).findUnique();
                if (defSA != null) {
                    if (isDefault == true) {
                        defSA.isDefault = false;
                        defSA.update();
                    }
                } else {
                    sa.isDefault = true;
                }

                sa.save();
                txn.commit();
                response.setBaseResponse(1, offset, 1, success, sa);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }
            response.setBaseResponse(0, 0, 0, inputParameter, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result updateShippingAddress(Long id) {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            JsonNode json = request().body().asJson();
            String recipientName = json.findPath("recipient_name").asText();
            String phone = json.findPath("phone").asText();
            String address = json.findPath("address").asText();
            String provinceId = json.has("province_id") ? json.findPath("province_id").asText() : "";
            String cityId = json.has("city_id") ? json.findPath("city_id").asText() : "";
            String postalCode = json.has("postal_code") ? json.findPath("postal_code").asText() : "";
            Boolean isDefault = !json.has("is_default") || json.findPath("is_default").asBoolean();

            Member model = Member.find.byId(actor.id);
            Transaction txn = Ebean.beginTransaction();
            try {
                Province prov = Province.findById(Long.valueOf(provinceId));
                City city = City.findById(Long.valueOf(cityId));
                ShippingAddress sa = ShippingAddress.find.where()
                    .eq("member", model)
                    .eq("is_deleted", false)
                    .eq("id", id)
                    .setMaxRows(1).findUnique();
                if (sa == null) {
                    response.setBaseResponse(0, 0, 0, notFound, null);
                    return notFound(Json.toJson(response));
                }

                sa.member = model;
                sa.recipientName = recipientName;
                sa.phone = phone;
                sa.address = address;
                // sa.isDefault = isDefault;
                sa.province = prov;
                sa.city = city;
                sa.postalCode = postalCode;

                List<ValidationError> errors = sa.validate();
                if (errors != null && errors.size() > 0) {
                    response.setBaseResponse(0, 0, 0, inputParameter, errors);
                    return badRequest(Json.toJson(response));
                }

                sa.update();
                txn.commit();
                response.setBaseResponse(1, offset, 1, success, sa);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }
            response.setBaseResponse(0, 0, 0, inputParameter, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result deleteShippingAddress(Long id) {
        Member currentMember = checkMemberAccessAuthorization();
        if (currentMember != null) {
            ShippingAddress sa = ShippingAddress.find.where()
                .eq("member", currentMember)
                .eq("is_deleted", false)
                .eq("id", id)
                .setMaxRows(1).findUnique();

            if (sa != null) {
                sa.isDeleted = true;
                sa.update();

                response.setBaseResponse(1, offset, 1, deleted, null);
                return ok(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return notFound(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result setDefaultShippingAddress(Long id) {
        Member currentMember = checkMemberAccessAuthorization();
        if (currentMember != null) {
            ShippingAddress sa = ShippingAddress.find.where()
                .eq("member", currentMember)
                .eq("is_deleted", false)
                .eq("id", id)
                .setMaxRows(1).findUnique();

            if (sa != null) {
                ShippingAddress defSA = ShippingAddress.find.where()
                    .eq("member", currentMember)
                    .eq("is_deleted", false)
                    .eq("is_default", true)
                    .not(Expr.eq("id", id))
                    .setMaxRows(1).findUnique();
                if (defSA != null) {
                    defSA.isDefault = false;
                    defSA.update();
                }

                sa.isDefault = true;
                sa.update();

                response.setBaseResponse(1, offset, 1, success, null);
                return ok(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return notFound(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

}
