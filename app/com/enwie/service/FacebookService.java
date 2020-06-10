package com.enwie.service;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.User;
 
public class FacebookService {

    public static User getMe(String accessToken) {
        FacebookClient client = new DefaultFacebookClient(accessToken, Version.VERSION_2_12);
        return client.fetchObject("me", User.class, Parameter.with("fields", "id,name,email"));
    }

    public String getId(String accessToken) {
        User me = getMe(accessToken);
        return me.getId();
    }

    public String getEmail(String accessToken) {
        User me = getMe(accessToken);
        return me.getEmail();
    }

}
