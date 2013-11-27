package com.mdrabic.rest.content;

import android.support.v4.app.LoaderManager.LoaderCallbacks;

/**
 * An extension of the LoaderCallbacks that defines two additional life cycle calls.
 *
 * @author mike
 */
public interface DetailedLoaderCallbacks<D> extends LoaderCallbacks<D> {

    /**
     * Called when the loader starts its background load.
     */
    void onLoaderStart();

    /**
     * Called when the loader finishes its background load.
     */
    void onLoaderFinish();
}
