package com.barry.tripplanner.trip.day;

import android.content.ContentResolver;
import android.content.Intent;
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
import com.barry.tripplanner.trip.stroke.StrokeListFragment;

public class DayListFragment extends DragRecycleListFragment implements DragListCallback {

    public static final String ARG_TRIP_ID = "trip_id";
    int mTripId = 0;

    @Override
    public void onResume() {
        mTripId = getTripId();
        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cl = (CursorLoader) super.onCreateLoader(id, args);
        cl.setSelection(TripProvider.FIELD_DAY_BELONG_TRIP + "=?");
        cl.setSelectionArgs(new String[]{mTripId + ""});
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
        Intent intent = new Intent(getActivity(), DayActivity.class);
        intent.putExtra(StrokeListFragment.ARG_TRIP_ID, mTripId);
        intent.putExtra(StrokeListFragment.ARG_DAY, cursor.getInt(cursor.getColumnIndex(TripProvider.FIELD_SORT_ID)) + 1);
        startActivity(intent);
    }

    @Override
    protected void onSync() {

    }

    private int getTripId() {
        return getArguments().getInt(ARG_TRIP_ID);
    }
}
