package com.mdrabic.rest.content;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mdrabic.rest.rest.EntityRestRequest;
import com.mdrabic.rest.rest.EntityRestResponse;
import com.mdrabic.rest.rest.RestRequestException;
import com.mdrabic.rest.util.Validate;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A loader that will handle making a REST request via a background thread.
 *
 * @author mike
 */
public class SimpleRestLoader<T> extends AsyncTaskLoader<List<T>> {

    private List<T> mEntityList;
    private EntityRestRequest<T> mAgencyRestRequest;
    private EntityRestResponse<T> mCurrentRestResponse;
    private DetailedLoaderCallbacks mDetailedCallback;
    private Comparator mComparator;
    private TypeReference mTypeReference;

    /**
     * Create an instance of the SimpleRestLoader.
     * @param c the context.
     * @param restRequest the rest request to execute.
     * @param comparator used to sort the returned data. Can be null.
     * @param typeReference the type of data returned.
     */
    public SimpleRestLoader(Context c, EntityRestRequest<T> restRequest,
                            Comparator comparator, TypeReference typeReference) {
        super(c);
        Validate.notNull(restRequest, "restRequest cannot be null");
        Validate.notNull(typeReference, "typeReference cannot be null");
        mAgencyRestRequest = restRequest;
        mComparator = comparator;
        mTypeReference = typeReference;
    }

    @Override
    public List<T> loadInBackground() {
        //signal the background task started
        if (mDetailedCallback != null) {
            mDetailedCallback.onLoaderStart();
        }

        try {
            mCurrentRestResponse = mAgencyRestRequest.executeForEntity(mTypeReference);
        } catch (RestRequestException e) {
            e.printStackTrace();
            //signal the background task finished
            if (mDetailedCallback != null) {
                mDetailedCallback.onLoaderFinish();
            }

            return mEntityList;
        }
        //if the model was unable to be populated with data, aka null, just return
        //the previous loaded data set which could be null if this is the first load.
        if (mCurrentRestResponse.getModel() != null) {
            T data = mCurrentRestResponse.getModel();
            if (data instanceof List)
                mEntityList = (List<T>) data;

            //optionally sort the data if a comparator was supplied.
            if (mComparator != null) {
                Collections.sort(mEntityList, mComparator);
            }
        }

        //signal the background task finished
        if (mDetailedCallback != null) {
            mDetailedCallback.onLoaderFinish();
        }

        return mEntityList;
    }

    @Override
    public void deliverResult(List<T> data) {
        mEntityList = data;

        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStartLoading() {
        if (takeContentChanged() || mEntityList == null) {
            forceLoad();
        } else {
            deliverResult(mEntityList);
        }
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        if (mEntityList != null) {
            mEntityList = null;
        }

    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    public void setDetailedCallbackListener(DetailedLoaderCallbacks callback) {
        mDetailedCallback = callback;
    }
}
