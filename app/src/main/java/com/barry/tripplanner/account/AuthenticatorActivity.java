package com.barry.tripplanner.account;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.barry.tripplanner.R;
import com.barry.tripplanner.provider.TripProvider;
import com.barry.tripplanner.sync.AsyncResponseParser;
import com.barry.tripplanner.trip.TripContent;
import com.barry.tripplanner.utils.AccountUtils;
import com.barry.tripplanner.utils.ConfigUtils;
import com.barry.tripplanner.utils.JSONBuilder;
import com.barry.tripplanner.utils.TripUtils;
import com.barry.tripplanner.utils.URLBuilder;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class AuthenticatorActivity extends AccountAuthenticatorActivity {

    static final String TAG = "AuthenticatorActivity";
    CallbackManager callbackManager;
    private final OkHttpClient mClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.d(TAG, "Token -> " + AccessToken.getCurrentAccessToken());
                Log.d(TAG, "Name -> " + Profile.getCurrentProfile().getName());
                Log.d(TAG, "id -> " + Profile.getCurrentProfile().getId());
                Log.d(TAG, "picture url -> " + Profile.getCurrentProfile().getProfilePictureUri(300, 300));

                RequestBody body = new JSONBuilder().setParameter(
                        "nickname", Profile.getCurrentProfile().getName(),
                        "avatar", Profile.getCurrentProfile().getProfilePictureUri(300, 300).toString(),
                        "userID", Profile.getCurrentProfile().getId()).build();

                String url = new URLBuilder(AuthenticatorActivity.this).host(R.string.host).path("register").toString();
                Log.d(TAG, "create User --> " + url);
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                mClient.newCall(request).enqueue(new AsyncResponseParser(AuthenticatorActivity.this, new AsyncResponseParser.NetError() {

                    @Override
                    public void onNetError(IOException e) {

                    }

                    @Override
                    public void onResponseError(int error) {

                    }
                }) {

                    @Override
                    protected void parseResponse(final JSONObject obj) throws Exception {
                        // TODO: Login with name, id, picture
                        // TODO: Login succeed
                        Log.d(TAG, "Register -> " + obj.toString());
                        Log.d(TAG, "Avatar -> " + obj.optString("avatar"));

                        Account account = new Account(Profile.getCurrentProfile().getName(), getString(R.string.tripAccountType));

                        AccountManager.get(AuthenticatorActivity.this).addAccountExplicitly(account, obj.optString("userID"), null);
                        AccountUtils.setAccountPicture(AuthenticatorActivity.this, obj.optString("avatar"));
                        ConfigUtils.setLocalUsageOnly(AuthenticatorActivity.this, false);
                        finish();
                    }
                });
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        // initDummyData();
    }

    private void initDummyData() {

        TripContent tripContent = new TripContent();
        tripContent.getContentValues().put(TripProvider.FIELD_ID, "東京小旅行".hashCode());
        tripContent.getContentValues().put(TripProvider.FIELD_TRIP_DESTINATION, "東京");
        tripContent.getContentValues().put(TripProvider.FIELD_TRIP_NAME, "東京小旅行");
        tripContent.getContentValues().put(TripProvider.FIELD_TRIP_PHOTO, "http://farm5.static.flickr.com/4060/4650494949_2d3185a48f_o.jpg");
        tripContent.getContentValues().put(TripProvider.FIELD_SORT_ID, 0);
        tripContent.getContentValues().put(TripProvider.FIELD_TRIP_START_DAY, "2015-3-10");
        tripContent.getContentValues().put(TripProvider.FIELD_TRIP_END_DAY, "2015-3-16");
        TripUtils.addTrip(this, tripContent, null);

        tripContent = new TripContent();
        tripContent.getContentValues().put(TripProvider.FIELD_ID, "古都跨年行".hashCode());
        tripContent.getContentValues().put(TripProvider.FIELD_TRIP_NAME, "古都跨年行");
        tripContent.getContentValues().put(TripProvider.FIELD_TRIP_DESTINATION, "京都");
        tripContent.getContentValues().put(TripProvider.FIELD_TRIP_PHOTO, "http://qglbbs.b0.upaiyun.com/forum/201407/21/155844wjhvzn76tkqugwpq.jpg");
        tripContent.getContentValues().put(TripProvider.FIELD_SORT_ID, 1);
        tripContent.getContentValues().put(TripProvider.FIELD_TRIP_START_DAY, "2016-12-29");
        tripContent.getContentValues().put(TripProvider.FIELD_TRIP_END_DAY, "2017-1-2");
        TripUtils.addTrip(this, tripContent, null);

        tripContent = new TripContent();
        tripContent.getContentValues().put(TripProvider.FIELD_ID, "北海道自然探險".hashCode());
        tripContent.getContentValues().put(TripProvider.FIELD_TRIP_NAME, "北海道自然探險");
        tripContent.getContentValues().put(TripProvider.FIELD_TRIP_DESTINATION, "北海道");
        tripContent.getContentValues().put(TripProvider.FIELD_TRIP_PHOTO, "http://www.4p.com.tw/eWeb_spunktour/IMGDB/000453/00002613.jpg");
        tripContent.getContentValues().put(TripProvider.FIELD_SORT_ID, 2);
        tripContent.getContentValues().put(TripProvider.FIELD_TRIP_START_DAY, "2015-4-10");
        tripContent.getContentValues().put(TripProvider.FIELD_TRIP_END_DAY, "2015-4-21");
        TripUtils.addTrip(this, tripContent, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void performLocalUsage(View view) {
        ConfigUtils.setLocalUsageOnly(this, true);
        finish();
    }
}
