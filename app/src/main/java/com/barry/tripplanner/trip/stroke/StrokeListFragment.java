package com.barry.tripplanner.trip.stroke;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.barry.tripplanner.base.AbstractRecyclerCursorAdapter;
import com.barry.tripplanner.base.DragRecycleListFragment;
import com.barry.tripplanner.provider.TripProvider;

public class StrokeListFragment extends DragRecycleListFragment {

    public static final String ARG_TRIP_ID = "trip_id";
    public static final String ARG_DAY = "day";

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cl = (CursorLoader) super.onCreateLoader(id, args);
        cl.setUri(mUri);
        cl.setSelection(TripProvider.FIELD_STROKE_BELONG_TRIP + "=? AND " + TripProvider.FIELD_STROKE_BELONG_DAY + "=?");
        cl.setSelectionArgs(new String[]{getTripId(), getDay()});
        return cl;
    }

    @Override
    protected AbstractRecyclerCursorAdapter getAdapter() {
        return null;
    }

    @Override
    protected Uri getUri() {
        return TripProvider.getProviderUri(getContext(), TripProvider.TABLE_STROKE);
    }

    @Override
    public void onMoveItem(int fromPos, int toPos) {

    }

    @Override
    public void onItemClick(Cursor cursor) {

    }

    @Override
    protected void onSync() {

    }

    private String getTripId() {
        return getArguments().getString(ARG_TRIP_ID);
    }

    private String getDay() {
        return getArguments().getString(ARG_DAY);
    }
}
