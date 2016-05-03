package com.barry.tripplanner.sync;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AttractionListParser extends AttractionParser {
    public AttractionListParser(Context c) {
        super(c);
    }

    @Override
    protected void parse(JSONObject object) throws JSONException {
        JSONArray array = object.getJSONArray("attractions");
        for (int i = 0; i < array.length(); i++) {
            parseAttraction(array.getJSONObject(i));
        }
    }
}
