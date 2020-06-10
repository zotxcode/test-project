package com.enwie.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.enwie.http.HTTPRequest3;
import com.enwie.http.response.global.ServiceResponse;
import com.enwie.internal.response.model.ParamPromoClient;
import com.enwie.internal.response.model.PromoClient;
import play.Play;

/**
 * Created by hendriksaragih on 7/28/17.
 */
public class InternalService extends HTTPRequest3 {
    private String url;
    private final String GET_PROMO_CLIENTS_PATH = "/api/promo-clients/";
    private static InternalService instance;

    public InternalService(){
        url = Play.application().configuration().getString("enwie.internal.api.url");
    }

    public static InternalService getInstance() {
        if (instance == null) {
            instance = new InternalService();
        }
        return instance;
    }

    private String buildPath(String path){
        return url.concat(path);
    }

    public PromoClient getPromoClients(String username, String fullname, Integer duration, Integer cutAmount, Integer minimalAmount, String couponPrefix, String client){
        PromoClient result = null;
        ParamPromoClient param = new ParamPromoClient();
        param.setUsername(username);
        param.setFullname(fullname);
        param.setDuration(duration);
        param.setCutAmount(cutAmount);
        param.setMinimalAmount(minimalAmount);
        param.setCouponPrefix(couponPrefix);
        param.setClient(client);

        ServiceResponse sresponse = post(buildPath(GET_PROMO_CLIENTS_PATH), param);
        ObjectMapper mapper = new ObjectMapper();
        if (sresponse.getCode() == 200){
            result = mapper.convertValue(sresponse.getData(), PromoClient.class);
        }
        return result;
    }
}
