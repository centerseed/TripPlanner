package com.barry.tripplanner.base;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.barry.tripplanner.R;
import com.barry.tripplanner.provider.TripProvider;
import com.barry.tripplanner.trip.DragTripAdapter;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;

import java.util.ArrayList;

abstract public class DragRecycleListFragment extends ContentFragment implements DragListCallback {
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private AbstractRecyclerCursorAdapter mAdapter;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;

    protected ArrayList<SortPair> mSortIDMap = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recycleview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //noinspection ConstantConditions
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recycleView);
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        // drag & drop manager
        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
        mRecyclerViewDragDropManager.setDraggingItemShadowDrawable(
                (NinePatchDrawable) ContextCompat.getDrawable(getContext(), R.drawable.material_shadow_z3));

        //adapter
        mAdapter = getAdapter();
        mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(mAdapter);      // wrap for dragging
        mRecyclerView.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter

        final GeneralItemAnimator animator = new RefactoredDefaultItemAnimator();

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(animator);

        // additional decorations
        //noinspection StatementWithEmptyBody
        if (supportsViewElevation()) {
            // Lollipop or later has native drop shadow feature. ItemShadowDecorator is not required.
        } else {
            mRecyclerView.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) ContextCompat.getDrawable(getContext(), R.drawable.material_shadow_z1)));
        }
        mRecyclerView.addItemDecoration(new SimpleListDividerDecorator(ContextCompat.getDrawable(getContext(), R.drawable.list_divider_h), true));
        mRecyclerViewDragDropManager.attachRecyclerView(mRecyclerView);
    }

    abstract protected AbstractRecyclerCursorAdapter getAdapter();

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cl = (CursorLoader) super.onCreateLoader(id, args);
        cl.setSortOrder(TripProvider.FIELD_SORT_ID + " ASC");
        return cl;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            mAdapter.swapCursor(cursor);
            updateSortIdMap(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    private boolean supportsViewElevation() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

    protected void updateSortIdMap(Cursor c) {
        mSortIDMap.clear();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            mSortIDMap.add(new SortPair(c.getInt(c.getColumnIndex(TripProvider.FIELD_ID)), c.getInt(c.getColumnIndex(TripProvider.FIELD_SORT_ID))));
            c.moveToNext();
        }
    }
    protected void resetSortIdMap(int fromPos, int toPos) {
        SortPair currpair = mSortIDMap.get(fromPos);
        if (fromPos < toPos) {
            mSortIDMap.add(++toPos, currpair);
            mSortIDMap.remove(fromPos);
        } else {
            mSortIDMap.add(toPos, currpair);
            mSortIDMap.remove(++fromPos);
        }

        for (int i = 0; i < mSortIDMap.size(); i++)
            mSortIDMap.get(i).setSortId(i);

        for (SortPair pair : mSortIDMap) {
            ContentValues values = new ContentValues();
            values.put(TripProvider.FIELD_SORT_ID, pair.getSortId());

            int id = pair.getId();
            getContext().getContentResolver().update(mUri, values, TripProvider.FIELD_ID + "=?", new String[]{id + ""});
        }
    }

    public class SortPair {
        private int mId;
        private int mSortId;

        public SortPair(int id, int sortId) {
            this.mId = id;
            this.mSortId = sortId;
        }

        public void setSortId(int sortId) {
            mSortId = sortId;
        }

        public int getId() {
            return mId;
        }

        public int getSortId() {
            return mSortId;
        }
    }

}
