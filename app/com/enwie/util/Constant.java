package com.enwie.util;

import play.Play;

/**
 * @author hendriksaragih
 */
public class Constant {
    private static Constant instance = null;
    private String emailUser = null;
    private String emailPassword = null;
    private String emailSmtp = null;
    private String emailSender = null;
    private String emailAdmin = null;
    private String frontEndUrl = null;
    private String imageUrl = null;
    private String ckeditorImageUrl = null;
    private String imagePath = null;
    private String mediaPath = null;
    private String apiKeyWeb = null;
    private String apiKeyIOS = null;
    private String apiKeyAndroid = null;
    private String oauthClientId = null;
    private String oauthDomainName = null;
    private String oauthRedirectURI = null;
    private String skinColor = null;
    private String appName = null;
    private Integer appId = null;
    private Long colorId = null;
    private Boolean isProduction = null;

    public static Constant getInstance() {
        if (instance == null) {
            instance = new Constant();
        }
        return instance;
    }

    public String getFrontEndUrl() {
        if (frontEndUrl == null){
            frontEndUrl = Play.application().configuration().getString("enwie.frontend.url");
        }
        return frontEndUrl;
    }

    public Long getColorId() {
        if (colorId == null){
            colorId = Play.application().configuration().getLong("enwie.color.id");
        }
        return colorId;
    }

    public String getEmailAdmin() {
        if (emailAdmin == null){
            emailAdmin = Play.application().configuration().getString("enwie.email.admin");
        }
        return emailAdmin;
    }

    public String getEmailUser() {
        if (emailUser == null){
            emailUser = Play.application().configuration().getString("enwie.email.user");
        }
        return emailUser;
    }

    public String getEmailPassword() {
        if (emailPassword == null){
            emailPassword = Play.application().configuration().getString("enwie.email.password");
        }
        return emailPassword;
    }

    public String getEmailSmtp() {
        if (emailSmtp == null){
            emailSmtp = Play.application().configuration().getString("enwie.email.smtp");
        }
        return emailSmtp;
    }

    public String getEmailSender() {
        if (emailSender == null){
            emailSender = Play.application().configuration().getString("enwie.email.sender");
        }
        return emailSender;
    }

    public String getImageUrl() {
        if (imageUrl == null){
            imageUrl = Play.application().configuration().getString("enwie.images.url");
        }
        return imageUrl;
    }

    public String getImagePath() {
        if (imagePath == null){
            imagePath = Play.application().configuration().getString("enwie.images.path");
        }
        return imagePath;
    }

    public String getMediaPath() {
        if (mediaPath == null){
            mediaPath = getImagePath();
        }
        return mediaPath;
    }

    public String getCKEditorImageUrl() {
        if (ckeditorImageUrl == null){
            ckeditorImageUrl = Play.application().configuration().getString("enwie.ckeditor_images.url");
        }
        return ckeditorImageUrl;
    }

    public String getApiKeyWeb() {
        if (apiKeyWeb == null){
            apiKeyWeb = Play.application().configuration().getString("enwie.api_key.web");
        }
        return apiKeyWeb;
    }

    public String getApiKeyIOS() {
        if (apiKeyIOS == null){
            apiKeyIOS = Play.application().configuration().getString("enwie.api_key.ios");
        }
        return apiKeyIOS;
    }

    public String getApiKeyAndroid() {
        if (apiKeyAndroid == null){
            apiKeyAndroid = Play.application().configuration().getString("enwie.api_key.android");
        }
        return apiKeyAndroid;
    }

    public String getOauthClientId() {
        if (oauthClientId == null){
            oauthClientId = Play.application().configuration().getString("enwie.oauth.client_id");
        }
        return oauthClientId;
    }

    public String getOauthDomainName() {
        if (oauthDomainName == null){
            oauthDomainName = Play.application().configuration().getString("enwie.oauth.domain_name");
        }
        return oauthDomainName;
    }

    public String getOauthRedirectURI() {
        if (oauthRedirectURI == null){
            oauthRedirectURI = Play.application().configuration().getString("enwie.oauth.redirect_uri");
        }
        return oauthRedirectURI;
    }

    public Boolean isProduction() {
        if (isProduction == null){
            isProduction = Play.application().configuration().getBoolean("enwie.is.production", false);
        }
        return isProduction;
    }

    public String getSkinColor() {
        if (skinColor == null){
            skinColor = Play.application().configuration().getString("enwie.skin.color", "");
        }
        return skinColor;
    }
    
    public String getAppName() {
        if (appName == null){
            appName = Play.application().configuration().getString("enwie.app.name", "");
        }
        return appName;
    }

    public Integer getAppId() {
        if (appId == null){
            appId = Play.application().configuration().getInt("enwie.app.id", 0);
        }
        return appId;
    }

    public Boolean isEversac(){
        return getAppId() == 1;
    }
}
