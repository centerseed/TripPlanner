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
    private final OkHttpClient mClient = new OkHttpClient();

    public TripSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mTripUri = TripProvider.getProviderUri(context, TripProvider.TABLE_TRIP);
    }

    @Override
    void pushLocalData() throws IOException, BaseResponseParser.AuthFailException {
        Cursor c = mContentResolver.query(mTripUri, null, TripProvider.FIELD_SYNC + "!=?", new String[]{TripProvider.SYNC_DONE}, null);
        if (c != null && c.moveToFirst()) {
            while (!c.isAfterLast()) {
                String sync = c.getString(c.getColumnIndex(TripProvider.FIELD_SYNC));
                if (TripProvider.SYNC_CREATE_TRIP.equals(sync)) {
                    createTrip(c, mUserID);
                }

                if (TripProvider.SYNC_UPDATE_TRIP.equals(sync)) {
                    String tripID = c.getString(c.getColumnIndex(TripProvider.FIELD_TRIP_ID));
                }

                if (TripProvider.SYNC_DELETE_TRIP.equals(sync)) {
                    String tripID = c.getString(c.getColumnIndex(TripProvider.FIELD_TRIP_ID));
                }
                c.moveToNext();
            }
            c.close();
        }
    }

    @Override
    void performSync(Bundle extras) throws IOException, BaseResponseParser.AuthFailException {
        if (TextUtils.isEmpty(mUserID)) return;


        if (TripProvider.SYNC_ALL_TRIP.equals(extras.getString(ACTION_SYNC))) {
            syncAllTrip(mUserID);
        }

        if (TripProvider.SYNC_TRIP.equals(extras.getString(ACTION_SYNC))) {
            String tripID = extras.getString(ARG_TRIP_ID);

            Cursor c = mContentResolver.query(mTripUri, null, TripProvider.FIELD_ID + "=?", new String[]{tripID}, null);
            if (c != null && c.moveToFirst()) {
                String sync = c.getString(c.getColumnIndex(TripProvider.FIELD_SYNC));
                syncTrip(mUserID, tripID, sync);
                c.close();
            }
        }

        if (TripProvider.SYNC_SYNC_ATTRACTIONS.equals(extras.getString(ACTION_SYNC))) {
            String tripID = extras.getString(ARG_TRIP_ID);
            pullAttractions(mUserID, tripID);
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
        if (TripProvider.SYNC_CREATE_TRIP.equals(sync)) {

        }

        if (TripProvider.SYNC_UPDATE_TRIP.equals(sync)) {

        }

        if (TripProvider.SYNC_DELETE_TRIP.equals(sync)) {

        }

        pullTrip(userID, tripID);
    }

    private void pullTrip(String userID, String tripID) throws IOException, BaseResponseParser.AuthFailException {
        String url = new URLBuilder(mContext).host(mHost).path("trip", userID, tripID).build().toString();
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
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        new TripParser(mContext).parse(mClient.newCall(request).execute());

        mContext.getContentResolver().delete(mTripUri, TripProvider.FIELD_ID + "=?", new String[]{id + ""});


        // delete local data
        // notify data change
    }

    private void modifyTrip() {

    }

    private void deleteTrip() {

    }

    private void pullAttractions(String userID, String tripID) {

    }
}
