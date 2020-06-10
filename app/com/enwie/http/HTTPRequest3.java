package com.enwie.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Iterables;
import com.enwie.http.response.global.ServiceResponse;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.WSResponse;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
/**
 * Created by hendriksaragih on 3/19/17.
 */
public class HTTPRequest3 {

    public String getParameter(Iterable<Param> params) {
        String parameter = "";
        int limit = Iterables.size(params);
        int count = 0;
        for (Param param : params) {
            parameter += param.getName() + "=" + param.getValue();
            count++;
            if (count != limit) {
                parameter += "&";
            }
        }
        return parameter;
    }

    public String getURL(String url, String method, String parameter) {
        String urlSet = url + method;
        if (!parameter.equals(""))
            urlSet += "?" + parameter;
        return urlSet;
    }

    public String baseGet(WSRequestHolder wsr) {
        try {
//            SSLCertificateValidation.disable();
            Promise<JsonNode> promise = wsr.get().map(new Function<WSResponse, JsonNode>() {
                @SuppressWarnings("deprecation")
                @Override
                public JsonNode apply(WSResponse response) throws Throwable {
                    ObjectNode result = Json.newObject();
                    result.put("code", response.getStatus());
                    result.put("data", response.asJson());
                    return result;
                }
            });
            return promise.get(60, TimeUnit.SECONDS).toString();
        } catch (Exception e) {
            e.printStackTrace();
            ObjectNode result = Json.newObject();
            result.put("code", 408);
            result.put("data", "Request Time Out.");
            return result.toString();
        }
    }

    public String basePost(WSRequestHolder wsr, Object requestBody) {
        try {
            SSLCertificateValidation.disable();
            System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
            Promise<JsonNode> promise = wsr.post(Json.toJson(requestBody)).map(new Function<WSResponse, JsonNode>() {
                @SuppressWarnings("deprecation")
                @Override
                public JsonNode apply(WSResponse response) throws Throwable {
                    ObjectNode result = Json.newObject();
                    JsonNode resp = response.asJson();
                    result.put("code", response.getStatus());
                    result.put("data", resp);
                    return result;
                }
            });
            return promise.get(60, TimeUnit.SECONDS).toString();
        } catch (Exception e) {
            e.printStackTrace();
            ObjectNode result = Json.newObject();
            result.put("code", 408);
            result.put("data", "Request Time Out.");
            return result.toString();
        }
    }

    public ServiceResponse get(String url, Param authorization, Param...headers) {
        WSRequestHolder requestHolder = WS.url(url);
        requestHolder.setHeader("Authorization", (String) authorization.getValue());
        requestHolder.setHeader("Accept", "application/json");
        requestHolder.setHeader("Content-Type", "application/json");
        for (Param param : headers) {
            requestHolder.setHeader(param.getName(), ""+param.getValue());
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(baseGet(requestHolder), ServiceResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ServiceResponse get(String url, String key) {
        WSRequestHolder requestHolder = WS.url(url);
        requestHolder.setHeader("key", key);
        requestHolder.setHeader("Accept", "application/json");
        requestHolder.setHeader("Content-Type", "application/json");
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(baseGet(requestHolder), ServiceResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ServiceResponse get(String url) {
        WSRequestHolder requestHolder = WS.url(url);
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(baseGet(requestHolder), ServiceResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ServiceResponse post(String url, Param authorization, final Object request, Param...headers) {
        WSRequestHolder requestHolder = WS.url(url);
        requestHolder.setHeader("Authorization", (String) authorization.getValue());
        requestHolder.setHeader("Accept", "application/json");
        requestHolder.setHeader("Content-Type", "application/json");
        for (Param param : headers) {
            requestHolder.setHeader(param.getName(), ""+param.getValue());
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(basePost(requestHolder, request), ServiceResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ServiceResponse post(String url, String key, final Object request) {
        WSRequestHolder requestHolder = WS.url(url);
        requestHolder.setHeader("key", key);
        requestHolder.setHeader("Accept", "application/json");
        requestHolder.setHeader("Content-Type", "application/json");
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(basePost(requestHolder, request), ServiceResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ServiceResponse postMidtrans(String url, String key, final Object request) {
        WSRequestHolder requestHolder = WS.url(url);
        requestHolder.setHeader("Authorization", "Basic "+key);
        requestHolder.setHeader("Accept", "application/json");
        requestHolder.setHeader("Content-Type", "application/json");
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(basePost(requestHolder, request), ServiceResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ServiceResponse post(String url, final Object request) {
        WSRequestHolder requestHolder = WS.url(url);
//        requestHolder.setHeader("Accept", "application/json");
        requestHolder.setHeader("Content-Type", "application/json");
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(basePost(requestHolder, request), ServiceResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
