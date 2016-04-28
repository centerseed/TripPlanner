package com.barry.tripplanner.sync;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.barry.tripplanner.base.BaseResponseParser;
import com.barry.tripplanner.provider.TripProvider;
import com.barry.tripplanner.trip.TripContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TripParser extends BaseResponseParser {
    public static final String TAG = "TripParser";

    public TripParser(Context c) {
        super(c);
    }

    @Override
    protected void parse(JSONObject object) throws JSONException {
        Log.d(TAG, object.toString());

        parseTrip(object);

        // TODO: update URI
    }

    /*
    "_id": "57205d0319fae4641ed9fb4d",
      "tripName": "北海道",
      "destination": "北海道",
      "avatar": "********",
      "startDate": "2016-11-22",
      "endDate": "2016-11-28",
      "privacy": "private",
     */
    protected void parseTrip(JSONObject object) {

        try {
            String tripID = object.optString("_id");
            TripContent mTripContent = new TripContent();
            mTripContent.getContentValues().put(TripProvider.FIELD_ID, tripID.hashCode());
            mTripContent.getContentValues().put(TripProvider.FIELD_TRIP_ID, object.optString("_id"));
            mTripContent.getContentValues().put(TripProvider.FIELD_TRIP_PHOTO, object.optString("avatar"));
            mTripContent.getContentValues().put(TripProvider.FIELD_TRIP_NAME, object.optString("tripName"));
            mTripContent.getContentValues().put(TripProvider.FIELD_TRIP_DESTINATION, object.optString("destination"));
            mTripContent.getContentValues().put(TripProvider.FIELD_SORT_ID, object.optString("sortID"));
            mTripContent.getContentValues().put(TripProvider.FIELD_TRIP_START_DAY, object.optString("startDate"));
            mTripContent.getContentValues().put(TripProvider.FIELD_TRIP_END_DAY, object.optString("endDate"));

            Uri tripUri = TripProvider.getProviderUri(mContext, TripProvider.TABLE_TRIP);
            mContext.getContentResolver().insert(tripUri, mTripContent.getContentValues());

            Uri dayUri = TripProvider.getProviderUri(mContext, TripProvider.TABLE_DAY);
            JSONArray dayArray = object.getJSONArray("days");
            for (int i = 0; i < dayArray.length(); i++) {
                JSONObject dayObj = dayArray.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(TripProvider.FIELD_ID, (tripID + dayObj.optInt("sortID")).hashCode());
                values.put(TripProvider.FIELD_DAY_BELONG_TRIP, object.optString("_id").hashCode());
                values.put(TripProvider.FIELD_DAY_HIGHLIGHT, dayObj.optString("highlight"));
                values.put(TripProvider.FIELD_SORT_ID, dayObj.optInt("sortID"));
                mContext.getContentResolver().insert(dayUri, values);

                Uri strokeUri = TripProvider.getProviderUri(mContext, TripProvider.TABLE_STROKE);
                JSONArray strokeArray = object.getJSONArray("strokes");
                for (int j = 0; j < strokeArray.length(); j++) {
                    JSONObject strokeObj = dayArray.getJSONObject(i);
                    values = new ContentValues();
                    values.put(TripProvider.FIELD_ID, (j + tripID + strokeObj.optString("sortID")).hashCode());
                    values.put(TripProvider.FIELD_STROKE_BELONG_TRIP, tripID);
                    values.put(TripProvider.FIELD_STROKE_BELONG_DAY, j);
                    values.put(TripProvider.FIELD_SORT_ID, strokeObj.optInt("sortID"));
                    mContext.getContentResolver().insert(strokeUri, values);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // TripUtils.addTrip();
    }
}
