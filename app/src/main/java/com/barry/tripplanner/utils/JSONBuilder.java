package com.barry.tripplanner.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.barry.tripplanner.provider.TripProvider;
import com.barry.tripplanner.trip.TripContent;
import com.barry.tripplanner.trip.attraction.AttractionContent;
import com.barry.tripplanner.trip.stroke.StrokeContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class JSONBuilder {
    private JSONObject mObject;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public JSONBuilder() {
        mObject = new JSONObject();
    }

    public JSONBuilder setParameter(String... params) {
        for (int i = 0; i < params.length; i += 2)
            if (params[i] != null && params[i + 1] != null)
                try {
                    mObject.put(params[i], params[i + 1]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        return this;
    }

    public JSONBuilder createAttractionJSON(Context context, Cursor c) {
        AttractionContent content = new AttractionContent();
        content.withCursor(c);

        try {
            mObject.put("name", content.getName());
            mObject.put("attrID", content.getAttrId());
            mObject.put("type", content.getType());
            mObject.put("lat", content.getLat());
            mObject.put("lng", content.getLng());
            mObject.put("snapshot", content.getSnapshot());
            mObject.put("rank", content.getRank());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public JSONBuilder createTripJSON(Context context, Cursor c) {
        TripContent content = new TripContent();
        Uri dayUri = TripProvider.getProviderUri(context, TripProvider.TABLE_DAY);
        Uri strokeUri = TripProvider.getProviderUri(context, TripProvider.TABLE_STROKE);

        content.withCursor(c);
        try {
            mObject.put("tripName", content.getName());
            mObject.put("avatar", content.getPicPhoto());
            mObject.put("destination", content.getDestination());
            mObject.put("sortID", content.getSortId());
            mObject.put("startDate", content.getStartDay());
            mObject.put("endDate", content.getEndDay());

        /*    String attrIDs[] = content.getAttractionIDs().split("|");
            JSONArray array = new JSONArray();
            for (int i = 0; i < attrIDs.length; i++) {
                array.put(attrIDs[i]);
            }
            mObject.put("attractionIDs", array); */

            JSONArray days = new JSONArray();
            Cursor dayCursor = context.getContentResolver().query(dayUri, null, TripProvider.FIELD_DAY_BELONG_TRIP + "=?", new String[]{content.getLocalId() + ""}, TripProvider.FIELD_SORT_ID + " ASC");
            if (dayCursor != null && dayCursor.moveToFirst()) {
                JSONArray strokes = new JSONArray();
                while (!dayCursor.isAfterLast()) {
                    Cursor strokeCursor = context.getContentResolver().query(strokeUri,
                            null,
                            TripProvider.FIELD_STROKE_BELONG_TRIP + "=? AND " + TripProvider.FIELD_STROKE_BELONG_TRIP + "=?",
                            new String[]{content.getLocalId() + "", dayCursor.getInt(dayCursor.getColumnIndex(TripProvider.FIELD_SORT_ID)) + ""},
                            TripProvider.FIELD_SORT_ID + " ASC");
                    if (strokeCursor != null && strokeCursor.moveToFirst()) {
                        while (!strokeCursor.isAfterLast()) {
                            StrokeContent strokeContent = new StrokeContent(context);
                            strokeContent.withCursor(strokeCursor);

                            JSONObject strokeObj = new JSONObject();
                            strokeObj.put("attractionID", strokeContent.getAttractionID());
                            strokeObj.put("strokeTime", strokeContent.getTime());
                            strokes.put(strokeObj);
                        }
                        strokeCursor.close();
                    }


                    JSONObject dayObj = new JSONObject();
                    dayObj.put("highlight", dayCursor.getString(dayCursor.getColumnIndex(TripProvider.FIELD_DAY_HIGHLIGHT)));
                    dayObj.put("strokes", strokes);
                    dayObj.put("sortID", dayCursor.getInt(dayCursor.getColumnIndex(TripProvider.FIELD_SORT_ID)));
                    days.put(dayObj);
                    dayCursor.moveToNext();
                }
                dayCursor.close();
            }

            mObject.put("days", days);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return this;
    }

    public RequestBody build() {
        return RequestBody.create(JSON, mObject.toString());
    }
    /*
    String json = new JSONBuilder().setParameter(
                "usersys_id", AccountUtils.getSysId(getContext()),
                "session_id", AccountUtils.getToken(getContext())).build();

        RequestBody body = RequestBody.create(Const.JSON, json);
    * */

}
