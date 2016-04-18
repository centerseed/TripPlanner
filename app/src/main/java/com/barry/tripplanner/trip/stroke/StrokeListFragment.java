package com.barry.tripplanner.trip.stroke;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.barry.tripplanner.R;
import com.barry.tripplanner.base.AbstractRecyclerCursorAdapter;
import com.barry.tripplanner.base.DragRecycleListFragment;
import com.barry.tripplanner.provider.TripProvider;
import com.barry.tripplanner.utils.StrokeUtils;
import com.barry.tripplanner.utils.TripUtils;

public class StrokeListFragment extends DragRecycleListFragment implements StrokeAdapter.StrokeListCallback {

    public static final String ARG_TRIP_ID = "trip_id";
    public static final String ARG_DAY = "day";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_fab, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddStrokeActivity.class);
                intent.putExtra(ARG_TRIP_ID, getTripId());
                intent.putExtra(ARG_DAY, getDay());
                startActivity(intent);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cl = (CursorLoader) super.onCreateLoader(id, args);
        cl.setUri(mUri);
        cl.setSelection(TripProvider.FIELD_STROKE_BELONG_TRIP + "=? AND " + TripProvider.FIELD_STROKE_BELONG_DAY + "=?");
        cl.setSelectionArgs(new String[]{getTripId() + "", getDay() + ""});
        return cl;
    }

    @Override
    protected AbstractRecyclerCursorAdapter getAdapter() {
        return new StrokeAdapter(getContext(), null, this);
    }

    @Override
    protected Uri getUri() {
        return TripProvider.getProviderUri(getContext(), TripProvider.TABLE_STROKE);
    }

    @Override
    public void onMoveItem(int fromPos, int toPos) {
        resetSortIdMap(fromPos, toPos);
        mResolver.notifyChange(mUri, null);

        TripUtils.updateDaySnippet(getContext(), getTripId(), getDay());
    }

    @Override
    public void onItemClick(Cursor cursor) {

    }

    @Override
    protected void onSync() {

    }

    private int getTripId() {
        return getArguments().getInt(ARG_TRIP_ID);
    }

    private int getDay() {
        return getArguments().getInt(ARG_DAY);
    }

    @Override
    public void onEditTime(final Cursor cursor, String time) {
        final CharSequence[] items = new CharSequence[] {"15 min", "30 mim", "1 hour", "2 hour", "4 hour", "8 hour", "12 hour"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getContext().getResources().getString(R.string.title_stroke_time));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                int id = cursor.getInt(cursor.getColumnIndex(TripProvider.FIELD_ID));
                StrokeUtils.updateStrokeTime(getContext(), id, items[item].toString());
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onLongClick(final Cursor cursor) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.title_is_delete_stroke)
                .setMessage(R.string.title_is_delete_stroke_detail)
                .setPositiveButton(R.string.title_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Toast.makeText(getContext(), R.string.gogo, Toast.LENGTH_SHORT).show();
                        TripUtils.deleteStroke(getContext(), cursor.getInt(cursor.getColumnIndex(TripProvider.FIELD_ID)));
                       // mResolver.notifyChange(mUri, null);
                    }
                })
                .setNegativeButton(R.string.title_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }
}
