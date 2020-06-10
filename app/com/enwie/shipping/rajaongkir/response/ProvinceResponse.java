package com.enwie.shipping.rajaongkir.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.enwie.shipping.rajaongkir.response.model.Province;

import java.util.ArrayList;

/**
 * Created by hendriksaragih on 7/28/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ProvinceResponse extends RajaOngkirResponse<ArrayList<Province>>{


}
