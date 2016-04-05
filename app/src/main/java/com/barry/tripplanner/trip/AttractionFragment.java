package com.barry.tripplanner.trip;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.barry.tripplanner.R;
import com.barry.tripplanner.base.RecyclerListFragment;
import com.barry.tripplanner.map.MapsActivity;
import com.barry.tripplanner.provider.TripProvider;

public class AttractionFragment extends RecyclerListFragment {

    public static final String ARG_TRIP_DESTINATION = "trip_destination";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
    protected Uri getUri() {
        return TripProvider.getProviderUri(getString(R.string.auth_provider_trip), TripProvider.TABLE_ATTRACTION);
    }

    @Override
    protected void onSync() {

    }

    private int getTripId() {
        return getArguments().getInt(DayListFragment.ARG_TRIP_ID);
    }
    private String getDestination() {
        return getArguments().getString(ARG_TRIP_DESTINATION);
    }
}
