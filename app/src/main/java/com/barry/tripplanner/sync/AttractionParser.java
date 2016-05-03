package com.barry.tripplanner.sync;

import android.content.Context;
import android.net.Uri;

import com.barry.tripplanner.base.BaseResponseParser;
import com.barry.tripplanner.provider.TripProvider;
import com.barry.tripplanner.trip.TripContent;
import com.barry.tripplanner.trip.attraction.AttractionContent;

import org.json.JSONException;
import org.json.JSONObject;

public class AttractionParser extends BaseResponseParser {
    public AttractionParser(Context c) {
        super(c);
    }

    @Override
    protected void parse(JSONObject object) throws JSONException {
        parseAttraction(object);
    }

    protected void parseAttraction(JSONObject object) {
        String attrID = object.optString("attrID");
        AttractionContent mAttractionContent = new AttractionContent();
        mAttractionContent.getContentValues().put(TripProvider.FIELD_SYNC, TripProvider.SYNC_DONE);
        mAttractionContent.getContentValues().put(TripProvider.FIELD_ID, attrID.hashCode());

        mAttractionContent.getContentValues().put(TripProvider.FIELD_ATTRACTION_ID, attrID);
        mAttractionContent.getContentValues().put(TripProvider.FIELD_ATTRACTION_NAME, object.optString("name"));
        mAttractionContent.getContentValues().put(TripProvider.FIELD_ATTRACTION_LAT, object.optString("lat"));
        mAttractionContent.getContentValues().put(TripProvider.FIELD_ATTRACTION_LNG, object.optString("lng"));
        mAttractionContent.getContentValues().put(TripProvider.FIELD_ATTRACTION_SNAPSHOT, object.optString("snapshot"));
        mAttractionContent.getContentValues().put(TripProvider.FIELD_ATTRACTION_RANK, object.optString("rank"));
        mAttractionContent.getContentValues().put(TripProvider.FIELD_ATTRACTION_TYPE, object.optString("type"));

        Uri attrUri = TripProvider.getProviderUri(mContext, TripProvider.TABLE_ATTRACTION);
        mContext.getContentResolver().insert(attrUri, mAttractionContent.getContentValues());
    }
}
