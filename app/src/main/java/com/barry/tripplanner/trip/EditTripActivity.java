package com.barry.tripplanner.trip;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.MenuItem;
import android.view.View;

import com.barry.tripplanner.R;
import com.barry.tripplanner.provider.TripProvider;
import com.barry.tripplanner.task.UpdateTripTask;

public class EditTripActivity extends CreateTripActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int REQUEST_EDIT_TRIP = 2000;
    public static final int RESULT_EDIT_SUCCESS = 200;
    public static final String ARG_TRIP_ID = "trip_id";
    public static final String ARG_TRIP_RESULT = "trip_result";

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDestination.setOnClickListener(null);
        mChoosePhotoLayout.setVisibility(View.GONE);
        mDestination.setOnClickListener(null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getSupportLoaderManager().initLoader(0, null, this);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;

        }
        if (item.getItemId() == R.id.action_done) {
            // TODO: update trip
            mTripValue.put(TripProvider.FIELD_TRIP_NAME, mName.getText().toString());
            mTripValue.put(TripProvider.FIELD_TRIP_DESTINATION, mDestination.getText().toString());
            mTripValue.put(TripProvider.FIELD_TRIP_START_DAY, mStartTime.getText().toString());
            mTripValue.put(TripProvider.FIELD_TRIP_END_DAY, mEndTime.getText().toString());
            new UpdateTripTask(this).withContent(mTripValue).execute();

            mName.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent result = new Intent();
                    result.putExtra(ARG_TRIP_RESULT, mTripValue);
                    setResult(RESULT_EDIT_SUCCESS, result);
                    finish();
                }
            }, 500);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cl = new CursorLoader(this);
        cl.setUri(TripProvider.getProviderUri(getString(R.string.auth_provider_trip), TripProvider.TABLE_TRIP));
        cl.setSelection(TripProvider.FIELD_ID + "=?");
        cl.setSelectionArgs(new String[]{getTripId() + ""});
        return cl;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            mTripValue = new ContentValues();
            mTripValue.put(TripProvider.FIELD_ID, cursor.getInt(cursor.getColumnIndex(TripProvider.FIELD_ID)));
            mTripValue.put(TripProvider.FIELD_TRIP_PHOTO, cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_TRIP_PHOTO)));
            mTripValue.put(TripProvider.FIELD_TRIP_NAME, cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_TRIP_NAME)));
            mTripValue.put(TripProvider.FIELD_TRIP_DESTINATION, cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_TRIP_DESTINATION)));
            mTripValue.put(TripProvider.FIELD_TRIP_START_DAY, cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_TRIP_START_DAY)));
            mTripValue.put(TripProvider.FIELD_TRIP_END_DAY, cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_TRIP_END_DAY)));

            mDestination.setText(cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_TRIP_DESTINATION)));
            mName.setText(cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_TRIP_NAME)));
            mStartTime.setText(cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_TRIP_START_DAY)));
            mEndTime.setText(cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_TRIP_END_DAY)));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private int getTripId() {
        return getIntent().getIntExtra(ARG_TRIP_ID, 0);
    }
}
