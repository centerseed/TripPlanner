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
        return TripProvider.getProviderUri(getContext().getString(R.string.auth_provider_stock), TripProvider.TABLE_TRIP);
    }

    @Override
    protected void onSync() {

    }

    private void initDummyData() {
        ContentValues values;
        //http://farm5.static.flickr.com/4060/4650494949_2d3185a48f_o.jpg
        // http://qglbbs.b0.upaiyun.com/forum/201407/21/155844wjhvzn76tkqugwpq.jpg
        //http://www.4p.com.tw/eWeb_spunktour/IMGDB/000453/00002613.jpg
        // http://www.gooden.link/image/Document/201602051103298887.jpg
        //http://cn.guidetoiceland.is/image/223042/x/0/most-unique-experiences-in-iceland-7.jpg
        mResolver = getContext().getContentResolver();

        values = new ContentValues();
        values.put(TripProvider.FIELD_ID, "東京".hashCode());
        values.put(TripProvider.FIELD_TRIP_NAME, "東京");
        values.put(TripProvider.FIELD_TRIP_PHOTO, "http://farm5.static.flickr.com/4060/4650494949_2d3185a48f_o.jpg");
        values.put(TripProvider.FIELD_SORT_ID, 0);
        mResolver.insert(mUri, values);

        values = new ContentValues();
        values.put(TripProvider.FIELD_ID, "京都".hashCode());
        values.put(TripProvider.FIELD_TRIP_NAME, "京都");
        values.put(TripProvider.FIELD_TRIP_PHOTO, "http://qglbbs.b0.upaiyun.com/forum/201407/21/155844wjhvzn76tkqugwpq.jpg");
        values.put(TripProvider.FIELD_SORT_ID, 1);
        mResolver.insert(mUri, values);

        values = new ContentValues();
        values.put(TripProvider.FIELD_ID, "北海道".hashCode());
        values.put(TripProvider.FIELD_TRIP_NAME, "北海道");
        values.put(TripProvider.FIELD_TRIP_PHOTO, "http://www.4p.com.tw/eWeb_spunktour/IMGDB/000453/00002613.jpg");
        values.put(TripProvider.FIELD_SORT_ID, 2);
        mResolver.insert(mUri, values);

        values = new ContentValues();
        values.put(TripProvider.FIELD_ID, "巴黎".hashCode());
        values.put(TripProvider.FIELD_TRIP_NAME, "巴黎");
        values.put(TripProvider.FIELD_TRIP_PHOTO, "http://www.gooden.link/image/Document/201602051103298887.jpg");
        values.put(TripProvider.FIELD_SORT_ID, 3);
        mResolver.insert(mUri, values);

        values = new ContentValues();
        values.put(TripProvider.FIELD_ID, "冰島".hashCode());
        values.put(TripProvider.FIELD_TRIP_NAME, "冰島");
        values.put(TripProvider.FIELD_TRIP_PHOTO, "http://image.lifetm.com/ProductPhoto/2011/05/31/jpg/20110531094136948C6EC818B96.jpg");
        values.put(TripProvider.FIELD_SORT_ID, 4);
        mResolver.insert(mUri, values);

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
        intent.putExtra(TripActivity.ARG_TRIP_PHOTO, cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_TRIP_PHOTO)));
        startActivity(intent);
    }
}
