package com.barry.tripplanner.base;

import android.content.ContentResolver;
import android.content.SyncStatusObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.MenuItem;
import android.view.View;

import com.barry.tripplanner.R;
import com.barry.tripplanner.utils.AccountUtils;

public abstract class SyncFragment extends Fragment {

    Object mHandlerListener;
    SwipeRefreshLayout mSwipeRefresh;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        mSwipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        if (mSwipeRefresh != null)
            mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    onSync();
                }
            });
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    protected abstract void onSync();

    public void registerSyncStatusListener(int authorityId) {
        final String authority = getString(authorityId);
        if (mSwipeRefresh != null && authority != null) {
            mSwipeRefresh.setEnabled(true);
            mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    onSync();
                }
            });
            mHandlerListener = ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE, new SyncStatusObserver() {
                @Override
                public void onStatusChanged(int which) {
                    final boolean syncing = ContentResolver.isSyncActive(AccountUtils.getCurrentAccount(getContext()), authority);
                    mSwipeRefresh.post(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRefresh.setRefreshing(syncing);
                        }
                    });
                }
            });
        }
    }

    public void unregisterSyncStatusListener() {
        if (mHandlerListener != null) {
            mSwipeRefresh.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefresh.setRefreshing(false);
                }
            });
            ContentResolver.removeStatusChangeListener(mHandlerListener);
            mHandlerListener = null;
        }
    }

    protected void showRefreshing() {
        if (mSwipeRefresh != null)
            mSwipeRefresh.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefresh.setRefreshing(true);
                }
            });
    }

    protected void hideRefreshing() {
        if (mSwipeRefresh != null)
            mSwipeRefresh.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefresh.setRefreshing(false);
                }
            });
    }
}
