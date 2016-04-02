package com.barry.tripplanner.account;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.barry.tripplanner.R;
import com.barry.tripplanner.utils.AccountUtils;
import com.barry.tripplanner.utils.ConfigUtils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class AuthenticatorActivity extends AccountAuthenticatorActivity {

    static final String TAG = "AuthenticatorActivity";
    CallbackManager callbackManager;

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

                // TODO: Login with name, id, picture
                // TODO: Login succeed
                Account account = new Account(Profile.getCurrentProfile().getName(), getString(R.string.tripAccountType));

                AccountManager.get(AuthenticatorActivity.this).addAccountExplicitly(account, Profile.getCurrentProfile().getId(), null);
                AccountUtils.setAccountPicture(AuthenticatorActivity.this, Profile.getCurrentProfile().getProfilePictureUri(300, 300).toString());
                finish();
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
