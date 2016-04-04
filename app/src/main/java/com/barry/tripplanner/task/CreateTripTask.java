package com.barry.tripplanner.task;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.barry.tripplanner.R;
import com.barry.tripplanner.provider.TripProvider;
import com.barry.tripplanner.utils.TimeUtils;

public class CreateTripTask extends AsyncTask<Void, Void, Void> {
    Context mContext;
    ContentValues mValues;
    public CreateTripTask(Context context) {
        mContext = context;
    }

    public CreateTripTask withContent(ContentValues values) {
        mValues = values;
        return this;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        int days = TimeUtils.daysBetween(mValues.getAsString(TripProvider.FIELD_TRIP_START_DAY), mValues.getAsString(TripProvider.FIELD_TRIP_END_DAY));

        Uri tripUri = TripProvider.getProviderUri(mContext.getString(R.string.auth_provider_trip), TripProvider.TABLE_TRIP);
        Uri dayUri = TripProvider.getProviderUri(mContext.getString(R.string.auth_provider_trip), TripProvider.TABLE_DAY);

        mContext.getContentResolver().insert(tripUri, mValues);

        for (int i = 0; i < days; i++) {
            ContentValues dayValue = new ContentValues();
            dayValue.put(TripProvider.FIELD_ID, (mValues.getAsString(TripProvider.FIELD_ID) + i).hashCode());
            dayValue.put(TripProvider.FIELD_DAY_BELONG_TRIP, mValues.getAsInteger(TripProvider.FIELD_ID));
            dayValue.put(TripProvider.FIELD_SORT_ID, i + "");
            mContext.getContentResolver().insert(dayUri, dayValue);
        }

        mContext.getContentResolver().notifyChange(tripUri, null);
        return null;
    }
}