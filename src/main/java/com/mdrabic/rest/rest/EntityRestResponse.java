package com.mdrabic.rest.rest;

/**
 * A RestResponse that is extended to hold a model of type T containing
 * parsed JSON data.
 *
 * @author mike
 */
public interface EntityRestResponse<T> extends RestResponse {

    /**
     * Get the model that was populated with the JSON data.
     * @return the populated model
     */
    T getModel();

}
