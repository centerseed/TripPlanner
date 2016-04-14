package com.barry.tripplanner.trip;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.barry.tripplanner.R;
import com.barry.tripplanner.base.AbstractRecyclerCursorAdapter;
import com.barry.tripplanner.base.DragListCallback;
import com.barry.tripplanner.base.DragRecycleListFragment;
import com.barry.tripplanner.provider.TripProvider;

public class DayListFragment extends DragRecycleListFragment implements DragListCallback {

    public static final String ARG_TRIP_ID = "trip_id";
    ContentResolver mResolver;

    @Override
    public void onResume() {
        mResolver = getContext().getContentResolver();
        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cl = (CursorLoader) super.onCreateLoader(id, args);
        cl.setSelection(TripProvider.FIELD_DAY_BELONG_TRIP + "=?");
        cl.setSelectionArgs(new String[]{getTripId() + ""});
        return cl;
    }

    @Override
    protected AbstractRecyclerCursorAdapter getAdapter() {
        return new DragDayAdapter(getContext(), null, this);
    }

    @Override
    protected Uri getUri() {
        return TripProvider.getProviderUri(getContext().getString(R.string.auth_provider_trip), TripProvider.TABLE_DAY);
    }

    @Override
    public void onMoveItem(int fromPos, int toPos) {
        resetSortIdMap(fromPos, toPos);
        mResolver.notifyChange(mUri, null);

        // TODO: 替換day下的stroke day id
    }

    @Override
    public void onItemClick(Cursor cursor) {

    }

    @Override
    protected void onSync() {

    }

    private int getTripId() {
        return getArguments().getInt(ARG_TRIP_ID);
    }
}
