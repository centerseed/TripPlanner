package com.barry.tripplanner.trip;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.barry.tripplanner.base.AbstractRecyclerCursorAdapter;

public class AttractionAdapter extends AbstractRecyclerCursorAdapter<Cursor> {
    public AttractionAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    class AttractionViewHolder extends RecyclerView.ViewHolder {

        public AttractionViewHolder(View itemView) {
            super(itemView);
        }
    }
}
