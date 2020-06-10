package com.enwie.internal.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.enwie.internal.response.model.PromoClient;

import java.util.ArrayList;

/**
 * Created by hendriksaragih on 7/28/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class PromoClientResponse extends InternalResponse<ArrayList<PromoClient>>{


}
