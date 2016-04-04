package com.barry.tripplanner.trip;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.barry.tripplanner.R;
import com.barry.tripplanner.base.AbstractRecyclerCursorAdapter;
import com.barry.tripplanner.base.DragListCallback;
import com.barry.tripplanner.base.DragRecycleListFragment;
import com.barry.tripplanner.provider.TripProvider;
import com.barry.tripplanner.task.CreateTripTask;

public class TripListFragment extends DragRecycleListFragment implements DragListCallback {
    ContentResolver mResolver;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_fab, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CreateTripActivity.class);
                startActivity(i);
            }
        });
        initDummyData();
    }

    @Override
    protected AbstractRecyclerCursorAdapter getAdapter() {
        return new DragTripAdapter(getContext(), null, this);
    }

    @Override
    protected Uri getUri() {
        return TripProvider.getProviderUri(getContext().getString(R.string.auth_provider_trip), TripProvider.TABLE_TRIP);
    }

    @Override
    protected void onSync() {

    }

    private void initDummyData() {
        ContentValues values;
        mResolver = getContext().getContentResolver();

        values = new ContentValues();
        values.put(TripProvider.FIELD_ID, "東京小旅行".hashCode());
        values.put(TripProvider.FIELD_TRIP_DESTINATION, "東京");
        values.put(TripProvider.FIELD_TRIP_NAME, "東京小旅行");
        values.put(TripProvider.FIELD_TRIP_PHOTO, "http://farm5.static.flickr.com/4060/4650494949_2d3185a48f_o.jpg");
        values.put(TripProvider.FIELD_SORT_ID, 0);
        values.put(TripProvider.FIELD_TRIP_START_DAY, "2015-3-10");
        values.put(TripProvider.FIELD_TRIP_END_DAY, "2015-3-16");
        new CreateTripTask(getContext()).withContent(values).execute();

        values = new ContentValues();
        values.put(TripProvider.FIELD_ID, "古都跨年行".hashCode());
        values.put(TripProvider.FIELD_TRIP_NAME, "古都跨年行");
        values.put(TripProvider.FIELD_TRIP_DESTINATION, "京都");
        values.put(TripProvider.FIELD_TRIP_PHOTO, "http://qglbbs.b0.upaiyun.com/forum/201407/21/155844wjhvzn76tkqugwpq.jpg");
        values.put(TripProvider.FIELD_SORT_ID, 1);
        values.put(TripProvider.FIELD_TRIP_START_DAY, "2016-12-29");
        values.put(TripProvider.FIELD_TRIP_END_DAY, "2017-1-2");
        new CreateTripTask(getContext()).withContent(values).execute();

        values = new ContentValues();
        values.put(TripProvider.FIELD_ID, "北海道自然探險".hashCode());
        values.put(TripProvider.FIELD_TRIP_NAME, "北海道自然探險");
        values.put(TripProvider.FIELD_TRIP_DESTINATION, "北海道");
        values.put(TripProvider.FIELD_TRIP_PHOTO, "http://www.4p.com.tw/eWeb_spunktour/IMGDB/000453/00002613.jpg");
        values.put(TripProvider.FIELD_SORT_ID, 2);
        values.put(TripProvider.FIELD_TRIP_START_DAY, "2015-4-10");
        values.put(TripProvider.FIELD_TRIP_END_DAY, "2015-4-21");
        new CreateTripTask(getContext()).withContent(values).execute();
    }

    @Override
    public void onMoveItem(int fromPos, int toPos) {
        resetSortIdMap(fromPos, toPos);
        mResolver.notifyChange(mUri, null);
    }

    @Override
    public void onItemClick(Cursor cursor) {
        Intent intent = new Intent(getActivity(), TripActivity.class);
        ContentValues values = new ContentValues();
        values.put(TripProvider.FIELD_ID, cursor.getInt(cursor.getColumnIndex(TripProvider.FIELD_ID)));
        values.put(TripProvider.FIELD_TRIP_PHOTO, cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_TRIP_PHOTO)));
        values.put(TripProvider.FIELD_TRIP_NAME, cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_TRIP_NAME)));
        values.put(TripProvider.FIELD_TRIP_DESTINATION, cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_TRIP_DESTINATION)));
        values.put(TripProvider.FIELD_TRIP_START_DAY, cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_TRIP_START_DAY)));
        values.put(TripProvider.FIELD_TRIP_END_DAY, cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_TRIP_END_DAY)));

        intent.putExtra(TripActivity.ARG_TRIP_VALUES, values);
        startActivity(intent);
    }
}
