package com.barry.tripplanner.trip;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.barry.tripplanner.beans.ThumbObject;

import java.util.ArrayList;

public class ThumbAdapter extends RecyclerView.Adapter {
    ArrayList<ThumbObject> mTthumbObjects;
    Context mContext;

    public ThumbAdapter(Context context, ArrayList<ThumbObject> arrayList) {
        mContext = context;
        mTthumbObjects = arrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
