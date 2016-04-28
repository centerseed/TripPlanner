package com.barry.tripplanner.utils;

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

    public RequestBody build() {
        return  RequestBody.create(JSON, mObject.toString());
    }
    /*
    String json = new JSONBuilder().setParameter(
                "usersys_id", AccountUtils.getSysId(getContext()),
                "session_id", AccountUtils.getToken(getContext())).build();

        RequestBody body = RequestBody.create(Const.JSON, json);
    * */
}
