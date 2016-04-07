package com.barry.tripplanner.trip;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.barry.tripplanner.R;
import com.barry.tripplanner.base.AbstractRecyclerCursorAdapter;
import com.barry.tripplanner.base.RecyclerListFragment;
import com.barry.tripplanner.map.MapsActivity;
import com.barry.tripplanner.provider.TripProvider;

public class AttractionFragment extends RecyclerListFragment implements AttractionAdapter.AttractionAdapterListener {

    public static final String ARG_TRIP_DESTINATION = "trip_destination";
    public static final String ARG_ATTRACTION_IDS = "attraction_ids";

    Uri mTripUri;
    String mIdsString;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
                intent.putExtra(MapsActivity.ARG_TRIP_ID, getTripId());
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
            cursor.close();
            reload();
        }
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
                whereclause += TripProvider.FIELD_ID + "=" + ids[i];
            else
                whereclause += " OR " + TripProvider.FIELD_ID + "=" + ids[i];
        }
        cl.setSelection(whereclause);
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
}
