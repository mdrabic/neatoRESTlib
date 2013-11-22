package com.mdrabic.rest.rest;

import com.mdrabic.rest.util.Validate;

/**
 * Implementation of EntityRestResponse that implements operations
 * for both EntityRestResponse and RestResponse. Nulls are not
 * permitted.
 *
 * @author mike
 */
public class ResourceResponse<T> implements EntityRestResponse<T> {

    private String mRawJson;
    private T mModel;

    /**
     * @param rawJson json data
     * @param model   an empty model
     */
    public ResourceResponse(String rawJson, T model) {
        Validate.notNull(rawJson, "rawJson cannot be null");
        mRawJson = rawJson;
        mModel = model;
    }

    @Override
    public String getRawJson() {
        return mRawJson;
    }

    /**
     * Get the model.
     * @return the model or null if the the data cannot be parsed into a model of
     * type T.
     */
    @Override
    public T getModel() {
        return mModel;
    }
}
