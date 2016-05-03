package com.barry.tripplanner.sync;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.barry.tripplanner.base.BaseResponseParser;
import com.barry.tripplanner.provider.TripProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TripListParser extends TripParser {
    public static final String TAG = "TripParser";

    public TripListParser(Context c) {
        super(c);
    }

    @Override
    protected void parse(JSONObject object) throws JSONException {
        Log.d(TAG, object.toString());

        Uri uri = TripProvider.getProviderUri(mContext, TripProvider.TABLE_TRIP);
        mContext.getContentResolver().delete(uri, TripProvider.FIELD_SYNC + "=?", new String[]{TripProvider.SYNC_DONE});

        JSONArray array = object.getJSONArray("trips");
        for (int i = 0; i < array.length(); i++) {
            parseTrip(array.getJSONObject(i));
        }
    }
}
