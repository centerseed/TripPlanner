package com.barry.tripplanner.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TripSyncService extends Service {

    private static TripSyncAdapter tripSyncAdapter = null;
    private static final Object sSyncAdapterLock = new Object();

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (tripSyncAdapter == null) {
                tripSyncAdapter = new TripSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return tripSyncAdapter.getSyncAdapterBinder();
    }
}