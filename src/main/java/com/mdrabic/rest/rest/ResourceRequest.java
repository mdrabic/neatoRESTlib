package com.mdrabic.rest.rest;

import com.mdrabic.rest.util.Validate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A implementation of the EntityRestRequest interface. Only HTTP GET request
 * are permitted. T should be a model of the expected JSON data that is
 * annotated with Jackson Annotations.
 *
 * @author mike
 */
public class ResourceRequest<T> implements EntityRestRequest<T> {

    private URL mUrl;
    //Thread safe and can be used across instances
    private static final ObjectMapper mMapper = new ObjectMapper();


    public ResourceRequest(URL url) {
        mMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        Validate.notNull(url, "URL cannot be null");
        mUrl = url;
    }

    /**
     * Submit an HTTP GET request, read the response and return the
     * data.
     * @return the data in the HTTP response message.
     */
    @Override
    public String execute() {
        HttpURLConnection urlConnection;
        String jsonData = "";

        urlConnection = connect(mUrl);

        try {
            InputStream stream = urlConnection.getInputStream();
            jsonData = readInputStream(stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
            String error = readInputStream(urlConnection.getErrorStream());
            throw new RestRequestException("Unable to open InputStream to " + mUrl + "\n" + error);
        } finally {
            urlConnection.disconnect();
        }

        return jsonData;
    }

    /**
     * Submit an HTTP GET request and parse the returned data from
     * the HTTP response into the Jackson annotated model of type T.
     * @param model of type T to use as the pojo to parse the json data into.
     * @return a RestResponse containing the data.
     */
    public EntityRestResponse<T> executeForEntity(TypeReference<T> model) {
        Validate.notNull(model, "Model object must not be null");
        EntityRestResponse<T> response = null;
        String rawJson = execute();
        T modelInstance = null;

        try {
            modelInstance = mMapper.readValue(rawJson, model);
            response = new ResourceResponse<T>(rawJson, modelInstance);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    /**
     * Attempt to open a connection to the specified url.
     * @param url to connect to.
     * @return a HTTP connection to the specified url
     * @throws RestRequestException if unable to open a connection.
     */
    private HttpURLConnection connect(URL url) {
        HttpURLConnection urlConnection;

        try {
            //default encoding is gzip, no need to set request.
            urlConnection = (HttpURLConnection) url.openConnection();
            //force a flush of the underlying output/input stream
            urlConnection.getResponseCode();
        } catch (EOFException e) {
            e.printStackTrace();
            throw new RestRequestException("Unable to open connection to " + mUrl);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RestRequestException("Unable to open connection to " + mUrl);
        }
        return urlConnection;
    }

    /**
     * Read the input stream and return the content as a String.
     * @param stream to read.
     * @return the content of the InputStream.
     */
    private String readInputStream(InputStream stream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder result = new StringBuilder();
        String line = "";

        try {
            while ((line = reader.readLine()) != null) {
               result.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RestRequestException("Error reading InputStream from HTTP connection");
        }

        return result.toString();
    }


}
