package com.barry.tripplanner.sync;

import android.content.Context;
import android.util.Log;

import com.barry.tripplanner.base.BaseResponseParser;

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

        JSONArray array = object.getJSONArray("trips");
        for (int i = 0; i < object.length(); i++) {
            parseTrip(array.getJSONObject(i));
        }
    }
}
