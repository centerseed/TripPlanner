package com.barry.tripplanner.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.barry.tripplanner.R;
import com.barry.tripplanner.provider.TripProvider;
import com.barry.tripplanner.trip.contentvalues.TripContent;

public class TripUtils {

    TripListener mTripListener;
    public interface TripListener {
        void onTripEditDone(int tripId, String tripName);
    }

    public static void addTrip(Context context, TripContent tripContent, TripListener tripListener) {
        new CreateTripTask(context).withContent(tripContent.getContentValues()).withListener(tripListener).execute();
    }

    public static void updateTrip(Context context, TripContent tripContent, TripListener tripListener) {
        new CreateTripTask(context).withContent(tripContent.getContentValues()).withListener(tripListener).execute();
    }

    public static int getDayCountInTrip(Context context, int tripId) {
        int currentDays = 0;
        Uri dayUri = TripProvider.getProviderUri(context.getString(R.string.auth_provider_trip), TripProvider.TABLE_DAY);
        Cursor c = context.getContentResolver().query(dayUri, null, TripProvider.FIELD_DAY_BELONG_TRIP + "=?", new String[]{tripId + ""}, null);
        if (c != null && c.moveToFirst()) {
            currentDays = c.getCount();
            c.close();
        }

        return currentDays;
    }

    public static void addStroke(int day) {

    }

    public static void addAttraction() {

    }

    public static class CreateTripTask extends AsyncTask<Void, Void, Void> {
        Context mContext;
        ContentValues mValues;
        TripListener mListener;
        int mId;

        public CreateTripTask(Context context) {
            mContext = context;
        }

        public CreateTripTask withContent(ContentValues values) {
            mValues = values;
            return this;
        }

        public CreateTripTask withListener(TripListener listener) {
            mListener = listener;
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
            }

            mContext.getContentResolver().notifyChange(tripUri, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (mListener != null) mListener.onTripEditDone(mValues.getAsInteger(TripProvider.FIELD_ID), mValues.getAsString(TripProvider.FIELD_TRIP_NAME));
        }
    }

    public class UpdateTripTask extends AsyncTask<Void, Void, Void> {

        Context mContext;
        ContentValues mValues;

        public UpdateTripTask(Context context) {
            mContext = context;
        }

        public UpdateTripTask withContent(ContentValues values) {
            mValues = values;
            return this;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            int days = TimeUtils.daysBetween(mValues.getAsString(TripProvider.FIELD_TRIP_START_DAY), mValues.getAsString(TripProvider.FIELD_TRIP_END_DAY));

            Uri tripUri = TripProvider.getProviderUri(mContext.getString(R.string.auth_provider_trip), TripProvider.TABLE_TRIP);
            Uri dayUri = TripProvider.getProviderUri(mContext.getString(R.string.auth_provider_trip), TripProvider.TABLE_DAY);

            mContext.getContentResolver().update(tripUri, mValues, TripProvider.FIELD_ID + "=?", new String[]{mValues.getAsString(TripProvider.FIELD_ID)});
            mContext.getContentResolver().notifyChange(tripUri, null);

            int currentDays = getDayCountInTrip(mContext, mValues.getAsInteger(TripProvider.FIELD_ID));

            if (currentDays < days) {
                for (int i = currentDays; i < days; i++) {
                    ContentValues dayValue = new ContentValues();
                    dayValue.put(TripProvider.FIELD_ID, (mValues.getAsString(TripProvider.FIELD_ID) + i).hashCode());
                    dayValue.put(TripProvider.FIELD_DAY_BELONG_TRIP, mValues.getAsInteger(TripProvider.FIELD_ID));
                    dayValue.put(TripProvider.FIELD_SORT_ID, i + "");
                    mContext.getContentResolver().insert(dayUri, dayValue);
                }
            } else if (currentDays > days) {
                for (int i = days; i < currentDays; i++) {
                    mContext.getContentResolver().delete(dayUri, TripProvider.FIELD_SORT_ID + "=?", new String[]{i + ""});
                }
            }

            return null;
        }
    }
}