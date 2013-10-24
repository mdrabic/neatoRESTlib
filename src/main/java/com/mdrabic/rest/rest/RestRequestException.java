package com.mdrabic.rest.rest;

/**
 * Base class for exceptions thrown by RestRequest whenever it encounters
 * errors executing the REST request.
 *
 * @author mike
 */
public class RestRequestException extends RuntimeException{

    private RestRequestException(){}

    public RestRequestException(String message) {
        super(message);
    }

}
