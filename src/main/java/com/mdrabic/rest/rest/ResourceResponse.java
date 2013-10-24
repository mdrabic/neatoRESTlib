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
     *
     * @param rawJson json data
     * @param model an empty model
     */
    public ResourceResponse(String rawJson, T model) {
        Validate.notNull(rawJson, "rawJson cannot be null");
        Validate.notNull(model, "model must not be null");
        mRawJson = rawJson;
        mModel = model;
    }

    @Override
    public String getRawJson() {
        return mRawJson;
    }

    @Override
    public T getModel() {
        return mModel;
    }
}
