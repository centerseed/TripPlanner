package com.barry.tripplanner.base;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.barry.tripplanner.R;

abstract public class RecyclerListFragment extends ContentFragment {
    protected AbstractRecyclerCursorAdapter m_adapter;
    protected RecyclerView m_recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        m_adapter = getAdapter();
        return inflater.inflate(R.layout.recycleview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        m_recyclerView = (RecyclerView) view.findViewById(R.id.recycleView);
        if (m_recyclerView != null) {
            m_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            m_recyclerView.setAdapter(m_adapter);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (m_adapter != null) m_adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (m_adapter != null) m_adapter.swapCursor(null);
    }

    public abstract AbstractRecyclerCursorAdapter getAdapter();
}
