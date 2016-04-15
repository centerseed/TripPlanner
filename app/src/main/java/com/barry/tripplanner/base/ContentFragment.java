package com.barry.tripplanner.base;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;

abstract public class ContentFragment extends SyncFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    protected Uri mUri;
    protected ContentResolver mResolver;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUri = getUri();
        mResolver = getContext().getContentResolver();
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cl = new CursorLoader(getActivity());
        cl.setUri(mUri);
        cl.forceLoad();
        return cl;
    }

    @Override
    public void onResume() {
        super.onResume();
        reload();
    }

    protected void reload() {
        if (mUri != null)
            getLoaderManager().restartLoader(0, null, this);
    }

    protected abstract Uri getUri();
}
