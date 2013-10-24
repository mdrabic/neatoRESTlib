package com.mdrabic.rest.rest;

/**
 * The base interface for all RestResponse types.
 *
 * @author mike
 */
public interface RestResponse {

    /**
     * Get the raw JSON data
     * @return the HTTP response code
     */
    String getRawJson();
}
