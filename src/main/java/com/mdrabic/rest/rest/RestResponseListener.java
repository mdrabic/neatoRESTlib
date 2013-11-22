package com.mdrabic.rest.rest;

/**
 * {WRITE A DESCRIPTION!!!}
 *
 * @author mike
 */
public interface RestResponseListener<T> {

    /**
     * Called after an HTTP response has been parsed by Jackson and
     * mapped to a pojo of type T.
     *
     * @param response json data mapped to model of type T.
     */
    void onResponseParsed(T response);
}
