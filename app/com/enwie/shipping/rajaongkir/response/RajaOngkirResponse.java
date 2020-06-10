package com.enwie.shipping.rajaongkir.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by hendriksaragih on 7/28/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class RajaOngkirResponse<T> {
    private RajaOngkir<T> rajaongkir;

    public RajaOngkir<T> getRajaongkir() {
        return rajaongkir;
    }

    public void setRajaongkir(RajaOngkir<T> rajaongkir) {
        this.rajaongkir = rajaongkir;
    }

    public T getResults(){
        return  rajaongkir.getResults();
    }

}

@JsonIgnoreProperties(ignoreUnknown=true)
class RajaOngkir<T> {
    private T results;

    public T getResults() {
        return results;
    }

    public void setResults(T results) {
        this.results = results;
    }
}