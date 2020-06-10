package com.enwie.service;


import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.enwie.http.response.global.ServiceResponse;
import com.enwie.util.CommonFunction;
import com.enwie.util.Constant;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Brand;
import models.InstagramBanner;
import models.Photo;
import models.UserCms;
import play.Play;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.WSResponse;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class InstagramService {

    public JsonNode fetchData()  {
        String url = Play.application().configuration().getString("enwie.instagram.api.url");
        WSRequestHolder requestHolder = WS.url(url);
        ServiceResponse sr = new ServiceResponse();
        ObjectNode result = Json.newObject();

        try {
            F.Promise<JsonNode> promise = requestHolder.get().map(new F.Function<WSResponse, JsonNode>() {
                @SuppressWarnings("deprecation")
                @Override
                public JsonNode apply(WSResponse response) throws Throwable {
                    result.put("code", response.getStatus());
                    result.put("data", response.asJson());
                    return result;
                }
            });
            return promise.get(120, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 408);
            result.put("data", "Request Time Out.");
            return result;
        }
    }

    public void syncBanner() {
        JsonNode json = fetchData();
        JsonNode timelineMedia = json.findPath("graphql").findPath("user").findPath("edge_owner_to_timeline_media").findPath("edges");
        Transaction txn = Ebean.beginTransaction();
        try {
            for(int i = 0; i < timelineMedia.size(); i++) {
                JsonNode timeline = timelineMedia.get(i).findPath("node");
                String id = timeline.findPath("id").asText();
                String shortcode = timeline.findPath("shortcode").asText();
                String displayUrl = timeline.findPath("display_url").asText();
                String caption = timeline.findPath("edge_media_to_caption").findPath("edges").get(0).findPath("node").findPath("text").asText();

                InstagramBanner cib = InstagramBanner.find.where()
                        .eq("is_deleted", false)
                        .eq("slug", id)
                        .eq("status", true).findUnique();
                if (cib == null) {
                    InstagramBanner ib = new InstagramBanner();

                    ib.name = shortcode;
                    ib.title = shortcode;
                    ib.keyword = shortcode;
                    ib.description = caption;
//                    ib.caption1 = caption;

                    ib.urlType = 2;
                    ib.imageUrl = displayUrl;
                    ib.externalUrl = "https://www.instagram.com/p/" + shortcode;

                    Calendar cal = Calendar.getInstance();
                    Date today = cal.getTime();
                    cal.add(Calendar.YEAR, 1); // to get previous year add -1
                    Date nextYear = cal.getTime();
                    ib.activeFrom = today;
                    ib.activeTo = nextYear;
                    ib.slug = id;
                    ib.status = true;
                    UserCms cms = UserCms.find.byId(2L);
                    ib.userCms = cms;
                    Brand brand = Brand.find.byId(Constant.getInstance().getAppId().longValue());
                    ib.brand = brand;

                    URL url = new URL(displayUrl);
                    BufferedImage img = ImageIO.read(url);
                    Map<String, String> listDir = Photo.listDirectory();
                    String filePath = Constant.getInstance().getImagePath() + listDir.get("ig-ban") + File.separator;
                    File dir = new File(filePath);
                    if(!dir.exists()){
                        dir.mkdir();
                    }

                    File file = new File(filePath + CommonFunction.getCurrentTime("ddMMYY-HHmmss")+"_"+shortcode+id+".jpg");
                    ImageIO.write(img, "jpg", file);

                    ib.imageUrl = Photo.createUrl("ig-ban", file.getName());

                    ib.save();
                }
            }
            txn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            txn.rollback();
        }
    }

}