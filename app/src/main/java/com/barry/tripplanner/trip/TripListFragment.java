package com.barry.tripplanner.trip;

import android.content.ContentResolver;
import android.content.ContentValues;
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
import com.barry.tripplanner.trip.contentvalues.TripContent;
import com.barry.tripplanner.utils.TripUtils;

public class TripListFragment extends DragRecycleListFragment implements DragListCallback, TripUtils.TripListener {
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
        mResolver = getContext().getContentResolver();

        TripContent tripContent = new TripContent();
        tripContent.getContentValues().put(TripProvider.FIELD_ID, "東京小旅行".hashCode());
        tripContent.getContentValues().put(TripProvider.FIELD_TRIP_DESTINATION, "東京");
        tripContent.getContentValues().put(TripProvider.FIELD_TRIP_NAME, "東京小旅行");
        tripContent.getContentValues().put(TripProvider.FIELD_TRIP_PHOTO, "http://farm5.static.flickr.com/4060/4650494949_2d3185a48f_o.jpg");
        tripContent.getContentValues().put(TripProvider.FIELD_SORT_ID, 0);
        tripContent.getContentValues().put(TripProvider.FIELD_TRIP_START_DAY, "2015-3-10");
        tripContent.getContentValues().put(TripProvider.FIELD_TRIP_END_DAY, "2015-3-16");
        TripUtils.addTrip(getContext(), tripContent, this);

        tripContent = new TripContent();
        tripContent.getContentValues().put(TripProvider.FIELD_ID, "古都跨年行".hashCode());
        tripContent.getContentValues().put(TripProvider.FIELD_TRIP_NAME, "古都跨年行");
        tripContent.getContentValues().put(TripProvider.FIELD_TRIP_DESTINATION, "京都");
        tripContent.getContentValues().put(TripProvider.FIELD_TRIP_PHOTO, "http://qglbbs.b0.upaiyun.com/forum/201407/21/155844wjhvzn76tkqugwpq.jpg");
        tripContent.getContentValues().put(TripProvider.FIELD_SORT_ID, 1);
        tripContent.getContentValues().put(TripProvider.FIELD_TRIP_START_DAY, "2016-12-29");
        tripContent.getContentValues().put(TripProvider.FIELD_TRIP_END_DAY, "2017-1-2");
        TripUtils.addTrip(getContext(), tripContent, this);

        tripContent = new TripContent();
        tripContent.getContentValues().put(TripProvider.FIELD_ID, "北海道自然探險".hashCode());
        tripContent.getContentValues().put(TripProvider.FIELD_TRIP_NAME, "北海道自然探險");
        tripContent.getContentValues().put(TripProvider.FIELD_TRIP_DESTINATION, "北海道");
        tripContent.getContentValues().put(TripProvider.FIELD_TRIP_PHOTO, "http://www.4p.com.tw/eWeb_spunktour/IMGDB/000453/00002613.jpg");
        tripContent.getContentValues().put(TripProvider.FIELD_SORT_ID, 2);
        tripContent.getContentValues().put(TripProvider.FIELD_TRIP_START_DAY, "2015-4-10");
        tripContent.getContentValues().put(TripProvider.FIELD_TRIP_END_DAY, "2015-4-21");
        TripUtils.addTrip(getContext(), tripContent, this);
    }

    @Override
    public void onMoveItem(int fromPos, int toPos) {
        resetSortIdMap(fromPos, toPos);
        mResolver.notifyChange(mUri, null);
    }

    @Override
    public void onItemClick(Cursor cursor) {
        Intent intent = new Intent(getActivity(), TripActivity.class);
        TripContent tripContent = new TripContent();
        tripContent.withCursor(cursor);

        intent.putExtra(TripActivity.ARG_TRIP_VALUES, tripContent);
        startActivity(intent);
    }

    @Override
    public void onTripEditDone(int tripId, String tripName) {

    }
}
