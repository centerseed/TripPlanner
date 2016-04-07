package com.barry.tripplanner.trip;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.barry.tripplanner.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ThumbAdapter extends RecyclerView.Adapter {
    ArrayList<String> mPhotoList;
    Context mContext;
    ArrayList<Boolean> mCheckList = new ArrayList<>();
    ThumbCallback mCallback;

    public interface ThumbCallback {
        void onThumbSelect(String url, boolean isCheck);
    }

    public ThumbAdapter(Context context, ArrayList<String> arrayList, ThumbCallback callback) {
        mContext = context;
        mPhotoList = arrayList;
        mCallback = callback;
        for (String str : arrayList) mCheckList.add(false);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.card_thumb, parent, false);
        return new ThumbViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ThumbViewHolder vh = (ThumbViewHolder) holder;
        Picasso.with(mContext).load(mPhotoList.get(position)).resize(320, 240).placeholder(android.R.drawable.ic_menu_gallery).into(vh.mImageView);

        boolean isCheck = mCheckList.get(position);
        if (isCheck) vh.mChecked.setVisibility(View.VISIBLE);
        else vh.mChecked.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mPhotoList == null ? 0 : mPhotoList.size();
    }

    class ThumbViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        ImageView mChecked;

        public ThumbViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.thumb);
            mChecked = (ImageView) itemView.findViewById(R.id.checked);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isCheck = mCheckList.get(getAdapterPosition());
                    if (!isCheck) mChecked.setVisibility(View.VISIBLE);
                    else mChecked.setVisibility(View.GONE);
                    for (int i = 0; i < mCheckList.size(); i++) {
                        mCheckList.set(i, false);
                    }
                    mCheckList.set(getAdapterPosition(), !isCheck);
                    if (mPhotoList != null)
                        mCallback.onThumbSelect(mPhotoList.get(getAdapterPosition()), !isCheck);
                }
            });
        }
    }
}
