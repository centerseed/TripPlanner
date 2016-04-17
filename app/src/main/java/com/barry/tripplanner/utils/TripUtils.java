package com.barry.tripplanner.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.barry.tripplanner.R;
import com.barry.tripplanner.provider.TripProvider;
import com.barry.tripplanner.trip.attraction.AttractionContent;
import com.barry.tripplanner.trip.stroke.StrokeContent;
import com.barry.tripplanner.trip.TripContent;

public class TripUtils {

    TripListener mTripListener;
    public interface TripListener {
        void onTripEditDone(int tripId, String tripName);
    }

    public static void addTrip(Context context, TripContent tripContent, TripListener tripListener) {
        new CreateTripTask(context).withContent(tripContent.getContentValues()).withListener(tripListener).execute();
    }

    public static void updateTrip(Context context, TripContent tripContent, TripListener tripListener) {
        new UpdateTripTask(context).withContent(tripContent.getContentValues()).withListener(tripListener).execute();
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

    public static void addStrokeWithAttraction(Context context, int tripId, int day, AttractionContent attraction) {
        addStroke(context, tripId, attraction.getContentValues().getAsInteger(TripProvider.FIELD_ID), day);
        addAttraction(context, tripId, attraction);

        updateDaySnippet(context, tripId, day);
    }

    public static void updateDaySnippet(Context context, int tripId, int day) {
        Uri strokeUri = TripProvider.getProviderUri(context.getString(R.string.auth_provider_trip), TripProvider.TABLE_STROKE);
        Cursor c = context.getContentResolver().query(strokeUri, null,
                TripProvider.FIELD_STROKE_BELONG_TRIP + "=? AND " + TripProvider.FIELD_STROKE_BELONG_DAY + "=?",
                new String[]{tripId + "", day + ""},
                TripProvider.FIELD_SORT_ID + " ASC");
        if (c != null && c.moveToFirst()) {
            String snippet = "";
            while (!c.isAfterLast()) {
                StrokeContent stroke = new StrokeContent(context);
                stroke.withCursor(c);
                snippet += stroke.getAtraction().getName() + " ";
                c.moveToNext();
            }
            c.close();

            ContentValues values = new ContentValues();
            values.put(TripProvider.FIELD_DAY_HIGHLIGHT, snippet);
            Uri dayUri = TripProvider.getProviderUri(context.getString(R.string.auth_provider_trip), TripProvider.TABLE_DAY);
            context.getContentResolver().update(dayUri, values, TripProvider.FIELD_DAY_BELONG_TRIP + "=? AND " +
            TripProvider.FIELD_SORT_ID + "=?", new String[]{tripId + "", day + ""});
        }
    }

    public static void addStroke(Context context, int tripId, int attrID, int day) {
        Uri strokeUri = TripProvider.getProviderUri(context.getString(R.string.auth_provider_trip), TripProvider.TABLE_STROKE);
        int sortIDinDay = 0;
        Cursor c = context.getContentResolver().query(strokeUri, null,
                TripProvider.FIELD_STROKE_BELONG_TRIP + "=? AND " + TripProvider.FIELD_STROKE_BELONG_DAY + "=?",
                new String[]{tripId + "", day + ""},
                null);
        if (c != null) {
            sortIDinDay = c.getCount();
            c.close();
        }

        StrokeContent strokeContent = new StrokeContent(context);
        strokeContent.getContentValues().put(TripProvider.FIELD_ID, (tripId + System.currentTimeMillis() + "").hashCode());
        strokeContent.getContentValues().put(TripProvider.FIELD_STROKE_BELONG_TRIP, tripId);
        strokeContent.getContentValues().put(TripProvider.FIELD_STROKE_BELONG_DAY, day);
        strokeContent.getContentValues().put(TripProvider.FIELD_STROKE_ATTRACTION_ID, attrID);
        strokeContent.getContentValues().put(TripProvider.FIELD_SORT_ID, sortIDinDay);

        context.getContentResolver().insert(strokeUri, strokeContent.getContentValues());
    }

    public static void deleteStroke(Context context, int strokeId) {
        Uri strokeUri = TripProvider.getProviderUri(context, TripProvider.TABLE_STROKE);
        context.getContentResolver().delete(strokeUri, TripProvider.FIELD_ID + "=?", new String[]{strokeId + ""});
    }

    public static void addAttraction(Context context, int tripId, AttractionContent attraction) {
        Uri tripUri = TripProvider.getProviderUri(context.getString(R.string.auth_provider_trip), TripProvider.TABLE_TRIP);
        Uri attractionUri = TripProvider.getProviderUri(context.getString(R.string.auth_provider_trip), TripProvider.TABLE_ATTRACTION);

        context.getContentResolver().insert(attractionUri, attraction.getContentValues());

        Cursor c = context.getContentResolver().query(tripUri, null, TripProvider.FIELD_ID + "=?", new String[]{tripId + ""}, null);
        if (c != null && c.moveToFirst()) {
            String attractionIDs = c.getString(c.getColumnIndex(TripProvider.FIELD_ATTRACTION_IDS));
            attractionIDs += "|" + attraction.getContentValues().getAsString(TripProvider.FIELD_ID);
            attractionIDs = attractionIDs.replace("null|", "");

            TripContent tripContent = new TripContent();
            tripContent.withCursor(c);
            tripContent.getContentValues().put(TripProvider.FIELD_ATTRACTION_IDS, attractionIDs);

            updateTrip(context, tripContent, null);
        }
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
                mContext.getContentResolver().insert(dayUri, dayValue);
            }

            mContext.getContentResolver().notifyChange(tripUri, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (mListener != null) mListener.onTripEditDone(mValues.getAsInteger(TripProvider.FIELD_ID), mValues.getAsString(TripProvider.FIELD_TRIP_NAME));
        }
    }

    public static class UpdateTripTask extends AsyncTask<Void, Void, Void> {

        Context mContext;
        ContentValues mValues;
        TripListener mListener;

        public UpdateTripTask(Context context) {
            mContext = context;
        }

        public UpdateTripTask withContent(ContentValues values) {
            mValues = values;
            return this;
        }

        public UpdateTripTask withListener(TripListener listener) {
            mListener = listener;
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

        @Override
        protected void onPostExecute(Void v) {
            if (mListener != null) mListener.onTripEditDone(mValues.getAsInteger(TripProvider.FIELD_ID), mValues.getAsString(TripProvider.FIELD_TRIP_NAME));
        }
    }
}