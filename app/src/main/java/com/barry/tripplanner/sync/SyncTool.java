package com.barry.tripplanner.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

import com.barry.tripplanner.R;
import com.barry.tripplanner.provider.TripProvider;
import com.barry.tripplanner.utils.AccountUtils;

public class SyncTool {
    Bundle mArgs;
    Context mContext;
    Account mAccount;
    String mUserID;

    public SyncTool with(Context c) {
        mContext = c;
        mArgs = new Bundle();
        mArgs.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        mArgs.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        mArgs.putString(TripSyncAdapter.ARG_USER_ID, mUserID);

        mAccount = AccountUtils.getCurrentAccount(mContext);
        mUserID = AccountManager.get(mContext).getPassword(mAccount);

        return this;
    }

    public void syncTrip(String tripId) {
        mArgs.putString(TripSyncAdapter.ARG_TRIP_ID, tripId);
        mArgs.putString(TripSyncAdapter.ACTION_SYNC, TripProvider.SYNC_TRIP);
        mContext.getContentResolver().requestSync(AccountUtils.getCurrentAccount(mContext), mContext.getString(R.string.auth_provider_trip), mArgs);
    }

    public void syncAllTrip() {
        mArgs.putString(TripSyncAdapter.ACTION_SYNC, TripProvider.SYNC_ALL_TRIP);
        mContext.getContentResolver().requestSync(AccountUtils.getCurrentAccount(mContext), mContext.getString(R.string.auth_provider_trip), mArgs);
    }
}
