package com.barry.tripplanner.trip;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
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
import com.barry.tripplanner.sync.SyncTool;
import com.barry.tripplanner.sync.TripSyncAdapter;
import com.barry.tripplanner.utils.AccountUtils;
import com.barry.tripplanner.utils.TripUtils;

public class TripListFragment extends DragRecycleListFragment implements DragListCallback, TripUtils.TripListener {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_fab_padding, container, false);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        registerSyncStatusListener(R.string.auth_provider_trip);
        onSync();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterSyncStatusListener();
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
        new SyncTool().with(getContext()).syncAllTrip();
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

        intent.putExtra(TripActivity.ARG_TRIP_ID, tripContent.getLocalId());
        startActivity(intent);
    }

    @Override
    public void onTripEditDone(String tripId, String tripName) {

    }
}
