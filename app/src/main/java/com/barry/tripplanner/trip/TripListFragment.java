package com.barry.tripplanner.trip;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.barry.tripplanner.R;
import com.barry.tripplanner.SortPair;
import com.barry.tripplanner.base.AbstractRecyclerCursorAdapter;
import com.barry.tripplanner.base.DragListCallback;
import com.barry.tripplanner.base.DragRecycleListFragment;
import com.barry.tripplanner.provider.TripProvider;

public class TripListFragment extends DragRecycleListFragment implements DragListCallback {
    ContentResolver mResolver;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_fab, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDummyData();
    }

    @Override
    protected AbstractRecyclerCursorAdapter getAdapter() {
        return new DragTripAdapter(getContext(), null, this);
    }

    @Override
    protected Uri getUri() {
        return TripProvider.getProviderUri(getContext().getString(R.string.auth_provider_stock), TripProvider.TABLE_TRIP);
    }

    @Override
    protected void onSync() {

    }

    private void initDummyData() {
        ContentValues values;
        mResolver = getContext().getContentResolver();

        for (int i = 0; i < 5; i++) {
            values = new ContentValues();
            values.put(TripProvider.FIELD_ID, i);
            values.put(TripProvider.FIELD_TRIP_NAME, "My Trip " + i);
            values.put(TripProvider.FIELD_SORT_ID, i);

            mResolver.insert(mUri, values);
        }
        mResolver.notifyChange(mUri, null);
    }

    @Override
    public void onMoveItem(int fromPos, int toPos) {
        resetSortIdMap(fromPos, toPos);
        mResolver.notifyChange(mUri, null);
    }

    @Override
    public void onItemClick(Cursor cursor) {
        Intent intent = new Intent(getActivity(), TripActivity.class);
        intent.putExtra(TripActivity.ARG_TRIP_ID, cursor.getInt(cursor.getColumnIndex(TripProvider.FIELD_ID)));
        intent.putExtra(TripActivity.ARG_TRIP_NAME, cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_TRIP_NAME)));
        startActivity(intent);
    }
}
