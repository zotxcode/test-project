package com.enwie.internal.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by hendriksaragih on 7/28/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class InternalResponse<T> {
    private Internal<T> internal;

    public Internal<T> getInternal() {
        return internal;
    }

    public void setInternal(Internal<T> internal) {
        this.internal = internal;
    }

    public T getResults(){
        return  internal.getResults();
    }

}

@JsonIgnoreProperties(ignoreUnknown=true)
class Internal<T> {
    private T results;

    public T getResults() {
        return results;
    }

    public void setResults(T results) {
        this.results = results;
    }
}