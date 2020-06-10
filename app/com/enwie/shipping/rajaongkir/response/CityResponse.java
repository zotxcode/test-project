package com.enwie.shipping.rajaongkir.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.enwie.shipping.rajaongkir.response.model.City;

import java.util.ArrayList;

/**
 * Created by hendriksaragih on 7/28/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class CityResponse extends RajaOngkirResponse<ArrayList<City>>{


}
