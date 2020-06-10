package com.enwie.midtrans;

import com.enwie.http.HTTPRequest3;
import com.enwie.http.response.global.ServiceResponse;
import com.enwie.midtrans.model.TransactionRequests;
import com.enwie.midtrans.model.TranscationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.SalesOrder;
import play.Play;

/**
 * Created by hendriksaragih on 7/28/17.
 */
public class MidtransService extends HTTPRequest3 {
    private String url;
    private String key;
    private final String TRANSACTION = "/snap/v1/transactions";
    private static MidtransService instance;

    public MidtransService(){
        url = Play.application().configuration().getString("enwie.midtrans.url");
        key = Play.application().configuration().getString("enwie.midtrans.key");
    }

    public static MidtransService getInstance() {
        if (instance == null) {
            instance = new MidtransService();
        }
        return instance;
    }

    private String buildPath(String path){
        return url.concat(path);
    }

    public TranscationResponse createTransaction(SalesOrder so){
        TranscationResponse result = null;
        TransactionRequests params = new TransactionRequests(so);
        ServiceResponse sresponse = postMidtrans(buildPath(TRANSACTION), key, params);
        ObjectMapper mapper = new ObjectMapper();
        if (sresponse.getCode() == 200 || sresponse.getCode() == 201){
            result = mapper.convertValue(sresponse.getData(), TranscationResponse.class);
        }
        return result;
    }
}
