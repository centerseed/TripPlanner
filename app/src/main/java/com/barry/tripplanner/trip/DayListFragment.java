package com.barry.tripplanner.trip;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.barry.tripplanner.R;
import com.barry.tripplanner.base.AbstractRecyclerCursorAdapter;
import com.barry.tripplanner.base.DragListCallback;
import com.barry.tripplanner.base.DragRecycleListFragment;
import com.barry.tripplanner.provider.TripProvider;

public class DayListFragment extends DragRecycleListFragment implements DragListCallback {

    ContentResolver mResolver;

    @Override
    public void onResume() {
        super.onResume();
        initDummyData();
    }

    @Override
    protected AbstractRecyclerCursorAdapter getAdapter() {
        return new DragDayAdapter(getContext(), null, this);
    }

    @Override
    protected Uri getUri() {
        return TripProvider.getProviderUri(getContext().getString(R.string.auth_provider_stock), TripProvider.TABLE_DAY);
    }

    @Override
    public void onMoveItem(int fromPos, int toPos) {
        resetSortIdMap(fromPos, toPos);
        mResolver.notifyChange(mUri, null);
    }

    @Override
    public void onItemClick(Cursor cursor) {

    }

    @Override
    protected void onSync() {

    }

    private void initDummyData() {
        mResolver = getContext().getContentResolver();
        for (int i = 0; i < 5; i++) {
            ContentValues values = new ContentValues();
            values.put(TripProvider.FIELD_ID, ("第" + i + "天的行程").hashCode());
            values.put(TripProvider.FIELD_DAY_BELONG_TRIP, getTripId());
            values.put(TripProvider.FIELD_SORT_ID, i + "");
            values.put(TripProvider.FIELD_DAY_HIGHLIGHT, "第" + i + "天的行程");

            mResolver.insert(mUri, values);
        }
        mResolver.notifyChange(mUri, null);
    }

    private int getTripId() {
        return getArguments().getInt(TripActivity.ARG_TRIP_ID);
    }
}
