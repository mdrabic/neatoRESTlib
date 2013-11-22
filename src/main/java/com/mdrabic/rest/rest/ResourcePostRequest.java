package com.mdrabic.rest.rest;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mdrabic.rest.util.Validate;
import org.apache.http.NameValuePair;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * A implementation of the EntityRestRequest interface. This class only supports
 * HTTP POST requests. T should be a model of the expected JSON data that is
 * annotated with Jackson Annotations.
 *
 * @author mike
 */
public class ResourcePostRequest<T> implements EntityRestRequest<T> {

    private URL mUrl;
    private List<NameValuePair> mParameters;
    //Thread safe and can be used across instances
    private static final ObjectMapper mMapper = new ObjectMapper();


    public ResourcePostRequest(URL url) {
        mMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        Validate.notNull(url, "URL cannot be null");
        mUrl = url;
    }

    /**
     * Submit an HTTP GET request, read the response and return the
     * data.
     *
     * @return the data in the HTTP response message.
     */
    @Override
    public String execute() throws RestRequestException {
        HttpURLConnection urlConnection;
        String jsonData = "";

        urlConnection = connect(mUrl);

        try {
            String query = getQuery(mParameters);
            urlConnection.setRequestProperty("Content-Length", "" + Integer.toString(query.length()));
            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writer.write(query);
            writer.flush();

            InputStream inputStream = urlConnection.getInputStream();
            jsonData = readInputStream(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
            String error = readInputStream(urlConnection.getErrorStream());
            throw new RestRequestException("Unable to process stream from " + mUrl + "\n" + error);
        } finally {
            urlConnection.disconnect();
        }

        return jsonData;
    }

    /**
     * Submit an HTTP POST request and parse the returned data from
     * the HTTP response into the Jackson annotated model of type T.
     *
     * @param model of type T to use as the pojo to parse response json data into.
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
        } catch (JsonParseException e) {
            //TODO throw this and let client handle it.
        } catch (JsonMappingException e) {
            //TODO throw this and let client handle it.
        } catch (IOException e) {
            //TODO throw this and let client handle it.
        }

        return response;
    }

    /**
     * Attempt to open a connection to the specified url.
     *
     * @param url to connect to.
     * @return a HTTP connection to the specified url
     * @throws RestRequestException if unable
     *                              to open a connection.
     */
    private HttpURLConnection connect(URL url) {
        HttpURLConnection urlConnection;

        try {
            //default encoding is gzip, no need to set request.
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //urlConnection.setRequestProperty("Accept-Charset", "utf-8");

        } catch (IOException e) {
            e.printStackTrace();
            throw new RestRequestException("Unable to open connection to " + mUrl);
        }
        return urlConnection;
    }

    /**
     * Read the input stream and return the content as a String.
     *
     * @param stream to read.
     * @return the content of the InputStream.
     */
    private String readInputStream(InputStream stream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder result = new StringBuilder();
        String line = "";

        if (stream != null) {
            try {
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RestRequestException("Error reading InputStream from HTTP connection");
            }
        }

        return result.toString();
    }

    /**
     * Add a parameter to be used in the HTTP POST request. This name value pair
     * does not need to be encoded. It will be UTF-8 encoded before execution.
     *
     * @param param to add to the POST request.
     */
    public void addParameter(NameValuePair param) {
        if (mParameters == null) {
            mParameters = new ArrayList<NameValuePair>();
        }

        mParameters.add(param);
    }

    /**
     * Clear the parameters to be used in the POST request.
     */
    public void clearParameters() {
        mParameters.clear();
    }

    /**
     * Get all the parameters used in the POST request.
     *
     * @return list of parameters.
     */
    public List<NameValuePair> getParameters() {
        return mParameters;
    }

    /**
     * Converts a list of NameValuePair to a String that can be used as the query in a
     * HTTP POST request.
     *
     * @param params to encode
     * @return a UTF-8 encoded string of the params.
     * @throws UnsupportedEncodingException
     */
    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }


}