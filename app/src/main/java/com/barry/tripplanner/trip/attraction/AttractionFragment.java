package com.barry.tripplanner.trip.attraction;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.barry.tripplanner.R;
import com.barry.tripplanner.base.AbstractRecyclerCursorAdapter;
import com.barry.tripplanner.base.RecyclerListFragment;
import com.barry.tripplanner.map.MapsActivity;
import com.barry.tripplanner.provider.TripProvider;
import com.barry.tripplanner.sync.SyncTool;
import com.barry.tripplanner.trip.day.DayListFragment;
import com.barry.tripplanner.utils.TripUtils;

public class AttractionFragment extends RecyclerListFragment implements AttractionAdapter.AttractionAdapterListener {

    public static final String ARG_TRIP_DESTINATION = "trip_destination";
    public static final String ARG_ATTRACTION_IDS = "attraction_ids";

    Uri mTripUri;
    String mIdsString;
    String mTripID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_adapter = getAdapter();
        mTripUri = TripProvider.getProviderUri(getString(R.string.auth_provider_trip), TripProvider.TABLE_TRIP);

        return inflater.inflate(R.layout.fragment_tab_recycler_fab, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra(MapsActivity.ARG_TRIP_LOCAL_ID, getTripId());
                intent.putExtra(MapsActivity.ARG_TRIP_ID, mTripID);
                intent.putExtra(MapsActivity.ARG_TRIP_DESTINATION, getDestination());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Cursor cursor = getActivity().getContentResolver().query(mTripUri, null, TripProvider.FIELD_ID + "=?", new String[]{getTripId() + ""}, null);
        if (cursor != null && cursor.moveToFirst()) {
            mIdsString = cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_ATTRACTION_IDS));
            mTripID = cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_TRIP_ID));
            cursor.close();
            reload();
        }

        if (mTripID != null)
            new SyncTool().with(getContext()).syncAttractions(mTripID);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cl = (CursorLoader) super.onCreateLoader(id, args);
        if (mIdsString == null) {
            cl.setSelection(TripProvider.FIELD_ID + "=0");
            return cl;
        }

        String ids[] = mIdsString.split("\\|");
        String whereclause = "";

        for (int i = 0; i < ids.length; i++) {
            if (i == 0)
                whereclause += TripProvider.FIELD_ATTRACTION_ID + "=?";
            else
                whereclause += " OR " + TripProvider.FIELD_ATTRACTION_ID + "=?";
        }
        cl.setSelection(whereclause);
        cl.setSelectionArgs(ids);
        return cl;
    }

    @Override
    public AbstractRecyclerCursorAdapter getAdapter() {
        return new AttractionAdapter(getContext(), null, this);
    }

    @Override
    protected Uri getUri() {
        return TripProvider.getProviderUri(getString(R.string.auth_provider_trip), TripProvider.TABLE_ATTRACTION);
    }

    @Override
    protected void onSync() {

    }

    private int getTripId() {
        return getArguments().getInt(DayListFragment.ARG_TRIP_ID);
    }

    private String getAttractionIDs() {
        return getArguments().getString(ARG_ATTRACTION_IDS);
    }


    private String getDestination() {
        return getArguments().getString(ARG_TRIP_DESTINATION);
    }

    @Override
    public void onAttractionClick(Cursor cursor) {
        Intent intent = new Intent(getActivity(), AttractionActivity.class);
        intent.putExtra(AttractionActivity.ARG_ATTRACTION_ID, cursor.getInt(cursor.getColumnIndex(TripProvider.FIELD_ID)));
        startActivity(intent);
    }

    @Override
    public void onAttractionLongClick(final Cursor cursor) {
        int currentDays = 0;
        Uri dayUri = TripProvider.getProviderUri(getString(R.string.auth_provider_trip), TripProvider.TABLE_DAY);
        Cursor c = getContext().getContentResolver().query(dayUri, null, TripProvider.FIELD_DAY_BELONG_TRIP + "=?", new String[]{getTripId() + ""}, null);
        if (c != null && c.moveToFirst()) {
            currentDays = c.getCount();
            c.close();
        }

        final CharSequence[] items = new CharSequence[currentDays + 1];
        for (int i = 1; i <= currentDays; i++) {
            String day = "第" + i + "天";
            items[i] = day;
        }
        items[0] = "先不選擇日期";

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Make your selection");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                TripUtils.addStroke(getContext(), getTripId(), cursor.getInt(cursor.getColumnIndex(TripProvider.FIELD_ID)), item - 1);
                TripUtils.updateDaySnippet(getContext(), getTripId(), item - 1);
                Uri dayUri = TripProvider.getProviderUri(getContext().getString(R.string.auth_provider_trip), TripProvider.TABLE_DAY);
                getContext().getContentResolver().notifyChange(dayUri, null);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
