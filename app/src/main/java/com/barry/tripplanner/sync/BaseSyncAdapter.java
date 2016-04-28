package com.barry.tripplanner.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.barry.tripplanner.R;
import com.barry.tripplanner.base.BaseResponseParser;
import com.barry.tripplanner.utils.AccountUtils;
import com.barry.tripplanner.utils.ConfigUtils;

import java.io.IOException;

public abstract class BaseSyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String TAG = "BaseSyncAdapter";
    public static final String ACTION_SYNC = "action_sync";
    public static final String ARG_USER_ID = "arg_user_id";
    public static final String ARG_TRIP_ID = "arg_trip_id";

    protected ContentResolver mContentResolver;
    protected Context mContext;
    protected String mHost;
    protected String mUserID;

    public BaseSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        init(context);
        mContext = context;
        mHost = mContext.getResources().getString(R.string.host);
    }

    protected void init(Context context) {
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        if (ConfigUtils.getLocalUsageOnly(mContext)) {
            Log.d(TAG, "local usage only");
            return;
        }

        if (null != extras) {
            mUserID = extras.getString(ARG_USER_ID);
            try {
                pushLocalData();
                performSync(extras);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (BaseResponseParser.AuthFailException e) {
                e.printStackTrace();
                AccountUtils.invalidCurrentAccount(mContext);
            }
        }
    }

    abstract void pushLocalData() throws IOException, BaseResponseParser.AuthFailException;
    abstract void performSync(Bundle extras) throws IOException, BaseResponseParser.AuthFailException;
}
