package com.mdrabic.rest.rest;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * EntityRestRequest extends the RestRequest interface by defining an
 * operation to request data from a server and map the response
 * containing the data to a model of type T.
 *
 * @author mike
 */
public interface EntityRestRequest<T> extends RestRequest{

    /**
     * Executes the request by making a connection to the server, reading the stream
     * and returning the data in a ResourceResponse.
     * @param model of type T to use as the pojo to parse the json data into.
     * @return a ResourceResponse of type T.
     */
    EntityRestResponse<T> executeForEntity(TypeReference<T> model);
}
