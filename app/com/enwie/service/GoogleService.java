package com.enwie.service;

import com.enwie.mapper.response.MapGooglePeople;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kevinsawicki.http.HttpRequest;

import java.io.IOException;

public class GoogleService {

    public static MapGooglePeople getUser(String userId, String token) throws IOException {
        String BASE_URL = "https://www.googleapis.com/plus/v1/";
        String BASE_URL_PEOPLE = BASE_URL + "people/";
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        String url = BASE_URL_PEOPLE + userId;
        System.out.println(url);
        HttpRequest request = HttpRequest
                .get(url, true, "access_token", token, "fields", "id,displayName,image,emails")
                .authorization("Bearer " + token);
        String body = request.body();
        System.out.println(body);
        return om.readValue(body, MapGooglePeople.class);
    }

    public static MapGooglePeople getMe(String key) throws IOException {
        System.out.println("gooogle get me");
        return getUser("me", key);
    }

}