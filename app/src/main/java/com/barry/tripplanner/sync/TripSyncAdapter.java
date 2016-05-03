package com.barry.tripplanner.sync;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.barry.tripplanner.base.BaseResponseParser;
import com.barry.tripplanner.provider.TripProvider;
import com.barry.tripplanner.utils.JSONBuilder;
import com.barry.tripplanner.utils.URLBuilder;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class TripSyncAdapter extends BaseSyncAdapter {
    public static final String TAG = "TripSyncAdapter";
    Uri mTripUri;
    Uri mDayUri;
    Uri mStrokeUri;
    Uri mAttractionUri;
    private final OkHttpClient mClient = new OkHttpClient();

    public TripSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mTripUri = TripProvider.getProviderUri(context, TripProvider.TABLE_TRIP);
        mDayUri = TripProvider.getProviderUri(context, TripProvider.TABLE_DAY);
        mStrokeUri = TripProvider.getProviderUri(context, TripProvider.TABLE_STROKE);
        mAttractionUri = TripProvider.getProviderUri(context, TripProvider.TABLE_ATTRACTION);
    }

    @Override
    void pushLocalData(Bundle extras) throws IOException, BaseResponseParser.AuthFailException {
        pushTripData();
        pushAttractionData(extras);
    }

    @Override
    void performSync(Bundle extras) throws IOException, BaseResponseParser.AuthFailException {
        if (TextUtils.isEmpty(mUserID)) return;


        if (TripProvider.SYNC_ALL_TRIP.equals(extras.getString(ACTION_SYNC))) {
            syncAllTrip(mUserID);
        }

        if (TripProvider.SYNC_TRIP.equals(extras.getString(ACTION_SYNC))) {

        }

        if (TripProvider.SYNC_SYNC_ATTRACTIONS.equals(extras.getString(ACTION_SYNC))) {
            String tripID = extras.getString(ARG_TRIP_ID);
            pullAttractions(mUserID, tripID);
            pullTrip(mUserID, tripID);
        }

        mContentResolver.notifyChange(mTripUri, null);
    }

    private void pushTripData() throws IOException, BaseResponseParser.AuthFailException {
        Cursor c = mContentResolver.query(mTripUri, null, TripProvider.FIELD_SYNC + "!=?", new String[]{TripProvider.SYNC_DONE}, null);
        if (c != null && c.moveToFirst()) {
            while (!c.isAfterLast()) {
                String sync = c.getString(c.getColumnIndex(TripProvider.FIELD_SYNC));
                if (TripProvider.SYNC_CREATE_TRIP.equals(sync)) {
                    createTrip(c, mUserID);
                }

                if (TripProvider.SYNC_UPDATE_TRIP.equals(sync)) {
                    updateTrip(c, mUserID);
                }

                if (TripProvider.SYNC_DELETE_TRIP.equals(sync)) {
                    deleteTrip(c, mUserID);
                }
                c.moveToNext();
            }
            c.close();
        }
    }

    private void pushAttractionData(Bundle extra) throws IOException, BaseResponseParser.AuthFailException {
        String tripId = extra.getString(TripSyncAdapter.ARG_TRIP_ID);
        Cursor c = mContentResolver.query(mAttractionUri, null, TripProvider.FIELD_SYNC + "!=?", new String[]{TripProvider.SYNC_DONE}, null);
        if (c != null && c.moveToFirst()) {
            while (!c.isAfterLast()) {
                String sync = c.getString(c.getColumnIndex(TripProvider.FIELD_SYNC));
                if (TripProvider.SYNC_CREATE_ATTRACTIONS.equals(sync)) {
                    createAttraction(c, mUserID, tripId);
                }

                if (TripProvider.SYNC_UPDATE_ATTRACTIONS.equals(sync)) {
                }

                if (TripProvider.SYNC_DELETE_ATTRACTIONS.equals(sync)) {
                }
                c.moveToNext();
            }
            c.close();
        }
    }

    /*

     */
    private void syncAllTrip(String userID) throws IOException, BaseResponseParser.AuthFailException {
        Log.d(TAG, "sync all trip");

        String url = new URLBuilder(mContext).host(mHost).path("trip", userID).build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();

        new TripListParser(mContext).parse(mClient.newCall(request).execute());
    }

    private void syncTrip(String userID, String tripID, String sync) throws IOException, BaseResponseParser.AuthFailException {
        pullTrip(userID, tripID);
    }

    private void pullTrip(String userID, String tripID) throws IOException, BaseResponseParser.AuthFailException {
        String url = new URLBuilder(mContext).host(mHost).path("trip", userID, tripID).build().toString();
        Log.d(TAG, "pullTrip --> " + url);
        Request request = new Request.Builder()
                .url(url)
                .build();

        new TripParser(mContext).parse(mClient.newCall(request).execute());
    }

    private void createTrip(Cursor c, String userId) throws IOException, BaseResponseParser.AuthFailException {
        int id = c.getInt(c.getColumnIndex(TripProvider.FIELD_ID));
        JSONBuilder builder = new JSONBuilder();
        RequestBody body = builder.createTripJSON(mContext, c).build();

        String url = new URLBuilder(mContext).host(mHost).path("trip", userId).build().toString();
        Log.d(TAG, "createTrip --> " + url);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        new TripParser(mContext).parse(mClient.newCall(request).execute());

        mContext.getContentResolver().delete(mTripUri, TripProvider.FIELD_ID + "=?", new String[]{id + ""});
        mContext.getContentResolver().delete(mDayUri, TripProvider.FIELD_DAY_BELONG_TRIP + "=?", new String[]{id + ""});
        mContext.getContentResolver().delete(mStrokeUri, TripProvider.FIELD_STROKE_BELONG_TRIP + "=?", new String[]{id + ""});

        mContext.getContentResolver().notifyChange(mTripUri, null);
    }

    private void updateTrip(Cursor c, String userId) throws IOException, BaseResponseParser.AuthFailException {
        String tripId = c.getString(c.getColumnIndex(TripProvider.FIELD_TRIP_ID));
        JSONBuilder builder = new JSONBuilder();
        RequestBody body = builder.createTripJSON(mContext, c).build();

        String url = new URLBuilder(mContext).host(mHost).path("trip", userId, tripId).build().toString();
        Log.d(TAG, "updateTrip --> " + url);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        new TripParser(mContext).parse(mClient.newCall(request).execute());
        mContext.getContentResolver().notifyChange(mTripUri, null);
    }

    private void deleteTrip(Cursor c, String userId) throws IOException, BaseResponseParser.AuthFailException {
        String tripId = c.getString(c.getColumnIndex(TripProvider.FIELD_TRIP_ID));

        String url = new URLBuilder(mContext).host(mHost).path("trip", userId, tripId).build().toString();
        Log.d(TAG, "deleteTrip --> " + url);

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();
        mClient.newCall(request).execute();

        mContext.getContentResolver().delete(mTripUri, TripProvider.FIELD_TRIP_ID + "=?", new String[]{tripId + ""});
    }

    private void pullAttractions(String userId, String tripId) throws IOException, BaseResponseParser.AuthFailException {
        String url = new URLBuilder(mContext).host(mHost).path("attraction", userId, tripId).build().toString();
        Log.d(TAG, "pullAttractions --> " + url);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        new AttractionListParser(mContext).parse(mClient.newCall(request).execute());
        mContext.getContentResolver().notifyChange(mAttractionUri, null);
    }

    private void createAttraction(Cursor c, String userId, String tripId) throws IOException {
        String url = new URLBuilder(mContext).host(mHost).path("attraction", userId, tripId).build().toString();
        Log.d(TAG, "createAttraction --> " + url);

        JSONBuilder builder = new JSONBuilder();
        RequestBody body = builder.createAttractionJSON(mContext, c).build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        mClient.newCall(request).execute();
    }
}
