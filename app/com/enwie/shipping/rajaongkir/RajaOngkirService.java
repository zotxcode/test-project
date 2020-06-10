package com.enwie.shipping.rajaongkir;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.enwie.http.HTTPRequest3;
import com.enwie.http.response.global.ServiceResponse;
import com.enwie.shipping.rajaongkir.response.CityResponse;
import com.enwie.shipping.rajaongkir.response.PriceResponse;
import com.enwie.shipping.rajaongkir.response.ProvinceResponse;
import com.enwie.shipping.rajaongkir.response.model.City;
import com.enwie.shipping.rajaongkir.response.model.ParamCost;
import com.enwie.shipping.rajaongkir.response.model.Province;
import com.enwie.shipping.rajaongkir.response.model.Service;
import play.Play;
import play.libs.Json;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hendriksaragih on 7/28/17.
 */
public class RajaOngkirService extends HTTPRequest3 {
    private String url;
    private String key;
    private final String GET_PROVINCE_PATH = "/province";
    private final String GET_CITY_PATH = "/city";
    private final String GET_PRICE_PATH = "/cost";
    private static RajaOngkirService instance;

    public RajaOngkirService(){
        url = Play.application().configuration().getString("enwie.shipping.rajaongkir.url");
        key = Play.application().configuration().getString("enwie.shipping.rajaongkir.key");
    }

    public static RajaOngkirService getInstance() {
        if (instance == null) {
            instance = new RajaOngkirService();
        }
        return instance;
    }

    private String buildPath(String path){
        return url.concat(path);
    }

    public List<Province> getProvince(){
        ProvinceResponse result = null;
        ServiceResponse sresponse = get(buildPath(GET_PROVINCE_PATH), key);
        ObjectMapper mapper = new ObjectMapper();
        if (sresponse.getCode() == 200){
            result = mapper.convertValue(sresponse.getData(), ProvinceResponse.class);
        }
        return result.getResults();
    }

    public List<City> getCity(){
        CityResponse result = null;
        ServiceResponse sresponse = get(buildPath(GET_CITY_PATH), key);
        ObjectMapper mapper = new ObjectMapper();
        if (sresponse.getCode() == 200){
            result = mapper.convertValue(sresponse.getData(), CityResponse.class);
        }
        return result.getResults();
    }

    public ArrayList<Service> getPrice(String courier, String origin, String destination, Integer weight){
        PriceResponse result = null;
        ParamCost param = new ParamCost();
        param.setCourier(courier);
        param.setOrigin(origin);
        param.setDestination(destination);
        param.setWeight(weight);
        ServiceResponse sresponse = post(buildPath(GET_PRICE_PATH), key, param);
        ObjectMapper mapper = new ObjectMapper();
        if (sresponse.getCode() == 200){
            result = mapper.convertValue(sresponse.getData(), PriceResponse.class);
        }
        return result.getResults();
    }

}
