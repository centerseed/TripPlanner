package com.barry.tripplanner.trip.attraction;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.barry.tripplanner.R;
import com.barry.tripplanner.base.AbstractRecyclerCursorAdapter;
import com.barry.tripplanner.base.RecyclerListFragment;
import com.barry.tripplanner.provider.TripProvider;
import com.barry.tripplanner.trip.stroke.StrokeListFragment;

public class AttractionChooseFragment extends RecyclerListFragment {

    String mIdsString;
    Uri mTripUri;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        m_adapter = getAdapter();
        mTripUri = TripProvider.getProviderUri(getString(R.string.auth_provider_trip), TripProvider.TABLE_TRIP);

        return super.onCreateView(inflater, container, savedInstanceState);
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
    public AbstractRecyclerCursorAdapter getAdapter() {
        return new AttractionChooseAdapter(getContext(), null);
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
    protected Uri getUri() {
        return TripProvider.getProviderUri(getContext(), TripProvider.TABLE_ATTRACTION);
    }

    @Override
    protected void onSync() {

    }

    private int getTripId() {
        return getArguments().getInt(StrokeListFragment.ARG_TRIP_ID);
    }
}
