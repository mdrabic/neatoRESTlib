package com.mdrabic.rest.rest;

/**
 * The RestRequest interface is the base interface for all RestRequest types. It
 * on basic function which is to execute the request and return any data as a
 * String
 *
 * @author mike
 */
public interface RestRequest {

    /**
     * Executes the request by making a connection to the server, reading the stream
     * and returning the data in a String.
     *
     * @return a String containing the results
     */
    String execute();
}
