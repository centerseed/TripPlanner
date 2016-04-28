package com.barry.tripplanner.base;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;


public abstract class BaseResponseParser {
    public static final String TAG = "BaseResponseParser";

    protected Context mContext;
    protected JSONObject mJsonObj;
    protected String mString;

    public BaseResponseParser(Context c) {
        mContext = c;

    }

    public BaseResponseParser parse(Response response) throws AuthFailException {
        parseNetStatus(response);
        return this;
    }

    public JSONObject getJSONObject() {
        return mJsonObj;
    }

    public String getString() {
        return mString;
    }

    private void parseNetStatus(Response response) throws AuthFailException {
        if (response.code() == 200) {
            try {
                String body = response.body().string();
                if (body != null) {
                    parse(new JSONObject(body));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (response.code() == 401) {
            Log.e(TAG, "Auth fail");

            throw new AuthFailException();
        } else {
            Log.e(TAG, "HTTP ERROR: " + response.message());
        }
    }

    protected abstract void parse(JSONObject object) throws JSONException;

    public class AuthFailException extends Exception {
    }
}
